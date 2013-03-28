namespace ThoughtWorks.CruiseControl.CCTrayLib.Monitoring
{
 public class IntegrationQueueNodeType
 {
  public static readonly IntegrationQueueNodeType RemotingServer = new IntegrationQueueNodeType("RemotingServer", 0);
  public static readonly IntegrationQueueNodeType HttpServer = new IntegrationQueueNodeType("HttpServer", 1);
  public static readonly IntegrationQueueNodeType QueueEmpty = new IntegrationQueueNodeType("QueueEmpty", 2);
        public static readonly IntegrationQueueNodeType QueuePopulated = new IntegrationQueueNodeType("QueuePopulated", 3);
        public static readonly IntegrationQueueNodeType CheckingModifications = new IntegrationQueueNodeType("CheckingModifications", 4);
  public static readonly IntegrationQueueNodeType Building = new IntegrationQueueNodeType("Building", 5);
  public static readonly IntegrationQueueNodeType PendingInQueue = new IntegrationQueueNodeType("PendingInQueue", 6);
  public readonly string Name;
  public readonly int ImageIndex;
  private IntegrationQueueNodeType( string name, int imageIndex )
  {
   Name = name;
   ImageIndex = imageIndex;
  }
 }
}
