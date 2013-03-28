using System.Collections.Generic;
using ThoughtWorks.CruiseControl.Core.Reporting.Dashboard.Navigation;
using ThoughtWorks.CruiseControl.Remote;
using ThoughtWorks.CruiseControl.Remote.Parameters;
using ThoughtWorks.CruiseControl.Remote.Security;
using ThoughtWorks.CruiseControl.Remote.Messages;
using System.IO;
using System;
namespace ThoughtWorks.CruiseControl.WebDashboard.ServerConnection
{
 public interface IFarmService
 {
        IBuildSpecifier[] GetMostRecentBuildSpecifiers(IProjectSpecifier projectSpecifier, int buildCount, string sessionToken);
        IBuildSpecifier[] GetBuildSpecifiers(IProjectSpecifier serverSpecifier, string sessionToken);
        void DeleteProject(IProjectSpecifier projectSpecifier, bool purgeWorkingDirectory, bool purgeArtifactDirectory, bool purgeSourceControlEnvironment, string sessionToken);
        string GetServerLog(IServerSpecifier serverSpecifier, string sessionToken);
        string GetServerLog(IProjectSpecifier specifier, string sessionToken);
        void ForceBuild(IProjectSpecifier projectSpecifier, string sessionToken);
        void AbortBuild(IProjectSpecifier projectSpecifier, string sessionToken);
        void Start(IProjectSpecifier projectSpecifier, string sessionToken);
        void Stop(IProjectSpecifier projectSpecifier, string sessionToken);
        void ForceBuild(IProjectSpecifier projectSpecifier, string sessionToken, Dictionary<string, string> parameters);
  ProjectStatusListAndExceptions GetProjectStatusListAndCaptureExceptions(string sessionToken);
        ProjectStatusListAndExceptions GetProjectStatusListAndCaptureExceptions(IServerSpecifier serverSpecifier, string sessionToken);
        ExternalLink[] GetExternalLinks(IProjectSpecifier projectSpecifier, string sessionToken);
  IServerSpecifier[] GetServerSpecifiers();
        IServerSpecifier GetServerConfiguration(string serverName);
  string GetServerVersion(IServerSpecifier serverSpecifier);
        string GetArtifactDirectory(IProjectSpecifier projectSpecifier, string sessionToken);
        string GetStatisticsDocument(IProjectSpecifier projectSpecifier, string sessionToken);
        CruiseServerSnapshotListAndExceptions GetCruiseServerSnapshotListAndExceptions(string sessionToken);
        CruiseServerSnapshotListAndExceptions GetCruiseServerSnapshotListAndExceptions(IServerSpecifier serverSpecifier, string sessionToken);
        string GetModificationHistoryDocument(IProjectSpecifier projectSpecifier, string sessionToken);
        string GetRSSFeed(IProjectSpecifier projectSpecifier);
        string Login(string server, LoginRequest credentials);
        void Logout(string server, string sessionToken);
        void ChangePassword(string server, string sessionToken, string oldPassword, string newPassword);
        void ResetPassword(string server, string sessionToken, string userName, string newPassword);
        string GetServerSecurity(IServerSpecifier serverSpecifier, string sessionToken);
        List<UserDetails> ListAllUsers(IServerSpecifier serverSpecifier, string sessionToken);
        List<SecurityCheckDiagnostics> DiagnoseSecurityPermissions(IProjectSpecifier projectSpecifier, string sessionToken, string userName);
        List<SecurityCheckDiagnostics> DiagnoseSecurityPermissions(IServerSpecifier serverSpecifier, string sessionToken, string userName);
        List<ParameterBase> ListBuildParameters(IProjectSpecifier projectSpecifier, string sessionToken);
        List<AuditRecord> ReadAuditRecords(IServerSpecifier serverSpecifier, string sessionToken, int startPosition, int numberOfRecords);
        List<AuditRecord> ReadAuditRecords(IServerSpecifier serverSpecifier, string sessionToken, int startPosition, int numberOfRecords, AuditFilterBase filter);
        string ProcessMessage(IServerSpecifier serverSpecifer, string action, string message);
        long GetFreeDiskSpace(IServerSpecifier serverSpecifier);
        Action<Stream> RetrieveFileTransfer(IProjectSpecifier projectSpecifier, string fileName, string sessionToken);
        Action<Stream> RetrieveFileTransfer(IBuildSpecifier buildSpecifier, string fileName, string sessionToken);
        PackageDetails[] RetrievePackageList(IProjectSpecifier projectSpecifier, string sessionToken);
        PackageDetails[] RetrievePackageList(IBuildSpecifier buildSpecifier, string sessionToken);
        ProjectStatusSnapshot TakeStatusSnapshot(IProjectSpecifier projectSpecifier, string sessionToken);
        string GetLinkedSiteId(IProjectSpecifier projectSpecifier, string sessionId, string siteName);
    }
}
