namespace ThoughtWorks.CruiseControl.Core.Config
{
    public interface IQueueConfiguration
    {
        string Name { get; set; }
        QueueDuplicateHandlingMode HandlingMode { get; set; }
        string LockQueueNames { get; set; }
    }
}
