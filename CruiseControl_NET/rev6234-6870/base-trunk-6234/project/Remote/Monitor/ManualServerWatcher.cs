using System;
using System.Threading;
namespace ThoughtWorks.CruiseControl.Remote.Monitor
{
    public class ManualServerWatcher
        : IServerWatcher, IDisposable
    {
        private readonly CruiseServerClientBase client;
        public ManualServerWatcher(CruiseServerClientBase client)
        {
            if (client == null) throw new ArgumentNullException("client");
            this.client = client;
        }
        public virtual void Refresh()
        {
            RetrieveSnapshot();
        }
        public void Dispose()
        {
        }
        public event EventHandler<ServerUpdateArgs> Update;
        private void RetrieveSnapshot()
        {
            ServerUpdateArgs args;
            try
            {
                CruiseServerSnapshot snapshot = null;
                try
                {
                    snapshot = client.GetCruiseServerSnapshot();
                }
                catch (NotImplementedException)
                {
                    snapshot = new CruiseServerSnapshot()
                    {
                        ProjectStatuses = client.GetProjectStatus()
                    };
                }
                args = new ServerUpdateArgs(snapshot);
            }
            catch (Exception error)
            {
                args = new ServerUpdateArgs(error);
            }
            if (Update != null)
            {
                Update(this, args);
            }
        }
    }
}
