using System;
using System.Collections.Generic;
using System.Text;
namespace ThoughtWorks.CruiseControl.Remote.Events
{
    public class IntegrationStartedEventArgs
        : ProjectEventArgs
    {
        private EventResult result = EventResult.Continue;
        private readonly IntegrationRequest request;
        public IntegrationStartedEventArgs(IntegrationRequest request, string projectName)
            : base(projectName)
        {
            this.request = request;
        }
        public IntegrationRequest Request
        {
            get { return this.request; }
        }
        public EventResult Result
        {
            get { return this.result; }
            set { this.result = value; }
        }
        public enum EventResult
        {
            Cancel,
            Delay,
            Continue,
        }
    }
}
