using System;
using System.Runtime.Remoting;
using System.Threading;
using NAnt.Core;
using NAnt.Core.Attributes;
using ThoughtWorks.CruiseControl.Remote;
namespace LoGeek.BuildServer.CustomTasks
{
 [TaskName("launchccnetbuild")]
 public class LaunchCruiseControlBuildTask : Task
 {
  private string _serverUrl;
  private string _projectName;
  private int _timeOutInSeconds = 30*60;
  private int _pollingIntervalInSeconds = 5;
  private ICruiseManager _cruiseManager;
  [TaskAttribute("serverurl", Required=true)]
  public string ServerUrl
  {
   get { return _serverUrl; }
   set { _serverUrl = value; }
  }
  [TaskAttribute("projectname", Required=true)]
  public string ProjectName
  {
   get { return _projectName; }
   set { _projectName = value; }
  }
  [TaskAttribute("timeoutinseconds", Required=false)]
  public int TimeOut
  {
   get { return _timeOutInSeconds; }
   set { _timeOutInSeconds = value; }
  }
  [TaskAttribute("pollinginterval", Required=false)]
  public int PollingInterval
  {
   get { return _pollingIntervalInSeconds; }
   set { _pollingIntervalInSeconds = value; }
  }
  protected override void ExecuteTask()
  {
   Log(Level.Info, "Connecting to CCNet server " + ServerUrl);
   _cruiseManager = (ICruiseManager) RemotingServices.Connect(typeof (ICruiseManager), ServerUrl);
   IntegrationStatus status = LaunchBuild(_cruiseManager, ProjectName, PollingInterval, TimeOut);
   if (status != IntegrationStatus.Success)
    throw new BuildException(string.Format("Project '{0}' failed : {1}", ProjectName, status));
  }
  private ProjectStatus GetCurrentProjectStatus(ICruiseManager cruiseManager, string name)
  {
   ProjectStatus[] allStatus = cruiseManager.GetProjectStatus();
   foreach (ProjectStatus status in allStatus)
   {
    if (status.Name == name)
     return status;
   }
   return null;
  }
  public IntegrationStatus LaunchBuild(ICruiseManager cruiseManager, string projectName, int pollingIntervalInSeconds, int timeOutInSeconds)
  {
   ProjectStatus status = GetCurrentProjectStatus(cruiseManager, projectName);
   if (status == null)
    throw new BuildException(string.Format("Project '{0}' not found on the build server.", projectName));
   if (status.Activity != ProjectActivity.Sleeping)
    throw new BuildException(string.Format("Project '{0}' activity is '{1}' instead of expected '{2}'", projectName, status.Activity, ProjectActivity.Sleeping));
   Log(Level.Info, "Forcing build for project '{0}'", projectName);
   cruiseManager.ForceBuild(projectName);
   DateTime startTime = DateTime.Now;
   TimeSpan timeout = new TimeSpan(0, 0, timeOutInSeconds);
   while (true)
   {
    TimeSpan elapsed = DateTime.Now - startTime;
    if (elapsed >= timeout)
     throw new BuildException(string.Format("Project '{0}' build timed-out (lasted more than {1} seconds)", projectName, timeOutInSeconds));
    Thread.Sleep(pollingIntervalInSeconds*1000);
    status = GetCurrentProjectStatus(cruiseManager, projectName);
    switch (status.Activity)
    {
     case ProjectActivity.Building:
      break;
     case ProjectActivity.CheckingModifications:
      break;
     case ProjectActivity.Sleeping:
      return status.BuildStatus;
     default:
      throw new Exception(string.Format("Unknown ProjectActivity '{0}'",status.Activity));
    }
   }
  }
 }
}
