using System;
using System.Reflection;
using System.Threading;
using ThoughtWorks.CruiseControl.Core.Config;
using ThoughtWorks.CruiseControl.Core.Logging;
using ThoughtWorks.CruiseControl.Core.State;
using ThoughtWorks.CruiseControl.Core.Util;
using ThoughtWorks.CruiseControl.Remote;
namespace ThoughtWorks.CruiseControl.Core
{
 public class CruiseServer : ICruiseServer
 {
  private readonly IProjectSerializer projectSerializer;
  private readonly IConfigurationService configurationService;
  private readonly ICruiseManager manager;
  private readonly ManualResetEvent monitor = new ManualResetEvent(true);
  private bool disposed;
  private IntegrationQueueManager integrationQueueManager;
  public CruiseServer(IConfigurationService configurationService,
                      IProjectIntegratorListFactory projectIntegratorListFactory,
                            IProjectSerializer projectSerializer,
                            IProjectStateManager stateManager)
  {
   this.configurationService = configurationService;
   this.configurationService.AddConfigurationUpdateHandler(new ConfigurationUpdateHandler(Restart));
   this.projectSerializer = projectSerializer;
   manager = new CruiseManager(this);
   InitializeServerThread();
   IConfiguration configuration = configurationService.Load();
   integrationQueueManager = new IntegrationQueueManager(projectIntegratorListFactory, configuration, stateManager);
  }
  public void Start()
  {
   Log.Info("Starting CruiseControl.NET Server");
   monitor.Reset();
   integrationQueueManager.StartAllProjects();
  }
  public void Start(string project)
  {
   integrationQueueManager.Start(project);
  }
  public void Stop()
  {
   Log.Info("Stopping CruiseControl.NET Server");
   integrationQueueManager.StopAllProjects();
   monitor.Set();
  }
  public void Stop(string project)
  {
   integrationQueueManager.Stop(project);
  }
  public void Abort()
  {
   Log.Info("Aborting CruiseControl.NET Server");
   integrationQueueManager.Abort();
   monitor.Set();
  }
  public void Restart()
  {
   Log.Info("Configuration changed: Restarting CruiseControl.NET Server ");
   IConfiguration configuration = configurationService.Load();
   integrationQueueManager.Restart(configuration);
  }
  public void WaitForExit()
  {
   monitor.WaitOne();
  }
  public void CancelPendingRequest(string projectName)
  {
   integrationQueueManager.CancelPendingRequest(projectName);
  }
        public CruiseServerSnapshot GetCruiseServerSnapshot()
  {
   return integrationQueueManager.GetCruiseServerSnapshot();
  }
  public ICruiseManager CruiseManager
  {
   get { return manager; }
  }
  public ProjectStatus[] GetProjectStatus()
  {
   return integrationQueueManager.GetProjectStatuses();
  }
  public void ForceBuild(string projectName, string enforcerName)
  {
   integrationQueueManager.ForceBuild(projectName, enforcerName);
  }
  public void AbortBuild(string projectName, string enforcerName)
  {
   GetIntegrator(projectName).AbortBuild(enforcerName);
  }
  public void WaitForExit(string projectName)
  {
   integrationQueueManager.WaitForExit(projectName);
  }
  public void Request(string project, IntegrationRequest request)
  {
   integrationQueueManager.Request(project, request);
  }
  public string GetLatestBuildName(string projectName)
  {
   return GetIntegrator(projectName).IntegrationRepository.GetLatestBuildName();
  }
  public string[] GetMostRecentBuildNames(string projectName, int buildCount)
  {
   return GetIntegrator(projectName).IntegrationRepository.GetMostRecentBuildNames(buildCount);
  }
  public string[] GetBuildNames(string projectName)
  {
   return GetIntegrator(projectName).IntegrationRepository.GetBuildNames();
  }
  public string GetLog(string projectName, string buildName)
  {
   return GetIntegrator(projectName).IntegrationRepository.GetBuildLog(buildName);
  }
  public string GetServerLog()
  {
   return new ServerLogFileReader().Read();
  }
  public string GetServerLog(string projectName)
  {
   return new ServerLogFileReader().Read(projectName);
  }
  public void AddProject(string serializedProject)
  {
   Log.Info("Adding project - " + serializedProject);
   try
   {
    IConfiguration configuration = configurationService.Load();
    IProject project = projectSerializer.Deserialize(serializedProject);
    configuration.AddProject(project);
    project.Initialize();
    configurationService.Save(configuration);
   }
   catch (ApplicationException e)
   {
    Log.Warning(e);
    throw new CruiseControlException("Failed to add project. Exception was - " + e.Message);
   }
  }
  public void DeleteProject(string projectName, bool purgeWorkingDirectory, bool purgeArtifactDirectory,
                            bool purgeSourceControlEnvironment)
  {
   Log.Info("Deleting project - " + projectName);
   try
   {
    IConfiguration configuration = configurationService.Load();
    configuration.Projects[projectName].Purge(purgeWorkingDirectory, purgeArtifactDirectory,
                                              purgeSourceControlEnvironment);
    configuration.DeleteProject(projectName);
    configurationService.Save(configuration);
   }
   catch (Exception e)
   {
    Log.Warning(e);
    throw new CruiseControlException("Failed to add project. Exception was - " + e.Message);
   }
  }
  public string GetProject(string name)
  {
   Log.Info("Getting project - " + name);
   return new NetReflectorProjectSerializer().Serialize(configurationService.Load().Projects[name]);
  }
  public string GetVersion()
  {
   Log.Info("Returning version number");
   try
   {
    return Assembly.GetExecutingAssembly().GetName().Version.ToString();
   }
   catch (ApplicationException e)
   {
    Log.Warning(e);
    throw new CruiseControlException("Failed to get project version . Exception was - " + e.Message);
   }
  }
  public void UpdateProject(string projectName, string serializedProject)
  {
   Log.Info("Updating project - " + projectName);
   try
   {
    IConfiguration configuration = configurationService.Load();
    configuration.Projects[projectName].Purge(true, false, true);
    configuration.DeleteProject(projectName);
    IProject project = projectSerializer.Deserialize(serializedProject);
    configuration.AddProject(project);
    project.Initialize();
    configurationService.Save(configuration);
   }
   catch (ApplicationException e)
   {
    Log.Warning(e);
    throw new CruiseControlException("Failed to add project. Exception was - " + e.Message);
   }
  }
  public ExternalLink[] GetExternalLinks(string projectName)
  {
   return LookupProject(projectName).ExternalLinks;
  }
  private IProject LookupProject(string projectName)
  {
   return GetIntegrator(projectName).Project;
  }
  public void SendMessage(string projectName, Message message)
  {
   Log.Info("New message received: " + message);
   LookupProject(projectName).AddMessage(message);
  }
  public string GetArtifactDirectory(string projectName)
  {
   return LookupProject(projectName).ArtifactDirectory;
  }
  public string GetStatisticsDocument(string projectName)
  {
   return GetIntegrator(projectName).Project.Statistics;
  }
        public string GetModificationHistoryDocument(string projectName)
        {
            return GetIntegrator(projectName).Project.ModificationHistory;
        }
        public string GetRSSFeed(string projectName)
        {
            return GetIntegrator(projectName).Project.RSSFeed;
        }
  private IProjectIntegrator GetIntegrator(string projectName)
  {
   return integrationQueueManager.GetIntegrator(projectName);
  }
  void IDisposable.Dispose()
  {
   lock (this)
   {
    if (disposed) return;
    disposed = true;
   }
   Abort();
  }
  private static void InitializeServerThread()
  {
   try
   {
    Thread.CurrentThread.Name = "CCNet Server";
   }
   catch (InvalidOperationException)
   {
   }
  }
 }
}
