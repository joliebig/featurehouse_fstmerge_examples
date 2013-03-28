namespace ThoughtWorks.CruiseControl.CCTrayLib.Monitoring
{
 public interface ICruiseProjectManager
 {
  void ForceBuild();
  void FixBuild(string fixingUserName);
  void AbortBuild();
  void StopProject();
  void StartProject();
  void CancelPendingRequest();
  string ProjectName { get; }
 }
}
