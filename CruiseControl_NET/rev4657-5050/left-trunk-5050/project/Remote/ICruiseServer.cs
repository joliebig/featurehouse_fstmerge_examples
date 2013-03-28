using System;
using ThoughtWorks.CruiseControl.Remote.Events;
namespace ThoughtWorks.CruiseControl.Remote
{
 public interface ICruiseServer : IDisposable
 {
  void Start();
  void Stop();
  void Abort();
  void WaitForExit();
  void Start(string project);
  void Stop(string project);
  void CancelPendingRequest(string projectName);
        CruiseServerSnapshot GetCruiseServerSnapshot();
  ICruiseManager CruiseManager { get; }
  ProjectStatus [] GetProjectStatus();
  void ForceBuild(string projectName, string enforcerName);
  void AbortBuild(string projectName, string enforcerName);
  void Request(string projectName, IntegrationRequest request);
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
  void SendMessage(string projectName, Message message);
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
        RemotingFileTransfer RetrieveFileTransfer(string project, string fileName, FileTransferSource source);
    }
}
