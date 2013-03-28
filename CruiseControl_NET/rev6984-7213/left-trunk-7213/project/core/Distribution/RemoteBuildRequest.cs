namespace ThoughtWorks.CruiseControl.Core.Distribution
{
    using System;
    using System.Collections.Generic;
    using System.Linq;
    using System.Threading;
using ThoughtWorks.CruiseControl.Remote;
using ThoughtWorks.CruiseControl.Core.Distribution.Messages;
    public class RemoteBuildRequest
    {
        private Timer statusCheck;
        private Func<string, RetrieveBuildStatusResponse> onCheck;
        private Action<RemoteBuildRequest> onCompleted;
        private RetrieveBuildStatusResponse lastResponse;
        public RemoteBuildRequest(
            IBuildMachine machine,
            string identifier,
            Func<string, RetrieveBuildStatusResponse> onCheck,
            Action<RemoteBuildRequest> onCompleted)
        {
            this.BuildMachine = machine;
            this.BuildIdentifier = identifier;
            this.onCheck = onCheck;
            this.onCompleted = onCompleted;
            this.statusCheck = new Timer(
                OnStatusCheck,
                null,
                5000,
                Timeout.Infinite);
        }
        public IBuildMachine BuildMachine { get; private set; }
        public string BuildIdentifier { get; private set; }
        public bool Cancelled { get; private set; }
        public IntegrationStatus Status
        {
            get { return this.lastResponse.Status; }
        }
        public ITaskResult Result { get; private set; }
        public void Cancel()
        {
            if (!this.Cancelled)
            {
                this.Cancelled = true;
                this.BuildMachine.CancelBuild(this.BuildIdentifier);
            }
        }
        private void OnStatusCheck(object state)
        {
            var response = this.onCheck(this.BuildIdentifier);
            this.lastResponse = response;
            if (response.Status == IntegrationStatus.Unknown)
            {
                this.statusCheck.Change(5000, Timeout.Infinite);
            }
            else
            {
                this.Result = new RemoteBuildTaskResult(response.Status);
                this.onCompleted(this);
            }
        }
    }
}
