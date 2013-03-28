using System;
using ThoughtWorks.CruiseControl.CCTrayLib.Configuration;
using ThoughtWorks.CruiseControl.Remote;
namespace ThoughtWorks.CruiseControl.CCTrayLib.Monitoring
{
    public interface ISingleServerMonitor : IServerMonitor, IProjectStatusRetriever
 {
  string ServerUrl { get; }
  string DisplayName { get; }
  BuildServerTransport Transport { get; }
        string SessionToken { get; }
  void CancelPendingRequest(string projectName);
  CruiseServerSnapshot CruiseServerSnapshot { get; }
  bool IsConnected { get; }
  Exception ConnectException { get; }
        void Start();
        void Stop();
        bool RefreshSession();
        event ServerSnapshotChangedEventHandler ServerSnapshotChanged;
    }
}
