namespace ThoughtWorks.CruiseControl.Core.Distribution
{
    using System;
using ThoughtWorks.CruiseControl.Remote;
    public class RemoteBuildTaskResult
        : ITaskResult
    {
        private readonly IntegrationStatus status;
        public RemoteBuildTaskResult(IntegrationStatus status)
        {
            this.status = status;
        }
        public string Data
        {
            get { throw new NotImplementedException(); }
        }
        public bool CheckIfSuccess()
        {
            return this.status == IntegrationStatus.Success;
        }
    }
}
