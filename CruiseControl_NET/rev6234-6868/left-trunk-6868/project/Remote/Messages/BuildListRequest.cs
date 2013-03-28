using System;
using System.Xml.Serialization;
namespace ThoughtWorks.CruiseControl.Remote.Messages
{
    [XmlRoot("buildListMessage")]
    [Serializable]
    public class BuildListRequest
        : ProjectRequest
    {
        private int numberOfBuilds;
        public BuildListRequest()
        {
        }
        public BuildListRequest(string sessionToken)
            : base(sessionToken)
        {
        }
        public BuildListRequest(string sessionToken, string projectName)
            : base(sessionToken, projectName)
        {
        }
        [XmlAttribute("number")]
        public int NumberOfBuilds
        {
            get { return numberOfBuilds; }
            set { numberOfBuilds = value; }
        }
    }
}
