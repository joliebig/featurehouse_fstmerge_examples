using ThoughtWorks.CruiseControl.Remote;
namespace ThoughtWorks.CruiseControl.CCTrayLib.Monitoring
{
 public interface IProjectMonitor : IPollable
 {
  ProjectState ProjectState { get; }
  IntegrationStatus IntegrationStatus { get; }
  ISingleProjectDetail Detail { get; }
  string SummaryStatusString { get; }
  string ProjectIntegratorState { get;}
  bool IsPending { get; }
  bool IsConnected { get;}
  event MonitorBuildOccurredEventHandler BuildOccurred;
  event MonitorPolledEventHandler Polled;
  event MessageEventHandler MessageReceived;
  void ForceBuild();
  void AbortBuild();
  void FixBuild(string fixingUserName);
  void StopProject();
  void StartProject();
  void CancelPending();
 }
}
