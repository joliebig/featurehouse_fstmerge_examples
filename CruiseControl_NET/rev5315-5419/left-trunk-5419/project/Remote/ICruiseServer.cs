using System;
using System.Collections.Generic;
using ThoughtWorks.CruiseControl.Remote.Security;
using ThoughtWorks.CruiseControl.Remote.Events;
using ThoughtWorks.CruiseControl.Remote.Parameters;
using ThoughtWorks.CruiseControl.Remote.Messages;
namespace ThoughtWorks.CruiseControl.Remote
{
 public interface ICruiseServer : IDisposable
 {
        void Abort();
  void Start();
        Response Start(ProjectRequest request);
  void Stop();
        Response Stop(ProjectRequest request);
        Response CancelPendingRequest(ProjectRequest request);
        Response SendMessage(MessageRequest request);
        SnapshotResponse GetCruiseServerSnapshot(ServerRequest request);
        [Obsolete("Use CruiseServerClient instead")]
  ICruiseManager CruiseManager { get; }
        ICruiseServerClient CruiseServerClient { get; }
        ProjectStatusResponse GetProjectStatus(ServerRequest request);
        Response ForceBuild(ProjectRequest request);
        Response AbortBuild(ProjectRequest request);
        void WaitForExit();
        Response WaitForExit(ProjectRequest request);
        DataResponse GetLatestBuildName(ProjectRequest request);
        DataListResponse GetBuildNames(ProjectRequest request);
        DataListResponse GetMostRecentBuildNames(BuildListRequest request);
        DataResponse GetLog(BuildRequest request);
        DataResponse GetServerLog(ServerRequest request);
        DataResponse GetServerVersion(ServerRequest request);
        Response AddProject(ChangeConfigurationRequest request);
        Response DeleteProject(ChangeConfigurationRequest request);
        Response UpdateProject(ChangeConfigurationRequest request);
        DataResponse GetProject(ProjectRequest request);
        ExternalLinksListResponse GetExternalLinks(ProjectRequest request);
        DataResponse GetArtifactDirectory(ProjectRequest request);
        DataResponse GetStatisticsDocument(ProjectRequest request);
        DataResponse GetModificationHistoryDocument(ProjectRequest request);
        DataResponse GetRSSFeed(ProjectRequest request);
        event EventHandler<CancelProjectEventArgs> ProjectStarting;
        event EventHandler<ProjectEventArgs> ProjectStarted;
        event EventHandler<CancelProjectEventArgs> ProjectStopping;
        event EventHandler<ProjectEventArgs> ProjectStopped;
        event EventHandler<CancelProjectEventArgs<string> > ForceBuildReceived;
        event EventHandler<ProjectEventArgs<string> > ForceBuildProcessed;
        event EventHandler<CancelProjectEventArgs<string> > AbortBuildReceived;
        event EventHandler<ProjectEventArgs<string> > AbortBuildProcessed;
        event EventHandler<CancelProjectEventArgs<Message> > SendMessageReceived;
        event EventHandler<ProjectEventArgs<Message> > SendMessageProcessed;
        event EventHandler<IntegrationStartedEventArgs> IntegrationStarted;
        event EventHandler<IntegrationCompletedEventArgs> IntegrationCompleted;
        LoginResponse Login(LoginRequest request);
        Response Logout(ServerRequest request);
        DataResponse GetSecurityConfiguration(ServerRequest request);
        ListUsersResponse ListUsers(ServerRequest request);
        DiagnoseSecurityResponse DiagnoseSecurityPermissions(DiagnoseSecurityRequest request);
        ReadAuditResponse ReadAuditRecords(ReadAuditRequest request);
        BuildParametersResponse ListBuildParameters(ProjectRequest request);
        Response ChangePassword(ChangePasswordRequest request);
        Response ResetPassword(ChangePasswordRequest request);
        DataResponse GetFreeDiskSpace(ServerRequest request);
        StatusSnapshotResponse TakeStatusSnapshot(ProjectRequest request);
        ListPackagesResponse RetrievePackageList(ProjectRequest request);
        RemotingFileTransfer RetrieveFileTransfer(string project, string fileName);
    }
}
