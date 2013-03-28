using ThoughtWorks.CruiseControl.Remote;
namespace ThoughtWorks.CruiseControl.Core.Queues
{
 public interface IIntegrationQueueItem
 {
  IProject Project { get; }
  IntegrationRequest IntegrationRequest { get; }
  IIntegrationQueueNotifier IntegrationQueueNotifier { get; }
 }
}
