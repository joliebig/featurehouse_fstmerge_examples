using System.Collections.Generic;
using ThoughtWorks.CruiseControl.Remote.Security;
using ThoughtWorks.CruiseControl.Remote.Parameters;
using ThoughtWorks.CruiseControl.Remote.Messages;
namespace ThoughtWorks.CruiseControl.Remote
{
 public interface ICruiseServerClient
        : IMessageProcessor
    {
  ProjectStatusResponse GetProjectStatus(ServerRequest request);
        Response Start(ProjectRequest request);
        Response Stop(ProjectRequest request);
        Response ForceBuild(ProjectRequest request);
        Response AbortBuild(ProjectRequest request);
        Response CancelPendingRequest(ProjectRequest request);
        Response SendMessage(MessageRequest request);
        Response WaitForExit(ProjectRequest request);
        SnapshotResponse GetCruiseServerSnapshot(ServerRequest request);
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
