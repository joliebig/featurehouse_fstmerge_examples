namespace ThoughtWorks.CruiseControl.Core.Tasks
{
    using System;
    using System.Text;
    using System.IO;
    using ThoughtWorks.CruiseControl.Core.Util;
    using Exortech.NetReflector;
    using ThoughtWorks.CruiseControl.Core.Config;
    [ReflectorType("FinalBuilder")]
 public class FinalBuilderTask
        : TaskBase, IConfigurationValidation
 {
  private readonly ProcessExecutor _executor;
  private readonly IRegistry _registry;
  private FinalBuilderVersion _fbversion;
  private string _fbcmdpath;
  public FinalBuilderTask() : this(new Registry(), new ProcessExecutor()) {}
  public FinalBuilderTask(IRegistry registry, ProcessExecutor executor)
  {
   _executor = executor;
   _registry = registry;
   _fbversion = FinalBuilderVersion.FBUnknown;
   _fbcmdpath = String.Empty;
  }
  [ReflectorProperty("ProjectFile", Required = true)]
  public string ProjectFile = string.Empty;
        [ReflectorProperty("ShowBanner", Required = false)]
  public bool ShowBanner;
        [ReflectorArray("FBVariables", Required = false)]
  public FBVariable[] FBVariables;
        [ReflectorProperty("FBVersion", Required = false)]
  public int FBVersion
  {
   get { return (int)_fbversion; }
   set { _fbversion = (FinalBuilderVersion)value; }
  }
        [ReflectorProperty("FBCMDPath", Required = false)]
  public string FBCMDPath
  {
            get { return _fbcmdpath; }
   set { _fbcmdpath = value; }
  }
        [ReflectorProperty("DontWriteToLog", Required = false)]
  public bool DontWriteToLog;
        [ReflectorProperty("UseTemporaryLogFile", Required = false)]
        public bool UseTemporaryLogFile;
        [ReflectorProperty("Timeout", Required = false)]
  public int Timeout;
        protected override bool Execute(IIntegrationResult result)
  {
            result.BuildProgressInformation.SignalStartRunTask(!string.IsNullOrEmpty(Description) ? Description :
                            string.Format("Executing FinalBuilder : BuildFile: {0} ", ProjectFile));
            ProcessResult processResult = AttemptToExecute(NewProcessInfoFrom(result), result.ProjectName);
   result.AddTaskResult(new ProcessTaskResult(processResult));
   if (processResult.TimedOut)
   {
    throw new BuilderException(this, "Build timed out (after " + Timeout + " seconds)");
   }
            return !processResult.Failed;
  }
  protected ProcessResult AttemptToExecute(ProcessInfo info, string projectName)
  {
   try
   {
    return _executor.Execute(info);
   }
   catch (Exception e)
   {
    throw new BuilderException(this, string.Format("FBCMD unable to execute: {0}\n{1}", info, e), e);
   }
  }
  private ProcessInfo NewProcessInfoFrom(IIntegrationResult result)
  {
            ProcessInfo info = new ProcessInfo(GetFBPath(), GetFBArgs());
   info.TimeOut = Timeout*1000;
            int idx = ProjectFile.LastIndexOf('\\');
            if (idx > -1)
              info.WorkingDirectory = ProjectFile.Remove(idx, ProjectFile.Length - idx);
   foreach (string varName in result.IntegrationProperties.Keys)
   {
    object obj1 = result.IntegrationProperties[varName];
    if ((obj1 != null) && !info.EnvironmentVariables.ContainsKey(varName))
    {
      info.EnvironmentVariables.Add(varName, StringUtil.AutoDoubleQuoteString(StringUtil.RemoveTrailingPathDelimeter(StringUtil.IntegrationPropertyToString(obj1))));
    }
   }
   return info;
  }
  private string GetFBArgs()
  {
   StringBuilder args = new StringBuilder();
   if (!ShowBanner)
   {
    args.Append("/B ");
   }
            if (UseTemporaryLogFile)
            {
                args.Append("/TL ");
            }
   else if (DontWriteToLog)
   {
    args.Append("/S ");
   }
   if (FBVariables != null && FBVariables.Length > 0)
   {
    args.Append("/V");
    for(int j = 0; j < FBVariables.Length; j++)
    {
     args.Append(FBVariables[j].Name);
     args.Append("=");
     args.Append(StringUtil.AutoDoubleQuoteString(FBVariables[j].Value));
     if(j < FBVariables.Length - 1)
     {
      args.Append(";");
     }
     else
     {
      args.Append(" ");
     }
    }
   }
   args.Append("/P");
   args.Append(StringUtil.AutoDoubleQuoteString(ProjectFile));
   return args.ToString();
  }
        public int GetFBVersion()
        {
            if (_fbversion == FinalBuilderVersion.FBUnknown)
            {
                try
                {
                    return Byte.Parse(ProjectFile.Substring(ProjectFile.Length - 1, 1));
                }
                catch
                {
                    throw new BuilderException(this, "Finalbuilder version could not be autodetected from_ project file name.");
                }
            }
            return (int)_fbversion;
        }
  public string GetFBPath()
  {
            int fbversion = GetFBVersion();
   string keyName = String.Format(@"SOFTWARE\VSoft\FinalBuilder\{0}.0", fbversion);
   string executableDir = _registry.GetLocalMachineSubKeyValue(keyName, "Location");
            if (string.IsNullOrEmpty((executableDir)))
   {
    throw new BuilderException(this, String.Format("Path to Finalbuilder {0} command line executable could not be found.", fbversion));
   }
            if (fbversion == 3)
            {
                return Path.GetDirectoryName(executableDir) + @"\FB3Cmd.exe";
            }
   return Path.GetDirectoryName(executableDir) + @"\FBCmd.exe";
  }
  private enum FinalBuilderVersion
  {
   FBUnknown = -1,
   FB3 = 3,
   FB4 = 4,
   FB5 = 5
        }
        public void Validate(IConfiguration configuration, ConfigurationTrace parent, IConfigurationErrorProcesser errorProcesser)
        {
            try
            {
                this.GetFBVersion();
                this.GetFBPath();
            }
            catch (Exception error)
            {
                errorProcesser.ProcessError(error);
            }
        }
    }
}
