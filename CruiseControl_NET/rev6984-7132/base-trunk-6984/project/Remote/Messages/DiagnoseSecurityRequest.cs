using System;
using System.Collections.Generic;
using System.Text;
using System.Xml.Serialization;
namespace ThoughtWorks.CruiseControl.Remote.Messages
{
    [XmlRoot("diagnoseSecurityMessage")]
    [Serializable]
    public class DiagnoseSecurityRequest
        : ServerRequest
    {
        private List<string> projects = new List<string>();
        private string userName;
        [XmlElement("project")]
        public List<string> Projects
        {
            get { return projects; }
            set { projects = value; }
        }
        [XmlAttribute("userName")]
        public string UserName
        {
            get { return userName; }
            set { userName = value; }
        }
    }
}
