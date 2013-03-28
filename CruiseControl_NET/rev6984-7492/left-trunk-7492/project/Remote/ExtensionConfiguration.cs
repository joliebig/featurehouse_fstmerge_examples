using System;
using System.Xml;
namespace ThoughtWorks.CruiseControl.Remote
{
    public class ExtensionConfiguration
    {
        private string type;
        private XmlElement[] configurationItems;
        public string Type
        {
            get { return type; }
            set { type = value; }
        }
        public XmlElement[] Items
        {
            get { return configurationItems; }
            set { configurationItems = value; }
        }
    }
}
