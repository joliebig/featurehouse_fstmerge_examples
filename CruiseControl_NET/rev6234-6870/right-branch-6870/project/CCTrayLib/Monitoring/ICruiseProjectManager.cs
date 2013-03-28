using ThoughtWorks.CruiseControl.Remote.Parameters;
using System.Collections.Generic;
using ThoughtWorks.CruiseControl.Remote;
using System.IO;
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
        void TransferFile(string fileName, Stream outputStream);
        List<ParameterBase> ListBuildParameters();
 }
}
