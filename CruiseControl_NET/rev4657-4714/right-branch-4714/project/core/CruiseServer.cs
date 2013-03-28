using System;
using System.Collections.Generic;
using System.Reflection;
using System.Threading;
using ThoughtWorks.CruiseControl.Core.Config;
using ThoughtWorks.CruiseControl.Core.Logging;
using ThoughtWorks.CruiseControl.Core.Security;
using ThoughtWorks.CruiseControl.Core.Util;
using ThoughtWorks.CruiseControl.Remote;
using ThoughtWorks.CruiseControl.Remote.Security;
namespace ThoughtWorks.CruiseControl.Core
{
    public class CruiseServer : ICruiseServer
    {
        private readonly IProjectSerializer projectSerializer;
        private readonly IConfigurationService configurationService;
        private readonly IConfiguration configuration;
        private readonly ICruiseManager manager;
        private readonly ManualResetEvent monitor = new ManualResetEvent(true);
        private ISecurityManager securityManager;
        private bool disposed;
        private IntegrationQueueManager integrationQueueManager;
        public CruiseServer(IConfigurationService configurationService,
                            IProjectIntegratorListFactory projectIntegratorListFactory, IProjectSerializer projectSerializer)
        {
            this.configurationService = configurationService;
            this.configurationService.AddConfigurationUpdateHandler(new ConfigurationUpdateHandler(Restart));
            this.projectSerializer = projectSerializer;
            manager = new CruiseManager(this);
            InitializeServerThread();
            IConfiguration configuration = configurationService.Load();
            integrationQueueManager = new IntegrationQueueManager(projectIntegratorListFactory, configuration);
            securityManager = configuration.SecurityManager;
        }
        public void Start()
        {
            Log.Info("Starting CruiseControl.NET Server");
            monitor.Reset();
            integrationQueueManager.StartAllProjects();
            Log.Info("Initialising security");
            securityManager.Initialise();
        }
        public void Start(string sessionToken, string project)
        {
            CheckSecurity(sessionToken, project, SecurityPermission.StartProject, SecurityEvent.StartProject);
            integrationQueueManager.Start(project);
        }
        public void Stop()
        {
            Log.Info("Stopping CruiseControl.NET Server");
            integrationQueueManager.StopAllProjects();
            monitor.Set();
        }
        public void Stop(string sessionToken, string project)
        {
            CheckSecurity(sessionToken, project, SecurityPermission.StopProject, SecurityEvent.StopProject);
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
        public void ForceBuild(string sessionToken, string projectName, string enforcerName)
        {
            string displayName = CheckSecurity(sessionToken, projectName, SecurityPermission.ForceBuild, SecurityEvent.ForceBuild);
            if (!string.IsNullOrEmpty(displayName)) enforcerName = displayName;
            integrationQueueManager.ForceBuild(projectName, enforcerName);
        }
        public void AbortBuild(string sessionToken, string projectName, string enforcerName)
        {
            string displayName = CheckSecurity(sessionToken, projectName, SecurityPermission.ForceBuild, SecurityEvent.AbortBuild);
            if (!string.IsNullOrEmpty(displayName)) enforcerName = displayName;
            GetIntegrator(projectName).AbortBuild(enforcerName);
        }
        public void WaitForExit(string projectName)
        {
            integrationQueueManager.WaitForExit(projectName);
        }
        public void Request(string sessionToken, string project, IntegrationRequest request)
        {
            string displayName = CheckSecurity(sessionToken, project, SecurityPermission.ForceBuild, SecurityEvent.ForceBuild);
            if (!string.IsNullOrEmpty(displayName))
            {
                request = new IntegrationRequest(request.BuildCondition, displayName);
            }
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
        public void SendMessage(string sessionToken, string projectName, Message message)
        {
            CheckSecurity(sessionToken, projectName, SecurityPermission.SendMessage, SecurityEvent.SendMessage);
            Log.Info("New message received: " + message);
            LookupProject(projectName).AddMessage(message);
        }
        public string GetArtifactDirectory(string projectName)
        {
            return LookupProject(projectName).ArtifactDirectory;
        }
        public string GetStatisticsDocument(string projectName)
        {
            return GetIntegrator(projectName).Project.Statistics.OuterXml;
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
                        string info = string.Format("ServerPermission {1} Denied for {0} {2}",
                            userName, permission, displayName);
                        Log.Warning(info);
                        securityManager.LogEvent(projectName, userName, eventType, SecurityRight.Deny, info);
                        throw new PermissionDeniedException(permission.ToString());
                    }
                    else
                    {
                        string info = string.Format("ServerPermissionGranted {0} {1} {2}",
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
                        string info = string.Format("ProjectPermissionDenied {0} {1} {2} ",
                            userName, permission, projectName, displayName);
                        Log.Warning(info);
                        securityManager.LogEvent(projectName, userName, eventType, SecurityRight.Deny, info);
                        throw new PermissionDeniedException(permission.ToString());
                    }
                    else
                    {
                        Log.Debug(string.Format("LOG_ProjectPermissionGranted {0} {1} {2} ",
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
                Log.Warning(string.Format("LOG_SessionNotFound {0}", sessionToken));
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
    }
}
