using System;
using ThoughtWorks.CruiseControl.Remote;
using System.Collections.Generic;
using ThoughtWorks.CruiseControl.Remote.Events;
namespace ThoughtWorks.CruiseControl.Core.Queues
{
    public interface IQueueManager
    {
        void StartAllProjects();
        void StopAllProjects();
        void Abort();
        void Restart(IConfiguration configuration);
        void Start(string project);
        void Stop(string project);
        void ForceBuild(string projectName, string enforcerName, Dictionary<string, string> buildValues);
        void Request(string project, IntegrationRequest request);
        void CancelPendingRequest(string projectName);
        void WaitForExit(string projectName);
        CruiseServerSnapshot GetCruiseServerSnapshot();
        ProjectStatus[] GetProjectStatuses();
        IProjectIntegrator GetIntegrator(string projectName);
        void AssociateIntegrationEvents(EventHandler<IntegrationStartedEventArgs> integrationStarted,
            EventHandler<IntegrationCompletedEventArgs> integrationCompleted);
    }
}
