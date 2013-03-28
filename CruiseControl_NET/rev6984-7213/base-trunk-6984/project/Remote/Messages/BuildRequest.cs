using System;
using System.Xml.Serialization;
namespace ThoughtWorks.CruiseControl.Remote.Messages
{
    [XmlRoot("buildMessage")]
    [Serializable]
    public class BuildRequest
        : ProjectRequest
    {
        private string buildName;
        public BuildRequest()
        {
        }
        public BuildRequest(string sessionToken)
            : base(sessionToken)
        {
        }
        public BuildRequest(string sessionToken, string projectName)
            : base(sessionToken, projectName)
        {
        }
        [XmlAttribute("build")]
        public string BuildName
        {
            get { return buildName; }
            set { buildName = value; }
        }
    }
}
