using System;
using System.Collections.Generic;
using System.Text;
using System.Xml.Serialization;
namespace ThoughtWorks.CruiseControl.Remote.Messages
{
    [XmlRoot("integrationMessage")]
    [Serializable]
    public class BuildIntegrationRequest
        : ProjectRequest
    {
        private BuildCondition buildCondition = BuildCondition.ForceBuild;
        private List<NameValuePair> buildValues = new List<NameValuePair>();
        public BuildIntegrationRequest()
            : base()
        {
        }
        public BuildIntegrationRequest(string sessionToken)
            : base(sessionToken)
        {
        }
        public BuildIntegrationRequest(string sessionToken, string projectName)
            : base(sessionToken, projectName)
        {
        }
        [XmlAttribute("condition")]
        public BuildCondition BuildCondition
        {
            get { return buildCondition; }
            set { buildCondition = value; }
        }
        [XmlElement("buildValue")]
        public List<NameValuePair> BuildValues
        {
            get { return buildValues; }
            set { buildValues = value; }
        }
        public NameValuePair AddBuildValue(string name, string value)
        {
            NameValuePair credential = new NameValuePair(name, value);
            buildValues.Add(credential);
            return credential;
        }
    }
}
