using ThoughtWorks.CruiseControl.Remote;
namespace ThoughtWorks.CruiseControl.Core
{
 public interface IProjectIntegrator
 {
  IProject Project { get; }
  string Name { get; }
  void Start();
  void Stop();
  void WaitForExit();
  void Abort();
  bool IsRunning { get; }
  ProjectIntegratorState State { get; }
  IIntegrationRepository IntegrationRepository { get; }
        void ForceBuild(string enforcerName);
  void AbortBuild(string enforcerName);
  void Request(IntegrationRequest request);
  void CancelPendingRequest();
 }
}
