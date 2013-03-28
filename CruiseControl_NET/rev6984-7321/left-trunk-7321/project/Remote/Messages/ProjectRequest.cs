using System;
using System.ComponentModel;
using System.Xml.Serialization;
namespace ThoughtWorks.CruiseControl.Remote.Messages
{
    [XmlRoot("projectMessage")]
    [Serializable]
    public class ProjectRequest
        : ServerRequest
    {
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
            this.ProjectName = projectName;
        }
        [XmlAttribute("project")]
        public string ProjectName { get; set; }
        [XmlAttribute("compress")]
        [DefaultValue(false)]
        public bool CompressData { get; set; }
    }
}
