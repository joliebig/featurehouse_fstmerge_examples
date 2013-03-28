using Exortech.NetReflector;
using System;
using System.Collections.Generic;
using System.IO;
using System.Xml;
namespace ThoughtWorks.CruiseControl.Core.Security
{
    public class ServerSecurityConfigurationInformation
    {
        private ISecurityManager securityManager;
        private List<ProjectSecurityConfigurationInformation> projectsList = new List<ProjectSecurityConfigurationInformation>();
        [ReflectorProperty("manager", InstanceTypeKey = "type")]
        public ISecurityManager Manager
        {
            get { return securityManager; }
            set { securityManager = value; }
        }
        [ReflectorProperty("projects")]
        public List<ProjectSecurityConfigurationInformation> Projects
        {
            get { return projectsList; }
        }
        public void AddProject(IProject project)
        {
            if (project.Security != null)
            {
                ProjectSecurityConfigurationInformation info = new ProjectSecurityConfigurationInformation();
                info.Name = project.Name;
                info.Security = project.Security;
                projectsList.Add(info);
            }
        }
        public override string ToString()
        {
            StringWriter buffer = new StringWriter();
            new ReflectorTypeAttribute("security").Write(new XmlTextWriter(buffer), this);
            string xmlData = HidePasswords(buffer.ToString());
            return xmlData;
        }
        public string HidePasswords(string xmlData)
        {
            XmlDocument document = new XmlDocument();
            document.LoadXml(xmlData);
            foreach (XmlElement passwordNode in document.SelectNodes("//password"))
            {
                passwordNode.InnerText = new string('*', 10);
            }
            return document.OuterXml;
        }
    }
}
