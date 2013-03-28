using System.Collections.Generic;
using System.Xml.Serialization;
namespace ThoughtWorks.CruiseControl.WebDashboard.Plugins.Administration
{
    public class FileLocation
    {
        private string location;
        private List<string> files = new List<string>();
        [XmlElement("location")]
        public string Location
        {
            get { return location; }
            set { location = value; }
        }
        [XmlArray("files")]
        [XmlArrayItem("file")]
        public List<string> Files
        {
            get { return files; }
        }
    }
}
