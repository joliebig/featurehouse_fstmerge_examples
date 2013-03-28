namespace ThoughtWorks.CruiseControl.Core.Distribution.Messages
{
    using System.ServiceModel;
    using ThoughtWorks.CruiseControl.Remote;
    [MessageContract]
    public class RetrieveBuildStatusResponse
    {
        [MessageBodyMember]
        public IntegrationStatus Status { get; set; }
    }
}
