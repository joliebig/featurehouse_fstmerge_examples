using System;
using System.Collections.Generic;
using System.Runtime.Remoting;
using ThoughtWorks.CruiseControl.Remote.Parameters;
using ThoughtWorks.CruiseControl.Remote.Security;
using System.IO;
namespace ThoughtWorks.CruiseControl.Remote
{
    public class CruiseServerRemotingClient
        : CruiseServerClientBase
    {
        private readonly string serverUri;
        private string targetServer;
        private ICruiseManager manager;
        private string userName = Environment.UserName;
        public CruiseServerRemotingClient(string serverAddress)
        {
            UriBuilder builder = new UriBuilder(serverAddress);
            if (builder.Port == -1) builder.Port = 21234;
            var uri = new Uri(builder.Uri, "/CruiseManager.rem");
            this.serverUri = uri.AbsoluteUri;
            this.manager = (ICruiseManager)RemotingServices.Connect(typeof(ICruiseManager), serverUri);
        }
        public override string TargetServer
        {
            get
            {
                if (string.IsNullOrEmpty(targetServer))
                {
                    var targetUri = new Uri(serverUri);
                    return targetUri.Host;
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
            get { return false; }
        }
        public override string Address
        {
            get { return serverUri; }
        }
        public override ProjectStatus[] GetProjectStatus()
        {
            var response = manager.GetProjectStatus();
            return response;
        }
        public override void ForceBuild(string projectName)
        {
            manager.ForceBuild(projectName, userName);
        }
        public override void ForceBuild(string projectName, List<NameValuePair> parameters)
        {
            ForceBuild(projectName);
        }
        public override void AbortBuild(string projectName)
        {
            manager.AbortBuild(projectName, userName);
        }
        public override void Request(string projectName, IntegrationRequest integrationRequest)
        {
            manager.Request(projectName, integrationRequest);
        }
        public override void StartProject(string project)
        {
            manager.Start(project);
        }
        public override void StopProject(string project)
        {
            manager.Stop(project);
        }
        public override void SendMessage(string projectName, Message message)
        {
            manager.SendMessage(projectName, message);
        }
        public override void WaitForExit(string projectName)
        {
            manager.WaitForExit(projectName);
        }
        public override void CancelPendingRequest(string projectName)
        {
            manager.CancelPendingRequest(projectName);
        }
        public override CruiseServerSnapshot GetCruiseServerSnapshot()
        {
            var response = manager.GetCruiseServerSnapshot();
            return response;
        }
        public override string GetLatestBuildName(string projectName)
        {
            var response = manager.GetLatestBuildName(projectName);
            return response;
        }
        public override string[] GetBuildNames(string projectName)
        {
            var response = manager.GetBuildNames(projectName);
            return response;
        }
        public override string[] GetMostRecentBuildNames(string projectName, int buildCount)
        {
            var response = manager.GetMostRecentBuildNames(projectName, buildCount);
            return response;
        }
        public override string GetLog(string projectName, string buildName)
        {
            var response = manager.GetLog(projectName, buildName);
            return response;
        }
        public override string GetServerLog()
        {
            var response = manager.GetServerLog();
            return response;
        }
        public override string GetServerLog(string projectName)
        {
            var response = manager.GetServerLog(projectName);
            return response;
        }
        public override string GetServerVersion()
        {
            var response = manager.GetServerVersion();
            return response;
        }
        public override void AddProject(string serializedProject)
        {
            manager.AddProject(serializedProject);
        }
        public override void DeleteProject(string projectName, bool purgeWorkingDirectory, bool purgeArtifactDirectory, bool purgeSourceControlEnvironment)
        {
            manager.DeleteProject(projectName, purgeWorkingDirectory, purgeArtifactDirectory, purgeSourceControlEnvironment);
        }
        public override string GetProject(string projectName)
        {
            var response = manager.GetProject(projectName);
            return response;
        }
        public override void UpdateProject(string projectName, string serializedProject)
        {
            manager.UpdateProject(projectName, serializedProject);
        }
        public override ExternalLink[] GetExternalLinks(string projectName)
        {
            var response = manager.GetExternalLinks(projectName);
            return response;
        }
        public override string GetArtifactDirectory(string projectName)
        {
            var response = manager.GetArtifactDirectory(projectName);
            return response;
        }
        public override string GetStatisticsDocument(string projectName)
        {
            var response = manager.GetStatisticsDocument(projectName);
            return response;
        }
        public override string GetModificationHistoryDocument(string projectName)
        {
            var response = manager.GetModificationHistoryDocument(projectName);
            return response;
        }
        public override string GetRSSFeed(string projectName)
        {
            var response = manager.GetRSSFeed(projectName);
            return response;
        }
        public override void TransferFile(string projectName, string fileName, Stream outputStream)
        {
            var fileKey = manager.OpenFile(projectName, fileName);
            string fileData = null;
            try
            {
                while ((fileData = manager.TransferFileData(fileKey)).Length > 0)
                {
                    var data = Convert.FromBase64String(fileData);
                    outputStream.Write(data, 0, data.Length);
                }
            }
            finally
            {
                manager.CloseFile(fileKey);
            }
        }
    }
}
