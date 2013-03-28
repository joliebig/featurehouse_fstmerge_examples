using System;
namespace ThoughtWorks.CruiseControl.Remote.Monitor
{
    public class BuildQueueRequestChangedArgs
        : EventArgs
    {
        public BuildQueueRequestChangedArgs(BuildQueueRequest buildQueue)
        {
            BuildQueueRequest = buildQueue;
        }
        public BuildQueueRequest BuildQueueRequest { get; protected set; }
    }
}
