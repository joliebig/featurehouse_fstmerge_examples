using System;
using System.Collections.Generic;
using System.IO;
using System.Text;
using System.Xml;
namespace ThoughtWorks.CruiseControl.Core.State
{
    public class XmlProjectStateManager
        : IProjectStateManager
    {
        private readonly string persistanceFileName = Path.Combine(Environment.CurrentDirectory, "ProjectsState.xml");
        private Dictionary<string, bool> projectStates = null;
        public void RecordProjectAsStopped(string projectName)
        {
            ChangeProjectState(projectName, false);
        }
        public void RecordProjectAsStartable(string projectName)
        {
            ChangeProjectState(projectName, true);
        }
        public bool CheckIfProjectCanStart(string projectName)
        {
            LoadProjectStates(false);
            if (projectStates.ContainsKey(projectName))
            {
                return projectStates[projectName];
            }
            else
            {
                return true;
            }
        }
        private void LoadProjectStates(bool forceLoad)
        {
            if (forceLoad || (projectStates == null))
            {
                projectStates = new Dictionary<string, bool>();
                if (File.Exists(persistanceFileName))
                {
                    XmlDocument stateDocument = new XmlDocument();
                    stateDocument.Load(persistanceFileName);
                    foreach (XmlElement projectState in stateDocument.SelectNodes("/state/project"))
                    {
                        ChangeProjectState(projectState.InnerText, false);
                    }
                }
            }
        }
        private void SaveProjectStates()
        {
            if (projectStates != null)
            {
                XmlDocument stateDocument = new XmlDocument();
                XmlElement rootElement = stateDocument.CreateElement("state");
                stateDocument.AppendChild(rootElement);
                foreach (string projectName in projectStates.Keys)
                {
                    if (!projectStates[projectName])
                    {
                        XmlElement projectElement = stateDocument.CreateElement("project");
                        projectElement.InnerText = projectName;
                        rootElement.AppendChild(projectElement);
                    }
                }
                using (XmlTextWriter xmlWriter = new XmlTextWriter(persistanceFileName, Encoding.UTF8))
                {
                    xmlWriter.Formatting = Formatting.Indented;
                    stateDocument.Save(xmlWriter);
                }
            }
        }
        private void ChangeProjectState(string projectName, bool newState)
        {
            LoadProjectStates(false);
            bool saveStates = true;
            if (projectStates.ContainsKey(projectName))
            {
                if (projectStates[projectName] != newState)
                {
                    projectStates[projectName] = newState;
                }
                else
                {
                    saveStates = false;
                }
            }
            else
            {
                projectStates.Add(projectName, newState);
            }
            if (saveStates) SaveProjectStates();
        }
    }
}
