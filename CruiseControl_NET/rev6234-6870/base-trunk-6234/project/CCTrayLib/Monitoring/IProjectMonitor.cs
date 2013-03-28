using ThoughtWorks.CruiseControl.Remote;
using ThoughtWorks.CruiseControl.Remote.Parameters;
using System.Collections.Generic;
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
  void ForceBuild(Dictionary<string, string> parameters);
  void AbortBuild();
  void FixBuild(string fixingUserName);
  void StopProject();
  void StartProject();
  void CancelPending();
        ProjectStatusSnapshot RetrieveSnapshot();
        PackageDetails[] RetrievePackageList();
        IFileTransfer RetrieveFileTransfer(string fileName);
        List<ParameterBase> ListBuildParameters();
    }
}
