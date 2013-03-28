using System;
using System.Collections;
using System.Collections.Generic;
using System.Net.Sockets;
using ThoughtWorks.CruiseControl.Core;
using ThoughtWorks.CruiseControl.Core.Reporting.Dashboard.Navigation;
using ThoughtWorks.CruiseControl.Core.Util;
using ThoughtWorks.CruiseControl.Remote;
using ThoughtWorks.CruiseControl.WebDashboard.Configuration;
using ThoughtWorks.CruiseControl.Remote.Parameters;
using ThoughtWorks.CruiseControl.Remote.Security;
using ThoughtWorks.CruiseControl.Remote.Messages;
namespace ThoughtWorks.CruiseControl.WebDashboard.ServerConnection
{
 public class ServerAggregatingCruiseManagerWrapper : ICruiseManagerWrapper, IFarmService
 {
  private readonly ICruiseManagerFactory managerFactory;
  private readonly IRemoteServicesConfiguration configuration;
  public ServerAggregatingCruiseManagerWrapper(IRemoteServicesConfiguration configuration, ICruiseManagerFactory managerFactory)
  {
   this.configuration = configuration;
   this.managerFactory = managerFactory;
  }
  public IBuildSpecifier GetLatestBuildSpecifier(IProjectSpecifier projectSpecifier)
  {
            DataResponse response = GetCruiseManager(projectSpecifier.ServerSpecifier)
                .GetLatestBuildName(GenerateProjectRequest(projectSpecifier, null));
            ValidateResponse(response);
   return new DefaultBuildSpecifier(projectSpecifier, response.Data);
  }
  public string GetLog(IBuildSpecifier buildSpecifier)
  {
            BuildRequest request = new BuildRequest(null, buildSpecifier.ProjectSpecifier.ProjectName);
            request.BuildName = buildSpecifier.BuildName;
            DataResponse response = GetCruiseManager(buildSpecifier)
                .GetLog(request);
            ValidateResponse(response);
            return response.Data;
  }
  public IBuildSpecifier[] GetBuildSpecifiers(IProjectSpecifier projectSpecifier)
  {
            DataListResponse response = GetCruiseManager(projectSpecifier.ServerSpecifier)
                .GetBuildNames(GenerateProjectRequest(projectSpecifier, null));
            ValidateResponse(response);
            return CreateBuildSpecifiers(projectSpecifier, response.Data.ToArray());
  }
  public IBuildSpecifier[] GetMostRecentBuildSpecifiers(IProjectSpecifier projectSpecifier, int buildCount)
  {
            BuildListRequest request = new BuildListRequest(null, projectSpecifier.ProjectName);
            request.NumberOfBuilds = buildCount;
            DataListResponse response = GetCruiseManager(projectSpecifier)
                .GetMostRecentBuildNames(request);
            ValidateResponse(response);
            return CreateBuildSpecifiers(projectSpecifier, response.Data.ToArray());
  }
  private IBuildSpecifier[] CreateBuildSpecifiers(IProjectSpecifier projectSpecifier, string[] buildNames)
  {
   ArrayList buildSpecifiers = new ArrayList();
   foreach (string buildName in buildNames)
   {
    buildSpecifiers.Add(new DefaultBuildSpecifier(projectSpecifier, buildName));
   }
   return (IBuildSpecifier[]) buildSpecifiers.ToArray(typeof (IBuildSpecifier));
  }
  public void DeleteProject(IProjectSpecifier projectSpecifier, bool purgeWorkingDirectory, bool purgeArtifactDirectory, bool purgeSourceControlEnvironment)
  {
            ChangeConfigurationRequest request = new ChangeConfigurationRequest(null,
                projectSpecifier.ProjectName);
            request.PurgeArtifactDirectory = purgeArtifactDirectory;
            request.PurgeSourceControlEnvironment = purgeSourceControlEnvironment;
            request.PurgeWorkingDirectory = purgeWorkingDirectory;
            Response response = GetCruiseManager(projectSpecifier)
                .DeleteProject(request);
            ValidateResponse(response);
  }
        public void Start(IProjectSpecifier projectSpecifier)
        {
            Start(projectSpecifier, null);
        }
        public void Stop(IProjectSpecifier projectSpecifier)
   {
            Stop(projectSpecifier, null);
   }
        public void ForceBuild(IProjectSpecifier projectSpecifier, string enforcerName)
        {
            ForceBuild(projectSpecifier, null, enforcerName, new Dictionary<string, string>());
  }
        public void ForceBuild(IProjectSpecifier projectSpecifier, string sessionToken, string enforcerName)
        {
            ForceBuild(projectSpecifier, sessionToken, enforcerName, new Dictionary<string, string>());
        }
        public void ForceBuild(IProjectSpecifier projectSpecifier, string enforcerName, Dictionary<string, string> parameters)
        {
            ForceBuild(projectSpecifier, null, enforcerName, parameters);
        }
        public void AbortBuild(IProjectSpecifier projectSpecifier, string enforcerName)
        {
            AbortBuild(projectSpecifier, null);
        }
        public void ForceBuild(IProjectSpecifier projectSpecifier, string sessionToken, string enforcerName, Dictionary<string, string> parameters)
        {
            BuildIntegrationRequest request = new BuildIntegrationRequest();
            request.SessionToken = sessionToken;
            request.ProjectName = projectSpecifier.ProjectName;
            request.BuildValues = NameValuePair.FromDictionary(parameters);
            var manager = GetCruiseManager(projectSpecifier.ServerSpecifier);
            var response = manager.ForceBuild(request);
            ValidateResponse(response);
        }
        public void AbortBuild(IProjectSpecifier projectSpecifier, string sessionToken, string enforcerName)
  {
            ProjectRequest request = GenerateProjectRequest(projectSpecifier, sessionToken);
            ValidateResponse(GetCruiseManager(projectSpecifier.ServerSpecifier).AbortBuild(request));
  }
        private string GetServerUrl(IServerSpecifier serverSpecifier)
        {
            var locations = ServerLocations;
            foreach (var serverLocation in locations)
            {
                if (StringUtil.EqualsIgnoreCase(serverLocation.Name, serverSpecifier.ServerName))
                {
                    return serverLocation.Url;
                }
            }
            throw new UnknownServerException(serverSpecifier.ServerName);
        }
  public ExternalLink[] GetExternalLinks(IProjectSpecifier projectSpecifier)
        {
            ExternalLinksListResponse response = GetCruiseManager(projectSpecifier.ServerSpecifier)
                .GetExternalLinks(GenerateProjectRequest(projectSpecifier, null));
            ValidateResponse(response);
            return response.ExternalLinks.ToArray();
        }
  public ProjectStatusListAndExceptions GetProjectStatusListAndCaptureExceptions(string sessionToken)
        {
   return GetProjectStatusListAndCaptureExceptions(GetServerSpecifiers(), sessionToken);
        }
        public ProjectStatusListAndExceptions GetProjectStatusListAndCaptureExceptions(IServerSpecifier serverSpecifier, string sessionToken)
    {
   return GetProjectStatusListAndCaptureExceptions(new IServerSpecifier[] {serverSpecifier}, sessionToken);
   }
        private ProjectStatusListAndExceptions GetProjectStatusListAndCaptureExceptions(IServerSpecifier[] serverSpecifiers, string sessionToken)
  {
   ArrayList projectStatusOnServers = new ArrayList();
   ArrayList exceptions = new ArrayList();
   foreach (IServerSpecifier serverSpecifier in serverSpecifiers)
   {
    try
    {
                    var manager = GetCruiseManager(serverSpecifier);
     foreach (ProjectStatus projectStatus in manager
                        .GetProjectStatus(new ServerRequest(sessionToken))
                        .Projects)
     {
      projectStatusOnServers.Add(new ProjectStatusOnServer(projectStatus, serverSpecifier));
     }
    }
    catch (SocketException)
    {
     AddException(exceptions, serverSpecifier, new CruiseControlException("Unable to connect to CruiseControl.NET server.  Please either start the server or check the url."));
    }
    catch (Exception e)
  {
     AddException(exceptions, serverSpecifier, e);
    }
  }
   return new ProjectStatusListAndExceptions((ProjectStatusOnServer[]) projectStatusOnServers.ToArray(typeof (ProjectStatusOnServer)),
                                             (CruiseServerException[]) exceptions.ToArray(typeof (CruiseServerException)));
  }
  private void AddException(ArrayList exceptions, IServerSpecifier serverSpecifier, Exception e)
  {
   exceptions.Add(new CruiseServerException(serverSpecifier.ServerName, GetServerUrl(serverSpecifier), e));
  }
  public string GetServerLog(IServerSpecifier serverSpecifier)
  {
            DataResponse response = GetCruiseManager(serverSpecifier)
                .GetServerLog(new ServerRequest());
            ValidateResponse(response);
            return response.Data;
  }
  public string GetServerLog(IProjectSpecifier projectSpecifier)
  {
            DataResponse response = GetCruiseManager(projectSpecifier.ServerSpecifier)
                .GetServerLog(GenerateProjectRequest(projectSpecifier, null));
            ValidateResponse(response);
            return response.Data;
  }
        public void Start(IProjectSpecifier projectSpecifier, string sessionToken)
  {
            ProjectRequest request = GenerateProjectRequest(projectSpecifier, sessionToken);
            ValidateResponse(GetCruiseManager(projectSpecifier.ServerSpecifier).Start(request));
  }
        public void Stop(IProjectSpecifier projectSpecifier, string sessionToken)
  {
            ProjectRequest request = GenerateProjectRequest(projectSpecifier, sessionToken);
            ValidateResponse(GetCruiseManager(projectSpecifier.ServerSpecifier).Stop(request));
  }
  public string GetServerVersion(IServerSpecifier serverSpecifier)
  {
            DataResponse response = GetCruiseManager(serverSpecifier)
                .GetServerVersion(new ServerRequest());
            ValidateResponse(response);
            return response.Data;
  }
  public IServerSpecifier[] GetServerSpecifiers()
  {
   ArrayList serverSpecifiers = new ArrayList();
   foreach (ServerLocation serverLocation in ServerLocations)
   {
    serverSpecifiers.Add(new DefaultServerSpecifier(serverLocation.Name, serverLocation.AllowForceBuild, serverLocation.AllowStartStopBuild));
   }
   return (IServerSpecifier[]) serverSpecifiers.ToArray(typeof (IServerSpecifier));
  }
  public void AddProject(IServerSpecifier serverSpecifier, string serializedProject)
  {
            ChangeConfigurationRequest request = new ChangeConfigurationRequest(null, null);
            request.ProjectDefinition = serializedProject;
            Response response = GetCruiseManager(serverSpecifier)
                .AddProject(request);
            ValidateResponse(response);
  }
  public string GetProject(IProjectSpecifier projectSpecifier)
  {
            DataResponse response = GetCruiseManager(projectSpecifier.ServerSpecifier)
                .GetProject(GenerateProjectRequest(projectSpecifier, null));
            ValidateResponse(response);
            return response.Data;
  }
  public void UpdateProject(IProjectSpecifier projectSpecifier, string serializedProject)
  {
            ChangeConfigurationRequest request = new ChangeConfigurationRequest(null,
                projectSpecifier.ProjectName);
            request.ProjectDefinition = serializedProject;
            Response response = GetCruiseManager(projectSpecifier)
                .UpdateProject(request);
            ValidateResponse(response);
  }
  public string GetArtifactDirectory(IProjectSpecifier projectSpecifier)
  {
            DataResponse response = GetCruiseManager(projectSpecifier.ServerSpecifier)
                .GetArtifactDirectory(GenerateProjectRequest(projectSpecifier, null));
            ValidateResponse(response);
            return response.Data;
  }
  public string GetStatisticsDocument(IProjectSpecifier projectSpecifier)
  {
            DataResponse response = GetCruiseManager(projectSpecifier.ServerSpecifier)
                .GetStatisticsDocument(GenerateProjectRequest(projectSpecifier, null));
            ValidateResponse(response);
            return response.Data;
  }
        public string GetModificationHistoryDocument(IProjectSpecifier projectSpecifier)
        {
            DataResponse response = GetCruiseManager(projectSpecifier.ServerSpecifier)
                .GetModificationHistoryDocument(GenerateProjectRequest(projectSpecifier, null));
            ValidateResponse(response);
            return response.Data;
        }
        public string GetRSSFeed(IProjectSpecifier projectSpecifier)
        {
            DataResponse response = GetCruiseManager(projectSpecifier.ServerSpecifier)
                .GetRSSFeed(GenerateProjectRequest(projectSpecifier, null));
            ValidateResponse(response);
            return response.Data;
        }
  private ICruiseServerClient GetCruiseManager(IBuildSpecifier buildSpecifier)
  {
   return GetCruiseManager(buildSpecifier.ProjectSpecifier);
  }
  private ICruiseServerClient GetCruiseManager(IProjectSpecifier projectSpecifier)
  {
   return GetCruiseManager(projectSpecifier.ServerSpecifier);
  }
  private ICruiseServerClient GetCruiseManager(IServerSpecifier serverSpecifier)
  {
            var uri = GetServerUrl(serverSpecifier);
   var manager = managerFactory.GetCruiseServerClient(uri);
            return manager;
  }
  private ServerLocation[] ServerLocations
        {
   get { return configuration.Servers; }
        }
  public IServerSpecifier GetServerConfiguration(string serverName)
  {
   foreach (ServerLocation serverLocation in ServerLocations)
        {
    if (serverLocation.ServerName == serverName)
     return serverLocation;
        }
   return null;
        }
        public CruiseServerSnapshotListAndExceptions GetCruiseServerSnapshotListAndExceptions()
        {
            return GetCruiseServerSnapshotListAndExceptions(GetServerSpecifiers());
        }
        public CruiseServerSnapshotListAndExceptions GetCruiseServerSnapshotListAndExceptions(IServerSpecifier serverSpecifier)
        {
            return GetCruiseServerSnapshotListAndExceptions(new IServerSpecifier[] { serverSpecifier });
        }
        public string Login(string server, LoginRequest credentials)
  {
            return GetCruiseManager(GetServerConfiguration(server)).Login(credentials).SessionToken;
  }
        public void Logout(string server, string sessionToken)
   {
            GetCruiseManager(GetServerConfiguration(server)).Logout(
                new ServerRequest(sessionToken));
  }
        public void ChangePassword(string server, string sessionToken, string oldPassword, string newPassword)
        {
            ChangePasswordRequest request = new ChangePasswordRequest();
            request.SessionToken = sessionToken;
            request.OldPassword = oldPassword;
            request.NewPassword = newPassword;
            ValidateResponse(
                GetCruiseManager(GetServerConfiguration(server))
                .ChangePassword(request));
        }
        public virtual void ResetPassword(string server, string sessionToken, string userName, string newPassword)
        {
            ChangePasswordRequest request = new ChangePasswordRequest();
            request.SessionToken = sessionToken;
            request.UserName = userName;
            request.NewPassword = newPassword;
            ValidateResponse(
                GetCruiseManager(GetServerConfiguration(server))
                .ResetPassword(request));
        }
        private CruiseServerSnapshotListAndExceptions GetCruiseServerSnapshotListAndExceptions(IServerSpecifier[] serverSpecifiers)
        {
            ArrayList cruiseServerSnapshotsOnServers = new ArrayList();
            ArrayList exceptions = new ArrayList();
            foreach (IServerSpecifier serverSpecifier in serverSpecifiers)
            {
                try
                {
                    SnapshotResponse response = GetCruiseManager(serverSpecifier).GetCruiseServerSnapshot(
                        new ServerRequest(null));
                    ValidateResponse(response);
                    cruiseServerSnapshotsOnServers.Add(
                        new CruiseServerSnapshotOnServer(response.Snapshot, serverSpecifier));
                }
                catch (SocketException)
                {
                    AddException(exceptions, serverSpecifier, new CruiseControlException("Unable to connect to CruiseControl.NET server.  Please either start the server or check the url."));
                }
                catch (Exception e)
                {
                    AddException(exceptions, serverSpecifier, e);
                }
            }
            return new CruiseServerSnapshotListAndExceptions(
                (CruiseServerSnapshotOnServer[])cruiseServerSnapshotsOnServers.ToArray(typeof(CruiseServerSnapshotOnServer)),
                (CruiseServerException[])exceptions.ToArray(typeof(CruiseServerException)));
        }
        public virtual string GetServerSecurity(IServerSpecifier serverSpecifier, string sessionToken)
        {
            return GetCruiseManager(serverSpecifier)
                .GetSecurityConfiguration(new ServerRequest(sessionToken))
                .Data;
        }
        public virtual List<UserDetails> ListAllUsers(IServerSpecifier serverSpecifier, string sessionToken)
        {
            ListUsersResponse response = GetCruiseManager(serverSpecifier)
                .ListUsers(new ServerRequest(sessionToken));
            ValidateResponse(response);
            return response.Users;
        }
        public virtual List<SecurityCheckDiagnostics> DiagnoseSecurityPermissions(IProjectSpecifier projectSpecifier, string sessionToken, string userName)
        {
            DiagnoseSecurityRequest request = new DiagnoseSecurityRequest();
            request.SessionToken = sessionToken;
            request.UserName = userName;
            request.Projects.Add(projectSpecifier.ProjectName);
            DiagnoseSecurityResponse response = GetCruiseManager(projectSpecifier).DiagnoseSecurityPermissions(request);
            ValidateResponse(response);
            return response.Diagnostics;
        }
        public virtual List<SecurityCheckDiagnostics> DiagnoseSecurityPermissions(IServerSpecifier serverSpecifier, string sessionToken, string userName)
        {
            DiagnoseSecurityRequest request = new DiagnoseSecurityRequest();
            request.SessionToken = sessionToken;
            request.UserName = userName;
            request.Projects.Add(string.Empty);
            DiagnoseSecurityResponse response = GetCruiseManager(serverSpecifier).DiagnoseSecurityPermissions(request);
            ValidateResponse(response);
            return response.Diagnostics;
        }
        public virtual List<ParameterBase> ListBuildParameters(IProjectSpecifier projectSpecifier)
        {
            return GetCruiseManager(projectSpecifier)
                .ListBuildParameters(GenerateProjectRequest(projectSpecifier, null))
                .Parameters;
        }
        public virtual List<AuditRecord> ReadAuditRecords(IServerSpecifier serverSpecifier, string sessionToken, int startPosition, int numberOfRecords)
        {
            ReadAuditRequest request = new ReadAuditRequest();
            request.SessionToken = sessionToken;
            request.StartRecord = startPosition;
            request.NumberOfRecords = numberOfRecords;
            ReadAuditResponse response = GetCruiseManager(serverSpecifier).ReadAuditRecords(request);
            ValidateResponse(response);
            return response.Records;
        }
        public virtual List<AuditRecord> ReadAuditRecords(IServerSpecifier serverSpecifier, string sessionToken, int startPosition, int numberOfRecords, AuditFilterBase filter)
        {
            ReadAuditRequest request = new ReadAuditRequest();
            request.SessionToken = sessionToken;
            request.StartRecord = startPosition;
            request.NumberOfRecords = numberOfRecords;
            request.Filter = filter;
            ReadAuditResponse response = GetCruiseManager(serverSpecifier).ReadAuditRecords(request);
            ValidateResponse(response);
            return response.Records;
        }
        private ProjectRequest GenerateProjectRequest(IProjectSpecifier projectSpecifier, string sessionToken)
        {
            ProjectRequest request = new ProjectRequest();
            request.SessionToken = sessionToken;
            request.ProjectName = projectSpecifier.ProjectName;
            return request;
        }
        private void ValidateResponse(Response value)
        {
            if (value.Result == ResponseResult.Failure)
            {
                if (value.ErrorMessages.Count == 1)
                {
                    ErrorMessage message = value.ErrorMessages[0];
                    switch (message.Type)
                    {
                        case "SessionInvalidException":
                            throw new SessionInvalidException(message.Message);
                        case "PermissionDeniedException":
                            throw new PermissionDeniedException(message.Message);
                        default:
                            throw new CruiseControlException(message.Message);
                    }
                }
                else
                {
                    string message = "Request request has failed on the remote server:" + Environment.NewLine +
                        value.ConcatenateErrors();
                    throw new CruiseControlException(message);
                }
                }
            }
        public string ProcessMessage(IServerSpecifier serverSpecifer, string action, string message)
        {
            string response = GetCruiseManager(serverSpecifer)
                .ProcessMessage(action, message);
            return response;
        }
        public virtual long GetFreeDiskSpace(IServerSpecifier serverSpecifier)
        {
            DataResponse response = GetCruiseManager(serverSpecifier).GetFreeDiskSpace(new ServerRequest());
            ValidateResponse(response);
            return Convert.ToInt64(response.Data);
        }
        public virtual RemotingFileTransfer RetrieveFileTransfer(IProjectSpecifier projectSpecifier, string fileName)
            {
            return GetCruiseManager(projectSpecifier).RetrieveFileTransfer(projectSpecifier.ProjectName, fileName);
        }
        public virtual RemotingFileTransfer RetrieveFileTransfer(IBuildSpecifier buildSpecifier, string fileName)
        {
            var logFile = new LogFile(buildSpecifier.BuildName);
            var fullName = string.Format("{0}\\{1}", logFile.Label, fileName);
            RemotingFileTransfer fileTransfer = GetCruiseManager(buildSpecifier)
                .RetrieveFileTransfer(buildSpecifier.ProjectSpecifier.ProjectName, fullName);
            return fileTransfer;
        }
        public virtual PackageDetails[] RetrievePackageList(IProjectSpecifier projectSpecifier)
        {
            var response = GetCruiseManager(projectSpecifier).RetrievePackageList(GenerateProjectRequest(projectSpecifier, null));
            ValidateResponse(response);
            return response.Packages.ToArray();
        }
        public virtual PackageDetails[] RetrievePackageList(IBuildSpecifier buildSpecifier)
        {
            var logFile = new LogFile(buildSpecifier.BuildName);
            var request = new BuildRequest(null, buildSpecifier.ProjectSpecifier.ProjectName);
            request.BuildName = logFile.Label;
            var response = GetCruiseManager(buildSpecifier).RetrievePackageList(request);
            ValidateResponse(response);
            return response.Packages.ToArray();
        }
        public ProjectStatusSnapshot TakeStatusSnapshot(IProjectSpecifier projectSpecifier)
        {
            var response = GetCruiseManager(projectSpecifier).TakeStatusSnapshot(GenerateProjectRequest(projectSpecifier, null));
            ValidateResponse(response);
            return response.Snapshot;
        }
    }
}
