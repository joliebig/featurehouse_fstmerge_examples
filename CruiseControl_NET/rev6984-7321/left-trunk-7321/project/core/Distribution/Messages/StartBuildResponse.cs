namespace ThoughtWorks.CruiseControl.Core.Distribution.Messages
{
    using System.ServiceModel;
    [MessageContract]
    public class StartBuildResponse
    {
        [MessageBodyMember]
        public string BuildIdentifier { get; set; }
    }
}
