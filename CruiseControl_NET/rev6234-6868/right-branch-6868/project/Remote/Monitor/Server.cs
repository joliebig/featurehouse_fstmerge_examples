using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Threading;
using ThoughtWorks.CruiseControl.Remote.Messages;
namespace ThoughtWorks.CruiseControl.Remote.Monitor
{
    public class Server
        : IDisposable, INotifyPropertyChanged, IEquatable<Server>
    {
        private Dictionary<string, Project> projects = new Dictionary<string, Project>();
        private Dictionary<string, BuildQueue> buildQueues = new Dictionary<string, BuildQueue>();
        private ReaderWriterLock syncLock = new ReaderWriterLock();
        private IServerWatcher watcher;
        private CruiseServerClientBase client;
        private Exception exception;
        private Version version;
        private DataBag data = new DataBag();
        private string displayName;
        public Server(string address)
        {
            if (string.IsNullOrEmpty(address)) throw new ArgumentNullException("address");
            var factory = new CruiseServerClientFactory();
            var client = factory.GenerateClient(address);
            InitialiseServer(client, new ManualServerWatcher(client), true);
        }
        public Server(string address, ClientStartUpSettings settings)
        {
            if (string.IsNullOrEmpty(address)) throw new ArgumentNullException("address");
            var factory = new CruiseServerClientFactory();
            var client = factory.GenerateClient(address, settings);
            InitialiseServer(client, new ManualServerWatcher(client), settings.FetchVersionOnStartUp);
        }
        public Server(CruiseServerClientBase client)
        {
            if (client == null) throw new ArgumentNullException("client");
            InitialiseServer(client, new ManualServerWatcher(client), true);
        }
        public Server(CruiseServerClientBase client, ClientStartUpSettings settings)
        {
            if (client == null) throw new ArgumentNullException("client");
            InitialiseServer(client, new ManualServerWatcher(client), settings.FetchVersionOnStartUp);
        }
        public Server(CruiseServerClientBase client, IServerWatcher watcher)
        {
            if (client == null) throw new ArgumentNullException("client");
            if (watcher == null) throw new ArgumentNullException("watcher");
            InitialiseServer(client, watcher, true);
        }
        public Server(CruiseServerClientBase client, IServerWatcher watcher, ClientStartUpSettings settings)
        {
            if (client == null) throw new ArgumentNullException("client");
            if (watcher == null) throw new ArgumentNullException("watcher");
            InitialiseServer(client, watcher, settings.FetchVersionOnStartUp);
        }
        public string DisplayName
        {
            get { return displayName; }
            set
            {
                displayName = value;
                FirePropertyChanged("DisplayName");
            }
        }
        public string Name
        {
            get { return client.TargetServer; }
        }
        public string TargetAddress
        {
            get
            {
                var value = string.Empty;
                if (string.IsNullOrEmpty(client.TargetServer))
                {
                    value = client.Address;
                }
                else
                {
                    value = string.Format("{0}->{1}", client.TargetServer, client.Address);
                }
                return value;
            }
        }
        public IEnumerable<Project> Projects
        {
            get
            {
                syncLock.AcquireReaderLock(5000);
                try
                {
                    return projects.Values;
                }
                finally
                {
                    syncLock.ReleaseReaderLock();
                }
            }
        }
        public IEnumerable<BuildQueue> BuildQueues
        {
            get
            {
                syncLock.AcquireReaderLock(5000);
                try
                {
                    return buildQueues.Values;
                }
                finally
                {
                    syncLock.ReleaseReaderLock();
                }
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
                    foreach (var project in projects.Values)
                    {
                        project.Exception = value;
                    }
                }
            }
        }
        public CruiseServerClientBase Client
        {
            get { return client; }
        }
        public IServerWatcher Watcher
        {
            get { return watcher; }
        }
        public Version Version
        {
            get { return version; }
        }
        public bool IsLoggedIn
        {
            get { return !string.IsNullOrEmpty(client.SessionToken); }
        }
        public DataBag Data
        {
            get { return data; }
        }
        public virtual string GetDisplayName()
        {
            var name = displayName;
            if (string.IsNullOrEmpty(name))
            {
                name = TargetAddress;
            }
            return name;
        }
        public virtual string GetDisplayName(bool includeAddress)
        {
            var name = GetDisplayName();
            if (!string.IsNullOrEmpty(name) && includeAddress)
            {
                name += " [" + TargetAddress + "]";
            }
            return name;
        }
        public virtual void Refresh()
        {
            watcher.Refresh();
        }
        public void Dispose()
        {
            var disposableWatcher = watcher as IDisposable;
            if (disposableWatcher != null)
            {
                disposableWatcher.Dispose();
            }
        }
        public Project FindProject(string name)
        {
            if (projects.ContainsKey(name)) return projects[name];
            return null;
        }
        public BuildQueue FindBuildQueue(string name)
        {
            if (buildQueues.ContainsKey(name)) return buildQueues[name];
            return null;
        }
        public bool Login(string userName, string password)
        {
            var credentials = new List<NameValuePair>();
            if (!string.IsNullOrEmpty(userName))
            {
                credentials.Add(
                    new NameValuePair(
                        LoginRequest.UserNameCredential,
                        userName));
            }
            if (!string.IsNullOrEmpty(password))
            {
                credentials.Add(
                    new NameValuePair(
                        LoginRequest.PasswordCredential,
                        password));
            }
            try
            {
                var result = client.Login(credentials);
                if (result) FireLoginChanged();
                return result;
            }
            catch (Exception error)
            {
                throw new CommunicationsException("An unexpected error has occurred", error);
            }
        }
        public void Logout()
        {
            if (IsLoggedIn)
            {
                Client.Logout();
                FireLoginChanged();
            }
        }
        public override bool Equals(object obj)
        {
            return Equals(obj as Server);
        }
        public virtual bool Equals(Server obj)
        {
            if (obj == null) return false;
            return (obj.Client.Address == Client.Address) &&
                (obj.Client.TargetServer == Client.TargetServer);
        }
        public override int GetHashCode()
        {
            return Client.TargetServer.GetHashCode() +
                Client.Address.GetHashCode();
        }
        public event EventHandler<ProjectChangedArgs> ProjectAdded;
        public event EventHandler<ProjectChangedArgs> ProjectRemoved;
        public event EventHandler<BuildQueueChangedArgs> BuildQueueAdded;
        public event EventHandler<BuildQueueChangedArgs> BuildQueueRemoved;
        public event PropertyChangedEventHandler PropertyChanged;
        public event EventHandler LoginChanged;
        protected void FireProjectAdded(Project project)
        {
            if (ProjectAdded != null)
            {
                var args = new ProjectChangedArgs(project);
                ProjectAdded(this, args);
            }
        }
        protected void FireProjectRemoved(Project project)
        {
            if (ProjectRemoved != null)
            {
                var args = new ProjectChangedArgs(project);
                ProjectRemoved(this, args);
            }
        }
        protected void FireBuildQueueAdded(BuildQueue buildQueue)
        {
            if (BuildQueueAdded != null)
            {
                var args = new BuildQueueChangedArgs(buildQueue);
                BuildQueueAdded(this, args);
            }
        }
        protected void FireBuildQueueRemoved(BuildQueue buildQueue)
        {
            if (BuildQueueRemoved != null)
            {
                var args = new BuildQueueChangedArgs(buildQueue);
                BuildQueueRemoved(this, args);
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
        protected void FireLoginChanged()
        {
            if (LoginChanged != null)
            {
                LoginChanged(this, EventArgs.Empty);
            }
        }
        protected virtual void OnWatcherUpdate(object sender, ServerUpdateArgs e)
        {
            var newProjects = new List<Project>();
            var oldProjects = new List<Project>();
            var newQueues = new List<BuildQueue>();
            var oldQueues = new List<BuildQueue>();
            var projectValues = new Dictionary<Project, ProjectStatus>();
            var queueValues = new Dictionary<BuildQueue, QueueSnapshot>();
            syncLock.AcquireWriterLock(10000);
            try
            {
                Exception = e.Exception;
                if (e.Exception == null)
                {
                    var oldProjectNames = new List<string>(projects.Keys);
                    foreach (var project in e.Snapshot.ProjectStatuses)
                    {
                        var projectName = project.Name;
                        if (oldProjectNames.Contains(projectName))
                        {
                            projectValues.Add(projects[projectName], project);
                            oldProjectNames.Remove(projectName);
                        }
                        else
                        {
                            var newProject = new Project(client, this, project);
                            newProjects.Add(newProject);
                        }
                    }
                    var oldQueueNames = new List<string>(buildQueues.Keys);
                    foreach (var queue in e.Snapshot.QueueSetSnapshot.Queues)
                    {
                        var queueName = queue.QueueName;
                        if (oldQueueNames.Contains(queueName))
                        {
                            queueValues.Add(buildQueues[queueName], queue);
                            oldQueueNames.Remove(queueName);
                        }
                        else
                        {
                            var newQueue = new BuildQueue(client, this, queue);
                            newQueues.Add(newQueue);
                        }
                    }
                    foreach (var project in oldProjectNames)
                    {
                        oldProjects.Add(projects[project]);
                    }
                    foreach (var queue in oldQueueNames)
                    {
                        oldQueues.Add(buildQueues[queue]);
                    }
                    foreach (var project in oldProjectNames)
                    {
                        projects.Remove(project);
                    }
                    foreach (var queue in oldQueueNames)
                    {
                        buildQueues.Remove(queue);
                    }
                    foreach (var project in newProjects)
                    {
                        projects.Add(project.Name, project);
                    }
                    foreach (var queue in newQueues)
                    {
                        buildQueues.Add(queue.Name, queue);
                    }
                }
            }
            finally
            {
                syncLock.ReleaseWriterLock();
            }
            foreach (var value in projectValues)
            {
                value.Key.Update(value.Value);
            }
            foreach (var value in queueValues)
            {
                value.Key.Update(value.Value);
            }
            foreach (var project in newProjects)
            {
                FireProjectAdded(project);
            }
            foreach (var queue in newQueues)
            {
                FireBuildQueueAdded(queue);
            }
            foreach (var project in oldProjects)
            {
                FireProjectRemoved(project);
            }
            foreach (var queue in oldQueues)
            {
                FireBuildQueueRemoved(queue);
            }
        }
        private void InitialiseServer(CruiseServerClientBase client, IServerWatcher watcher, bool fetchVersion)
        {
            this.watcher = watcher;
            this.watcher.Update += OnWatcherUpdate;
            this.client = client;
            if (fetchVersion)
            {
                try
                {
                    client.ProcessSingleAction(s =>
                    {
                        version = new Version(client.GetServerVersion());
                    }, client);
                }
                catch
                {
                }
            }
        }
    }
}
