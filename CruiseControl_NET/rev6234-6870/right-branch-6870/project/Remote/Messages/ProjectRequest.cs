using System;
using System.Xml.Serialization;
namespace ThoughtWorks.CruiseControl.Remote.Messages
{
    [XmlRoot("projectMessage")]
    [Serializable]
    public class ProjectRequest
        : ServerRequest
    {
        private string projectName;
        public ProjectRequest()
        {
        }
        public ProjectRequest(string sessionToken)
            : base(sessionToken)
        {
        }
        public ProjectRequest(string sessionToken, string projectName)
            : base(sessionToken)
        {
            this.projectName = projectName;
        }
        [XmlAttribute("project")]
        public string ProjectName
        {
            get { return projectName; }
            set { projectName = value; }
        }
    }
}
