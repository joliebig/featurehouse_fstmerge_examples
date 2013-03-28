using System;
using System.IO;
using System.Text.RegularExpressions;
using System.Threading;
using ThoughtWorks.CruiseControl.Core.Util;
namespace ThoughtWorks.CruiseControl.Core.Sourcecontrol.Telelogic
{
 public class SynergyCommand : ISynergyCommand
 {
  public const string SessionToken = "CCM_ADDR";
  public const string DateTimeFormat = "CCM_NO_LOCALE_TIMES";
  private static readonly object PadLock;
  private ProcessExecutor executor;
  private SynergyConnectionInfo connection;
  private SynergyProjectInfo project;
  private bool disposed;
  private bool isOpen;
  public SynergyCommand(SynergyConnectionInfo connectionInfo, SynergyProjectInfo projectInfo)
  {
   disposed = false;
   isOpen = false;
   executor = new ProcessExecutor();
   connection = connectionInfo;
   project = projectInfo;
   AppDomain.CurrentDomain.DomainUnload += new EventHandler(AppDomain_Unload);
   AppDomain.CurrentDomain.ProcessExit += new EventHandler(AppDomain_Unload);
  }
  static SynergyCommand()
  {
   PadLock = new object();
  }
  ~SynergyCommand()
  {
   Dispose();
  }
  public void AppDomain_Unload(object sender, EventArgs e)
  {
   Close();
  }
  public void Dispose()
  {
   Close();
   if (! disposed)
   {
    GC.SuppressFinalize(this);
    disposed = true;
   }
  }
  private void Open()
  {
   ProcessInfo info;
   ProcessResult result;
   int originalTimeout;
   string temp;
   string message;
   if (! isOpen)
   {
    info = SynergyCommandBuilder.Start(connection, project);
    Log.Debug("Queued for critical section to open CM Synergy session; blocking until lock is acquired.");
    lock (PadLock)
    {
     Log.Debug("Acquired lock to open a session");
     result = executor.Execute(info);
     Log.Debug("Releasing lock to open a session");
    }
    if (result.TimedOut)
    {
     message = String.Format(@"Synergy connection timed out after {0} seconds.", connection.Timeout);
     throw(new CruiseControlException(message));
    }
    if (result.Failed)
    {
     if (connection.PollingEnabled)
     {
      if (IsDatabaseProtected(result.StandardError, connection.Host, connection.Database))
      {
       Log.Warning(String.Format("Database {0} on Host {1} Is Protected.  Waiting 60 seconds to reconnect.", connection.Host, connection.Database));
       Thread.Sleep(new TimeSpan(0, 1, 0));
       originalTimeout = connection.Timeout;
       connection.Timeout -= 60;
       Open();
       connection.Timeout = originalTimeout;
       return;
      }
     }
    }
    temp = result.StandardOutput;
    if (null != temp && temp.Length > 0)
    {
     connection.SessionId = temp.Trim();
     Log.Info(String.Concat("CCM_ADDR set to '", connection.SessionId, "'"));
    }
    else
    {
     throw(new CruiseControlException("CM Synergy logon failed"));
    }
    Initialize();
    info = SynergyCommandBuilder.GetSubProjects(connection, project);
    info.EnvironmentVariables[SessionToken] = connection.SessionId;
    executor.Execute(info);
    info = SynergyCommandBuilder.SetProjectRelease(connection, project);
    info.EnvironmentVariables[SessionToken] = connection.SessionId;
    executor.Execute(info);
    isOpen = true;
   }
  }
  private void Close()
  {
   ProcessInfo info;
   if (isOpen)
   {
    info = SynergyCommandBuilder.Stop(connection);
    executor.Execute(info);
    connection.Reset();
   }
   isOpen = false;
  }
  private void Initialize()
  {
   ProcessInfo info;
   ProcessResult result;
   string temp;
   try
   {
    info = SynergyCommandBuilder.GetDelimiter(connection);
    info.EnvironmentVariables[SessionToken] = connection.SessionId;
    result = Execute(info);
    temp = result.StandardOutput;
    if (temp.Length == 0)
     throw(new CruiseControlException("Failed to read the CM Synergy delimiter"));
    connection.Delimiter = temp[0];
   }
   catch (Exception inner)
   {
    throw(new CruiseControlException("Failed to read the CM Synergy database delimiter", inner));
   }
   try
   {
    info = SynergyCommandBuilder.GetProjectFullName(connection, project);
    info.EnvironmentVariables[SessionToken] = connection.SessionId;
    result = Execute(info);
    temp = result.StandardOutput.Trim();
    project.ObjectName = temp;
   }
   catch (Exception inner)
   {
    temp = String.Concat(@"CM Synergy Project """, project.ProjectSpecification, @""" not found");
    throw(new CruiseControlException(temp, inner));
   }
   try
   {
    info = SynergyCommandBuilder.GetWorkArea(connection, project);
    info.EnvironmentVariables[SessionToken] = connection.SessionId;
    result = Execute(info);
    project.WorkAreaPath = Path.GetFullPath(result.StandardOutput.Trim());
    if (! Directory.Exists(project.WorkAreaPath))
    {
     throw(new CruiseControlException(String.Concat("CM Synergy work area '", result.StandardOutput.Trim(), "' not found.")));
    }
    Log.Info(String.Concat(project.ProjectSpecification, " work area is '", project.WorkAreaPath, "'"));
   }
   catch (Exception inner)
   {
    temp = String.Concat(@"CM Synergy Work Area for Project """, project.ProjectSpecification, @""" could not be determined.");
    throw(new CruiseControlException(temp, inner));
   }
  }
  private void ValidateSession()
  {
   bool isValid = (null != connection.SessionId && connection.SessionId.Length > 0);
   if (isOpen && isValid)
   {
    ProcessInfo info = SynergyCommandBuilder.Heartbeat(connection);
    if (null != project && null != project.WorkAreaPath && project.WorkAreaPath.Length > 0)
    {
     info = new ProcessInfo(info.FileName, info.Arguments, project.WorkAreaPath);
    }
    if (null != connection && null != connection.SessionId && connection.SessionId.Length > 0)
    {
     info.EnvironmentVariables[SessionToken] = connection.SessionId;
    }
    ProcessResult result = executor.Execute(info);
    isValid = IsSessionAlive(result.StandardOutput, connection.SessionId, connection.Database);
    if (! isValid)
    {
     Close();
    }
   }
   if (! isValid)
   {
    Open();
   }
  }
  public bool IsSessionAlive(string status, string sessionId, string database)
  {
   Regex grep;
   const string template = @"(?im:(@\s+{0}[\s\S]*Database:\s+{1}))";
   string pattern;
   pattern = String.Format(template, Regex.Escape(sessionId), Regex.Escape(database));
   grep = new Regex(pattern, RegexOptions.CultureInvariant);
   return (grep.IsMatch(status));
  }
  public bool IsDatabaseProtected(string status, string host, string database)
  {
   Regex grep;
   const string template = @"(?im-x:(Warning: Database {0} on host {1} is protected\.\s+Starting a session is not allowed\.))";
   string pattern;
   bool isProtected;
   pattern = String.Format(template, Regex.Escape(database), Regex.Escape(host));
   grep = new Regex(pattern, RegexOptions.CultureInvariant);
   isProtected = grep.IsMatch(status);
   return (isProtected);
  }
  public ProcessResult Execute(ProcessInfo processInfo)
  {
   return Execute(processInfo, true);
  }
  public ProcessResult Execute(ProcessInfo processInfo, bool failOnError)
  {
   ValidateSession();
   if (null != project && null != project.WorkAreaPath && project.WorkAreaPath.Length > 0)
   {
    processInfo = new ProcessInfo(processInfo.FileName, processInfo.Arguments, project.WorkAreaPath);
   }
   processInfo.EnvironmentVariables[SessionToken] = connection.SessionId;
   processInfo.EnvironmentVariables[DateTimeFormat] = DateTimeFormat;
   processInfo.TimeOut = connection.Timeout*1000;
   ProcessResult result = executor.Execute(processInfo);
   if (result.TimedOut)
   {
    string message = String.Format(@"Synergy source control operation has timed out after {0} seconds. Process command: ""{1}"" {2}", connection.Timeout, processInfo.FileName, processInfo.PublicArguments);
    throw(new CruiseControlException(message));
   }
   if (result.Failed && failOnError)
   {
    string message = String.Format("Synergy source control operation failed.\r\n" + "Command: \"{0}\" {1}\r\n" + "Error Code: {2}\r\n" + "Errors:\r\n{3}\r\n{4}", processInfo.FileName, processInfo.PublicArguments, result.ExitCode, result.StandardError, result.StandardOutput);
    if (result.HasErrorOutput)
    {
     Log.Warning(string.Format("Synergy wrote output to stderr: {0}", result.StandardError));
    }
    throw(new CruiseControlException(message));
   }
   return result;
  }
 }
}
