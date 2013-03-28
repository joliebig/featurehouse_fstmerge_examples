using ThoughtWorks.CruiseControl.CCTrayLib.Configuration;
using ThoughtWorks.CruiseControl.Remote;
namespace ThoughtWorks.CruiseControl.CCTrayLib.Monitoring
{
 public interface ICruiseServerManager
 {
  string DisplayName { get; }
        BuildServer Configuration { get; }
        string SessionToken { get; }
  void CancelPendingRequest(string projectName);
        CruiseServerSnapshot GetCruiseServerSnapshot();
        CCTrayProject[] GetProjectList();
        bool Login();
        void Logout();
     }
}
