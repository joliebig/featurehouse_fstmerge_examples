using System;
using System.Collections.Generic;
using System.IO;
using System.Text;
using System.Xml;
using ThoughtWorks.CruiseControl.Core.Util;
namespace ThoughtWorks.CruiseControl.Core.State
{
    public class XmlProjectStateManager
        : IProjectStateManager
    {
  private readonly IFileSystem fileSystem;
  private readonly IExecutionEnvironment executionEnvironment;
        private readonly string persistanceFileName;
        private Dictionary<string, bool> projectStates = null;
        private bool isLoading;
        public XmlProjectStateManager()
   : this(new SystemIoFileSystem(), new ExecutionEnvironment())
        {
        }
  public XmlProjectStateManager(IFileSystem fileSystem, IExecutionEnvironment executionEnvironment)
  {
   this.fileSystem = fileSystem;
   this.executionEnvironment = executionEnvironment;
   persistanceFileName = Path.Combine(this.executionEnvironment.GetDefaultProgramDataFolder(ApplicationType.Server), "ProjectsState.xml");
   fileSystem.EnsureFolderExists(persistanceFileName);
  }
  public string PersistanceFileName
  {
   get { return persistanceFileName; }
  }
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
                if (fileSystem.FileExists(persistanceFileName))
                {
                    isLoading = true;
                    try
                    {
                        var stateDocument = new XmlDocument();
                        using (var stream = fileSystem.OpenInputStream(persistanceFileName))
                        {
                            stateDocument.Load(stream);
                        }
                        foreach (XmlElement projectState in stateDocument.SelectNodes("/state/project"))
                        {
                            ChangeProjectState(projectState.InnerText, false);
                        }
                    }
                    finally
                    {
                        isLoading = false;
                    }
                }
            }
        }
        private void SaveProjectStates()
        {
            if (projectStates != null)
            {
                var stateDocument = new XmlDocument();
                var rootElement = stateDocument.CreateElement("state");
                stateDocument.AppendChild(rootElement);
                foreach (var projectName in projectStates.Keys)
                {
                    if (!projectStates[projectName])
                    {
                        var projectElement = stateDocument.CreateElement("project");
                        projectElement.InnerText = projectName;
                        rootElement.AppendChild(projectElement);
                    }
                }
                using (var stream = fileSystem.OpenOutputStream(persistanceFileName))
                {
                    var settings = new XmlWriterSettings()
                    {
                        Encoding = Encoding.UTF8,
                        OmitXmlDeclaration = true,
                        Indent = false
                    };
                    using (var xmlWriter = XmlTextWriter.Create(stream, settings))
                    {
                        stateDocument.Save(xmlWriter);
                    }
                }
            }
        }
        private void ChangeProjectState(string projectName, bool newState)
        {
            LoadProjectStates(false);
            var saveStates = true;
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
            if (saveStates && !isLoading) SaveProjectStates();
        }
    }
}
