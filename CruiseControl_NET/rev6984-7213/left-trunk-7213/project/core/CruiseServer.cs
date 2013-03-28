namespace ThoughtWorks.CruiseControl.Core
{
    using System;
    using System.Collections.Generic;
    using System.Configuration;
    using System.IO;
    using System.Reflection;
    using System.Threading;
    using System.Web;
    using System.Web.Caching;
    using ThoughtWorks.CruiseControl.Core.Config;
    using ThoughtWorks.CruiseControl.Core.Logging;
    using ThoughtWorks.CruiseControl.Core.Queues;
    using ThoughtWorks.CruiseControl.Core.Security;
    using ThoughtWorks.CruiseControl.Core.State;
    using ThoughtWorks.CruiseControl.Core.Util;
    using ThoughtWorks.CruiseControl.Remote;
    using ThoughtWorks.CruiseControl.Remote.Events;
    using ThoughtWorks.CruiseControl.Remote.Messages;
    using ThoughtWorks.CruiseControl.Remote.Parameters;
    using ThoughtWorks.CruiseControl.Remote.Security;
    public class CruiseServer
        : CruiseServerEventsBase, ICruiseServer
    {
        private readonly IProjectSerializer projectSerializer;
        private readonly IConfigurationService configurationService;
  private readonly IFileSystem fileSystem;
  private readonly IExecutionEnvironment executionEnvironment;
        private IConfiguration configuration;
        [Obsolete]
        private readonly ICruiseManager manager;
        private readonly ICruiseServerClient serverClient;
        private readonly ManualResetEvent monitor = new ManualResetEvent(true);
        private ISecurityManager securityManager;
        private readonly List<ICruiseServerExtension> extensions = new List<ICruiseServerExtension>();
        private Dictionary<string, DateTime> receivedRequests = new Dictionary<string, DateTime>();
        private bool disposed;
        private IQueueManager integrationQueueManager;
        private Dictionary<Type, object> services = new Dictionary<Type,object>();
     private readonly string programmDataFolder;
        private object logCacheLock = new object();
        private TimeSpan cacheTime;
        public CruiseServer(IConfigurationService configurationService,
                            IProjectIntegratorListFactory projectIntegratorListFactory,
                            IProjectSerializer projectSerializer,
                            IProjectStateManager stateManager,
       IFileSystem fileSystem,
       IExecutionEnvironment executionEnvironment,
                            List<ExtensionConfiguration> extensionList)
        {
            this.configurationService = configurationService;
            this.projectSerializer = projectSerializer;
   this.fileSystem = fileSystem;
   this.executionEnvironment = executionEnvironment;
            manager = new CruiseManager(this);
            serverClient = new CruiseServerClient(this);
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
         programmDataFolder = this.executionEnvironment.GetDefaultProgramDataFolder(ApplicationType.Server);
            var cacheTimeInConfig = ConfigurationManager.AppSettings["cacheTime"];
            if (string.IsNullOrEmpty(cacheTimeInConfig))
            {
                this.cacheTime = new TimeSpan(0, 5, 0);
                Log.Info("Log cache time set to 5 minutes");
            }
            else
            {
                this.cacheTime = TimeSpan.FromSeconds(Convert.ToDouble(cacheTimeInConfig));
                if (this.cacheTime.TotalSeconds < 5)
                {
                    this.cacheTime = TimeSpan.MinValue;
                    Log.Info("Log cache has been turned off");
                }
                else
                {
                    Log.Info("Log cache time set to " + this.cacheTime.TotalSeconds.ToString() + " seconds");
                }
            }
        }
        [Obsolete("Use CruiseServerClient instead")]
        public ICruiseManager CruiseManager
        {
            get { return manager; }
        }
        public ICruiseServerClient CruiseServerClient
        {
            get { return serverClient; }
        }
        public ISecurityManager SecurityManager
        {
            get { return securityManager; }
        }
        public ICompressionService CompressionService { get; set; }
        public void InitialiseServices()
        {
            services.Add(typeof(IFileSystem), new SystemIoFileSystem());
            services.Add(typeof(ILogger), new DefaultLogger());
        }
        public void Start()
        {
            Log.Info("Starting CruiseControl.NET Server");
            monitor.Reset();
   if (!fileSystem.DirectoryExists(programmDataFolder))
            {
    Log.Info("Initialising data folder: '{0}'", programmDataFolder);
             fileSystem.EnsureFolderExists(programmDataFolder);
            }
            this.InitialiseDistributedBuilds();
            integrationQueueManager.StartAllProjects();
            Log.Info("Initialising security");
            securityManager.Initialise();
            if ((this.extensions != null) && (this.extensions.Count > 0))
            {
                Log.Info("Starting extensions");
                foreach (ICruiseServerExtension extension in this.extensions)
                {
                    extension.Start();
                }
            }
        }
        private void InitialiseDistributedBuilds()
        {
            if ((this.configuration.BuildAgents != null) &&
                (this.configuration.BuildAgents.Count > 0))
            {
                Log.Debug("Initialising build agents");
                foreach (var agent in this.configuration.BuildAgents)
                {
                    agent.Initialise();
                }
            }
            if ((this.configuration.BuildMachines != null) &&
                (this.configuration.BuildMachines.Count > 0))
            {
                Log.Debug("Initialising remote build machine connections");
                foreach (var machine in this.configuration.BuildMachines)
                {
                    machine.Initialise();
                }
            }
        }
        private void TerminateDistributedBuilds()
        {
            if ((this.configuration.BuildMachines != null) &&
                (this.configuration.BuildMachines.Count > 0))
            {
                Log.Debug("Terminating remote build machine connections");
                foreach (var machine in this.configuration.BuildMachines)
                {
                    machine.Terminate();
                }
            }
            if ((this.configuration.BuildAgents != null) &&
                (this.configuration.BuildAgents.Count > 0))
            {
                Log.Debug("Terminating build agents");
                foreach (var agent in this.configuration.BuildAgents)
                {
                    agent.Terminate();
                }
            }
        }
        public Response Start(ProjectRequest request)
        {
            Response response = RunProjectRequest(request,
                SecurityPermission.StartStopProject,
                SecurityEvent.StartProject,
                delegate(ProjectRequest arg, Response resp)
                {
                    if (!FireProjectStarting(arg.ProjectName))
                    {
                        integrationQueueManager.Start(arg.ProjectName);
                        FireProjectStarted(arg.ProjectName);
                    }
                });
            return response;
        }
        public void Stop()
        {
            if ((this.extensions != null) && (this.extensions.Count > 0))
            {
                Log.Info("Stopping extensions");
                foreach (ICruiseServerExtension extension in extensions)
                {
                    extension.Stop();
                }
            }
            Log.Info("Stopping CruiseControl.NET Server");
            integrationQueueManager.StopAllProjects();
            this.TerminateDistributedBuilds();
            monitor.Set();
        }
        public Response Stop(ProjectRequest request)
        {
            Response response = RunProjectRequest(request,
                SecurityPermission.StartStopProject,
                SecurityEvent.StopProject,
                delegate(ProjectRequest arg, Response resp)
                {
                    if (!FireProjectStopping(arg.ProjectName))
                    {
                        integrationQueueManager.Stop(arg.ProjectName);
                        FireProjectStopped(arg.ProjectName);
                    }
                });
            return response;
        }
        public void Abort()
        {
            if ((this.extensions != null) && (this.extensions.Count > 0))
            {
                Log.Info("Aborting extensions");
                foreach (ICruiseServerExtension extension in extensions)
                {
                    extension.Abort();
                }
            }
            Log.Info("Aborting CruiseControl.NET Server");
            integrationQueueManager.Abort();
            this.TerminateDistributedBuilds();
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
        public virtual Response WaitForExit(ProjectRequest request)
        {
            Response response = RunProjectRequest(request,
                null,
                null,
                delegate(ProjectRequest arg, Response resp)
                {
                    integrationQueueManager.WaitForExit(arg.ProjectName);
                });
            return response;
        }
        public virtual Response ForceBuild(ProjectRequest request)
        {
            Response response = RunProjectRequest(request,
                SecurityPermission.ForceAbortBuild,
                SecurityEvent.ForceBuild,
                delegate(ProjectRequest arg, Response resp)
                {
                    string userName = securityManager.GetDisplayName(arg.SessionToken, request.DisplayName);
                    if (!FireForceBuildReceived(arg.ProjectName, userName))
                    {
                        IntegrationRequest integrationRequest;
                        if (request is BuildIntegrationRequest)
                        {
                            BuildIntegrationRequest actualRequest = arg as BuildIntegrationRequest;
                            integrationRequest = new IntegrationRequest(actualRequest.BuildCondition, request.SourceName, userName);
                            integrationRequest.BuildValues = NameValuePair.ToDictionary(actualRequest.BuildValues);
                        }
                        else
                        {
                            integrationRequest = new IntegrationRequest(BuildCondition.ForceBuild, request.SourceName, userName);
                        }
                        GetIntegrator(arg.ProjectName).Request(integrationRequest);
                        FireForceBuildProcessed(arg.ProjectName, userName);
                    }
                });
            return response;
        }
        public virtual Response AbortBuild(ProjectRequest request)
        {
            Response response = RunProjectRequest(request,
                SecurityPermission.ForceAbortBuild,
                SecurityEvent.AbortBuild,
                delegate(ProjectRequest arg, Response resp)
                {
                    string userName = securityManager.GetDisplayName(arg.SessionToken, request.DisplayName);
                    if (!FireAbortBuildReceived(arg.ProjectName, userName))
                    {
                        GetIntegrator(arg.ProjectName).AbortBuild(userName);
                        FireAbortBuildProcessed(arg.ProjectName, userName);
                    }
                });
            return response;
        }
        public virtual Response CancelPendingRequest(ProjectRequest request)
        {
            Response response = RunProjectRequest(request,
                SecurityPermission.ForceAbortBuild,
                SecurityEvent.CancelRequest,
                delegate(ProjectRequest arg, Response resp)
                {
                    integrationQueueManager.CancelPendingRequest(arg.ProjectName);
                });
            return response;
        }
        public virtual SnapshotResponse GetCruiseServerSnapshot(ServerRequest request)
        {
            CruiseServerSnapshot snapshot = null;
            SnapshotResponse response = new SnapshotResponse(RunServerRequest(request,
                null,
                null,
                delegate(ServerRequest arg)
                {
                    snapshot = integrationQueueManager.GetCruiseServerSnapshot();
                    snapshot.ProjectStatuses = FilterProjects(request.SessionToken,
                        snapshot.ProjectStatuses);
                }));
            response.Snapshot = snapshot;
            return response;
        }
        public virtual ProjectStatusResponse GetProjectStatus(ServerRequest request)
        {
            ProjectStatus[] data = null;
            ProjectStatusResponse response = new ProjectStatusResponse(RunServerRequest(request,
                null,
                null,
                delegate(ServerRequest arg)
                {
                    data = integrationQueueManager.GetProjectStatuses();
                    data = FilterProjects(request.SessionToken, data);
                }));
            if (data != null) response.Projects.AddRange(data);
            return response;
        }
        public virtual Response SendMessage(MessageRequest request)
        {
            Response response = RunProjectRequest(request,
                SecurityPermission.SendMessage,
                SecurityEvent.SendMessage,
                delegate(ProjectRequest arg, Response resp)
                {
                    Log.Info("New message received: " + request.Message);
                    Message message = new Message(request.Message,request.Kind );
                    if (!FireSendMessageReceived(arg.ProjectName, message))
                    {
                        LookupProject(arg.ProjectName).AddMessage(message);
                        FireSendMessageProcessed(arg.ProjectName, message);
                    }
                });
            return response;
        }
        public DataResponse GetLatestBuildName(ProjectRequest request)
        {
            string data = null;
            DataResponse response = new DataResponse(RunProjectRequest(request,
                SecurityPermission.ViewProject,
                null,
                delegate(ProjectRequest arg, Response resp)
                {
                    data = GetIntegrator(arg.ProjectName).IntegrationRepository.GetLatestBuildName();
                }));
            response.Data = data;
            return response;
        }
        public virtual DataListResponse GetMostRecentBuildNames(BuildListRequest request)
        {
            string[] data = { };
            DataListResponse response = new DataListResponse(RunProjectRequest(request,
                SecurityPermission.ViewProject,
                null,
                delegate(ProjectRequest arg, Response resp)
                {
                    data = GetIntegrator(request.ProjectName)
                        .IntegrationRepository
                        .GetMostRecentBuildNames(request.NumberOfBuilds);
                }));
            if (data != null) response.Data.AddRange(data);
            return response;
        }
        public DataListResponse GetBuildNames(ProjectRequest request)
        {
            List<string> data = new List<string>();
            DataListResponse response = new DataListResponse(RunProjectRequest(request,
                SecurityPermission.ViewProject,
                null,
                delegate(ProjectRequest arg, Response resp)
                {
                    data.AddRange(GetIntegrator(arg.ProjectName).
                        IntegrationRepository.
                        GetBuildNames());
                }));
            response.Data = data;
            return response;
        }
        public virtual DataResponse GetLog(BuildRequest request)
        {
            string data = null;
            DataResponse response = new DataResponse(RunProjectRequest(request,
                SecurityPermission.ViewProject,
                null,
                delegate(ProjectRequest arg, Response resp)
                {
                    data = this.RetrieveLogData(request.ProjectName, request.BuildName, request.CompressData);
                }));
            response.Data = data;
            GC.Collect();
            return response;
        }
        public DataResponse GetServerLog(ServerRequest request)
        {
            string data = null;
            DataResponse response = null;
            if (request is ProjectRequest)
            {
                response = new DataResponse(RunProjectRequest(request as ProjectRequest,
                    SecurityPermission.ViewConfiguration,
                    null,
                    delegate(ProjectRequest arg, Response resp)
                    {
                        data = new ServerLogFileReader().Read((arg as ProjectRequest).ProjectName);
                    }));
            }
            else
            {
                response = new DataResponse(RunServerRequest(request,
                    SecurityPermission.ViewConfiguration,
                    null,
                    delegate(ServerRequest arg)
                    {
                        data = new ServerLogFileReader().Read();
                    }));
            }
            response.Data = data;
            return response;
        }
        public virtual Response AddProject(ChangeConfigurationRequest request)
        {
            Response response = RunProjectRequest(request,
                SecurityPermission.ChangeProjectConfiguration,
                SecurityEvent.AddProject,
                delegate(ProjectRequest arg, Response resp)
                {
                    Log.Info("Adding project - " + request.ProjectDefinition);
                    try
                    {
                        IConfiguration configuration = configurationService.Load();
                        IProject project = projectSerializer.Deserialize(request.ProjectDefinition);
                        configuration.AddProject(project);
                        project.Initialize();
                        configurationService.Save(configuration);
                    }
                    catch (ApplicationException e)
                    {
                        Log.Warning(e);
                        throw new CruiseControlException("Failed to add project. Exception was - " + e.Message, e);
                    }
                });
            return response;
        }
        public virtual Response DeleteProject(ChangeConfigurationRequest request)
        {
            Response response = RunProjectRequest(request,
                SecurityPermission.ChangeProjectConfiguration,
                SecurityEvent.DeleteProject,
                delegate(ProjectRequest arg, Response resp)
                {
                    Log.Info("Deleting project - " + request.ProjectName);
                    try
                    {
                        IConfiguration configuration = configurationService.Load();
                        configuration.Projects[request.ProjectName]
                            .Purge(request.PurgeWorkingDirectory,
                                request.PurgeArtifactDirectory,
                                request.PurgeSourceControlEnvironment);
                        configuration.DeleteProject(request.ProjectName);
                        configurationService.Save(configuration);
                    }
                    catch (Exception e)
                    {
                        Log.Warning(e);
                        throw new CruiseControlException("Failed to delete project. Exception was - " + e.Message, e);
                    }
                });
            return response;
        }
        public virtual Response UpdateProject(ChangeConfigurationRequest request)
        {
            Response response = RunProjectRequest(request,
                SecurityPermission.ChangeProjectConfiguration,
                SecurityEvent.UpdateProject,
                delegate(ProjectRequest arg, Response resp)
                {
                    Log.Info("Updating project - " + request.ProjectName);
                    try
                    {
                        IConfiguration configuration = configurationService.Load();
                        configuration.Projects[request.ProjectName].Purge(true, false, true);
                        configuration.DeleteProject(request.ProjectName);
                        IProject project = projectSerializer.Deserialize(request.ProjectDefinition);
                        configuration.AddProject(project);
                        project.Initialize();
                        configurationService.Save(configuration);
                    }
                    catch (ApplicationException e)
                    {
                        Log.Warning(e);
                        throw new CruiseControlException("Failed to add project. Exception was - " + e.Message, e);
                    }
                });
            return response;
        }
        public DataResponse GetProject(ProjectRequest request)
        {
            string data = null;
            DataResponse response = new DataResponse(RunProjectRequest(request,
                SecurityPermission.ViewConfiguration,
                null,
                delegate(ProjectRequest arg, Response resp)
                {
                    Log.Info("Getting project - " + request.ProjectName);
                    data = new NetReflectorProjectSerializer()
                        .Serialize(configurationService.Load().Projects[arg.ProjectName]);
                }));
            response.Data = data;
            return response;
        }
        public DataResponse GetServerVersion(ServerRequest request)
        {
            string data = null;
            DataResponse response = new DataResponse(RunServerRequest(request,
                null,
                null,
                delegate(ServerRequest arg)
                {
                    Log.Trace("Returning version number");
                    try
                    {
                        data = Assembly.GetExecutingAssembly().GetName().Version.ToString();
                    }
                    catch (ApplicationException e)
                    {
                        Log.Warning(e);
                        throw new CruiseControlException("Failed to get project version . Exception was - " + e.Message, e);
                    }
                }));
            response.Data = data;
            return response;
        }
        public ExternalLinksListResponse GetExternalLinks(ProjectRequest request)
        {
            List<ExternalLink> data = new List<ExternalLink>();
            ExternalLinksListResponse response = new ExternalLinksListResponse(RunProjectRequest(request,
                SecurityPermission.ViewProject,
                null,
                delegate(ProjectRequest arg, Response resp)
                {
                    data.AddRange(LookupProject(arg.ProjectName).ExternalLinks);
                }));
            response.ExternalLinks = data;
            return response;
        }
        public DataResponse GetArtifactDirectory(ProjectRequest request)
        {
            string data = null;
            DataResponse response = new DataResponse(RunProjectRequest(request,
                SecurityPermission.ViewProject,
                null,
                delegate(ProjectRequest arg, Response resp)
                {
                    data = GetIntegrator(arg.ProjectName).Project.ArtifactDirectory;
                }));
            response.Data = data;
            return response;
        }
        public DataResponse GetStatisticsDocument(ProjectRequest request)
        {
            string data = null;
            DataResponse response = new DataResponse(RunProjectRequest(request,
                SecurityPermission.ViewProject,
                null,
                delegate(ProjectRequest arg, Response resp)
                {
                    data = GetIntegrator(arg.ProjectName).Project.Statistics;
                }));
            response.Data = data;
            return response;
        }
        public DataResponse GetModificationHistoryDocument(ProjectRequest request)
        {
            string data = null;
            DataResponse response = new DataResponse(RunProjectRequest(request,
                SecurityPermission.ViewProject,
                null,
                delegate(ProjectRequest arg, Response resp)
                {
                    data = GetIntegrator(arg.ProjectName).Project.ModificationHistory;
                }));
            response.Data = data;
            return response;
        }
        public DataResponse GetRSSFeed(ProjectRequest request)
        {
            string data = null;
            DataResponse response = new DataResponse(RunProjectRequest(request,
                SecurityPermission.ViewProject,
                null,
                delegate(ProjectRequest arg, Response resp)
                {
                    data = GetIntegrator(arg.ProjectName).Project.RSSFeed;
                }));
            response.Data = data;
            return response;
        }
        public void Dispose()
        {
            lock (this)
            {
                if (disposed) return;
                disposed = true;
            }
            Abort();
        }
        public DataResponse GetFreeDiskSpace(ServerRequest request)
        {
            string data = null;
            DataResponse response = new DataResponse(RunServerRequest(request,
                null,
                null,
                delegate(ServerRequest arg)
                {
                    var drive = ConfigurationManager.AppSettings["DataDrive"];
                    if (string.IsNullOrEmpty(drive))
                    {
                        if (System.IO.Path.DirectorySeparatorChar == '/')
                            drive = "/";
                        else
                            drive = "C:";
                    }
                    var fileSystem = new SystemIoFileSystem();
                    data = fileSystem.GetFreeDiskSpace(drive).ToString();
                }));
            response.Data = data;
            return response;
        }
        public virtual StatusSnapshotResponse TakeStatusSnapshot(ProjectRequest request)
        {
            ProjectStatusSnapshot snapshot = null;
            StatusSnapshotResponse response = new StatusSnapshotResponse(RunProjectRequest(request,
                SecurityPermission.ViewProject,
                null,
                delegate(ProjectRequest arg, Response resp)
                {
                    IProjectIntegrator integrator = GetIntegrator(request.ProjectName);
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
                        throw new NoSuchProjectException(request.ProjectName);
                    }
                }));
            response.Snapshot = snapshot;
            return response;
        }
        public virtual ListPackagesResponse RetrievePackageList(ProjectRequest request)
        {
            List<PackageDetails> packages = null;
            ListPackagesResponse response = new ListPackagesResponse(RunProjectRequest(request,
                SecurityPermission.ViewProject,
                null,
                delegate(ProjectRequest arg, Response resp)
                {
                    if (request is BuildRequest)
                    {
                        var actualRequest = request as BuildRequest;
                        packages = GetIntegrator(request.ProjectName).Project.RetrievePackageList(actualRequest.BuildName);
                    }
                    else
                    {
                        packages = GetIntegrator(request.ProjectName).Project.RetrievePackageList();
                    }
                }));
            response.Packages = packages;
            return response;
        }
        public virtual FileTransferResponse RetrieveFileTransfer(FileTransferRequest request)
        {
            var response = new FileTransferResponse(request);
            try
            {
                var sourceProject = GetIntegrator(request.ProjectName).Project;
                var filePath = Path.Combine(sourceProject.ArtifactDirectory, request.FileName);
                var fileInfo = new FileInfo(filePath);
                if (!fileInfo.FullName.StartsWith(sourceProject.ArtifactDirectory, StringComparison.InvariantCultureIgnoreCase))
                {
                    var message = string.Format("Files can only be retrieved from_ the artefact folder - unable to retrieve {0}", request.FileName);
                    Log.Warning(message);
                    throw new CruiseControlException(message);
                }
                else if (fileInfo.FullName.StartsWith(Path.Combine(sourceProject.ArtifactDirectory, "buildlogs"), StringComparison.InvariantCultureIgnoreCase))
                {
                    var message = string.Format("Unable to retrieve files from_ the build logs folder - unable to retrieve {0}", request.FileName);
                    Log.Warning(message);
                    throw new CruiseControlException(message);
                }
                RemotingFileTransfer fileTransfer = null;
                if (fileInfo.Exists)
                {
                    Log.Debug(string.Format("Retrieving file '{0}' from_ '{1}'", request.FileName, request.ProjectName));
                    fileTransfer = new RemotingFileTransfer(File.OpenRead(filePath));
                }
                else
                {
                    Log.Warning(string.Format("Unable to find file '{0}' in '{1}'", request.FileName, request.ProjectName));
                }
                response.FileTransfer = fileTransfer;
                response.Result = ResponseResult.Success;
            }
            catch (Exception error)
            {
                response.Result = ResponseResult.Failure;
                response.ErrorMessages.Add(
                    new ErrorMessage(error.Message));
            }
            return response;
        }
        public virtual LoginResponse Login(LoginRequest request)
        {
            string sessionToken = null;
            LoginResponse response = new LoginResponse(RunServerRequest(request,
                null,
                null,
                delegate(ServerRequest arg)
                {
                    sessionToken = securityManager.Login(request);
                }));
            response.SessionToken = sessionToken;
            return response;
        }
        public virtual Response Logout(ServerRequest request)
        {
            Response response = RunServerRequest(request,
                null,
                null,
                delegate(ServerRequest arg)
                {
                    securityManager.Logout(request.SessionToken);
                });
            return response;
        }
        public virtual DataResponse GetSecurityConfiguration(ServerRequest request)
        {
            Log.Info("GetSecurityConfiguration");
            string configData = null;
            DataResponse response = new DataResponse(RunServerRequest(request,
                SecurityPermission.ViewSecurity,
                SecurityEvent.GetSecurityConfiguration,
                delegate(ServerRequest arg)
                {
                    ServerSecurityConfigurationInformation config = new ServerSecurityConfigurationInformation();
                    config.Manager = securityManager;
                    foreach (IProject project in configuration.Projects)
                    {
                        config.AddProject(project);
                    }
                    configData = config.ToString();
                }));
            return response;
        }
        public virtual ListUsersResponse ListUsers(ServerRequest request)
        {
            Log.Info("Listing users");
            List<UserDetails> users = new List<UserDetails>();
            ListUsersResponse response = new ListUsersResponse(RunServerRequest(request,
                SecurityPermission.ViewSecurity,
                SecurityEvent.ListAllUsers,
                delegate(ServerRequest arg)
                {
                    users = securityManager.ListAllUsers();
                }));
            response.Users = users;
            return response;
        }
        public DiagnoseSecurityResponse DiagnoseSecurityPermissions(DiagnoseSecurityRequest request)
        {
            List<SecurityCheckDiagnostics> diagnoses = new List<SecurityCheckDiagnostics>();
            DiagnoseSecurityResponse response = new DiagnoseSecurityResponse(RunServerRequest(request,
                SecurityPermission.ViewSecurity,
                SecurityEvent.DiagnoseSecurityPermissions,
                delegate(ServerRequest arg)
                {
                    Array permissions = Enum.GetValues(typeof(SecurityPermission));
                    foreach (string projectName in request.Projects)
                    {
                        if (string.IsNullOrEmpty(projectName))
                        {
                            Log.Info(string.Format("DiagnoseServerPermission for user {0}", request.UserName));
                        }
                        else
                        {
                            Log.Info(string.Format("DiagnoseProjectPermission for user {0} project {1}", request.UserName, projectName));
                        }
                        foreach (SecurityPermission permission in permissions)
                        {
                            SecurityCheckDiagnostics diagnostics = new SecurityCheckDiagnostics();
                            diagnostics.Permission = permission.ToString();
                            diagnostics.Project = projectName;
                            diagnostics.User = request.UserName;
                            diagnostics.IsAllowed = DiagnosePermission(request.UserName, projectName, permission);
                            diagnoses.Add(diagnostics);
                        }
                    }
                }));
            response.Diagnostics = diagnoses;
            return response;
        }
        public ReadAuditResponse ReadAuditRecords(ReadAuditRequest request)
        {
            List<AuditRecord> records = new List<AuditRecord>();
            ReadAuditResponse response = new ReadAuditResponse(RunServerRequest(request,
                SecurityPermission.ViewSecurity,
                SecurityEvent.ViewAuditLog,
                delegate(ServerRequest arg)
                {
                    records = securityManager.ReadAuditRecords(request.StartRecord,
                        request.NumberOfRecords,
                        request.Filter);
                }));
            response.Records = records;
            return response;
        }
        public virtual Response ChangePassword(ChangePasswordRequest request)
        {
            Response response = RunServerRequest(request,
                null,
                null,
                delegate(ServerRequest arg)
                {
                    string displayName = securityManager.GetDisplayName(request.SessionToken, request.UserName);
                    Log.Debug(string.Format("Changing password for '{0}'", displayName));
                    securityManager.ChangePassword(request.SessionToken,
                        request.OldPassword,
                        request.NewPassword);
                });
            return response;
        }
        public virtual Response ResetPassword(ChangePasswordRequest request)
        {
            Response response = RunServerRequest(request,
                null,
                null,
                delegate(ServerRequest arg)
                {
                    string displayName = securityManager.GetDisplayName(request.SessionToken, request.UserName);
                    Log.Debug(string.Format("'{0}' is resetting password for '{1}'", displayName, request.UserName));
                    securityManager.ResetPassword(request.SessionToken,
                        request.UserName,
                        request.NewPassword);
                });
            return response;
        }
        public virtual BuildParametersResponse ListBuildParameters(ProjectRequest request)
        {
            List<ParameterBase> parameters = new List<ParameterBase>();
            BuildParametersResponse response = new BuildParametersResponse(RunProjectRequest(request,
                SecurityPermission.ViewProject,
                null,
                delegate(ProjectRequest arg, Response resp)
                {
                    IProjectIntegrator projectIntegrator = GetIntegrator(arg.ProjectName);
                    if (projectIntegrator == null) throw new NoSuchProjectException(arg.ProjectName);
                    IProject project = projectIntegrator.Project;
                    if (project is IParamatisedProject)
                    {
                        parameters = (project as IParamatisedProject).ListBuildParameters();
                    }
                }));
            response.Parameters = parameters;
            return response;
        }
        public virtual object RetrieveService(Type serviceType)
        {
            if (services.ContainsKey(serviceType))
            {
                return services[serviceType];
            }
            else
            {
                return null;
            }
        }
        public virtual void AddService(Type serviceType, object service)
        {
            if (service != null)
            {
                services[serviceType] = service;
            }
        }
        private void ValidateRequest(ServerRequest request)
        {
            try
            {
                if (securityManager.Channel != null)
                {
                    securityManager.Channel.Validate(request.ChannelInformation);
                }
                if (request.Timestamp < DateTime.Now.AddDays(-1))
                {
                    throw new CruiseControlException("Request is too old");
                }
                if (receivedRequests.ContainsKey(request.Identifier))
                {
                    if (receivedRequests.ContainsKey(request.Identifier))
                    {
                        if (receivedRequests[request.Identifier] < DateTime.Now.AddDays(-1))
                        {
                            receivedRequests.Remove(request.Identifier);
                        }
                        else
                        {
                            throw new CruiseControlException("This request has already been processed");
                        }
                        receivedRequests.Add(request.Identifier, request.Timestamp);
                    }
                }
            }
            catch (SecurityException error)
            {
                Log.Warning("Message validation failed: {0}", error.Message);
                throw;
            }
        }
        private Response RunProjectRequest(ProjectRequest request,
            SecurityPermission? permission,
            SecurityEvent? eventType,
            ProjectRequestAction action)
        {
            var response = new Response(request);
            try
            {
                ValidateRequest(request);
                if (permission.HasValue)
                {
                    CheckSecurity(request.SessionToken,
                        request.ProjectName,
                        permission.Value,
                        eventType);
                }
                action(request, response);
                if (response.Result == ResponseResult.Unknown)
                {
                    response.Result = ResponseResult.Success;
                }
            }
            catch (Exception error)
            {
                Log.Warning(error);
                response.Result = ResponseResult.Failure;
                response.ErrorMessages.Add(
                    new ErrorMessage(
                        error.Message,
                        error.GetType().Name));
            }
            return response;
        }
        private Response RunServerRequest(ServerRequest request,
            SecurityPermission? permission,
            SecurityEvent? eventType,
            Action<ServerRequest> action)
        {
            Response response = new Response(request);
            try
            {
                ValidateRequest(request);
                if (permission.HasValue)
                {
                    CheckSecurity(request.SessionToken,
                        null,
                        permission.Value,
                        eventType);
                }
                action(request);
                response.Result = ResponseResult.Success;
            }
            catch (Exception error)
            {
                if (!(error is SecurityException))
                {
                    Log.Warning(error);
                }
                response.Result = ResponseResult.Failure;
                response.ErrorMessages.Add(
                    new ErrorMessage(
                        error.Message,
                        error.GetType().Name));
            }
            return response;
        }
        private ProjectStatus[] FilterProjects(string sessionToken,
            ProjectStatus[] projects)
        {
            List<ProjectStatus> allowedProjects = new List<ProjectStatus>();
            string userName = securityManager.GetUserName(sessionToken);
            bool defaultIsAllowed = (securityManager.GetDefaultRight(SecurityPermission.ViewProject) == SecurityRight.Allow);
            foreach (ProjectStatus project in projects)
            {
                IProjectIntegrator projectIntegrator = GetIntegrator(project.Name);
                bool isAllowed = true;
                if (projectIntegrator != null)
                {
                    IProjectAuthorisation authorisation = projectIntegrator.Project.Security;
                    if ((authorisation != null) && authorisation.RequiresSession(securityManager))
                    {
                        var thisUserName = userName;
                        if (string.IsNullOrEmpty(thisUserName)) thisUserName = authorisation.GuestAccountName;
                        if (thisUserName == null)
                        {
                            isAllowed = defaultIsAllowed;
                        }
                        else
                        {
                            isAllowed = authorisation.CheckPermission(securityManager,
                                thisUserName,
                                SecurityPermission.ViewProject,
                                SecurityRight.Allow);
                        }
                    }
                }
                if (isAllowed)
                {
                    allowedProjects.Add(project);
                }
            }
            return allowedProjects.ToArray();
        }
        private void OnIntegrationStarted(object sender, IntegrationStartedEventArgs args)
        {
            args.Result = FireIntegrationStarted(args.Request, args.ProjectName);
        }
        private void OnIntegrationCompleted(object sender, IntegrationCompletedEventArgs args)
        {
            FireIntegrationCompleted(args.Request, args.ProjectName, args.Status);
        }
        private IProject LookupProject(string projectName)
        {
            return GetIntegrator(projectName).Project;
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
        private void InitializeServerThread()
        {
            try
            {
                Thread.CurrentThread.Name = "CCNet Server";
            }
            catch (InvalidOperationException)
            {
            }
        }
        private bool DiagnosePermission(string userName, string projectName, SecurityPermission permission)
        {
            bool isAllowed = false;
            if (userName != null)
            {
                if (string.IsNullOrEmpty(projectName))
                {
                    isAllowed = securityManager.CheckServerPermission(userName, permission);
                }
                else
                {
                    IProjectIntegrator projectIntegrator = GetIntegrator(projectName);
                    if ((projectIntegrator != null) &&
                        (projectIntegrator.Project != null) &&
                        (projectIntegrator.Project.Security != null))
                    {
                        IProjectAuthorisation authorisation = projectIntegrator.Project.Security;
                        isAllowed = authorisation.CheckPermission(securityManager,
                            userName,
                            permission,
                            securityManager.GetDefaultRight(permission));
                    }
                }
            }
            return isAllowed;
        }
        private string CheckSecurity(string sessionToken,
            string projectName,
            SecurityPermission permission,
            SecurityEvent? eventType)
        {
            IProjectAuthorisation authorisation = null;
            bool requiresSession = securityManager.RequiresSession;
            string userName = securityManager.GetUserName(sessionToken);
            string displayName = securityManager.GetDisplayName(sessionToken, null) ?? userName;
            if (!string.IsNullOrEmpty(projectName))
            {
                IProjectIntegrator projectIntegrator = GetIntegrator(projectName);
                if ((projectIntegrator != null) &&
                    (projectIntegrator.Project != null) &&
                    (projectIntegrator.Project.Security != null))
                {
                    authorisation = projectIntegrator.Project.Security;
                    requiresSession = authorisation.RequiresSession(securityManager);
                }
                else if ((projectIntegrator != null) &&
                    (projectIntegrator.Project != null) &&
                    (projectIntegrator.Project.Security == null))
                {
                    string errorMessage = string.Format("Security not found for project {0}", projectName);
                    Log.Error(errorMessage);
                    if (eventType.HasValue)
                    {
                        securityManager.LogEvent(projectName,
                            userName,
                            eventType.Value,
                            SecurityRight.Deny,
                            errorMessage);
                    }
                    throw new SecurityException(errorMessage);
                }
                else
                {
                    string errorMessage = string.Format("project not found {0}", projectName);
                    Log.Error(errorMessage);
                    if (eventType.HasValue)
                    {
                        securityManager.LogEvent(projectName,
                            userName,
                            eventType.Value,
                            SecurityRight.Deny,
                            errorMessage);
                    }
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
                        if (eventType.HasValue)
                        {
                            securityManager.LogEvent(projectName,
                                userName,
                                eventType.Value,
                                SecurityRight.Deny,
                                info);
                        }
                        throw new PermissionDeniedException(permission.ToString());
                    }
                    else
                    {
                        string info = string.Format("{2} [{0}] has been granted {1} permission at the server",
                            userName, permission, displayName);
                        Log.Debug(info);
                        if (eventType.HasValue)
                        {
                            securityManager.LogEvent(projectName,
                                userName,
                                eventType.Value,
                                SecurityRight.Allow,
                                info);
                        }
                        return displayName;
                    }
                }
                else
                {
                    if (!authorisation.CheckPermission(securityManager,
                        userName,
                        permission,
                        securityManager.GetDefaultRight(permission)))
                    {
                        string info = string.Format("{3} [{0}] has been denied {1} permission on '{2}'",
                            userName, permission, projectName, displayName);
                        Log.Warning(info);
                        if (eventType.HasValue)
                        {
                            securityManager.LogEvent(projectName,
                                userName,
                                eventType.Value,
                                SecurityRight.Deny,
                                info);
                        }
                        throw new PermissionDeniedException(permission.ToString());
                    }
                    else
                    {
                        Log.Debug(string.Format("{3} [{0}] has been granted {1} permission on '{2}'",
                            userName,
                            permission,
                            projectName,
                            displayName));
                        if (eventType.HasValue)
                        {
                            securityManager.LogEvent(projectName,
                                userName,
                                eventType.Value,
                                SecurityRight.Allow,
                                null);
                        }
                        return displayName;
                    }
                }
            }
            else
            {
                SecurityRight defaultRight = securityManager.GetDefaultRight(permission);
                switch (defaultRight)
                {
                    case SecurityRight.Allow:
                        Log.Debug(string.Format("{3} [{0}] has been granted {1} permission on '{2}'",
                            userName,
                            permission,
                            projectName,
                            displayName));
                        return string.Empty;
                    default:
                        var info = string.Format("Session with token '{0}' is not valid", sessionToken);
                        Log.Warning(info);
                        if (eventType.HasValue)
                        {
                            securityManager.LogEvent(projectName,
                                null,
                                eventType.Value,
                                SecurityRight.Deny,
                                info);
                        }
                        throw new SessionInvalidException();
                }
            }
        }
        public virtual DataResponse GetLinkedSiteId(ProjectItemRequest request)
        {
            string data = null;
            DataResponse response = new DataResponse(RunProjectRequest(request,
                SecurityPermission.ViewProject,
                null,
                (arg, resp) =>
                {
                    var project = GetIntegrator(arg.ProjectName).Project;
                    if (project.LinkedSites != null)
                    {
                        foreach (var siteLink in project.LinkedSites)
                        {
                            if (string.Equals(request.ItemName, siteLink.Name, StringComparison.CurrentCultureIgnoreCase))
                            {
                                data = siteLink.Value;
                                break;
                            }
                        }
                    }
                }));
            response.Data = data;
            return response;
        }
        private string RetrieveLogData(string projectName, string buildName, bool compress)
        {
            var cache = HttpRuntime.Cache;
            var logKey = projectName +
                buildName +
                (compress ? "-c" : string.Empty);
            var loadData = false;
            SynchronisedData logData;
            if (this.cacheTime != TimeSpan.MinValue)
            {
                lock (logCacheLock)
                {
                    logData = cache[logKey] as SynchronisedData;
                    if (logData == null)
                    {
                        Log.Debug("Adding new cache entry, current cache size is " + cache.Count);
                        Log.Debug("Current memory in use by GC is " + GC.GetTotalMemory(false));
                        logData = new SynchronisedData();
                        cache.Add(
                            logKey,
                            logData,
                            null,
                            Cache.NoAbsoluteExpiration,
                            this.cacheTime,
                            CacheItemPriority.BelowNormal,
                            (key, value, reason) =>
                            {
                                Log.Debug("Log for " + key + " has been removed from_ the cache - " + reason.ToString());
                            });
                        loadData = true;
                    }
                }
            }
            else
            {
                logData = new SynchronisedData();
                loadData = true;
            }
            if (loadData)
            {
                Log.Debug("Loading log for " + logKey + (this.cacheTime != TimeSpan.MinValue ? " into cache" : string.Empty));
                logData.LoadData(() =>
                {
                    var buildLog = this.GetIntegrator(projectName)
                        .IntegrationRepository
                        .GetBuildLog(buildName);
                    if (compress)
                    {
                        var size = buildLog.Length;
                        buildLog = this.CompressLogData(buildLog);
                        Log.Debug("Build log compressed - from_ " + size.ToString() + " to " + buildLog.Length.ToString());
                    }
                    return buildLog;
                });
                Log.Debug("Current memory in use by GC is " + GC.GetTotalMemory(false));
            }
            else
            {
                Log.Debug("Retrieving log for " + logKey + " from_ cache");
                logData.WaitForLoad(10000);
            }
            if (logData.Data == null)
            {
                cache.Remove(logKey);
                throw new ApplicationException("Unable to retrieve log data");
            }
            return logData.Data as string;
        }
        private string CompressLogData(string logData)
        {
            var compressionService = this.CompressionService ?? new ZipCompressionService();
            return compressionService.CompressString(logData);
        }
        private delegate void ProjectRequestAction(ProjectRequest request, Response response);
    }
}
