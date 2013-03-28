using System;
using System.Collections.Generic;
using ThoughtWorks.CruiseControl.Remote.Security;
using ThoughtWorks.CruiseControl.Remote.Events;
using ThoughtWorks.CruiseControl.Remote.Parameters;
namespace ThoughtWorks.CruiseControl.Remote
{
 public interface ICruiseServer : IDisposable
 {
  void Start();
  void Stop();
  void Abort();
  void WaitForExit();
        void Start(string sessionToken, string project);
        void Stop(string sessionToken, string project);
        void CancelPendingRequest(string sessionToken, string projectName);
        CruiseServerSnapshot GetCruiseServerSnapshot();
  ICruiseManager CruiseManager { get; }
  ProjectStatus [] GetProjectStatus();
        void ForceBuild(string sessionToken, string projectName, string enforcerName, Dictionary<string, string> parameters);
  void AbortBuild(string sessionToken,string projectName, string enforcerName);
        void Request(string sessionToken, string projectName, IntegrationRequest request);
  void WaitForExit(string projectName);
  string GetLatestBuildName(string projectName);
  string[] GetBuildNames(string projectName);
  string[] GetMostRecentBuildNames(string projectName, int buildCount);
  string GetLog(string projectName, string buildName);
  string GetServerLog();
  string GetServerLog(string projectName);
  string GetVersion();
  void AddProject(string serializedProject);
  void DeleteProject(string projectName, bool purgeWorkingDirectory, bool purgeArtifactDirectory, bool purgeSourceControlEnvironment);
  string GetProject(string name);
  void UpdateProject(string projectName, string serializedProject);
  ExternalLink[] GetExternalLinks(string projectName);
        void SendMessage(string sessionToken, string projectName, Message message);
  string GetArtifactDirectory(string projectName);
  string GetStatisticsDocument(string projectName);
        string GetModificationHistoryDocument(string projectName);
        string GetRSSFeed(string projectName);
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
        long GetFreeDiskSpace();
        ProjectStatusSnapshot TakeStatusSnapshot(string projectName);
        PackageDetails[] RetrievePackageList(string projectName);
        PackageDetails[] RetrievePackageList(string projectName, string buildLabel);
        RemotingFileTransfer RetrieveFileTransfer(string project, string fileName);
        string Login(ISecurityCredentials credentials);
        void Logout(string sesionToken);
        string GetSecurityConfiguration(string sessionToken);
        List<UserDetails> ListAllUsers(string sessionToken);
        List<SecurityCheckDiagnostics> DiagnoseSecurityPermissions(string sessionToken, string userName, params string[] projectNames);
        List<AuditRecord> ReadAuditRecords(string sessionToken, int startPosition, int numberOfRecords);
        List<AuditRecord> ReadAuditRecords(string sessionToken, int startPosition, int numberOfRecords, IAuditFilter filter);
        void ChangePassword(string sessionToken, string oldPassword, string newPassword);
        void ResetPassword(string sessionToken, string userName, string newPassword);
        List<ParameterBase> ListBuildParameters(string projectName);
    }
}
