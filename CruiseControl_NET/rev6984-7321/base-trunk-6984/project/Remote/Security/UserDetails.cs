using System;
using System.Collections.Generic;
using System.Text;
using System.Xml.Serialization;
namespace ThoughtWorks.CruiseControl.Remote.Security
{
    [XmlRoot("user")]
    [Serializable]
    public class UserDetails
    {
        private string userName;
        private string displayName;
        private string type;
        public UserDetails()
        {
        }
        public UserDetails(string userName)
            : this(userName, null, null)
        {
        }
        public UserDetails(string userName, string displayName)
            : this(userName, displayName, null)
        {
        }
        public UserDetails(string userName, string displayName, string type)
        {
            this.userName = userName;
            this.displayName = displayName;
            this.type = type;
        }
        [XmlAttribute("name")]
        public string UserName
        {
            get { return userName; }
            set { userName = value; }
        }
        [XmlAttribute("display")]
        public string DisplayName
        {
            get { return displayName; }
            set { displayName = value; }
        }
        [XmlAttribute("type")]
        public string Type
        {
            get { return type; }
            set { type = value; }
        }
    }
}
