using System;
namespace ThoughtWorks.CruiseControl.Remote.Events
{
    public class ProjectEventArgs<TData>
        : EventArgs
    {
        private readonly string projectName;
        private readonly TData data;
        public ProjectEventArgs(string projectName, TData data)
        {
            this.projectName = projectName;
            this.data = data;
        }
        public string ProjectName
        {
            get { return this.projectName; }
        }
        public TData Data
        {
            get { return this.data; }
        }
    }
}
