namespace ThoughtWorks.CruiseControl.Core.Distribution.Messages
{
    using System.ServiceModel;
    [MessageContract]
    public class RetrieveBuildStatusRequest
    {
        [MessageBodyMember]
        public string BuildIdentifier { get; set; }
    }
}
