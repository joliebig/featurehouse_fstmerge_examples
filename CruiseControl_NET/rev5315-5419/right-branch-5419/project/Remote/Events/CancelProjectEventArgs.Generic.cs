using System;
namespace ThoughtWorks.CruiseControl.Remote.Events
{
    public class CancelProjectEventArgs<TData>
        : ProjectEventArgs<TData>
    {
        private bool isCanceled = false;
        public CancelProjectEventArgs(string projectName, TData data)
            : base(projectName, data)
        {
        }
        public bool Cancel
        {
            get { return this.isCanceled; }
            set { this.isCanceled = value; }
        }
    }
}
