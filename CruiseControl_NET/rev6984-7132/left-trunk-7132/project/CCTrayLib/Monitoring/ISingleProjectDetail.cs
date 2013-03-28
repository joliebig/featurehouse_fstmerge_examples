using System;
using ThoughtWorks.CruiseControl.Remote;
using ThoughtWorks.CruiseControl.CCTrayLib.Configuration;
namespace ThoughtWorks.CruiseControl.CCTrayLib.Monitoring
{
 public interface ISingleProjectDetail
 {
        string ProjectName { get; }
        CCTrayProject Configuration { get; }
        ProjectState ProjectState { get; }
  bool IsConnected { get; }
        string ServerName { get; }
        string Category { get; }
  ProjectActivity Activity { get; }
  string LastBuildLabel { get; }
  DateTime LastBuildTime { get; }
  DateTime NextBuildTime { get; }
  string ProjectIntegratorState { get; }
  string WebURL { get; }
  string CurrentMessage { get; }
        string CurrentBuildStage { get; }
        Message[] Messages { get; }
  TimeSpan EstimatedTimeRemainingOnCurrentBuild { get; }
  Exception ConnectException { get; }
 }
}
