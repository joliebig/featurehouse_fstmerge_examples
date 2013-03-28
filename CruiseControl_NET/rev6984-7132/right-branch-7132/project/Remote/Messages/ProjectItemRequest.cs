using System;
using System.Collections.Generic;
using System.Text;
using System.Xml.Serialization;
namespace ThoughtWorks.CruiseControl.Remote.Messages
{
    [XmlRoot("projectItemMessage")]
    [Serializable]
    public class ProjectItemRequest
        : ProjectRequest
    {
        public ProjectItemRequest()
            : base()
        {
        }
        public ProjectItemRequest(string sessionToken)
            : base(sessionToken)
        {
        }
        public ProjectItemRequest(string sessionToken, string projectName)
            : base(sessionToken, projectName)
        {
        }
        [XmlAttribute("itemName")]
        public string ItemName { get;set;}
    }
}
