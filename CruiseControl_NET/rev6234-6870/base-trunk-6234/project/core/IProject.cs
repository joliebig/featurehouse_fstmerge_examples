using System.Xml;
using ThoughtWorks.CruiseControl.Core.Security;
using ThoughtWorks.CruiseControl.Remote;
using System.Collections.Generic;
namespace ThoughtWorks.CruiseControl.Core
{
 public interface IProject : IIntegratable
 {
  string Name
  {
   get;
  }
        NameValuePair[] LinkedSites { get; set; }
  string Category
  {
   get;
  }
        string Description { get; }
  ITrigger Triggers
  {
   get;
  }
  string WebURL
  {
   get;
  }
  string WorkingDirectory
  {
   get;
  }
  string ArtifactDirectory
  {
   get;
  }
  void Purge(bool purgeWorkingDirectory, bool purgeArtifactDirectory, bool purgeSourceControlEnvironment);
  ExternalLink[] ExternalLinks { get; }
  string Statistics { get; }
        string ModificationHistory { get; }
        string RSSFeed { get; }
  IIntegrationRepository IntegrationRepository { get; }
  string QueueName { get; set; }
  int QueuePriority { get; }
  void Initialize();
  ProjectStatus CreateProjectStatus(IProjectIntegrator integrator);
        ProjectActivity CurrentActivity { get; }
  void AbortRunningBuild();
  void AddMessage(Message message);
  void NotifyPendingState();
  void NotifySleepingState();
        IProjectAuthorisation Security { get; }
        int MaxSourceControlRetries { get; }
        DisplayLevel AskForForceBuildReason { get; }
        bool stopProjectOnReachingMaxSourceControlRetries { get; }
        Sourcecontrol.Common.SourceControlErrorHandlingPolicy SourceControlErrorHandling { get; }
        ProjectInitialState InitialState { get; }
        ProjectStartupMode StartupMode { get; }
        List<PackageDetails> RetrievePackageList();
        List<PackageDetails> RetrievePackageList(string buildName);
    }
}
