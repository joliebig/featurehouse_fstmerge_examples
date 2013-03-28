using System;
using System.Xml.Serialization;
namespace ThoughtWorks.CruiseControl.Remote.Messages
{
    [XmlRoot("changeConfigurationRequest")]
    [Serializable]
    public class ChangeConfigurationRequest
        : ProjectRequest
    {
        private string projectDefinition;
        private bool purgeWorkingDirectory;
        private bool purgeArtifactDirectory;
        private bool purgeSourceControlEnvironment;
        public ChangeConfigurationRequest()
        {
        }
        public ChangeConfigurationRequest(string sessionToken)
            : base(sessionToken)
        {
        }
        public ChangeConfigurationRequest(string sessionToken, string projectName)
            : base(sessionToken, projectName)
        {
        }
        [XmlElement("definition")]
        public string ProjectDefinition
        {
            get { return projectDefinition; }
            set { projectDefinition = value; }
        }
        [XmlAttribute("purgeWorking")]
        public bool PurgeWorkingDirectory
        {
            get { return purgeWorkingDirectory; }
            set { purgeWorkingDirectory = value; }
        }
        [XmlAttribute("purgeArtifact")]
        public bool PurgeArtifactDirectory
        {
            get { return purgeArtifactDirectory; }
            set { purgeArtifactDirectory = value; }
        }
        [XmlAttribute("purgeSourceControl")]
        public bool PurgeSourceControlEnvironment
        {
            get { return purgeSourceControlEnvironment; }
            set { purgeSourceControlEnvironment = value; }
        }
    }
}
