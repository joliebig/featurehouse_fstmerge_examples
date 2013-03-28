using System;
using System.Collections.Generic;
using System.Text;
using System.Xml.Serialization;
namespace ThoughtWorks.CruiseControl.WebDashboard.Plugins.Administration
{
    public class ConfigurationAttribute
    {
        private string name;
        private string value;
        [XmlAttribute("name")]
        public string Name
        {
            get { return name; }
            set { name = value; }
        }
        [XmlAttribute("value")]
        public string Value
        {
            get { return value; }
            set { this.value = value; }
        }
    }
}
