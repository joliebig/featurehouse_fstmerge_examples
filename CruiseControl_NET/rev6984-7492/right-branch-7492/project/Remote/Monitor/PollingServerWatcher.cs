using System;
using System.Threading;
namespace ThoughtWorks.CruiseControl.Remote.Monitor
{
    public class PollingServerWatcher
        : IServerWatcher, IDisposable
    {
        private readonly CruiseServerClientBase client;
        private Thread pollingThread;
        private long interval = 5;
        private DateTime nextRefresh;
        private bool disposing;
        public PollingServerWatcher(CruiseServerClientBase client)
        {
            if (client == null) throw new ArgumentNullException("client");
            this.client = client;
            nextRefresh = DateTime.Now.AddSeconds(interval);
            pollingThread = new Thread(Poll);
            pollingThread.IsBackground = true;
            pollingThread.Start();
        }
        public long Interval
        {
            get { return interval; }
            set
            {
                interval = value;
                nextRefresh = DateTime.Now.AddSeconds(interval);
            }
        }
        public virtual void Refresh()
        {
            nextRefresh = DateTime.Now;
        }
        public void Dispose()
        {
            disposing = true;
        }
        public event EventHandler<ServerUpdateArgs> Update;
        private void Poll()
        {
            Thread.CurrentThread.Name = "Server watcher: " + client.Address;
            while (!disposing)
            {
                Thread.Sleep(500);
                if (!disposing && (DateTime.Now > nextRefresh))
                {
                    RetrieveSnapshot();
                    nextRefresh = DateTime.Now.AddSeconds(interval);
                }
            }
        }
        private void RetrieveSnapshot()
        {
            ServerUpdateArgs args;
            try
            {
                CruiseServerSnapshot snapshot = null;
                try
                {
                    client.ProcessSingleAction<object>(o =>
                    {
                        snapshot = client.GetCruiseServerSnapshot();
                    }, null);
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
