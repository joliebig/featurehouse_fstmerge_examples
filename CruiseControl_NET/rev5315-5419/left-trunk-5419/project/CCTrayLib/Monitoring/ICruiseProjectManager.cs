using ThoughtWorks.CruiseControl.Remote.Parameters;
using System.Collections.Generic;
using ThoughtWorks.CruiseControl.Remote;
namespace ThoughtWorks.CruiseControl.CCTrayLib.Monitoring
{
 public interface ICruiseProjectManager
 {
        void ForceBuild(string sessionToken, Dictionary<string, string> parameters);
  void FixBuild(string sessionToken, string fixingUserName);
        void AbortBuild(string sessionToken);
        void StopProject(string sessionToken);
        void StartProject(string sessionToken);
        void CancelPendingRequest(string sessionToken);
  string ProjectName { get; }
        ProjectStatusSnapshot RetrieveSnapshot();
        PackageDetails[] RetrievePackageList();
        IFileTransfer RetrieveFileTransfer(string fileName);
        List<ParameterBase> ListBuildParameters();
 }
}
