using System.Collections;
using ThoughtWorks.CruiseControl.Core.Config;
using ThoughtWorks.CruiseControl.Remote;
namespace ThoughtWorks.CruiseControl.Core.Queues
{
 public interface IIntegrationQueue : IList
 {
  string Name { get; }
        bool IsLocked { get; }
        IQueueConfiguration Configuration { get; }
  void Enqueue(IIntegrationQueueItem integrationQueueItem);
  void Dequeue();
  void RemovePendingRequest(IProject project);
  void RemoveProject(IProject project);
  IIntegrationQueueItem[] GetQueuedIntegrations();
  IntegrationRequest GetNextRequest(IProject project);
  bool HasItemOnQueue(IProject project);
  bool HasItemPendingOnQueue(IProject project);
        void ToggleQueueLocks(bool acquire);
        void LockQueue(IIntegrationQueue requestingQueue);
        void UnlockQueue(IIntegrationQueue requestingQueue);
 }
}
