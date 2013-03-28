using System;
using System.Collections.Generic;
using System.Text;
using ThoughtWorks.CruiseControl.Remote.Messages;
using ThoughtWorks.CruiseControl.Remote.Security;
using ThoughtWorks.CruiseControl.Remote.Parameters;
namespace ThoughtWorks.CruiseControl.Remote
{
    public class CruiseServerClient
    {
        private readonly IServerConnection connection;
        private string targetServer;
        private string sessionToken;
        public CruiseServerClient(IServerConnection connection)
        {
            this.connection = connection;
        }
        public string TargetServer
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
        public string SessionToken
        {
            get { return sessionToken; }
            set { sessionToken = value; }
        }
        public bool IsBusy
        {
            get { return connection.IsBusy; }
        }
        public IServerConnection Connection
        {
            get { return connection; }
        }
        public ProjectStatus[] GetProjectStatus()
        {
            ProjectStatusResponse resp = ValidateResponse(
                connection.SendMessage("GetProjectStatus", GenerateServerRequest()))
                as ProjectStatusResponse;
            return resp.Projects.ToArray();
        }
        public void ForceBuild(string projectName)
        {
            Response resp = connection.SendMessage("ForceBuild", GenerateProjectRequest(projectName));
            ValidateResponse(resp);
        }
        public void ForceBuild(string projectName, List<NameValuePair> parameters)
        {
            BuildIntegrationRequest request = new BuildIntegrationRequest(sessionToken, projectName);
            request.BuildValues = parameters;
            Response resp = connection.SendMessage("ForceBuild", request);
            ValidateResponse(resp);
        }
        public void AbortBuild(string projectName)
        {
            Response resp = connection.SendMessage("AbortBuild", GenerateProjectRequest(projectName));
            ValidateResponse(resp);
        }
        public void Request(string projectName, IntegrationRequest integrationRequest)
        {
            BuildIntegrationRequest request = new BuildIntegrationRequest(null, projectName);
            request.BuildCondition = integrationRequest.BuildCondition;
            Response resp = connection.SendMessage("ForceBuild", request);
            ValidateResponse(resp);
        }
        public void StartProject(string project)
        {
            Response resp = connection.SendMessage("Start", GenerateProjectRequest(project));
            ValidateResponse(resp);
        }
        public void StopProject(string project)
        {
            Response resp = connection.SendMessage("Stop", GenerateProjectRequest(project));
            ValidateResponse(resp);
        }
        public void SendMessage(string projectName, Message message)
        {
            MessageRequest request = new MessageRequest();
            request.ProjectName = projectName;
            request.Message = message.Text;
            Response resp = connection.SendMessage("SendMessage", request);
            ValidateResponse(resp);
        }
        public void WaitForExit(string projectName)
        {
            Response resp = connection.SendMessage("WaitForExit", GenerateProjectRequest(projectName));
            ValidateResponse(resp);
        }
        public void CancelPendingRequest(string projectName)
        {
            Response resp = connection.SendMessage("CancelPendingRequest", GenerateProjectRequest(projectName));
            ValidateResponse(resp);
        }
        public CruiseServerSnapshot GetCruiseServerSnapshot()
        {
            SnapshotResponse resp = ValidateResponse(
                connection.SendMessage("GetCruiseServerSnapshot", GenerateServerRequest()))
                as SnapshotResponse;
            return resp.Snapshot;
        }
        public string GetLatestBuildName(string projectName)
        {
            DataResponse resp = ValidateResponse(
                connection.SendMessage("GetLatestBuildName", GenerateProjectRequest(projectName)))
                as DataResponse;
            return resp.Data;
        }
        public string[] GetBuildNames(string projectName)
        {
            DataListResponse resp = ValidateResponse(
                connection.SendMessage("GetBuildNames", GenerateProjectRequest(projectName)))
                as DataListResponse;
            return resp.Data.ToArray();
        }
        public string[] GetMostRecentBuildNames(string projectName, int buildCount)
        {
            BuildListRequest request = new BuildListRequest(null, projectName);
            request.NumberOfBuilds = buildCount;
            DataListResponse resp = ValidateResponse(
                connection.SendMessage("GetMostRecentBuildNames", request))
                as DataListResponse;
            return resp.Data.ToArray();
        }
        public string GetLog(string projectName, string buildName)
        {
            BuildRequest request = new BuildRequest(null, projectName);
            request.BuildName = buildName;
            DataResponse resp = ValidateResponse(
                connection.SendMessage("GetLog", request))
                as DataResponse;
            return resp.Data;
        }
        public string GetServerLog()
        {
            DataResponse resp = ValidateResponse(
                connection.SendMessage("GetServerLog", GenerateServerRequest()))
                as DataResponse;
            return resp.Data;
        }
        public string GetServerLog(string projectName)
        {
            DataResponse resp = ValidateResponse(
                connection.SendMessage("GetServerLog", GenerateProjectRequest(projectName)))
                as DataResponse;
            return resp.Data;
        }
        public string GetServerVersion()
        {
            DataResponse resp = ValidateResponse(
                connection.SendMessage("GetServerVersion", GenerateServerRequest()))
                as DataResponse;
            return resp.Data;
        }
        public void AddProject(string serializedProject)
        {
            ChangeConfigurationRequest request = new ChangeConfigurationRequest();
            request.ProjectDefinition = serializedProject;
            Response resp = connection.SendMessage("AddProject", request);
            ValidateResponse(resp);
        }
        public void DeleteProject(string projectName, bool purgeWorkingDirectory, bool purgeArtifactDirectory, bool purgeSourceControlEnvironment)
        {
            ChangeConfigurationRequest request = new ChangeConfigurationRequest(null, projectName);
            request.PurgeWorkingDirectory = purgeWorkingDirectory;
            request.PurgeArtifactDirectory = purgeArtifactDirectory;
            request.PurgeSourceControlEnvironment = purgeSourceControlEnvironment;
            Response resp = connection.SendMessage("DeleteProject", request);
            ValidateResponse(resp);
        }
        public string GetProject(string projectName)
        {
            DataResponse resp = ValidateResponse(
                connection.SendMessage("GetProject", GenerateProjectRequest(projectName)))
                as DataResponse;
            return resp.Data;
        }
        public void UpdateProject(string projectName, string serializedProject)
        {
            ChangeConfigurationRequest request = new ChangeConfigurationRequest(null, projectName);
            request.ProjectDefinition = serializedProject;
            Response resp = connection.SendMessage("UpdateProject", request);
            ValidateResponse(resp);
        }
        public ExternalLink[] GetExternalLinks(string projectName)
        {
            ExternalLinksListResponse resp = ValidateResponse(
                connection.SendMessage("GetExternalLinks", GenerateProjectRequest(projectName)))
                as ExternalLinksListResponse;
            return resp.ExternalLinks.ToArray();
        }
        public string GetArtifactDirectory(string projectName)
        {
            DataResponse resp = ValidateResponse(
                connection.SendMessage("GetArtifactDirectory", GenerateProjectRequest(projectName)))
                as DataResponse;
            return resp.Data;
        }
        public string GetStatisticsDocument(string projectName)
        {
            DataResponse resp = ValidateResponse(
                connection.SendMessage("GetStatisticsDocument", GenerateProjectRequest(projectName)))
                as DataResponse;
            return resp.Data;
        }
        public string GetModificationHistoryDocument(string projectName)
        {
            DataResponse resp = ValidateResponse(
                connection.SendMessage("GetModificationHistoryDocument", GenerateProjectRequest(projectName)))
                as DataResponse;
            return resp.Data;
        }
        public string GetRSSFeed(string projectName)
        {
            DataResponse resp = ValidateResponse(
                connection.SendMessage("GetRSSFeed", GenerateProjectRequest(projectName)))
                as DataResponse;
            return resp.Data;
        }
        public virtual bool Login(List<NameValuePair> Credentials)
        {
            sessionToken=null;
            LoginRequest request = new LoginRequest();
            request.Credentials.AddRange(Credentials);
            request.ServerName = TargetServer;
            LoginResponse resp = ValidateResponse(
                connection.SendMessage("Login", request))
                as LoginResponse;
            ValidateResponse(resp);
            if (!string.IsNullOrEmpty(resp.SessionToken))
            {
                sessionToken = resp.SessionToken;
                return true;
            }
            else
            {
                return false;
            }
        }
        public virtual void Logout()
        {
            sessionToken = null;
            ValidateResponse(
                connection.SendMessage("Logout",
                    GenerateServerRequest()));
        }
        public virtual string GetSecurityConfiguration()
        {
            DataResponse resp = ValidateResponse(
                connection.SendMessage("GetSecurityConfiguration", GenerateServerRequest()))
                as DataResponse;
            return resp.Data;
        }
        public virtual List<UserDetails> ListUsers()
        {
            ListUsersResponse resp = ValidateResponse(
                connection.SendMessage("ListUsers", GenerateServerRequest()))
                as ListUsersResponse;
            return resp.Users;
        }
        public virtual List<SecurityCheckDiagnostics> DiagnoseSecurityPermissions(string userName, params string[] projects)
        {
            DiagnoseSecurityRequest request = new DiagnoseSecurityRequest();
            request.ServerName = TargetServer;
            request.UserName = userName;
            if (projects != null) request.Projects.AddRange(projects);
            DiagnoseSecurityResponse resp = ValidateResponse(
                connection.SendMessage("DiagnoseSecurityPermissions", request))
                as DiagnoseSecurityResponse;
            return resp.Diagnostics;
        }
        public virtual List<AuditRecord> ReadAuditRecords(int startRecord, int numberOfRecords)
        {
            return ReadAuditRecords(startRecord, numberOfRecords, null);
        }
        public virtual List<AuditRecord> ReadAuditRecords(int startRecord, int numberOfRecords, AuditFilterBase filter)
        {
            ReadAuditRequest request = new ReadAuditRequest();
            request.ServerName = TargetServer;
            request.StartRecord = startRecord;
            request.NumberOfRecords = numberOfRecords;
            request.Filter = filter;
            ReadAuditResponse resp = ValidateResponse(
                connection.SendMessage("ReadAuditRecords", request))
                as ReadAuditResponse;
            return resp.Records;
        }
        public virtual List<ParameterBase> ListBuildParameters(string projectName)
        {
            BuildParametersResponse resp = ValidateResponse(
                connection.SendMessage("ListBuildParameters", GenerateProjectRequest(projectName)))
                as BuildParametersResponse;
            return resp.Parameters;
        }
        public virtual List<string> RetrieveCapacities()
        {
            DataListResponse resp = ValidateResponse(
                connection.SendMessage("RetrieveCapacities", GenerateServerRequest()))
                as DataListResponse;
            return resp.Data;
        }
        public virtual void ChangePassword(string oldPassword, string newPassword)
        {
            ChangePasswordRequest request = new ChangePasswordRequest();
            request.ServerName = TargetServer;
            request.OldPassword = oldPassword;
            request.NewPassword = newPassword;
            ValidateResponse(
                connection.SendMessage("ChangePassword", request));
        }
        public virtual void ResetPassword(string userName, string newPassword)
        {
            ChangePasswordRequest request = new ChangePasswordRequest();
            request.ServerName = TargetServer;
            request.UserName = userName;
            request.NewPassword = newPassword;
            ValidateResponse(
                connection.SendMessage("ResetPassword", request));
        }
        private ServerRequest GenerateServerRequest()
        {
            ServerRequest request = new ServerRequest(sessionToken);
            request.ServerName = TargetServer;
            return request;
        }
        private ProjectRequest GenerateProjectRequest(string projectName)
        {
            ProjectRequest request = new ProjectRequest(sessionToken, projectName);
            request.ServerName = TargetServer;
            return request;
        }
        private Response ValidateResponse(Response response)
        {
            if (response.Result == ResponseResult.Failure)
            {
                string message = "Request processing has failed on the remote server:" + Environment.NewLine +
                    response.ConcatenateErrors();
                throw new CommunicationsException(message);
            }
            return response;
        }
    }
}
