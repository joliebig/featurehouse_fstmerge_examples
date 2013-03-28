using ThoughtWorks.CruiseControl.Remote;
namespace ThoughtWorks.CruiseControl.Core.Queues
{
 public class IntegrationQueueItem : IIntegrationQueueItem
 {
  private IProject project;
  private IntegrationRequest integrationRequest;
  private IIntegrationQueueNotifier integrationQueueNotifier;
  public IntegrationQueueItem(IProject project, IntegrationRequest integrationRequest, IIntegrationQueueNotifier integrationQueueNotifier)
  {
   this.project = project;
   this.integrationRequest = integrationRequest;
   this.integrationQueueNotifier = integrationQueueNotifier;
  }
  public IProject Project
  {
   get { return project; }
  }
  public IntegrationRequest IntegrationRequest
  {
   get { return integrationRequest; }
  }
  public IIntegrationQueueNotifier IntegrationQueueNotifier
  {
   get { return integrationQueueNotifier; }
  }
 }
}
