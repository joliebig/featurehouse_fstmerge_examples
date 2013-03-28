namespace ThoughtWorks.CruiseControl.Core.Distribution.Messages
{
    using System.ServiceModel;
    [MessageContract]
    public class CheckIfBuildCanRunRequest
    {
        [MessageBodyMember]
        public string ProjectName { get; set; }
    }
}
