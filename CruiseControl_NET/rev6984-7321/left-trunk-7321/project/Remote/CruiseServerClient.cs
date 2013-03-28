using System;
using System.Collections.Generic;
using System.Text;
using ThoughtWorks.CruiseControl.Remote.Messages;
using ThoughtWorks.CruiseControl.Remote.Security;
using ThoughtWorks.CruiseControl.Remote.Parameters;
using System.Xml;
namespace ThoughtWorks.CruiseControl.Remote
{
    public class CruiseServerClient
        : CruiseServerClientBase
    {
        private readonly IServerConnection connection;
        private string targetServer;
        public CruiseServerClient(IServerConnection connection)
        {
            this.connection = connection;
        }
        public override string TargetServer
        {
            get
            {
                if (string.IsNullOrEmpty(targetServer))
                {
                    return connection.ServerName;
                }
                else
                {
                    return targetServer;
                }
            }
            set { targetServer = value; }
        }
        public override bool IsBusy
        {
            get { return connection.IsBusy; }
        }
        public IServerConnection Connection
        {
            get { return connection; }
        }
        public override string Address
        {
            get { return connection.Address; }
        }
        public override ProjectStatus[] GetProjectStatus()
        {
            ProjectStatusResponse resp = ValidateResponse(
                connection.SendMessage("GetProjectStatus", GenerateServerRequest()))
                as ProjectStatusResponse;
            return resp.Projects.ToArray();
        }
        public override void ForceBuild(string projectName)
        {
            Response resp = connection.SendMessage("ForceBuild", GenerateProjectRequest(projectName));
            ValidateResponse(resp);
        }
        public override void ForceBuild(string projectName, List<NameValuePair> parameters)
        {
            if (string.IsNullOrEmpty(projectName)) throw new ArgumentNullException("projectName");
            BuildIntegrationRequest request = new BuildIntegrationRequest(SessionToken, projectName);
            request.BuildValues = parameters;
            request.ServerName = TargetServer;
            Response resp = connection.SendMessage("ForceBuild", request);
            ValidateResponse(resp);
        }
        public override void AbortBuild(string projectName)
        {
            Response resp = connection.SendMessage("AbortBuild", GenerateProjectRequest(projectName));
            ValidateResponse(resp);
        }
        public override void Request(string projectName, IntegrationRequest integrationRequest)
        {
            if (string.IsNullOrEmpty(projectName)) throw new ArgumentNullException("projectName");
            BuildIntegrationRequest request = new BuildIntegrationRequest(SessionToken, projectName);
            request.BuildCondition = integrationRequest.BuildCondition;
            request.ServerName = TargetServer;
            Response resp = connection.SendMessage("ForceBuild", request);
            ValidateResponse(resp);
        }
        public override void StartProject(string projectName)
        {
            Response resp = connection.SendMessage("Start", GenerateProjectRequest(projectName));
            ValidateResponse(resp);
        }
        public override void StopProject(string projectName)
        {
            Response resp = connection.SendMessage("Stop", GenerateProjectRequest(projectName));
            ValidateResponse(resp);
        }
        public override void SendMessage(string projectName, Message message)
        {
            if (string.IsNullOrEmpty(projectName)) throw new ArgumentNullException("projectName");
            MessageRequest request = new MessageRequest();
            request.SessionToken = SessionToken;
            request.ProjectName = projectName;
            request.Message = message.Text;
            request.Kind = message.Kind;
            request.ServerName = TargetServer;
            Response resp = connection.SendMessage("SendMessage", request);
            ValidateResponse(resp);
        }
        public override void WaitForExit(string projectName)
        {
            Response resp = connection.SendMessage("WaitForExit", GenerateProjectRequest(projectName));
            ValidateResponse(resp);
        }
        public override void CancelPendingRequest(string projectName)
        {
            Response resp = connection.SendMessage("CancelPendingRequest", GenerateProjectRequest(projectName));
            ValidateResponse(resp);
        }
        public override CruiseServerSnapshot GetCruiseServerSnapshot()
        {
            SnapshotResponse resp = ValidateResponse(
                connection.SendMessage("GetCruiseServerSnapshot", GenerateServerRequest()))
                as SnapshotResponse;
            return resp.Snapshot;
        }
        public override string GetLatestBuildName(string projectName)
        {
            DataResponse resp = ValidateResponse(
                connection.SendMessage("GetLatestBuildName", GenerateProjectRequest(projectName)))
                as DataResponse;
            return resp.Data;
        }
        public override string[] GetBuildNames(string projectName)
        {
            DataListResponse resp = ValidateResponse(
                connection.SendMessage("GetBuildNames", GenerateProjectRequest(projectName)))
                as DataListResponse;
            return resp.Data.ToArray();
        }
        public override string[] GetMostRecentBuildNames(string projectName, int buildCount)
        {
            if (string.IsNullOrEmpty(projectName)) throw new ArgumentNullException("projectName");
            BuildListRequest request = new BuildListRequest(SessionToken, projectName);
            request.NumberOfBuilds = buildCount;
            request.ServerName = TargetServer;
            DataListResponse resp = ValidateResponse(
                connection.SendMessage("GetMostRecentBuildNames", request))
                as DataListResponse;
            return resp.Data.ToArray();
        }
        public override string GetLog(string projectName, string buildName, bool compress)
        {
            if (string.IsNullOrEmpty(projectName)) throw new ArgumentNullException("projectName");
            BuildRequest request = new BuildRequest(SessionToken, projectName);
            request.BuildName = buildName;
            request.ServerName = TargetServer;
            request.CompressData = compress;
            DataResponse resp = ValidateResponse(
                connection.SendMessage("GetLog", request))
                as DataResponse;
            return resp.Data;
        }
        public override string GetServerLog()
        {
            DataResponse resp = ValidateResponse(
                connection.SendMessage("GetServerLog", GenerateServerRequest()))
                as DataResponse;
            return resp.Data;
        }
        public override string GetServerLog(string projectName)
        {
            DataResponse resp = ValidateResponse(
                connection.SendMessage("GetServerLog", GenerateProjectRequest(projectName)))
                as DataResponse;
            return resp.Data;
        }
        public override string GetServerVersion()
        {
            DataResponse resp = ValidateResponse(
                connection.SendMessage("GetServerVersion", GenerateServerRequest()))
                as DataResponse;
            return resp.Data;
        }
        public override void AddProject(string serializedProject)
        {
            ChangeConfigurationRequest request = new ChangeConfigurationRequest();
            request.SessionToken = SessionToken;
            request.ProjectDefinition = serializedProject;
            request.ServerName = TargetServer;
            Response resp = connection.SendMessage("AddProject", request);
            ValidateResponse(resp);
        }
        public override void DeleteProject(string projectName, bool purgeWorkingDirectory, bool purgeArtifactDirectory, bool purgeSourceControlEnvironment)
        {
            if (string.IsNullOrEmpty(projectName)) throw new ArgumentNullException("projectName");
            ChangeConfigurationRequest request = new ChangeConfigurationRequest(SessionToken, projectName);
            request.PurgeWorkingDirectory = purgeWorkingDirectory;
            request.PurgeArtifactDirectory = purgeArtifactDirectory;
            request.PurgeSourceControlEnvironment = purgeSourceControlEnvironment;
            request.ServerName = TargetServer;
            Response resp = connection.SendMessage("DeleteProject", request);
            ValidateResponse(resp);
        }
        public override string GetProject(string projectName)
        {
            DataResponse resp = ValidateResponse(
                connection.SendMessage("GetProject", GenerateProjectRequest(projectName)))
                as DataResponse;
            return resp.Data;
        }
        public override void UpdateProject(string projectName, string serializedProject)
        {
            if (string.IsNullOrEmpty(projectName)) throw new ArgumentNullException("projectName");
            ChangeConfigurationRequest request = new ChangeConfigurationRequest(SessionToken, projectName);
            request.ProjectDefinition = serializedProject;
            request.ServerName = TargetServer;
            Response resp = connection.SendMessage("UpdateProject", request);
            ValidateResponse(resp);
        }
        public override ExternalLink[] GetExternalLinks(string projectName)
        {
            ExternalLinksListResponse resp = ValidateResponse(
                connection.SendMessage("GetExternalLinks", GenerateProjectRequest(projectName)))
                as ExternalLinksListResponse;
            return resp.ExternalLinks.ToArray();
        }
        public override string GetArtifactDirectory(string projectName)
        {
            DataResponse resp = ValidateResponse(
                connection.SendMessage("GetArtifactDirectory", GenerateProjectRequest(projectName)))
                as DataResponse;
            return resp.Data;
        }
        public override string GetStatisticsDocument(string projectName)
        {
            DataResponse resp = ValidateResponse(
                connection.SendMessage("GetStatisticsDocument", GenerateProjectRequest(projectName)))
                as DataResponse;
            return resp.Data;
        }
        public override string GetModificationHistoryDocument(string projectName)
        {
            DataResponse resp = ValidateResponse(
                connection.SendMessage("GetModificationHistoryDocument", GenerateProjectRequest(projectName)))
                as DataResponse;
            return resp.Data;
        }
        public override string GetRSSFeed(string projectName)
        {
            DataResponse resp = ValidateResponse(
                connection.SendMessage("GetRSSFeed", GenerateProjectRequest(projectName)))
                as DataResponse;
            return resp.Data;
        }
        public override bool Login(List<NameValuePair> Credentials)
        {
            SessionToken = null;
            LoginRequest request = new LoginRequest();
            request.Credentials.AddRange(Credentials);
            request.ServerName = TargetServer;
            LoginResponse resp = ValidateResponse(
                connection.SendMessage("Login", request))
                as LoginResponse;
            ValidateResponse(resp);
            if (!string.IsNullOrEmpty(resp.SessionToken))
            {
                SessionToken = resp.SessionToken;
                return true;
            }
            else
            {
                return false;
            }
        }
        public override void Logout()
        {
            if (SessionToken != null)
            {
                ValidateResponse(
                    connection.SendMessage("Logout",
                        GenerateServerRequest()));
                SessionToken = null;
            }
        }
        public override string GetSecurityConfiguration()
        {
            DataResponse resp = ValidateResponse(
                connection.SendMessage("GetSecurityConfiguration", GenerateServerRequest()))
                as DataResponse;
            return resp.Data;
        }
        public override List<UserDetails> ListUsers()
        {
            ListUsersResponse resp = ValidateResponse(
                connection.SendMessage("ListUsers", GenerateServerRequest()))
                as ListUsersResponse;
            return resp.Users;
        }
        public override List<SecurityCheckDiagnostics> DiagnoseSecurityPermissions(string userName, params string[] projects)
        {
            DiagnoseSecurityRequest request = new DiagnoseSecurityRequest();
            request.SessionToken = SessionToken;
            request.ServerName = TargetServer;
            request.UserName = userName;
            request.ServerName = TargetServer;
            if (projects != null) request.Projects.AddRange(projects);
            DiagnoseSecurityResponse resp = ValidateResponse(
                connection.SendMessage("DiagnoseSecurityPermissions", request))
                as DiagnoseSecurityResponse;
            return resp.Diagnostics;
        }
        public override List<AuditRecord> ReadAuditRecords(int startRecord, int numberOfRecords)
        {
            return ReadAuditRecords(startRecord, numberOfRecords, null);
        }
        public override List<AuditRecord> ReadAuditRecords(int startRecord, int numberOfRecords, AuditFilterBase filter)
        {
            ReadAuditRequest request = new ReadAuditRequest();
            request.SessionToken = SessionToken;
            request.ServerName = TargetServer;
            request.StartRecord = startRecord;
            request.NumberOfRecords = numberOfRecords;
            request.Filter = filter;
            ReadAuditResponse resp = ValidateResponse(
                connection.SendMessage("ReadAuditRecords", request))
                as ReadAuditResponse;
            return resp.Records;
        }
        public override List<ParameterBase> ListBuildParameters(string projectName)
        {
            BuildParametersResponse resp = ValidateResponse(
                connection.SendMessage("ListBuildParameters", GenerateProjectRequest(projectName)))
                as BuildParametersResponse;
            return resp.Parameters;
        }
        public override void ChangePassword(string oldPassword, string newPassword)
        {
            ChangePasswordRequest request = new ChangePasswordRequest();
            request.SessionToken = SessionToken;
            request.ServerName = TargetServer;
            request.OldPassword = oldPassword;
            request.NewPassword = newPassword;
            ValidateResponse(
                connection.SendMessage("ChangePassword", request));
        }
        public override void ResetPassword(string userName, string newPassword)
        {
            ChangePasswordRequest request = new ChangePasswordRequest();
            request.SessionToken = SessionToken;
            request.ServerName = TargetServer;
            request.UserName = userName;
            request.NewPassword = newPassword;
            ValidateResponse(
                connection.SendMessage("ResetPassword", request));
        }
        public override ProjectStatusSnapshot TakeStatusSnapshot(string projectName)
        {
            var request = GenerateProjectRequest(projectName);
            var response = connection.SendMessage("TakeStatusSnapshot", request);
            ValidateResponse(response);
            return (response as StatusSnapshotResponse).Snapshot;
        }
        public override List<PackageDetails> RetrievePackageList(string projectName)
        {
            var request = GenerateProjectRequest(projectName);
            var response = connection.SendMessage("RetrievePackageList", request);
            ValidateResponse(response);
            return (response as ListPackagesResponse).Packages;
        }
        public override IFileTransfer RetrieveFileTransfer(string projectName, string fileName)
        {
            if (string.IsNullOrEmpty(projectName)) throw new ArgumentNullException("projectName");
            var request = new FileTransferRequest(SessionToken, projectName, fileName);
            request.ServerName = TargetServer;
            var response = connection.SendMessage("RetrieveFileTransfer", request);
            ValidateResponse(response);
            return (response as FileTransferResponse).FileTransfer;
        }
        public override long GetFreeDiskSpace()
        {
            var request = GenerateServerRequest();
            var response = connection.SendMessage("GetFreeDiskSpace", request);
            ValidateResponse(response);
            return Convert.ToInt64((response as DataResponse).Data);
        }
        public override string GetLinkedSiteId(string projectName, string siteName)
        {
            if (string.IsNullOrEmpty(projectName)) throw new ArgumentNullException("projectName");
            var request = new ProjectItemRequest(SessionToken, projectName);
            request.ItemName = siteName;
            request.ServerName = TargetServer;
            var response = connection.SendMessage("GetLinkedSiteId", request);
            ValidateResponse(response);
            return (response as DataResponse).Data;
        }
        public override Response ProcessMessage(string action, ServerRequest message)
        {
            Response response = connection.SendMessage(action, message);
            return response;
        }
        public override string ProcessMessage(string action, string message)
        {
            Response response = new Response();
            try
            {
                var messageXml = new XmlDocument();
                messageXml.LoadXml(message);
                var messageType = XmlConversionUtil.FindMessageType(messageXml.DocumentElement.Name);
                if (messageType == null)
                {
                    response.Result = ResponseResult.Failure;
                    response.ErrorMessages.Add(
                        new ErrorMessage(
                            string.Format(
                                "Unable to translate message: '{0}' is unknown",
                                messageXml.DocumentElement.Name)));
                }
                var request = XmlConversionUtil.ConvertXmlToObject(messageType, message);
                response = connection.SendMessage(action, request as ServerRequest);
            }
            catch (Exception error)
            {
                response.Result = ResponseResult.Failure;
                response.ErrorMessages.Add(
                    new ErrorMessage("Unable to process error: " + error.Message));
            }
            return response.ToString();
        }
        public override IEnumerable<string> ListServers()
        {
            var response = ValidateResponse(
                connection.SendMessage("ListServers", GenerateServerRequest()))
                as DataListResponse;
            return response.Data;
        }
        protected override void DoDispose()
        {
            var disposable = connection as IDisposable;
            if (disposable != null)
            {
                disposable.Dispose();
            }
        }
        private ServerRequest GenerateServerRequest()
        {
            ServerRequest request = new ServerRequest(SessionToken);
            request.ServerName = TargetServer;
            request.DisplayName = this.DisplayName;
            return request;
        }
        private ProjectRequest GenerateProjectRequest(string projectName)
        {
            if (string.IsNullOrEmpty(projectName)) throw new ArgumentNullException("projectName");
            ProjectRequest request = new ProjectRequest(SessionToken, projectName);
            request.DisplayName = this.DisplayName;
            request.ServerName = TargetServer;
            return request;
        }
        private Response ValidateResponse(Response response)
        {
            if ((response.Result == ResponseResult.Failure) || (response.Result == ResponseResult.Unknown))
            {
                string message = "Request processing has failed on the remote server:" + Environment.NewLine +
                    response.ConcatenateErrors();
                var errorType = response.ErrorMessages.Count == 1 ?
                    response.ErrorMessages[0].Type :
                    null;
                throw new CommunicationsException(message, errorType);
            }
            return response;
        }
    }
}
