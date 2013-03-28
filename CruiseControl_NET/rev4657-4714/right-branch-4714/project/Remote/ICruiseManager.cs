using System.Collections.Generic;
using ThoughtWorks.CruiseControl.Remote.Security;
namespace ThoughtWorks.CruiseControl.Remote
{
    public interface ICruiseManager
    {
        ProjectStatus[] GetProjectStatus();
        void ForceBuild(string projectName, string enforcerName);
        void AbortBuild(string projectName, string enforcerName);
        void Request(string projectName, IntegrationRequest integrationRequest);
        void CancelPendingRequest(string projectName);
        void Start(string project);
        void Stop(string project);
        void SendMessage(string projectName, Message message);
        void ForceBuild(string sessionToken, string projectName, string enforcerName);
        void AbortBuild(string sessionToken, string projectName, string enforcerName);
        void Request(string sessionToken, string projectName, IntegrationRequest integrationRequest);
        void Start(string sessionToken, string project);
        void Stop(string sessionToken, string project);
        void SendMessage(string sessionToken, string projectName, Message message);
        void CancelPendingRequest(string sessionToken, string projectName);
        void WaitForExit(string projectName);
        CruiseServerSnapshot GetCruiseServerSnapshot();
        string GetLatestBuildName(string projectName);
        string[] GetBuildNames(string projectName);
        string[] GetMostRecentBuildNames(string projectName, int buildCount);
        string GetLog(string projectName, string buildName);
        string GetServerLog();
        string GetServerLog(string projectName);
        string GetServerVersion();
        void AddProject(string serializedProject);
        void DeleteProject(string projectName, bool purgeWorkingDirectory, bool purgeArtifactDirectory, bool purgeSourceControlEnvironment);
        string GetProject(string projectName);
        void UpdateProject(string projectName, string serializedProject);
        ExternalLink[] GetExternalLinks(string projectName);
        string GetArtifactDirectory(string projectName);
        string GetStatisticsDocument(string projectName);
        string GetModificationHistoryDocument(string projectName);
        string GetRSSFeed(string projectName);
        string Login(ISecurityCredentials credentials);
        void Logout(string sesionToken);
        bool ValidateSession(string sessionToken);
        string GetSecurityConfiguration(string sessionToken);
        List<UserDetails> ListAllUsers(string sessionToken);
        List<SecurityCheckDiagnostics> DiagnoseSecurityPermissions(string sessionToken, string userName, params string[] projectNames);
        List<AuditRecord> ReadAuditRecords(string sessionToken, int startPosition, int numberOfRecords);
        List<AuditRecord> ReadAuditRecords(string sessionToken, int startPosition, int numberOfRecords, IAuditFilter filter);
        void ChangePassword(string sessionToken, string oldPassword, string newPassword);
        void ResetPassword(string sessionToken, string userName, string newPassword);
    }
}
