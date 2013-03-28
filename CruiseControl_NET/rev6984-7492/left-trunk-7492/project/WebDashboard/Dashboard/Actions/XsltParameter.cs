using System;
using System.Collections.Generic;
using System.Runtime.Serialization;
using System.Text;
using System.Xml;
using System.Xml.Serialization;
using Exortech.NetReflector;
namespace ThoughtWorks.CruiseControl.Remote
{
    [Serializable]
    [ReflectorType("xsltParameter")]
    public class XsltParameter
    {
        private string name;
        private string namedValue;
        [XmlAttribute("name")]
        [ReflectorProperty("name")]
        public string Name
        {
            get { return name; }
            set { name = value; }
        }
        [XmlAttribute("value")]
        [ReflectorProperty("value")]
        public string Value
        {
            get { return namedValue; }
            set { namedValue = value; }
        }
    }
}
