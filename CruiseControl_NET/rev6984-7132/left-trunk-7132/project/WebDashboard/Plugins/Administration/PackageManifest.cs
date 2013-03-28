using System.Collections.Generic;
using System.Xml.Serialization;
using System;
namespace ThoughtWorks.CruiseControl.WebDashboard.Plugins.Administration
{
    [XmlRoot("package")]
    public class PackageManifest
        : IComparable
    {
        private string name;
        private string description;
        private string fileName;
        private bool isInstalled;
        private PackageType type;
        private List<FileLocation> fileLocations = new List<FileLocation>();
        private List<ConfigurationSetting> configSettings = new List<ConfigurationSetting>();
        [XmlElement("name")]
        public string Name
        {
            get { return name; }
            set { name = value; }
        }
        [XmlElement("description")]
        public string Description
        {
            get { return description; }
            set { description = value; }
        }
        [XmlElement("type")]
        public PackageType Type
        {
            get { return type; }
            set { type = value; }
        }
        [XmlIgnore]
        public string FileName
        {
            get { return fileName; }
            set { fileName = value; }
        }
        [XmlIgnore]
        public bool IsInstalled
        {
            get { return isInstalled; }
            set { isInstalled = value; }
        }
        [XmlArray("folders")]
        [XmlArrayItem("folder")]
        public List<FileLocation> FileLocations
        {
            get { return fileLocations; }
        }
        [XmlArray("configuration")]
        [XmlArrayItem("setting")]
        public List<ConfigurationSetting> ConfigurationSettings
        {
            get { return configSettings; }
        }
        public override string ToString()
        {
            return name;
        }
        public int CompareTo(object obj)
        {
            if (obj is PackageManifest)
            {
                return string.Compare(name, (obj as PackageManifest).name);
            }
            else
            {
                return 0;
            }
        }
    }
}
