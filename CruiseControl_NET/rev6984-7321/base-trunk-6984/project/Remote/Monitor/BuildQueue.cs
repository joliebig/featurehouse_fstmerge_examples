using System.ComponentModel;
using System;
using System.Collections.Generic;
namespace ThoughtWorks.CruiseControl.Remote.Monitor
{
    public class BuildQueue
        : INotifyPropertyChanged
    {
        private readonly CruiseServerClientBase client;
        private readonly Server server;
        private Dictionary<string, BuildQueueRequest> requests = new Dictionary<string, BuildQueueRequest>();
        private QueueSnapshot buildQueue;
        private Exception exception;
        private object syncLock = new object();
        private DataBag data = new DataBag();
        public BuildQueue(CruiseServerClientBase client,Server server, QueueSnapshot buildQueue)
        {
            if (client == null) throw new ArgumentNullException("client");
            if (server == null) throw new ArgumentNullException("server");
            if (buildQueue == null) throw new ArgumentNullException("buildQueue");
            this.client = client;
            this.server = server;
            this.buildQueue = buildQueue;
        }
        public Server Server
        {
            get { return server; }
        }
        public string Name
        {
            get { return InnerBuildQueue.QueueName; }
        }
        public IEnumerable<BuildQueueRequest> Requests
        {
            get
            {
                lock (syncLock) { return requests.Values; }
            }
        }
        public Exception Exception
        {
            get { return exception; }
            set
            {
                if (((value != null) && (exception != null) && (value.Message != exception.Message)) ||
                    ((value == null) && (exception != null)) ||
                    ((value != null) && (exception == null)))
                {
                    exception = value;
                    FirePropertyChanged("Exception");
                }
            }
        }
        public DataBag Data
        {
            get { return data; }
        }
        public void Update(QueueSnapshot value)
        {
            if (value == null) throw new ArgumentNullException("value");
            var changes = new List<string>();
            var newRequests = new List<BuildQueueRequest>();
            var oldRequests = new List<BuildQueueRequest>();
            lock (syncLock)
            {
                var requestValues = new Dictionary<BuildQueueRequest, QueuedRequestSnapshot>();
                var oldRequestNames = new List<string>(requests.Keys);
                foreach (var request in value.Requests)
                {
                    var requestName = request.ProjectName;
                    if (oldRequestNames.Contains(requestName))
                    {
                        requestValues.Add(requests[requestName], request);
                        oldRequestNames.Remove(requestName);
                    }
                    else
                    {
                        var newRequest = new BuildQueueRequest(client, this, request);
                        newRequests.Add(newRequest);
                    }
                }
                foreach (var request in oldRequestNames)
                {
                    oldRequests.Add(requests[request]);
                }
                foreach (var request in oldRequestNames)
                {
                    requests.Remove(request);
                }
                foreach (var request in newRequests)
                {
                    if (!requests.ContainsKey(request.Name))
                    {
                        requests.Add(request.Name, request);
                    }
                }
                buildQueue = value;
                foreach (var requestValue in requestValues)
                {
                    requestValue.Key.Update(requestValue.Value);
                }
            }
            foreach (var request in newRequests)
            {
                FireBuildQueueRequestAdded(request);
            }
            foreach (var request in oldRequests)
            {
                FireBuildQueueRequestRemoved(request);
            }
            foreach (var change in changes)
            {
                FirePropertyChanged(change);
            }
        }
        public override bool Equals(object obj)
        {
            return Equals(obj as BuildQueue);
        }
        public virtual bool Equals(BuildQueue obj)
        {
            if (obj == null) return false;
            return obj.Server.Equals(Server) &&
                (obj.Name == Name);
        }
        public override int GetHashCode()
        {
            return (this.Server == null ? 0 : this.Server.GetHashCode()) +
                (this.Name ?? string.Empty).GetHashCode();
        }
        public event EventHandler<BuildQueueRequestChangedArgs> BuildQueueRequestAdded;
        public event EventHandler<BuildQueueRequestChangedArgs> BuildQueueRequestRemoved;
        public event PropertyChangedEventHandler PropertyChanged;
        protected QueueSnapshot InnerBuildQueue
        {
            get { return buildQueue; }
        }
        protected void FireBuildQueueRequestAdded(BuildQueueRequest request)
        {
            if (BuildQueueRequestAdded != null)
            {
                var args = new BuildQueueRequestChangedArgs(request);
                BuildQueueRequestAdded(this, args);
            }
        }
        protected void FireBuildQueueRequestRemoved(BuildQueueRequest request)
        {
            if (BuildQueueRequestRemoved != null)
            {
                var args = new BuildQueueRequestChangedArgs(request);
                BuildQueueRequestRemoved(this, args);
            }
        }
        protected void FirePropertyChanged(string propertyName)
        {
            if (PropertyChanged != null)
            {
                var args = new PropertyChangedEventArgs(propertyName);
                PropertyChanged(this, args);
            }
        }
    }
}
