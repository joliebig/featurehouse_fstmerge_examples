using System;
namespace ThoughtWorks.CruiseControl.Remote.Monitor
{
    public interface IServerWatcher
    {
        void Refresh();
        event EventHandler<ServerUpdateArgs> Update;
    }
}
