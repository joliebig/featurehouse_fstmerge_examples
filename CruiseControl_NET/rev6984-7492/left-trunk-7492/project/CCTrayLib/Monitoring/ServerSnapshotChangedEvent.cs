using System;
using System.Collections.Generic;
using System.Text;
using ThoughtWorks.CruiseControl.CCTrayLib.Configuration;
namespace ThoughtWorks.CruiseControl.CCTrayLib.Monitoring
{
    public class ServerSnapshotChangedEventArgs
    {
        private List<string> addedProjects = new List<string>();
        private List<string> deletedProjects = new List<string>();
        public ServerSnapshotChangedEventArgs(string server,
            BuildServer configuration,
            IEnumerable<string> addedProjects,
            IEnumerable<string> deletedProjects)
        {
            this.Server = server;
            this.Configuration = configuration;
            this.addedProjects.AddRange(addedProjects ?? new string[0]);
            this.deletedProjects.AddRange(deletedProjects ?? new string[0]);
        }
        public string Server { get; private set; }
        public BuildServer Configuration { get; private set; }
        public IList<string> AddedProjects
        {
            get { return addedProjects; }
        }
        public IList<string> DeletedProjects
        {
            get { return deletedProjects; }
        }
    }
    public delegate void ServerSnapshotChangedEventHandler(object sender, ServerSnapshotChangedEventArgs args);
}
