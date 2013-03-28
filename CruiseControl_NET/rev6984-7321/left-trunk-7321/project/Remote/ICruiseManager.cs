using System;
namespace ThoughtWorks.CruiseControl.Remote
{
    [Obsolete("Use ICruiseServerClient instead")]
 public interface ICruiseManager
 {
  ProjectStatus [] GetProjectStatus();
  void ForceBuild(string projectName, string enforcerName );
  void AbortBuild(string projectName, string enforcerName);
  void Request(string projectName, IntegrationRequest integrationRequest);
  void Start(string project);
  void Stop(string project);
  void SendMessage(string projectName, Message message);
  void WaitForExit(string projectName);
  void CancelPendingRequest(string projectName);
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
        long GetFreeDiskSpace();
        RemotingFileTransfer RetrieveFileTransfer(string project, string fileName);
    }
}
