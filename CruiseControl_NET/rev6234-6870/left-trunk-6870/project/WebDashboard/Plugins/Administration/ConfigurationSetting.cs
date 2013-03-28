using System.Xml.Serialization;
namespace ThoughtWorks.CruiseControl.WebDashboard.Plugins.Administration
{
    public class ConfigurationSetting
    {
        private string path;
        private string filter;
        private string name;
        private string value;
        private ConfigurationAttribute[] attributes = new ConfigurationAttribute[0];
        [XmlElement("path")]
        public string Path
        {
            get { return path; }
            set { path = value; }
        }
        [XmlElement("filter")]
        public string Filter
        {
            get { return filter; }
            set { filter = value; }
        }
        [XmlElement("name")]
        public string Name
        {
            get { return name; }
            set { name = value; }
        }
        [XmlElement("value")]
        public string Value
        {
            get { return value; }
            set { this.value = value; }
        }
        [XmlArray("attributes")]
        [XmlArrayItem("attribute")]
        public ConfigurationAttribute[] Attributes
        {
            get { return attributes; }
            set { attributes = value; }
        }
    }
}
