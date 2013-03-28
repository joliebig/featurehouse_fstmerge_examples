using System;
using System.Collections.Generic;
using System.Xml.Serialization;
namespace ThoughtWorks.CruiseControl.Remote.Security
{
    [Serializable]
    public class SecurityCheckDiagnostics
    {
        private string permissionName;
        private string projectName;
        private string userName;
        private bool isAllowed;
        [XmlAttribute("permission")]
        public string Permission
        {
            get { return permissionName; }
            set { permissionName = value; }
        }
        [XmlAttribute("project")]
        public string Project
        {
            get { return projectName; }
            set { projectName = value; }
        }
        [XmlAttribute("user")]
        public string User
        {
            get { return userName; }
            set { userName = value; }
        }
        [XmlAttribute("allowed")]
        public bool IsAllowed
        {
            get { return isAllowed; }
            set { isAllowed = value; }
        }
    }
}
