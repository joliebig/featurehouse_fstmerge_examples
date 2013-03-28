using System;
using System.Collections.Generic;
using System.ComponentModel;
using ThoughtWorks.CruiseControl.Remote.Parameters;
namespace ThoughtWorks.CruiseControl.Remote.Monitor
{
    public class Project
        : INotifyPropertyChanged, IEquatable<Project>
    {
        private readonly CruiseServerClientBase client;
        private readonly Server server;
        private ProjectStatus project;
        private Exception exception;
        private Dictionary<string, ProjectBuild> builds = new Dictionary<string, ProjectBuild>();
        private bool buildsLoaded;
        private object lockObject = new object();
        private object snapshotLock = new object();
        private ProjectStatusSnapshot statusSnapshot;
        private DataBag data = new DataBag();
        private IEnumerable<ParameterBase> parameters;
        public Project(CruiseServerClientBase client,Server server, ProjectStatus project)
        {
            if (client == null) throw new ArgumentNullException("client");
            if (server == null) throw new ArgumentNullException("server");
            if (project == null) throw new ArgumentNullException("project");
            this.client = client;
            this.server = server;
            this.project = project;
        }
        public Server Server
        {
            get { return server; }
        }
        public string Name
        {
            get { return InnerProject.Name; }
        }
        public string BuildStage
        {
            get { return InnerProject.BuildStage; }
        }
        public ProjectIntegratorState Status
        {
            get { return InnerProject.Status; }
        }
        public IntegrationStatus BuildStatus
        {
            get { return InnerProject.BuildStatus; }
        }
        public ProjectActivity Activity
        {
            get { return InnerProject.Activity; }
        }
        public string Description
        {
            get { return InnerProject.Description; }
        }
        public string Category
        {
            get { return InnerProject.Category; }
        }
        public BuildQueue BuildQueue
        {
            get { return server.FindBuildQueue(InnerProject.Queue); }
        }
        public string Queue
        {
            get { return InnerProject.Queue; }
        }
        public int QueuePriority
        {
            get { return InnerProject.QueuePriority; }
        }
        public string WebURL
        {
            get { return InnerProject.WebURL; }
        }
        public DateTime LastBuildDate
        {
            get { return InnerProject.LastBuildDate; }
        }
        public string LastBuildLabel
        {
            get { return InnerProject.LastBuildLabel; }
        }
        public string LastSuccessfulBuildLabel
        {
            get { return InnerProject.LastSuccessfulBuildLabel; }
        }
        public DateTime NextBuildTime
        {
            get { return InnerProject.NextBuildTime; }
        }
        public IEnumerable<Message> Messages
        {
            get { return InnerProject.Messages; }
        }
        public IEnumerable<ProjectBuild> Builds
        {
            get
            {
                if (!buildsLoaded) LoadBuilds(InnerProject);
                return builds.Values;
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
        public void Update(ProjectStatus value)
        {
            if (value == null) throw new ArgumentNullException("value");
            var changes = new List<string>();
            if (project.Activity != value.Activity) changes.Add("Activity");
            if (project.BuildStage != value.BuildStage) changes.Add("BuildStage");
            if (project.BuildStatus != value.BuildStatus) changes.Add("BuildStatus");
            if (project.Category != value.Category) changes.Add("Category");
            if (project.Description != value.Description) changes.Add("Description");
            if (project.LastBuildDate != value.LastBuildDate) changes.Add("LastBuildDate");
            if (project.LastBuildLabel != value.LastBuildLabel) changes.Add("LastBuildLabel");
            if (project.LastSuccessfulBuildLabel != value.LastSuccessfulBuildLabel) changes.Add("LastSuccessfulBuildLabel");
            if (project.NextBuildTime != value.NextBuildTime) changes.Add("NextBuildTime");
            if (project.Queue != value.Queue) changes.Add("Queue");
            if (project.QueuePriority != value.QueuePriority) changes.Add("QueuePriority");
            if (project.Status != value.Status) changes.Add("Status");
            if (project.WebURL != value.WebURL) changes.Add("WebURL");
            if (project.Messages.Length != value.Messages.Length)
            {
                changes.Add("Messages");
            }
            else
            {
                var messageChanged = false;
                for (var loop = 0; loop < project.Messages.Length; loop++)
                {
                    messageChanged = (project.Messages[loop].Text != value.Messages[loop].Text);
                    if (messageChanged) break;
                }
                if (messageChanged) changes.Add("Messages");
            }
            LoadBuilds(value);
            project = value;
            statusSnapshot = null;
            foreach (var change in changes)
            {
                FirePropertyChanged(change);
            }
            FireUpdated();
        }
        public void ForceBuild()
        {
            client.ProcessSingleAction(p =>
            {
                client.ForceBuild(p.Name);
            }, InnerProject);
        }
        public void ForceBuild(List<NameValuePair> parameters)
        {
            client.ProcessSingleAction(p =>
            {
                client.ForceBuild(p.Name, parameters);
            }, InnerProject);
        }
        public void AbortBuild()
        {
            client.ProcessSingleAction(p =>
            {
                client.AbortBuild(p.Name);
            }, InnerProject);
        }
        public void Start()
        {
            client.ProcessSingleAction(p =>
            {
                client.StartProject(p.Name);
            }, InnerProject);
        }
        public void Stop()
        {
            client.ProcessSingleAction(p =>
            {
                client.StopProject(p.Name);
            }, InnerProject);
        }
        public ProjectStatusSnapshot RetrieveCurrentStatus()
        {
            if (statusSnapshot == null)
            {
                lock (snapshotLock)
                {
                    if (statusSnapshot == null)
                    {
                        client.ProcessSingleAction(p =>
                        {
                            statusSnapshot = client.TakeStatusSnapshot(p.Name);
                        }, InnerProject);
                    }
                }
            }
            return statusSnapshot;
        }
        public IEnumerable<ParameterBase> RetrieveParameters()
        {
            if (parameters == null)
            {
                parameters = client.ListBuildParameters(project.Name);
            }
            return parameters;
        }
        public override bool Equals(object obj)
        {
            return Equals(obj as Project);
        }
        public virtual bool Equals(Project obj)
        {
            if (obj == null) return false;
            return obj.Server.Equals(Server) &&
                (obj.Name == Name);
        }
        public override int GetHashCode()
        {
            return (Server == null ? 0 : Server.GetHashCode()) +
                (Name ?? string.Empty).GetHashCode();
        }
        public event PropertyChangedEventHandler PropertyChanged;
        public event EventHandler Updated;
        protected ProjectStatus InnerProject
        {
            get { return project; }
        }
        protected void FirePropertyChanged(string propertyName)
        {
            if (PropertyChanged != null)
            {
                var args = new PropertyChangedEventArgs(propertyName);
                PropertyChanged(this, args);
            }
        }
        protected void FireUpdated()
        {
            if (Updated != null)
            {
                Updated(this, EventArgs.Empty);
            }
        }
        protected virtual void LoadBuilds(ProjectStatus value)
        {
            lock (lockObject)
            {
                buildsLoaded = true;
                if (builds.Count == 0)
                {
                    string[] buildNames = { };
                    try
                    {
                        client.ProcessSingleAction(p =>
                        {
                            buildNames = client.GetBuildNames(p.Name);
                        }, InnerProject);
                    }
                    catch
                    {
                    }
                    foreach (var buildName in buildNames ?? new string[0])
                    {
                        builds.Add(buildName, new ProjectBuild(buildName, this, client));
                    }
                }
                else
                {
                    if (project.LastBuildDate != value.LastBuildDate)
                    {
                        string[] buildNames = { };
                        try
                        {
                            client.ProcessSingleAction(p =>
                            {
                                buildNames = client.GetMostRecentBuildNames(p.Name, 10);
                            }, InnerProject);
                        }
                        catch
                        {
                        }
                        foreach (var buildName in buildNames)
                        {
                            if (!builds.ContainsKey(buildName))
                            {
                                builds.Add(buildName, new ProjectBuild(buildName, this, client));
                            }
                        }
                    }
                }
            }
        }
    }
}
