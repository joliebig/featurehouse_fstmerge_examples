using System.ComponentModel;
using ThoughtWorks.CruiseControl.CCTrayLib.Monitoring;
using ThoughtWorks.CruiseControl.Remote;
using System.Windows.Forms;
using System.Collections.Generic;
using ThoughtWorks.CruiseControl.Remote.Parameters;
using System.IO;
namespace ThoughtWorks.CruiseControl.CCTrayLib.Presentation
{
 public class SynchronizedProjectMonitor : IProjectMonitor
 {
  private readonly IProjectMonitor projectMonitor;
  private readonly ISynchronizeInvoke synchronizeInvoke;
  public SynchronizedProjectMonitor(IProjectMonitor projectMonitor, ISynchronizeInvoke synchronizeInvoke)
  {
   this.projectMonitor = projectMonitor;
   this.synchronizeInvoke = synchronizeInvoke;
   projectMonitor.Polled += new MonitorPolledEventHandler(ProjectMonitor_Polled);
   projectMonitor.BuildOccurred += new MonitorBuildOccurredEventHandler(ProjectMonitor_BuildOccurred);
   projectMonitor.MessageReceived += new MessageEventHandler(ProjectMonitor_MessageReceived);
  }
  public ProjectState ProjectState
  {
   get { return projectMonitor.ProjectState; }
  }
  public ISingleProjectDetail Detail
  {
   get { return projectMonitor.Detail; }
  }
  public string SummaryStatusString
  {
   get { return projectMonitor.SummaryStatusString; }
  }
  public string ProjectIntegratorState
  {
   get { return projectMonitor.ProjectIntegratorState; }
  }
        public void ForceBuild(Dictionary<string, string> parameters)
  {
   projectMonitor.ForceBuild(parameters);
  }
  public void AbortBuild()
  {
   projectMonitor.AbortBuild();
  }
  public void FixBuild(string fixingUserName)
  {
   projectMonitor.FixBuild(fixingUserName);
  }
  public void StopProject()
  {
   projectMonitor.StopProject();
  }
  public void StartProject()
  {
   projectMonitor.StartProject();
  }
  public void CancelPending()
  {
   projectMonitor.CancelPending();
  }
  public void Poll()
  {
   projectMonitor.Poll();
  }
  public void OnPollStarting()
  {
   projectMonitor.OnPollStarting();
  }
  public event MonitorBuildOccurredEventHandler BuildOccurred;
  public event MonitorPolledEventHandler Polled;
  public event MessageEventHandler MessageReceived;
  private void ProjectMonitor_Polled(object sender, MonitorPolledEventArgs args)
  {
            if (Polled != null)
            {
                var canInvoke = true;
                if (synchronizeInvoke is Control) canInvoke = !(synchronizeInvoke as Control).IsDisposed;
                if (canInvoke) synchronizeInvoke.BeginInvoke(Polled, new object[] { sender, args });
            }
  }
  private void ProjectMonitor_BuildOccurred(object sender, MonitorBuildOccurredEventArgs args)
  {
            if (BuildOccurred != null)
            {
                var canInvoke = true;
                if (synchronizeInvoke is Control) canInvoke = !(synchronizeInvoke as Control).IsDisposed;
                if (canInvoke) synchronizeInvoke.BeginInvoke(BuildOccurred, new object[] { sender, args });
            }
  }
  private void ProjectMonitor_MessageReceived(string projectName, ThoughtWorks.CruiseControl.Remote.Message message)
  {
            if (MessageReceived != null)
            {
                var canInvoke = true;
                if (synchronizeInvoke is Control) canInvoke = !(synchronizeInvoke as Control).IsDisposed;
                string caption = string.Concat("Project Name : ", projectName);
                if (canInvoke) synchronizeInvoke.BeginInvoke(MessageReceived, new object[] { caption, message });
            }
  }
  public IntegrationStatus IntegrationStatus
  {
   get { return projectMonitor.IntegrationStatus; }
  }
  public bool IsPending
  {
   get { return projectMonitor.IsPending; }
  }
  public bool IsConnected
  {
   get { return projectMonitor.IsConnected; }
  }
        public virtual ProjectStatusSnapshot RetrieveSnapshot()
        {
            return projectMonitor.RetrieveSnapshot();
        }
        public virtual PackageDetails[] RetrievePackageList()
        {
            return projectMonitor.RetrievePackageList();
        }
        public void TransferFile(string fileName, Stream outputStream)
        {
            projectMonitor.TransferFile(fileName, outputStream);
        }
        public List<ParameterBase> ListBuildParameters()
        {
            return projectMonitor.ListBuildParameters();
        }
    }
}
