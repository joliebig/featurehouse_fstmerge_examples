using System;
using System.Collections.Generic;
using System.Text;
using System.Xml.Serialization;
using System.IO;
namespace ThoughtWorks.CruiseControl.Remote
{
    [XmlRoot("package")]
    [Serializable]
    public class PackageDetails
    {
        private string name;
        private string buildLabel;
        private DateTime dateTime;
        private int numberOfFiles;
        private long size;
        private string fileName;
        public PackageDetails()
        {
        }
        public PackageDetails(string package)
        {
            this.fileName = package;
        }
        [XmlAttribute("name")]
        public string Name
        {
            get { return name; }
            set { name = value; }
        }
        [XmlElement("buildLabel")]
        public string BuildLabel
        {
            get { return buildLabel; }
            set { buildLabel = value; }
        }
        [XmlAttribute("dateTime")]
        public DateTime DateTime
        {
            get { return dateTime; }
            set { dateTime = value; }
        }
        [XmlAttribute("numberOfFiles")]
        public int NumberOfFiles
        {
            get { return numberOfFiles; }
            set { numberOfFiles = value; }
        }
        [XmlAttribute("size")]
        public long Size
        {
            get { return size; }
            set { size = value; }
        }
        [XmlElement("fileName")]
        public string FileName
        {
            get { return fileName; }
            set { fileName = value; }
        }
    }
}
