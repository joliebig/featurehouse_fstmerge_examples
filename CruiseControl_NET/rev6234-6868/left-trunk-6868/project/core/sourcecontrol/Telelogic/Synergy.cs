using System;
using System.IO;
using Exortech.NetReflector;
using ThoughtWorks.CruiseControl.Core.Util;
namespace ThoughtWorks.CruiseControl.Core.Sourcecontrol.Telelogic
{
 [ReflectorType("synergy")]
 public class Synergy
        : SourceControlBase, IDisposable
 {
  private ISynergyCommand command;
  private SynergyConnectionInfo connection;
  private SynergyProjectInfo project;
  private IModificationUrlBuilder urlBuilder;
  private SynergyParser parser;
  public Synergy() : this(new SynergyConnectionInfo(), new SynergyProjectInfo())
  {}
  public Synergy(SynergyConnectionInfo connection, SynergyProjectInfo project) : this(connection, project, new SynergyCommand(connection, project), new SynergyParser())
  {}
  public Synergy(SynergyConnectionInfo connection, SynergyProjectInfo project, ISynergyCommand command, SynergyParser parser)
  {
   this.connection = connection;
   this.project = project;
   this.command = command;
   this.parser = parser;
  }
  ~Synergy()
  {
   Dispose();
  }
  [ReflectorProperty("connection", InstanceType=typeof (SynergyConnectionInfo))]
  public SynergyConnectionInfo Connection
  {
   get { return connection; }
   set
   {
    connection = value;
    OpenNewCommand();
   }
  }
  private void OpenNewCommand()
  {
   command.Dispose();
   command = new SynergyCommand(connection, project);
  }
        [ReflectorProperty("project", InstanceType = typeof(SynergyProjectInfo))]
  public SynergyProjectInfo Project
  {
   get { return project; }
   set
   {
    project = value;
    OpenNewCommand();
   }
  }
        [ReflectorProperty("changeSynergy", InstanceType = typeof(ChangeSynergyUrlBuilder), Required = false)]
  public IModificationUrlBuilder UrlBuilder
  {
   get { return urlBuilder; }
   set
   {
    urlBuilder = value;
    ChangeSynergyUrlBuilder temp = urlBuilder as ChangeSynergyUrlBuilder;
    if (null != temp)
    {
     temp.SetCredentials(connection);
    }
   }
  }
  public void Dispose()
  {
   command.Dispose();
  }
        public override void Initialize(IProject project)
  {}
        public override void GetSource(IIntegrationResult integration)
  {
            integration.BuildProgressInformation.SignalStartRunTask("Getting source from_ Telelogic Synergy");
   Reconcile();
   ProcessInfo info = SynergyCommandBuilder.Reconfigure(connection, project);
   command.Execute(info);
   project.LastReconfigureTime = GetReconfigureTime();
  }
        public override void Purge(IProject project)
  {}
        public override Modification[] GetModifications(IIntegrationResult from_, IIntegrationResult to)
  {
   return GetModifications(from_.LastModificationDate);
  }
  private Modification[] GetModifications(DateTime from_)
  {
   Modification[] modifications = new Modification[0];
   if (project.TemplateEnabled)
   {
    command.Execute(SynergyCommandBuilder.UseReconfigureTemplate(connection, project));
   }
   command.Execute(SynergyCommandBuilder.UpdateReconfigureProperites(connection, project));
   ProcessResult result = command.Execute(SynergyCommandBuilder.GetNewTasks(connection, project, from_), false);
   if (! result.Failed)
   {
    string comments = result.StandardOutput;
    result = command.Execute(SynergyCommandBuilder.GetTaskObjects(connection, project), false);
    if (! result.Failed)
    {
     result = command.Execute(SynergyCommandBuilder.GetObjectPaths(connection, project), false);
     if (! result.Failed)
     {
      modifications = parser.Parse(comments, result.StandardOutput, from_);
      if (null != urlBuilder)
      {
       urlBuilder.SetupModification(modifications);
      }
     }
    }
   }
            FillIssueUrl(modifications);
   return modifications;
  }
        public override void LabelSourceControl(IIntegrationResult result)
  {
   DateTime currentReconfigureTime = GetReconfigureTime();
   if (currentReconfigureTime != project.LastReconfigureTime)
   {
    string message = String.Format(@"Invalid project state.  Cannot add tasks to shared folder '{0}' because " + @"the integration project '{1}' was internally reconfigured at '{2}' " + @"and externally reconfigured at '{3}'.  Projects cannot be reconfigured " + @"during an integration run.", project.TaskFolder, project.ProjectSpecification, project.LastReconfigureTime, currentReconfigureTime);
    throw(new CruiseControlException(message));
   }
   result.Modifications = GetModifications(DateTime.MinValue);
   if (null != result.Modifications && result.Modifications.Length > 0)
   {
    command.Execute(SynergyCommandBuilder.AddLabelToTaskComment(connection, project, result));
    if (SynergyProjectInfo.DefaultTaskFolder != project.TaskFolder)
    {
     command.Execute(SynergyCommandBuilder.AddTasksToFolder(connection, project, result));
    }
   }
   if (project.BaseliningEnabled)
   {
    command.Execute(SynergyCommandBuilder.CreateBaseline(connection, project, result));
   }
  }
  private void Reconcile()
  {
   command.Execute(SynergyCommandBuilder.Heartbeat(connection));
   if (null != project.ReconcilePaths)
   {
    string fullPath;
    foreach (string path in project.ReconcilePaths)
    {
     if (! Path.IsPathRooted(path))
      fullPath = Path.Combine(project.WorkAreaPath, path);
     else
      fullPath = path;
     fullPath = Path.GetFullPath(fullPath);
     Log.Info(String.Concat("Reconciling work area path '", path, "'"));
     command.Execute(SynergyCommandBuilder.Reconcile(connection, project, path));
    }
   }
  }
  public DateTime GetReconfigureTime()
  {
   ProcessResult result = command.Execute(SynergyCommandBuilder.GetLastReconfigureTime(connection, project));
   try
   {
    return DateTime.Parse(result.StandardOutput.Trim(), connection.FormatProvider);
   }
   catch (Exception inner)
   {
    throw(new CruiseControlException("Failed to read the project's last reconfigure time.", inner));
   }
  }
        [ReflectorProperty("issueUrlBuilder", InstanceTypeKey = "type", Required = false)]
        public IModificationUrlBuilder IssueUrlBuilder;
        private void FillIssueUrl(Modification[] modifications)
        {
            if (IssueUrlBuilder != null)
            {
                IssueUrlBuilder.SetupModification(modifications);
            }
        }
 }
}
