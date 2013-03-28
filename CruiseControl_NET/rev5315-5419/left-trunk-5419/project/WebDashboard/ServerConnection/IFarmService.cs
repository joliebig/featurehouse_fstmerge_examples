using System.Collections.Generic;
using ThoughtWorks.CruiseControl.Core.Reporting.Dashboard.Navigation;
using ThoughtWorks.CruiseControl.Remote;
using ThoughtWorks.CruiseControl.Remote.Parameters;
using ThoughtWorks.CruiseControl.Remote.Security;
using ThoughtWorks.CruiseControl.Remote.Messages;
namespace ThoughtWorks.CruiseControl.WebDashboard.ServerConnection
{
 public interface IFarmService
 {
  IBuildSpecifier[] GetMostRecentBuildSpecifiers(IProjectSpecifier projectSpecifier, int buildCount);
  IBuildSpecifier[] GetBuildSpecifiers(IProjectSpecifier serverSpecifier);
  void DeleteProject(IProjectSpecifier projectSpecifier, bool purgeWorkingDirectory, bool purgeArtifactDirectory, bool purgeSourceControlEnvironment);
  string GetServerLog(IServerSpecifier serverSpecifier);
  string GetServerLog(IProjectSpecifier specifier);
  void Start(IProjectSpecifier projectSpecifier);
  void Stop(IProjectSpecifier projectSpecifier);
        void ForceBuild(IProjectSpecifier projectSpecifier, string enforcerName);
        void ForceBuild(IProjectSpecifier projectSpecifier, string sessionToken, string enforcerName);
        void ForceBuild(IProjectSpecifier projectSpecifier, string enforcerName, Dictionary<string, string> parameters);
  void AbortBuild(IProjectSpecifier projectSpecifier, string enforcerName);
        void Start(IProjectSpecifier projectSpecifier, string sessionToken);
        void Stop(IProjectSpecifier projectSpecifier, string sessionToken);
        void ForceBuild(IProjectSpecifier projectSpecifier, string sessionToken, string enforcerName, Dictionary<string, string> parameters);
        void AbortBuild(IProjectSpecifier projectSpecifier, string sessionToken, string enforcerName);
  ProjectStatusListAndExceptions GetProjectStatusListAndCaptureExceptions(string sessionToken);
        ProjectStatusListAndExceptions GetProjectStatusListAndCaptureExceptions(IServerSpecifier serverSpecifier, string sessionToken);
  ExternalLink[] GetExternalLinks(IProjectSpecifier projectSpecifier);
  IServerSpecifier[] GetServerSpecifiers();
  IServerSpecifier GetServerConfiguration(string serverName);
  string GetServerVersion(IServerSpecifier serverSpecifier);
  string GetArtifactDirectory(IProjectSpecifier projectSpecifier);
  string GetStatisticsDocument(IProjectSpecifier projectSpecifier);
        CruiseServerSnapshotListAndExceptions GetCruiseServerSnapshotListAndExceptions();
        CruiseServerSnapshotListAndExceptions GetCruiseServerSnapshotListAndExceptions(IServerSpecifier serverSpecifier);
        string GetModificationHistoryDocument(IProjectSpecifier projectSpecifier);
        string GetRSSFeed(IProjectSpecifier projectSpecifier);
        string Login(string server, LoginRequest credentials);
        void Logout(string server, string sessionToken);
        void ChangePassword(string server, string sessionToken, string oldPassword, string newPassword);
        void ResetPassword(string server, string sessionToken, string userName, string newPassword);
        string GetServerSecurity(IServerSpecifier serverSpecifier, string sessionToken);
        List<UserDetails> ListAllUsers(IServerSpecifier serverSpecifier, string sessionToken);
        List<SecurityCheckDiagnostics> DiagnoseSecurityPermissions(IProjectSpecifier projectSpecifier, string sessionToken, string userName);
        List<SecurityCheckDiagnostics> DiagnoseSecurityPermissions(IServerSpecifier serverSpecifier, string sessionToken, string userName);
        List<ParameterBase> ListBuildParameters(IProjectSpecifier projectSpecifier);
        List<AuditRecord> ReadAuditRecords(IServerSpecifier serverSpecifier, string sessionToken, int startPosition, int numberOfRecords);
        List<AuditRecord> ReadAuditRecords(IServerSpecifier serverSpecifier, string sessionToken, int startPosition, int numberOfRecords, AuditFilterBase filter);
        string ProcessMessage(IServerSpecifier serverSpecifer, string action, string message);
        long GetFreeDiskSpace(IServerSpecifier serverSpecifier);
        RemotingFileTransfer RetrieveFileTransfer(IProjectSpecifier projectSpecifier, string fileName);
        RemotingFileTransfer RetrieveFileTransfer(IBuildSpecifier buildSpecifier, string fileName);
        PackageDetails[] RetrievePackageList(IProjectSpecifier projectSpecifier);
        PackageDetails[] RetrievePackageList(IBuildSpecifier buildSpecifier);
        ProjectStatusSnapshot TakeStatusSnapshot(IProjectSpecifier projectSpecifier);
    }
}
