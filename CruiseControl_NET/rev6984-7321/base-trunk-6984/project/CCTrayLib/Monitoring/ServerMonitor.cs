using System;
using System.Diagnostics;
using ThoughtWorks.CruiseControl.CCTrayLib.Configuration;
using ThoughtWorks.CruiseControl.Remote;
using System.Collections.Generic;
namespace ThoughtWorks.CruiseControl.CCTrayLib.Monitoring
{
    public class ServerMonitor : ISingleServerMonitor
 {
  public event MonitorServerPolledEventHandler Polled;
  public event MonitorServerQueueChangedEventHandler QueueChanged;
        private CruiseServerSnapshot lastCruiseServerSnapshot;
  private readonly ICruiseServerManager cruiseServerManager;
  private Exception connectException;
        private List<string> currentProjects = new List<string>();
        private bool areCurrentProjectsLoaded = false;
  public ServerMonitor(ICruiseServerManager cruiseServerManager)
  {
   this.cruiseServerManager = cruiseServerManager;
  }
  public void CancelPendingRequest(string projectName)
  {
   cruiseServerManager.CancelPendingRequest(projectName);
  }
        public CruiseServerSnapshot CruiseServerSnapshot
  {
            get { return lastCruiseServerSnapshot; }
  }
        public string SessionToken
        {
            get { return cruiseServerManager.SessionToken; }
        }
        public ProjectStatus GetProjectStatus(string projectName)
        {
            if (lastCruiseServerSnapshot == null || lastCruiseServerSnapshot.ProjectStatuses == null)
            {
                return null;
            }
            foreach (ProjectStatus status in lastCruiseServerSnapshot.ProjectStatuses)
            {
                if (status.Name == projectName)
                    return status;
            }
            throw new ApplicationException("Project '" + projectName + "' not found on server");
        }
  public void Poll()
  {
   try
   {
       CruiseServerSnapshot cruiseServerSnapshot = cruiseServerManager.GetCruiseServerSnapshot();
                if ((lastCruiseServerSnapshot == null)
                    || (cruiseServerSnapshot == null)
                    || lastCruiseServerSnapshot.IsQueueSetSnapshotChanged(cruiseServerSnapshot.QueueSetSnapshot))
                {
                    OnQueueChanged(new MonitorServerQueueChangedEventArgs(this));
                }
                lastCruiseServerSnapshot = cruiseServerSnapshot;
                DetectAnyChanges(cruiseServerSnapshot);
   }
   catch (Exception ex)
   {
    Trace.WriteLine("ServerMonitorPoll Exception: " + ex);
                lastCruiseServerSnapshot = null;
    connectException = ex;
                OnQueueChanged(new MonitorServerQueueChangedEventArgs(this));
            }
   OnPolled(new MonitorServerPolledEventArgs(this));
  }
  public void OnPollStarting()
  {
   if (cruiseServerManager is ICache)
    ((ICache)cruiseServerManager).InvalidateCache();
  }
  public string ServerUrl
  {
   get { return cruiseServerManager.Configuration.Url; }
  }
  public string DisplayName
  {
   get { return cruiseServerManager.DisplayName; }
  }
  public BuildServerTransport Transport
  {
   get { return cruiseServerManager.Configuration.Transport; }
  }
  public bool IsConnected
  {
   get { return lastCruiseServerSnapshot != null; }
  }
  public Exception ConnectException
  {
   get { return connectException; }
  }
  protected void OnPolled(MonitorServerPolledEventArgs args)
  {
   if (Polled != null) Polled(this, args);
  }
  protected void OnQueueChanged(MonitorServerQueueChangedEventArgs args)
  {
   if (QueueChanged != null) QueueChanged(this, args);
  }
        public void Start()
        {
            cruiseServerManager.Login();
        }
        public void Stop()
        {
            cruiseServerManager.Logout();
        }
        public bool RefreshSession()
        {
            cruiseServerManager.Logout();
            return cruiseServerManager.Login();
        }
        public event ServerSnapshotChangedEventHandler ServerSnapshotChanged;
        private void DetectAnyChanges(CruiseServerSnapshot cruiseServerSnapshot)
        {
            if ((cruiseServerSnapshot != null) && (ServerSnapshotChanged != null))
            {
                var newProjects = new List<string>();
                var oldProjects = new List<string>(currentProjects);
                foreach (var project in cruiseServerSnapshot.ProjectStatuses)
                {
                    if (!oldProjects.Contains(project.Name))
                    {
                        newProjects.Add(project.Name);
                    }
                    else
                    {
                        oldProjects.Remove(project.Name);
                    }
                }
                if (areCurrentProjectsLoaded &&
                    ((newProjects.Count > 0) || (oldProjects.Count > 0)))
                {
                    var args = new ServerSnapshotChangedEventArgs(cruiseServerManager.DisplayName,
                        cruiseServerManager.Configuration,
                        newProjects,
                        oldProjects);
                    ServerSnapshotChanged(this, args);
                }
                if ((newProjects.Count > 0) || (oldProjects.Count > 0))
                {
                    foreach (var oldProject in oldProjects)
                    {
                        currentProjects.Remove(oldProject);
                    }
                    currentProjects.AddRange(newProjects);
                }
                areCurrentProjectsLoaded = true;
            }
        }
    }
}
