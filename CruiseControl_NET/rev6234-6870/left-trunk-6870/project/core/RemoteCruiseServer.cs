using System;
using System.Runtime.Remoting;
using System.Runtime.Remoting.Channels;
using ThoughtWorks.CruiseControl.Core.Util;
using ThoughtWorks.CruiseControl.Remote;
using ThoughtWorks.CruiseControl.Remote.Security;
using System.Collections.Generic;
using ThoughtWorks.CruiseControl.Remote.Parameters;
using ThoughtWorks.CruiseControl.Remote.Messages;
namespace ThoughtWorks.CruiseControl.Core
{
    public class RemoteCruiseServer
        : CruiseServerEventsBase, ICruiseServer, IDisposable
    {
        public const string ManagerUri = "CruiseManager.rem";
        public const string DefaultManagerUri = "tcp://localhost:21234/" + ManagerUri;
        public const string ServerClientUri = "CruiseServerClient.rem";
        public const string DefaultServerClientUri = "tcp://localhost:21234/" + ServerClientUri;
        private ICruiseServer server;
        private bool disposed;
        private IExecutionEnvironment environment = new ExecutionEnvironment();
        public RemoteCruiseServer(ICruiseServer server, string remotingConfigurationFile)
        {
            this.server = server;
            RemotingConfiguration.Configure(remotingConfigurationFile, false);
            RegisterManagerForRemoting();
            RegisterServerClientForRemoting();
        }
        private void RegisterManagerForRemoting()
        {
            MarshalByRefObject marshalByRef = (MarshalByRefObject)server.CruiseManager;
            RemotingServices.Marshal(marshalByRef, ManagerUri);
            foreach (IChannel channel in ChannelServices.RegisteredChannels)
            {
                Log.Info("Registered channel: " + channel.ChannelName);
                if (environment.IsRunningOnWindows)
                {
                    if (channel is IChannelReceiver)
                    {
                        foreach (string url in ((IChannelReceiver)channel).GetUrlsForUri(ManagerUri))
                        {
                            Log.Info("CruiseManager: Listening on url: " + url);
                        }
                    }
                }
            }
        }
        private void RegisterServerClientForRemoting()
        {
            MarshalByRefObject marshalByRef = (MarshalByRefObject)server.CruiseServerClient;
            RemotingServices.Marshal(marshalByRef, ServerClientUri);
            foreach (IChannel channel in ChannelServices.RegisteredChannels)
            {
                Log.Info("Registered channel: " + channel.ChannelName);
                if (environment.IsRunningOnWindows)
                {
                    if (channel is IChannelReceiver)
                    {
                        foreach (string url in ((IChannelReceiver)channel).GetUrlsForUri(ServerClientUri))
                        {
                            Log.Info("CruiseServerClient: Listening on url: " + url);
                        }
                    }
                }
            }
        }
        ~RemoteCruiseServer()
        {
            try
            {
                this.Dispose();
            }
            catch
            {
            }
        }
        public void Dispose()
        {
            lock (this)
            {
                if (disposed) return;
                disposed = true;
            }
            Log.Info("Disconnecting remote server: ");
            RemotingServices.Disconnect((MarshalByRefObject)server.CruiseManager);
            RemotingServices.Disconnect((MarshalByRefObject)server.CruiseServerClient);
            foreach (IChannel channel in ChannelServices.RegisteredChannels)
            {
                Log.Info("Unregistering channel: " + channel.ChannelName);
                ChannelServices.UnregisterChannel(channel);
            }
            server.Dispose();
        }
        public virtual void Abort()
        {
            server.Abort();
        }
        public virtual void Start()
        {
            server.Start();
        }
        public virtual Response Start(ProjectRequest request)
        {
            return server.Start(request);
        }
        public virtual void Stop()
        {
            server.Stop();
        }
        public virtual Response Stop(ProjectRequest request)
        {
            return server.Stop(request);
        }
        public virtual Response CancelPendingRequest(ProjectRequest request)
        {
            return server.CancelPendingRequest(request);
        }
        public virtual Response SendMessage(MessageRequest request)
        {
            return server.SendMessage(request);
        }
        public virtual SnapshotResponse GetCruiseServerSnapshot(ServerRequest request)
        {
            return server.GetCruiseServerSnapshot(request);
        }
        [Obsolete("Use CruiseServerClient instead")]
        public virtual ICruiseManager CruiseManager
        {
            get { return server.CruiseManager; }
        }
        public virtual ICruiseServerClient CruiseServerClient
        {
            get { return server.CruiseServerClient; }
        }
        public virtual ProjectStatusResponse GetProjectStatus(ServerRequest request)
        {
            return server.GetProjectStatus(request);
        }
        public virtual Response ForceBuild(ProjectRequest request)
        {
            return server.ForceBuild(request);
        }
        public virtual Response AbortBuild(ProjectRequest request)
        {
            return server.AbortBuild(request);
        }
        public virtual void WaitForExit()
        {
            server.WaitForExit();
        }
        public virtual Response WaitForExit(ProjectRequest request)
        {
            return server.WaitForExit(request);
        }
        public virtual DataResponse GetLatestBuildName(ProjectRequest request)
        {
            return server.GetLatestBuildName(request);
        }
        public virtual DataListResponse GetBuildNames(ProjectRequest request)
        {
            return server.GetBuildNames(request);
        }
        public virtual DataListResponse GetMostRecentBuildNames(BuildListRequest request)
        {
            return server.GetMostRecentBuildNames(request);
        }
        public virtual DataResponse GetLog(BuildRequest request)
        {
            return server.GetLog(request);
        }
        public virtual DataResponse GetServerLog(ServerRequest request)
        {
            return server.GetServerLog(request);
        }
        public virtual DataResponse GetServerVersion(ServerRequest request)
        {
            return server.GetServerVersion(request);
        }
        public virtual Response AddProject(ChangeConfigurationRequest request)
        {
            return server.AddProject(request);
        }
        public virtual Response DeleteProject(ChangeConfigurationRequest request)
        {
            return server.DeleteProject(request);
        }
        public virtual Response UpdateProject(ChangeConfigurationRequest request)
        {
            return server.UpdateProject(request);
        }
        public virtual DataResponse GetProject(ProjectRequest request)
        {
            return server.GetProject(request);
        }
        public virtual ExternalLinksListResponse GetExternalLinks(ProjectRequest request)
        {
            return server.GetExternalLinks(request);
        }
        public virtual DataResponse GetArtifactDirectory(ProjectRequest request)
        {
            return server.GetArtifactDirectory(request);
        }
        public virtual DataResponse GetStatisticsDocument(ProjectRequest request)
        {
            return server.GetStatisticsDocument(request);
        }
        public virtual DataResponse GetModificationHistoryDocument(ProjectRequest request)
        {
            return server.GetModificationHistoryDocument(request);
        }
        public virtual DataResponse GetRSSFeed(ProjectRequest request)
        {
            return server.GetRSSFeed(request);
        }
        public virtual LoginResponse Login(LoginRequest request)
        {
            return server.Login(request);
        }
        public virtual Response Logout(ServerRequest request)
        {
            return server.Logout(request);
        }
        public virtual DataResponse GetSecurityConfiguration(ServerRequest request)
        {
            return server.GetSecurityConfiguration(request);
        }
        public virtual ListUsersResponse ListUsers(ServerRequest request)
        {
            return server.ListUsers(request);
        }
        public virtual DiagnoseSecurityResponse DiagnoseSecurityPermissions(DiagnoseSecurityRequest request)
        {
            return server.DiagnoseSecurityPermissions(request);
        }
        public virtual ReadAuditResponse ReadAuditRecords(ReadAuditRequest request)
        {
            return server.ReadAuditRecords(request);
        }
        public virtual BuildParametersResponse ListBuildParameters(ProjectRequest request)
        {
            return server.ListBuildParameters(request);
        }
        public virtual Response ChangePassword(ChangePasswordRequest request)
        {
            return server.ChangePassword(request);
        }
        public virtual Response ResetPassword(ChangePasswordRequest request)
        {
            return server.ResetPassword(request);
        }
        public virtual DataResponse GetFreeDiskSpace(ServerRequest request)
        {
            return server.GetFreeDiskSpace(request);
        }
        public virtual StatusSnapshotResponse TakeStatusSnapshot(ProjectRequest request)
        {
            return server.TakeStatusSnapshot(request);
        }
        public virtual ListPackagesResponse RetrievePackageList(ProjectRequest request)
        {
            return server.RetrievePackageList(request);
        }
        public virtual FileTransferResponse RetrieveFileTransfer(FileTransferRequest request)
        {
            return server.RetrieveFileTransfer(request);
        }
        public virtual object RetrieveService(Type serviceType)
        {
            return server.RetrieveService(serviceType);
        }
        public virtual void AddService(Type serviceType, object service)
        {
            server.AddService(serviceType, service);
        }
        public virtual DataResponse GetLinkedSiteId(ProjectItemRequest request)
        {
            return server.GetLinkedSiteId(request);
        }
    }
}
