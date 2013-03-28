using System;
using System.Collections.Specialized;
using ThoughtWorks.CruiseControl.Remote;
using System.Collections.Generic;
using ThoughtWorks.CruiseControl.Remote.Parameters;
using System.Xml;
using System.IO;
namespace ThoughtWorks.CruiseControl.CCTrayLib.Monitoring
{
 public class HttpCruiseProjectManager : ICruiseProjectManager
 {
  private readonly string projectName;
        private readonly CruiseServerClientBase client;
  private readonly ICruiseServerManager serverManager;
  private Uri dashboardUri;
        private Uri parametersUri;
  private Uri webUrl;
  private string serverAlias = "local";
  public HttpCruiseProjectManager(CruiseServerClientBase client, string projectName, ICruiseServerManager serverManager)
  {
   this.projectName = projectName;
            this.client = client;
   this.serverManager = serverManager;
  }
        public void ForceBuild(string sessionToken, Dictionary<string, string> parameters)
  {
            client.SessionToken = sessionToken;
            client.ForceBuild(projectName, NameValuePair.FromDictionary(parameters));
  }
        public void AbortBuild(string sessionToken)
  {
            client.SessionToken = sessionToken;
            client.AbortBuild(projectName);
  }
  public void FixBuild(string sessionToken, string fixingUserName)
  {
   throw new NotImplementedException("Fix build not currently supported on projects monitored via HTTP");
  }
        public void StopProject(string sessionToken)
  {
            client.SessionToken = sessionToken;
            client.StopProject(projectName);
        }
        public void StartProject(string sessionToken)
  {
            client.SessionToken = sessionToken;
            client.StartProject(projectName);
        }
        public void CancelPendingRequest(string sessionToken)
  {
   throw new NotImplementedException("Cancel pending not currently supported on projects monitored via HTTP");
  }
  public string ProjectName
  {
   get { return projectName; }
  }
        public virtual ProjectStatusSnapshot RetrieveSnapshot()
        {
            ProjectStatusSnapshot snapshot = new ProjectStatusSnapshot();
            snapshot.Name = projectName;
            snapshot.Status = ItemBuildStatus.Unknown;
            return snapshot;
        }
        public virtual PackageDetails[] RetrievePackageList()
        {
            PackageDetails[] list = new PackageDetails[0];
            return list;
        }
        public void TransferFile(string fileName, Stream outputStream)
        {
            throw new InvalidOperationException();
        }
        public virtual List<ParameterBase> ListBuildParameters()
        {
            var parameters = client.ListBuildParameters(projectName);
            return parameters;
        }
 }
}
