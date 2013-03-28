using System;
using System.Collections.Generic;
using System.Configuration;
using System.IO;
using System.Reflection;
using System.Threading;
using ThoughtWorks.CruiseControl.Core.Config;
using ThoughtWorks.CruiseControl.Core.Logging;
using ThoughtWorks.CruiseControl.Core.Queues;
using ThoughtWorks.CruiseControl.Core.Security;
using ThoughtWorks.CruiseControl.Core.State;
using ThoughtWorks.CruiseControl.Core.Util;
using ThoughtWorks.CruiseControl.Remote;
using ThoughtWorks.CruiseControl.Remote.Events;
using ThoughtWorks.CruiseControl.Remote.Security;
using ThoughtWorks.CruiseControl.Remote.Parameters;
namespace ThoughtWorks.CruiseControl.Core
{
 public class CruiseServer
        : CruiseServerEventsBase, ICruiseServer
 {
  private readonly IProjectSerializer projectSerializer;
  private readonly IConfigurationService configurationService;
        private IConfiguration configuration;
  private readonly ICruiseManager manager;
  private readonly ManualResetEvent monitor = new ManualResetEvent(true);
        private ISecurityManager securityManager;
        private readonly List<ICruiseServerExtension> extensions = new List<ICruiseServerExtension>();
  private bool disposed;
  private IQueueManager integrationQueueManager;
  public CruiseServer(IConfigurationService configurationService,
                      IProjectIntegratorListFactory projectIntegratorListFactory,
                            IProjectSerializer projectSerializer,
                            IProjectStateManager stateManager,
                            List<ExtensionConfiguration> extensionList)
  {
   this.configurationService = configurationService;
   this.projectSerializer = projectSerializer;
   manager = new CruiseManager(this);
   InitializeServerThread();
   configuration = configurationService.Load();
   integrationQueueManager = IntegrationQueueManagerFactory.CreateManager(projectIntegratorListFactory, configuration, stateManager);
            integrationQueueManager.AssociateIntegrationEvents(OnIntegrationStarted, OnIntegrationCompleted);
            securityManager = configuration.SecurityManager;
            if (extensionList != null)
            {
                InitialiseExtensions(extensionList);
            }
            this.configurationService.AddConfigurationUpdateHandler(new ConfigurationUpdateHandler(Restart));
        }
        private void OnIntegrationStarted(object sender, IntegrationStartedEventArgs args)
        {
            FireIntegrationStarted(args.Request, args.ProjectName);
        }
        private void OnIntegrationCompleted(object sender, IntegrationCompletedEventArgs args)
        {
            FireIntegrationCompleted(args.Request, args.ProjectName, args.Status);
        }
        public void Start()
  {
   Log.Info("Starting CruiseControl.NET Server");
   monitor.Reset();
   integrationQueueManager.StartAllProjects();
            Log.Info("Initialising security");
            securityManager.Initialise();
            Log.Info("Starting extensions");
            foreach (ICruiseServerExtension extension in extensions)
            {
                extension.Start();
            }
        }
  public void Start(string sessionToken, string project)
  {
            if (!FireProjectStarting(project))
            {
             CheckSecurity(sessionToken, project, SecurityPermission.StartProject, SecurityEvent.StartProject);
                integrationQueueManager.Start(project);
                FireProjectStarted(project);
            }
  }
  public void Stop()
  {
            Log.Info("Stopping extensions");
            foreach (ICruiseServerExtension extension in extensions)
            {
                extension.Stop();
            }
            Log.Info("Stopping CruiseControl.NET Server");
   integrationQueueManager.StopAllProjects();
   monitor.Set();
  }
  public void Stop(string sessionToken, string project)
  {
            if (!FireProjectStopping(project))
            {
             CheckSecurity(sessionToken, project, SecurityPermission.StartProject, SecurityEvent.StopProject);
                integrationQueueManager.Stop(project);
                FireProjectStopped(project);
            }
  }
  public void Abort()
  {
            Log.Info("Aborting extensions");
            foreach (ICruiseServerExtension extension in extensions)
            {
                extension.Abort();
            }
            Log.Info("Aborting CruiseControl.NET Server");
   integrationQueueManager.Abort();
   monitor.Set();
  }
  public void Restart()
  {
   Log.Info("Configuration changed: Restarting CruiseControl.NET Server ");
   configuration = configurationService.Load();
   integrationQueueManager.Restart(configuration);
            securityManager = configuration.SecurityManager;
            securityManager.Initialise();
  }
  public void WaitForExit()
  {
   monitor.WaitOne();
  }
        public void CancelPendingRequest(string sessionToken, string projectName)
  {
            CheckSecurity(sessionToken, projectName, SecurityPermission.ForceBuild, SecurityEvent.CancelRequest);
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
        public void ForceBuild(string sessionToken, string projectName, string enforcerName, Dictionary<string, string> buildValues)
  {
            if (!FireForceBuildReceived(projectName, enforcerName))
            {
             string displayName = CheckSecurity(sessionToken, projectName, SecurityPermission.ForceBuild, SecurityEvent.ForceBuild);
             if (!string.IsNullOrEmpty(displayName)) enforcerName = displayName;
                integrationQueueManager.ForceBuild(projectName, enforcerName, buildValues);
                FireForceBuildProcessed(projectName, enforcerName);
            }
  }
  public void AbortBuild(string sessionToken, string projectName, string enforcerName)
  {
            if (!FireAbortBuildReceived(projectName, enforcerName))
            {
             string displayName = CheckSecurity(sessionToken, projectName, SecurityPermission.ForceBuild, SecurityEvent.AbortBuild);
             if (!string.IsNullOrEmpty(displayName)) enforcerName = displayName;
                GetIntegrator(projectName).AbortBuild(enforcerName);
                FireAbortBuildProcessed(projectName, enforcerName);
            }
        }
  public void WaitForExit(string projectName)
  {
   integrationQueueManager.WaitForExit(projectName);
  }
        public void Request(string sessionToken, string project, IntegrationRequest request)
        {
            if (!FireForceBuildReceived(project, request.Source))
            {
             string displayName = CheckSecurity(sessionToken, project, SecurityPermission.ForceBuild, SecurityEvent.ForceBuild);
             if (!string.IsNullOrEmpty(displayName))
             {
                 request = new IntegrationRequest(request.BuildCondition, displayName);
             }
                integrationQueueManager.Request(project, request);
                FireForceBuildProcessed(project, request.Source);
            }
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
        public void SendMessage(string sessionToken, string projectName, Message message)
  {
            if (!FireSendMessageReceived(projectName, message))
            {
             CheckSecurity(sessionToken, projectName, SecurityPermission.SendMessage, SecurityEvent.SendMessage);
                Log.Info("New message received: " + message);
                LookupProject(projectName).AddMessage(message);
                FireSendMessageProcessed(projectName, message);
            }
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
        private void InitialiseExtensions(List<ExtensionConfiguration> extensionList)
        {
            foreach (ExtensionConfiguration extensionConfig in extensionList)
            {
                Type extensionType = Type.GetType(extensionConfig.Type);
                if (extensionType == null) throw new NullReferenceException(string.Format("Unable to find extension '{0}'", extensionConfig.Type));
                ICruiseServerExtension extension = Activator.CreateInstance(extensionType) as ICruiseServerExtension;
                if (extension == null) throw new NullReferenceException(string.Format("Unable to create an instance of '{0}'", extensionType.FullName));
                extension.Initialise(this, extensionConfig);
                extensions.Add(extension);
            }
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
  public long GetFreeDiskSpace()
        {
         string drive = ConfigurationManager.AppSettings["DataDrive"];
         if (string.IsNullOrEmpty(drive))
         {
          if (System.IO.Path.DirectorySeparatorChar == '/')
           drive = "/";
          else
           drive = "C:";
         }
         IFileSystem fileSystem = new SystemIoFileSystem();
   return fileSystem.GetFreeDiskSpace(drive);
        }
        public virtual ProjectStatusSnapshot TakeStatusSnapshot(string projectName)
        {
            ProjectStatusSnapshot snapshot = null;
            IProjectIntegrator integrator = GetIntegrator(projectName);
            if (integrator != null)
            {
                if (integrator.Project is IStatusSnapshotGenerator)
                {
                    snapshot = (integrator.Project as IStatusSnapshotGenerator).GenerateSnapshot()
                        as ProjectStatusSnapshot;
                }
                else
                {
                    ProjectStatus status = integrator.Project.CreateProjectStatus(integrator);
                    snapshot = new ProjectStatusSnapshot();
                    snapshot.Name = integrator.Project.Name;
                    if (status.Activity.IsBuilding())
                    {
                        snapshot.Status = ItemBuildStatus.Running;
                    }
                    else if (status.Activity.IsPending())
                    {
                        snapshot.Status = ItemBuildStatus.Pending;
                    }
                    else if (status.Activity.IsSleeping())
                    {
                        switch (status.BuildStatus)
                        {
                            case IntegrationStatus.Success:
                                snapshot.Status = ItemBuildStatus.CompletedSuccess;
                                break;
                            case IntegrationStatus.Exception:
                            case IntegrationStatus.Failure:
                                snapshot.Status = ItemBuildStatus.CompletedSuccess;
                                break;
                        }
                    }
                }
            }
            else
            {
                throw new NoSuchProjectException(projectName);
            }
            return snapshot;
        }
        public virtual PackageDetails[] RetrievePackageList(string projectName)
        {
            List<PackageDetails> packages = GetIntegrator(projectName).Project.RetrievePackageList();
            return packages.ToArray();
        }
        public virtual PackageDetails[] RetrievePackageList(string projectName, string buildLabel)
        {
            List<PackageDetails> packages = GetIntegrator(projectName).Project.RetrievePackageList(buildLabel);
            return packages.ToArray();
        }
        public virtual RemotingFileTransfer RetrieveFileTransfer(string project, string fileName)
        {
            var sourceProject = GetIntegrator(project).Project;
            var filePath = Path.Combine(sourceProject.ArtifactDirectory, fileName);
            var fileInfo = new FileInfo(filePath);
            if (!fileInfo.FullName.StartsWith(sourceProject.ArtifactDirectory, StringComparison.InvariantCultureIgnoreCase))
            {
                var message = string.Format("Files can only be retrieved from_ the artefact folder - unable to retrieve {0}", fileName);
                Log.Warning(message);
                throw new CruiseControlException(message);
            }
            else if (fileInfo.FullName.StartsWith(Path.Combine(sourceProject.ArtifactDirectory, "buildlogs"), StringComparison.InvariantCultureIgnoreCase))
            {
                var message = string.Format("Unable to retrieve files from_ the build logs folder - unable to retrieve {0}", fileName);
                Log.Warning(message);
                throw new CruiseControlException(message);
            }
            RemotingFileTransfer fileTransfer = null;
            if (fileInfo.Exists)
            {
                Log.Debug(string.Format("Retrieving file '{0}' from_ '{1}'", fileName, project));
                fileTransfer = new RemotingFileTransfer(File.OpenRead(filePath));
            }
            else
            {
                Log.Warning(string.Format("Unable to find file '{0}' in '{1}'", fileName, project));
            }
            return fileTransfer;
        }
        public string Login(ISecurityCredentials credentials)
        {
            return securityManager.Login(credentials);
        }
        public void Logout(string sesionToken)
        {
            securityManager.Logout(sesionToken);
        }
         private string CheckSecurity(string sessionToken, string projectName, SecurityPermission permission, SecurityEvent eventType)
        {
            IProjectAuthorisation authorisation = null;
            bool requiresSession = true;
            string displayName = securityManager.GetDisplayName(sessionToken);
            string userName = securityManager.GetUserName(sessionToken);
            if (!string.IsNullOrEmpty(projectName))
            {
                IProjectIntegrator projectIntegrator = GetIntegrator(projectName);
                if ((projectIntegrator != null) &&
                    (projectIntegrator.Project != null) &&
                    (projectIntegrator.Project.Security != null))
                {
                    authorisation = projectIntegrator.Project.Security;
                    requiresSession = authorisation.RequiresSession;
                }
                else if ((projectIntegrator != null) &&
                    (projectIntegrator.Project != null) &&
                    (projectIntegrator.Project.Security == null))
                {
                    string errorMessage = string.Format("Security not found for project {0}", projectName);
                    Log.Error(errorMessage);
                    securityManager.LogEvent(projectName, userName, eventType, SecurityRight.Deny, errorMessage);
                    throw new SecurityException(errorMessage);
                }
                else
                {
                    string errorMessage = string.Format("project not found {0}", projectName);
                    Log.Error(errorMessage);
                    securityManager.LogEvent(projectName, userName, eventType, SecurityRight.Deny, errorMessage);
                    throw new NoSuchProjectException(projectName);
                }
            }
            if (!requiresSession || (userName != null))
            {
                if (string.IsNullOrEmpty(projectName))
                {
                    if (!securityManager.CheckServerPermission(userName, permission))
                    {
                        string info = string.Format("{2} [{0}] has been denied {1} permission at the server",
                            userName, permission, displayName);
                        Log.Warning(info);
                        securityManager.LogEvent(projectName, userName, eventType, SecurityRight.Deny, info);
                        throw new PermissionDeniedException(permission.ToString());
                    }
                    else
                    {
                        string info = string.Format("{2} [{0}] has been granted {1} permission at the server",
                            userName, permission, displayName);
                        Log.Debug(info);
                        securityManager.LogEvent(projectName, userName, eventType, SecurityRight.Allow, info);
                        return displayName;
                    }
                }
                else
                {
                    if (!authorisation.CheckPermission(securityManager, userName, permission))
                    {
                        string info = string.Format("{3} [{0}] has been denied {1} permission on '{2}'",
                            userName, permission, projectName, displayName);
                        Log.Warning(info);
                        securityManager.LogEvent(projectName, userName, eventType, SecurityRight.Deny, info);
                        throw new PermissionDeniedException(permission.ToString());
                    }
                    else
                    {
                        Log.Debug(string.Format("{3} [{0}] has been granted {1} permission on '{2}'",
                            userName,
                            permission,
                            projectName,
                            displayName));
                        securityManager.LogEvent(projectName, userName, eventType, SecurityRight.Allow, null);
                        return displayName;
                    }
                }
            }
            else
            {
                Log.Warning(string.Format("Session with token '{0}' is not valid", sessionToken));
                securityManager.LogEvent(projectName, null, eventType, SecurityRight.Deny, "EVENT_SessionNotFound");
                throw new SessionInvalidException();
            }
        }
        public virtual List<UserDetails> ListAllUsers(string sessionToken)
        {
            Log.Info("Listing users");
            CheckSecurity(sessionToken, string.Empty, SecurityPermission.ViewSecurity, SecurityEvent.ListAllUsers);
            return securityManager.ListAllUsers();
        }
        public virtual List<SecurityCheckDiagnostics> DiagnoseSecurityPermissions(string sessionToken, string userName, params string[] projectNames)
        {
            CheckSecurity(sessionToken, string.Empty, SecurityPermission.ViewSecurity, SecurityEvent.DiagnoseSecurityPermissions);
            List<SecurityCheckDiagnostics> diagnoses = new List<SecurityCheckDiagnostics>();
            Array permissions = Enum.GetValues(typeof(SecurityPermission));
            foreach (string projectName in projectNames)
            {
                if (string.IsNullOrEmpty(projectName))
                {
                    Log.Info(string.Format("DiagnoseServerPermission for user {0}", userName));
                }
                else
                {
                    Log.Info(string.Format("DiagnoseProjectPermission for user {0} project {1}", userName, projectName));
                }
                foreach (SecurityPermission permission in permissions)
                {
                    SecurityCheckDiagnostics diagnostics = new SecurityCheckDiagnostics();
                    diagnostics.Permission = permission.ToString();
                    diagnostics.Project = projectName;
                    diagnostics.User = userName;
                    diagnostics.IsAllowed = DiagnosePermission(userName, projectName, permission);
                    diagnoses.Add(diagnostics);
                }
            }
            return diagnoses;
        }
        private bool DiagnosePermission(string userName, string projectName, SecurityPermission permission)
        {
            bool isAllowed = false;
            if (userName != null)
            {
                IProjectIntegrator projectIntegrator = GetIntegrator(projectName);
                if ((projectIntegrator != null) &&
                    (projectIntegrator.Project != null) &&
                    (projectIntegrator.Project.Security != null))
                {
                    IProjectAuthorisation authorisation = projectIntegrator.Project.Security;
                    isAllowed = authorisation.CheckPermission(securityManager, userName, permission);
                }
            }
            return isAllowed;
        }
        public virtual List<AuditRecord> ReadAuditRecords(string sessionToken, int startPosition, int numberOfRecords)
        {
            CheckSecurity(sessionToken, string.Empty, SecurityPermission.ViewSecurity, SecurityEvent.ViewAuditLog);
            return securityManager.ReadAuditRecords(startPosition, numberOfRecords);
        }
        public virtual List<AuditRecord> ReadAuditRecords(string sessionToken, int startPosition, int numberOfRecords, IAuditFilter filter)
        {
            CheckSecurity(sessionToken, string.Empty, SecurityPermission.ViewSecurity, SecurityEvent.ViewAuditLog);
            return securityManager.ReadAuditRecords(startPosition, numberOfRecords, filter);
        }
        public virtual void ChangePassword(string sessionToken, string oldPassword, string newPassword)
        {
            string displayName = securityManager.GetDisplayName(sessionToken);
            Log.Debug(string.Format("Changing password for '{0}'", displayName));
            securityManager.ChangePassword(sessionToken, oldPassword, newPassword);
        }
        public virtual void ResetPassword(string sessionToken, string userName, string newPassword)
        {
            string displayName = securityManager.GetDisplayName(sessionToken);
            Log.Debug(string.Format("'{0}' is resetting password for '{1}'", displayName, userName));
            securityManager.ResetPassword(sessionToken, userName, newPassword);
        }
        public virtual string GetSecurityConfiguration(string sessionToken)
        {
            Log.Info("GetSecurityConfiguration");
            CheckSecurity(sessionToken, string.Empty, SecurityPermission.ViewSecurity, SecurityEvent.GetSecurityConfiguration);
            ServerSecurityConfigurationInformation config = new ServerSecurityConfigurationInformation();
            config.Manager = securityManager;
            foreach (IProject project in configuration.Projects)
            {
                config.AddProject(project);
            }
            return config.ToString();
        }
        public virtual List<ParameterBase> ListBuildParameters(string projectName)
        {
            List<ParameterBase> parameters = new List<ParameterBase>();
            IProjectIntegrator projectIntegrator = GetIntegrator(projectName);
            if (projectIntegrator == null) throw new NoSuchProjectException(projectName);
            IProject project = projectIntegrator.Project;
            if (project is IParamatisedProject)
            {
                parameters = (project as IParamatisedProject).ListBuildParameters();
            }
            return parameters;
        }
    }
}
