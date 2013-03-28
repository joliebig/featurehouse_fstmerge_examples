using System;
namespace ThoughtWorks.CruiseControl.Remote.Monitor
{
    public class ProjectChangedArgs
        : EventArgs
    {
        public ProjectChangedArgs(Project project)
        {
            Project = project;
        }
        public Project Project { get; protected set; }
    }
}
