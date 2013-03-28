using System.Collections;
using ThoughtWorks.CruiseControl.Core.Config;
using ThoughtWorks.CruiseControl.Core.Queues;
using ThoughtWorks.CruiseControl.Core.Util;
using ThoughtWorks.CruiseControl.Remote;
namespace ThoughtWorks.CruiseControl.Core
{
 public class IntegrationQueueManager
 {
  private readonly IProjectIntegratorListFactory projectIntegratorListFactory;
  private IProjectIntegratorList projectIntegrators;
  private readonly IntegrationQueueSet integrationQueues = new IntegrationQueueSet();
  public IntegrationQueueManager(IProjectIntegratorListFactory projectIntegratorListFactory,
                                 IConfiguration configuration)
  {
   this.projectIntegratorListFactory = projectIntegratorListFactory;
   Initialize(configuration);
  }
        public CruiseServerSnapshot GetCruiseServerSnapshot()
  {
      ProjectStatus[] projectStatuses = GetProjectStatuses();
      QueueSetSnapshot queueSetSnapshot = integrationQueues.GetIntegrationQueueSnapshot();
            return new CruiseServerSnapshot(projectStatuses, queueSetSnapshot);
  }
  public void StartAllProjects()
  {
   foreach (IProjectIntegrator integrator in projectIntegrators)
   {
                bool canStart = (integrator.Project == null) ||
                    (integrator.Project.StartupState == ProjectInitialState.Started);
                if (canStart) integrator.Start();
   }
  }
  public void StopAllProjects()
  {
   foreach (IProjectIntegrator integrator in projectIntegrators)
   {
    integrator.Stop();
   }
   WaitForIntegratorsToExit();
   integrationQueues.Clear();
  }
  public void Abort()
  {
   foreach (IProjectIntegrator integrator in projectIntegrators)
   {
    integrator.Abort();
   }
   WaitForIntegratorsToExit();
   integrationQueues.Clear();
  }
  public ProjectStatus[] GetProjectStatuses()
  {
   ArrayList projectStatusList = new ArrayList();
   foreach (IProjectIntegrator integrator in projectIntegrators)
   {
    IProject project = integrator.Project;
    projectStatusList.Add(project.CreateProjectStatus(integrator));
   }
   return (ProjectStatus[]) projectStatusList.ToArray(typeof (ProjectStatus));
  }
  public IProjectIntegrator GetIntegrator(string projectName)
  {
   IProjectIntegrator integrator = projectIntegrators[projectName];
   if (integrator == null) throw new NoSuchProjectException(projectName);
   return integrator;
  }
  public void ForceBuild(string projectName, string enforcerName)
  {
            GetIntegrator(projectName).ForceBuild(enforcerName);
  }
  public void WaitForExit(string projectName)
  {
   GetIntegrator(projectName).WaitForExit();
  }
  public void Request(string project, IntegrationRequest request)
  {
   GetIntegrator(project).Request(request);
  }
  public void CancelPendingRequest(string projectName)
  {
   GetIntegrator(projectName).CancelPendingRequest();
  }
  public void Stop(string project)
  {
   GetIntegrator(project).Stop();
  }
  public void Start(string project)
  {
   GetIntegrator(project).Start();
  }
  public void Restart(IConfiguration configuration)
  {
   StopAllProjects();
   Initialize(configuration);
   StartAllProjects();
  }
  private void WaitForIntegratorsToExit()
  {
   foreach (IProjectIntegrator integrator in projectIntegrators)
   {
    integrator.WaitForExit();
   }
  }
  private void Initialize(IConfiguration configuration)
  {
   foreach (IProject project in configuration.Projects)
   {
                IQueueConfiguration config = configuration.FindQueueConfiguration(project.QueueName);
    integrationQueues.Add(project.QueueName, config);
   }
   projectIntegrators = projectIntegratorListFactory.CreateProjectIntegrators(configuration.Projects, integrationQueues);
   if (projectIntegrators.Count == 0)
   {
    Log.Info("No projects found");
   }
  }
  public string[] GetQueueNames()
  {
   return integrationQueues.GetQueueNames();
  }
 }
}
