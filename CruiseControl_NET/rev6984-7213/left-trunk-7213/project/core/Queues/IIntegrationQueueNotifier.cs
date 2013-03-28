namespace ThoughtWorks.CruiseControl.Core.Queues
{
 public interface IIntegrationQueueNotifier
 {
  void NotifyEnteringIntegrationQueue();
  void NotifyExitingIntegrationQueue(bool isPendingItemCancelled);
 }
}
