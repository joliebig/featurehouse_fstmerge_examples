using ThoughtWorks.CruiseControl.CCTrayLib.Configuration;
using ThoughtWorks.CruiseControl.Remote;
namespace ThoughtWorks.CruiseControl.CCTrayLib.Monitoring
{
 public interface ICruiseServerManager
 {
  string ServerUrl { get; }
  string DisplayName { get; }
  BuildServerTransport Transport { get; }
  void CancelPendingRequest(string projectName);
        CruiseServerSnapshot GetCruiseServerSnapshot();
     }
}
