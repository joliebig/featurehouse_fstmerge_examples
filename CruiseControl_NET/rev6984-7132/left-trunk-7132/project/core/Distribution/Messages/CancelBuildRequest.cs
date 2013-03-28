namespace ThoughtWorks.CruiseControl.Core.Distribution.Messages
{
    using System.ServiceModel;
    [MessageContract]
    public class CancelBuildRequest
    {
        [MessageBodyMember]
        public string BuildIdentifier { get; set; }
    }
}
