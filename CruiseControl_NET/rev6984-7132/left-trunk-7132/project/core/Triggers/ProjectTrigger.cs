using System;
using System.Net.Sockets;
using Exortech.NetReflector;
using ThoughtWorks.CruiseControl.Core.Util;
using ThoughtWorks.CruiseControl.Remote;
namespace ThoughtWorks.CruiseControl.Core.Triggers
{
    [ReflectorType("projectTrigger")]
 public class ProjectTrigger : ITrigger
 {
  public const string DefaultServerUri = RemoteCruiseServer.DefaultManagerUri;
  private const int DefaultIntervalSeconds = 5;
  private readonly ICruiseManagerFactory managerFactory;
  private ProjectStatus lastStatus;
  private ProjectStatus currentStatus;
  public ProjectTrigger() : this(new RemoteCruiseManagerFactory())
  {}
  public ProjectTrigger(ICruiseManagerFactory managerFactory)
  {
   this.managerFactory = managerFactory;
            this.ServerUri = DefaultServerUri;
            this.TriggerStatus = IntegrationStatus.Success;
            this.InnerTrigger = ProjectTrigger.NewIntervalTrigger();
            this.TriggerFirstTime = false;
  }
        [ReflectorProperty("project")]
        public string Project { get; set; }
        [ReflectorProperty("serverUri", Required = false)]
        public string ServerUri { get; set; }
        [ReflectorProperty("triggerStatus", Required = false)]
        public IntegrationStatus TriggerStatus { get; set; }
        [ReflectorProperty("innerTrigger", InstanceTypeKey = "type", Required = false)]
        public ITrigger InnerTrigger { get; set; }
        [ReflectorProperty("triggerFirstTime", Required = false)]
        public bool TriggerFirstTime { get; set; }
  public void IntegrationCompleted()
  {
   lastStatus = currentStatus;
   InnerTrigger.IntegrationCompleted();
  }
  private ProjectStatus GetCurrentProjectStatus()
  {
   Log.Debug("Retrieving ProjectStatus from_ server: " + ServerUri);
   ProjectStatus[] currentStatuses = managerFactory.GetCruiseManager(ServerUri).GetProjectStatus();
   foreach (ProjectStatus projectStatus in currentStatuses)
   {
    if (projectStatus.Name == Project)
    {
                    Log.Debug("Found status for dependent project {0} is {1}",projectStatus.Name,projectStatus.BuildStatus);
     return projectStatus;
    }
   }
   throw new NoSuchProjectException(Project);
  }
  public DateTime NextBuild
  {
   get
   {
                return InnerTrigger.NextBuild;
   }
  }
  public IntegrationRequest Fire()
  {
   IntegrationRequest request = InnerTrigger.Fire();
   if (request == null) return null;
   InnerTrigger.IntegrationCompleted();
            try
            {
                currentStatus = GetCurrentProjectStatus();
                if (lastStatus == null)
                {
                    lastStatus = currentStatus;
                    if (TriggerFirstTime && currentStatus.BuildStatus == TriggerStatus)
                    {
                        return request;
                    }
                    return null;
                }
                if (currentStatus.LastBuildDate > lastStatus.LastBuildDate && currentStatus.BuildStatus == TriggerStatus)
                {
                    return request;
                }
            }
            catch (SocketException)
            {
                Log.Warning("Skipping Fire() because ServerUri " + ServerUri + " was not found.");
            }
            return null;
  }
  private static ITrigger NewIntervalTrigger()
  {
   IntervalTrigger trigger = new IntervalTrigger();
   trigger.IntervalSeconds = DefaultIntervalSeconds;
   trigger.BuildCondition = BuildCondition.ForceBuild;
   return trigger;
  }
 }
}
