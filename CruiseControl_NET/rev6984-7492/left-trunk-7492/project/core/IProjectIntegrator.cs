using ThoughtWorks.CruiseControl.Remote;
using System;
using ThoughtWorks.CruiseControl.Remote.Events;
using System.Collections.Generic;
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
        void ForceBuild(string enforcerName, Dictionary<string, string> buildValues);
  void AbortBuild(string enforcerName);
  void Request(IntegrationRequest request);
  void CancelPendingRequest();
        event EventHandler<IntegrationStartedEventArgs> IntegrationStarted;
        event EventHandler<IntegrationCompletedEventArgs> IntegrationCompleted;
    }
}
