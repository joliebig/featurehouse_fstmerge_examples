using System;
using System.IO;
using System.Xml;
using Exortech.NetReflector;
namespace ThoughtWorks.CruiseControl.Core.Publishers
{
    [ReflectorType("importManifest")]
    public class ManifestImporter
        : IManifestGenerator
    {
        private string fileName;
        [ReflectorProperty("filename")]
        public string FileName
        {
            get { return fileName; }
            set { fileName = value; }
        }
        public XmlDocument Generate(IIntegrationResult result, string[] packagedFiles)
        {
            if (string.IsNullOrEmpty(fileName)) throw new ArgumentOutOfRangeException("FileName");
            XmlDocument manifest = new XmlDocument();
            string actualFile = fileName;
            if (!Path.IsPathRooted(actualFile)) actualFile = result.BaseFromWorkingDirectory(actualFile);
            manifest.Load(actualFile);
            return manifest;
        }
    }
}
