namespace ThoughtWorks.CruiseControl.Core.Distribution.Messages
{
    using System.ServiceModel;
    [MessageContract]
    public class CheckIfBuildCanRunResponse
    {
        [MessageBodyMember]
        public bool CanBuild { get; set; }
    }
}
