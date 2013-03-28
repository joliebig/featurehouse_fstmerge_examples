using System.Xml.Serialization;
namespace ThoughtWorks.CruiseControl.CCTrayLib.Configuration
{
 [XmlType("Project")]
 public class CCTrayProject
 {
  private BuildServer buildServer;
  private string projectName;
  private bool showProject;
  public CCTrayProject()
  {
   buildServer = new BuildServer();
   showProject = true;
  }
  public CCTrayProject(string serverUrl, string projectName)
  {
            buildServer = new BuildServer(serverUrl);
   ServerUrl = serverUrl;
   ProjectName = projectName;
   showProject = true;
  }
  public CCTrayProject(BuildServer buildServer, string projectName)
  {
   this.buildServer = buildServer;
   this.projectName = projectName;
   showProject = true;
  }
  [XmlAttribute(AttributeName="serverUrl")]
  public string ServerUrl
  {
   get { return buildServer.Url; }
            set
            {
                if (buildServer.Url == null)
                {
                    buildServer = new BuildServer(value);
                }
                else
                {
                    buildServer = new BuildServer(value, buildServer.Transport, buildServer.ExtensionName, buildServer.ExtensionSettings);
                }
            }
  }
  [XmlAttribute(AttributeName="projectName")]
  public string ProjectName
  {
   get { return projectName; }
   set { projectName = value; }
  }
  [XmlAttribute(AttributeName = "showProject")]
  public bool ShowProject
  {
   get { return showProject; }
   set { showProject = value; }
  }
        [XmlAttribute(AttributeName = "extension")]
        public string ExtensionName
        {
            get { return buildServer.ExtensionName; }
            set
            {
                buildServer.Transport = string.IsNullOrEmpty(value) ? BuildServerTransport.HTTP : BuildServerTransport.Extension;
                buildServer.ExtensionName = value;
            }
        }
        [XmlAttribute(AttributeName = "settings")]
        public string ExtensionSettings
        {
            get { return buildServer.ExtensionSettings; }
            set { buildServer.ExtensionSettings = value; }
        }
        [XmlAttribute(AttributeName = "securityType")]
        public string SecurityType
        {
            get { return buildServer.SecurityType; }
            set { buildServer.SecurityType = value; }
        }
        [XmlAttribute(AttributeName = "securitySettings")]
        public string SecuritySettings
        {
            get { return buildServer.SecuritySettings; }
            set { buildServer.SecuritySettings = value; }
        }
  [XmlIgnore]
  public BuildServer BuildServer
  {
   get { return buildServer; }
   set { buildServer = value; }
  }
        public override bool Equals(object obj)
        {
            CCTrayProject objToCompare = obj as CCTrayProject;
            if (objToCompare != null)
            {
                bool isSame = string.Equals(projectName, objToCompare.projectName);
                if (isSame)
                {
                    if ((buildServer != null) && (objToCompare.buildServer != null))
                    {
                        isSame = string.Equals(buildServer.Url, objToCompare.buildServer.Url);
                    }
                    else if ((buildServer != null) && (objToCompare.buildServer != null))
                    {
                        isSame = true;
                    }
                    else
                    {
                        isSame = false;
                    }
                }
                return isSame;
            }
            else
            {
                return false;
            }
        }
        public override int GetHashCode()
        {
            string hashCode = string.Empty;
            if (projectName != null) hashCode = projectName;
            if (buildServer != null)
            {
                hashCode += buildServer.Url;
            }
            return hashCode.GetHashCode();
        }
 }
}
