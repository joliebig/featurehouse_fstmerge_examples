using System.Collections.Generic;
using System.Xml;
using Exortech.NetReflector;
namespace ThoughtWorks.CruiseControl.Core.Publishers
{
    [ReflectorType("defaultManifestGenerator")]
    public class ManifestGenerator
        : IManifestGenerator
    {
        public XmlDocument Generate(IIntegrationResult result, string[] packagedFiles)
        {
            XmlDocument manifest = new XmlDocument();
            XmlElement rootElement = manifest.CreateElement("manifest");
            manifest.AppendChild(rootElement);
            AddManifestHeader(result, rootElement);
            foreach (string file in packagedFiles)
            {
                XmlElement fileElement = manifest.CreateElement("file");
                fileElement.SetAttribute("name", file);
                rootElement.AppendChild(fileElement);
            }
            return manifest;
        }
        private void AddManifestHeader(IIntegrationResult result, XmlElement rootElement)
        {
            XmlDocument manifest = rootElement.OwnerDocument;
            XmlElement headerElement = manifest.CreateElement("header");
            rootElement.AppendChild(headerElement);
            headerElement.SetAttribute("project", result.ProjectName);
            headerElement.SetAttribute("label", result.Label);
            headerElement.SetAttribute("build", result.BuildCondition.ToString());
            headerElement.SetAttribute("status", result.Status.ToString());
            var changes = new Dictionary<string, XmlElement>();
            foreach (Modification modification in result.Modifications)
            {
                if (!changes.ContainsKey(modification.ChangeNumber))
                {
                    XmlElement modificationElement = manifest.CreateElement("modification");
                    headerElement.AppendChild(modificationElement);
                    modificationElement.SetAttribute("user", modification.UserName);
                    modificationElement.SetAttribute("changeNumber", modification.ChangeNumber);
                    if (!string.IsNullOrEmpty(modification.Comment))
                    {
                        XmlElement commentElement = manifest.CreateElement("comment");
                        commentElement.InnerText = modification.Comment;
                        modificationElement.AppendChild(commentElement);
                    }
                    modificationElement.SetAttribute("time", modification.ModifiedTime.ToString("s"));
                    changes.Add(modification.ChangeNumber, modificationElement);
                }
                XmlElement fileElement = manifest.CreateElement("file");
                fileElement.SetAttribute("name", modification.FileName);
                fileElement.SetAttribute("type", modification.Type);
                changes[modification.ChangeNumber].AppendChild(fileElement);
            }
        }
    }
}
