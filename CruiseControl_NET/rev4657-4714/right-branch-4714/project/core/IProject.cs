using System.Xml;
using ThoughtWorks.CruiseControl.Core.Security;
using ThoughtWorks.CruiseControl.Remote;
namespace ThoughtWorks.CruiseControl.Core
{
 public interface IProject : IIntegratable
 {
  string Name
  {
   get;
  }
  string Category
  {
   get;
  }
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
  XmlDocument Statistics { get; }
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
        int MaxAmountOfSourceControlExceptions { get; }
        ProjectInitialState StartupState { get; }
    }
}
