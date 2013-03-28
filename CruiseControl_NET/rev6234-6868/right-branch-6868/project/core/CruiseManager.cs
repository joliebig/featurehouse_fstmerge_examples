using System;
using System.Collections.Generic;
using ThoughtWorks.CruiseControl.Core.Util;
using ThoughtWorks.CruiseControl.Remote;
using ThoughtWorks.CruiseControl.Remote.Security;
using ThoughtWorks.CruiseControl.Remote.Parameters;
using ThoughtWorks.CruiseControl.Remote.Messages;
namespace ThoughtWorks.CruiseControl.Core
{
    [Obsolete("Use ICruiseServerClient instead")]
 public class CruiseManager : MarshalByRefObject, ICruiseManager
 {
  private readonly ICruiseServer cruiseServer;
  public CruiseManager(ICruiseServer cruiseServer)
  {
   this.cruiseServer = cruiseServer;
  }
        public override object InitializeLifetimeService()
  {
            return null;
  }
        public ProjectStatus[] GetProjectStatus()
        {
            ProjectStatusResponse resp = cruiseServer.GetProjectStatus(GenerateServerRequest());
            ValidateResponse(resp);
            return resp.Projects.ToArray();
        }
        public void ForceBuild(string projectName, string enforcerName)
        {
            Response resp = cruiseServer.ForceBuild(GenerateProjectRequest(projectName));
            ValidateResponse(resp);
        }
        public void AbortBuild(string projectName, string enforcerName)
  {
            Response resp = cruiseServer.AbortBuild(GenerateProjectRequest(projectName));
            ValidateResponse(resp);
  }
  public void Request(string projectName, IntegrationRequest integrationRequest)
  {
            BuildIntegrationRequest request = new BuildIntegrationRequest(null, projectName);
            request.BuildCondition = integrationRequest.BuildCondition;
            Response resp = cruiseServer.ForceBuild(request);
            ValidateResponse(resp);
  }
  public void Start(string project)
  {
            Response resp = cruiseServer.Start(GenerateProjectRequest(project));
            ValidateResponse(resp);
  }
  public void Stop(string project)
  {
            Response resp = cruiseServer.Stop(GenerateProjectRequest(project));
            ValidateResponse(resp);
  }
  public void SendMessage(string projectName, Message message)
  {
            MessageRequest request = new MessageRequest();
            request.ProjectName = projectName;
            request.Message = message.Text;
            Response resp = cruiseServer.SendMessage(request);
            ValidateResponse(resp);
        }
        public void WaitForExit(string projectName)
        {
            Response resp = cruiseServer.WaitForExit(GenerateProjectRequest(projectName));
            ValidateResponse(resp);
  }
  public void CancelPendingRequest(string projectName)
  {
            Response resp = cruiseServer.CancelPendingRequest(GenerateProjectRequest(projectName));
            ValidateResponse(resp);
  }
        public CruiseServerSnapshot GetCruiseServerSnapshot()
  {
            SnapshotResponse resp = cruiseServer.GetCruiseServerSnapshot(GenerateServerRequest());
            ValidateResponse(resp);
            return resp.Snapshot;
  }
  public string GetLatestBuildName(string projectName)
  {
            DataResponse resp = cruiseServer.GetLatestBuildName(GenerateProjectRequest(projectName));
            ValidateResponse(resp);
            return resp.Data;
  }
  public string[] GetBuildNames(string projectName)
  {
            DataListResponse resp = cruiseServer.GetBuildNames(GenerateProjectRequest(projectName));
            ValidateResponse(resp);
            return resp.Data.ToArray();
  }
  public string[] GetMostRecentBuildNames(string projectName, int buildCount)
  {
            BuildListRequest request = new BuildListRequest(null, projectName);
            request.NumberOfBuilds = buildCount;
            DataListResponse resp = cruiseServer.GetMostRecentBuildNames(request);
            ValidateResponse(resp);
            return resp.Data.ToArray();
  }
  public string GetLog(string projectName, string buildName)
  {
            BuildRequest request = new BuildRequest(null, projectName);
            request.BuildName = buildName;
            DataResponse resp = cruiseServer.GetLog(request);
            ValidateResponse(resp);
            return resp.Data;
  }
  public string GetServerLog()
  {
            DataResponse resp = cruiseServer.GetServerLog(GenerateServerRequest());
            ValidateResponse(resp);
            return resp.Data;
  }
  public string GetServerLog(string projectName)
  {
            DataResponse resp = cruiseServer.GetServerLog(GenerateProjectRequest(projectName));
            ValidateResponse(resp);
            return resp.Data;
        }
        public string GetServerVersion()
        {
            DataResponse resp = cruiseServer.GetServerVersion(GenerateServerRequest());
            ValidateResponse(resp);
            return resp.Data;
  }
  public void AddProject(string serializedProject)
  {
            ChangeConfigurationRequest request = new ChangeConfigurationRequest();
            request.ProjectDefinition = serializedProject;
            Response resp = cruiseServer.AddProject(request);
            ValidateResponse(resp);
  }
  public void DeleteProject(string projectName, bool purgeWorkingDirectory, bool purgeArtifactDirectory, bool purgeSourceControlEnvironment)
  {
            ChangeConfigurationRequest request = new ChangeConfigurationRequest(null, projectName);
            request.PurgeWorkingDirectory = purgeWorkingDirectory;
            request.PurgeArtifactDirectory = purgeArtifactDirectory;
            request.PurgeSourceControlEnvironment = purgeSourceControlEnvironment;
            Response resp = cruiseServer.DeleteProject(request);
            ValidateResponse(resp);
  }
  public string GetProject(string projectName)
  {
            DataResponse resp = cruiseServer.GetProject(GenerateProjectRequest(projectName));
            ValidateResponse(resp);
            return resp.Data;
  }
  public void UpdateProject(string projectName, string serializedProject)
  {
            ChangeConfigurationRequest request = new ChangeConfigurationRequest(null, projectName);
            request.ProjectDefinition = serializedProject;
            Response resp = cruiseServer.UpdateProject(request);
            ValidateResponse(resp);
  }
  public ExternalLink[] GetExternalLinks(string projectName)
  {
            ExternalLinksListResponse resp = cruiseServer.GetExternalLinks(GenerateProjectRequest(projectName));
            ValidateResponse(resp);
            return resp.ExternalLinks.ToArray();
  }
  public string GetArtifactDirectory(string projectName)
  {
            DataResponse resp = cruiseServer.GetArtifactDirectory(GenerateProjectRequest(projectName));
            ValidateResponse(resp);
            return resp.Data;
  }
  public string GetStatisticsDocument(string projectName)
  {
            DataResponse resp = cruiseServer.GetStatisticsDocument(GenerateProjectRequest(projectName));
            ValidateResponse(resp);
            return resp.Data;
  }
        public string GetModificationHistoryDocument(string projectName)
        {
            DataResponse resp = cruiseServer.GetModificationHistoryDocument(GenerateProjectRequest(projectName));
            ValidateResponse(resp);
            return resp.Data;
        }
        public string GetRSSFeed(string projectName)
        {
            DataResponse resp = cruiseServer.GetRSSFeed(GenerateProjectRequest(projectName));
            ValidateResponse(resp);
            return resp.Data;
  }
        public long GetFreeDiskSpace()
        {
            DataResponse resp = cruiseServer.GetFreeDiskSpace(GenerateServerRequest());
            ValidateResponse(resp);
            return Convert.ToInt64(resp.Data);
        }
        public virtual RemotingFileTransfer RetrieveFileTransfer(string project, string fileName)
        {
            var request = new FileTransferRequest();
            request.ProjectName = project;
            request.FileName = fileName;
            var response = cruiseServer.RetrieveFileTransfer(request);
            ValidateResponse(response);
            return response.FileTransfer as RemotingFileTransfer;
        }
        private ServerRequest GenerateServerRequest()
        {
            ServerRequest request = new ServerRequest();
            return request;
        }
        private ProjectRequest GenerateProjectRequest(string projectName)
        {
            ProjectRequest request = new ProjectRequest();
            request.ProjectName = projectName;
            return request;
        }
        private void ValidateResponse(Response response)
        {
            if (response.Result == ResponseResult.Failure)
        {
                string message = "Request request has failed on the remote server:" + Environment.NewLine +
                    response.ConcatenateErrors();
                throw new CruiseControlException(message);
        }
        }
    }
}
