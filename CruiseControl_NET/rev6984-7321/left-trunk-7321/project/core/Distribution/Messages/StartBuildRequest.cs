namespace ThoughtWorks.CruiseControl.Core.Distribution.Messages
{
    using System.ServiceModel;
using ThoughtWorks.CruiseControl.Remote;
using System.Collections.Generic;
    [MessageContract]
    public class StartBuildRequest
    {
        [MessageBodyMember]
        public string ProjectName { get; set; }
        [MessageBodyMember]
        public string ProjectDefinition { get; set; }
        [MessageBodyMember]
        public BuildCondition BuildCondition { get; set; }
        [MessageBodyMember]
        public string Source { get; set; }
        [MessageBodyMember]
        public string UserName { get; set; }
        [MessageBodyMember]
        public Dictionary<string, string> BuildValues { get; set; }
    }
}
