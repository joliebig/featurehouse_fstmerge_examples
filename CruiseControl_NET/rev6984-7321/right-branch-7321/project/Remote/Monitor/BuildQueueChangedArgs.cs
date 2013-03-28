using System;
namespace ThoughtWorks.CruiseControl.Remote.Monitor
{
    public class BuildQueueChangedArgs
        : EventArgs
    {
        public BuildQueueChangedArgs(BuildQueue buildQueue)
        {
            BuildQueue = buildQueue;
        }
        public BuildQueue BuildQueue { get; protected set; }
    }
}
