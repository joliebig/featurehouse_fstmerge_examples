using System.Collections;
using ThoughtWorks.CruiseControl.Core.Config;
using ThoughtWorks.CruiseControl.Remote;
using System;
namespace ThoughtWorks.CruiseControl.Core.Queues
{
 public interface IIntegrationQueue : IList
 {
  string Name { get; }
        bool IsBlocked { get; }
        IQueueConfiguration Configuration { get; }
  void Enqueue(IIntegrationQueueItem integrationQueueItem);
  void Dequeue();
  void RemovePendingRequest(IProject project);
  void RemoveProject(IProject project);
  IIntegrationQueueItem[] GetQueuedIntegrations();
  IntegrationRequest GetNextRequest(IProject project);
  bool HasItemOnQueue(IProject project);
  bool HasItemPendingOnQueue(IProject project);
        bool BlockQueue(IIntegrationQueue requestingQueue);
        void UnblockQueue(IIntegrationQueue requestingQueue);
        bool TryLock(out IDisposable queueLock);
 }
}
