using System;
namespace ThoughtWorks.CruiseControl.Remote.Events
{
    public class ProjectEventArgs
        : EventArgs
    {
        private readonly string projectName;
        public ProjectEventArgs(string projectName)
        {
            this.projectName = projectName;
        }
        public string ProjectName
        {
            get { return this.projectName; }
        }
    }
}
