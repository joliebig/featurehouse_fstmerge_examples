using System;
using System.Collections.Generic;
using System.Text;
namespace ThoughtWorks.CruiseControl.Remote.Events
{
    public class IntegrationCompletedEventArgs
        : ProjectEventArgs
    {
        private readonly IntegrationRequest request;
        private readonly IntegrationStatus status;
        public IntegrationCompletedEventArgs(IntegrationRequest request, string projectName, IntegrationStatus status)
            : base(projectName)
        {
            this.request = request;
            this.status = status;
        }
        public IntegrationRequest Request
        {
            get { return this.request; }
        }
        public IntegrationStatus Status
        {
            get { return this.status; }
        }
    }
}
