using System;
using System.Collections.Generic;
using System.ComponentModel;
namespace ThoughtWorks.CruiseControl.Remote.Monitor
{
    public class BuildQueueRequest
        : INotifyPropertyChanged, IEquatable<BuildQueueRequest>
    {
        private readonly CruiseServerClientBase client;
        private readonly BuildQueue buildQueue;
        private QueuedRequestSnapshot snapshot;
        public BuildQueueRequest(CruiseServerClientBase client, BuildQueue buildQueue, QueuedRequestSnapshot snapshot)
        {
            if (client == null) throw new ArgumentNullException("client");
            if (buildQueue == null) throw new ArgumentNullException("buildQueue");
            if (snapshot == null) throw new ArgumentNullException("snapshot");
            this.client = client;
            this.buildQueue = buildQueue;
            this.snapshot = snapshot;
        }
        public BuildQueue BuildQueue
        {
            get { return buildQueue; }
        }
        public string Name
        {
            get { return InnerBuildQueueRequest.ProjectName; }
        }
        public Project Project
        {
            get { return buildQueue.Server.FindProject(InnerBuildQueueRequest.ProjectName); }
        }
        public ProjectActivity Activity
        {
            get { return InnerBuildQueueRequest.Activity; }
        }
        public DateTime RequestTime
        {
            get { return InnerBuildQueueRequest.RequestTime; }
        }
        public void Update(QueuedRequestSnapshot value)
        {
            if (value == null) throw new ArgumentNullException("value");
            var changes = new List<string>();
            if (snapshot.Activity != value.Activity) changes.Add("Activity");
            if (snapshot.RequestTime != value.RequestTime) changes.Add("RequestTime");
            snapshot = value;
            foreach (var change in changes)
            {
                FirePropertyChanged(change);
            }
        }
        public override bool Equals(object obj)
        {
            return Equals(obj as BuildQueueRequest);
        }
        public bool Equals(BuildQueueRequest obj)
        {
            if (obj == null) return false;
            return (obj.Project.Equals(Project) &&
                obj.RequestTime.Equals(RequestTime));
        }
        public override int GetHashCode()
        {
            return (this.BuildQueue == null ? 0 : this.BuildQueue.GetHashCode()) +
                (this.Name ?? string.Empty).GetHashCode() +
                (InnerBuildQueueRequest == null ? 0 : this.RequestTime.GetHashCode());
        }
        public event PropertyChangedEventHandler PropertyChanged;
        protected QueuedRequestSnapshot InnerBuildQueueRequest
        {
            get { return snapshot; }
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
