using System;
namespace ThoughtWorks.CruiseControl.Remote.Events
{
    public class CancelProjectEventArgs
        : ProjectEventArgs
    {
        private bool isCanceled = false;
        public CancelProjectEventArgs(string projectName)
            : base(projectName)
        {
        }
        public bool Cancel
        {
            get { return this.isCanceled; }
            set { this.isCanceled = value; }
        }
    }
}
