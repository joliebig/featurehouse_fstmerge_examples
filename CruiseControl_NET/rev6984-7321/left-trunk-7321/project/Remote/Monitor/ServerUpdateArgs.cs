using System;
namespace ThoughtWorks.CruiseControl.Remote.Monitor
{
    public class ServerUpdateArgs
        : EventArgs
    {
        public ServerUpdateArgs(CruiseServerSnapshot snapshot)
        {
            Snapshot = snapshot;
        }
        public ServerUpdateArgs(Exception error)
        {
            Exception = error;
        }
        public CruiseServerSnapshot Snapshot { get; protected set; }
        public Exception Exception { get; private set; }
    }
}
