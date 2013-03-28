using System;
using ThoughtWorks.CruiseControl.CCTrayLib.Configuration;
using ThoughtWorks.CruiseControl.Remote;
namespace ThoughtWorks.CruiseControl.CCTrayLib.Monitoring
{
 public class HttpCruiseServerManager : ICruiseServerManager
 {
  private readonly IWebRetriever webRetriever;
  private readonly IDashboardXmlParser dashboardXmlParser;
  private readonly BuildServer buildServer;
  private readonly string displayName;
  public HttpCruiseServerManager(IWebRetriever webRetriever, IDashboardXmlParser dashboardXmlParser,
   BuildServer buildServer)
  {
   this.webRetriever = webRetriever;
   this.dashboardXmlParser = dashboardXmlParser;
   this.buildServer = buildServer;
   displayName = GetDisplayNameFromUri(buildServer.Uri);
  }
  public string ServerUrl
  {
   get { return buildServer.Url; }
  }
  public string DisplayName
  {
   get { return displayName; }
  }
  public BuildServerTransport Transport
  {
   get { return buildServer.Transport; }
  }
  public void CancelPendingRequest(string projectName)
  {
   throw new NotImplementedException("Cancel pending not currently supported on servers monitored via HTTP");
  }
        public CruiseServerSnapshot GetCruiseServerSnapshot()
  {
   string xml = webRetriever.Get(buildServer.Uri);
   return dashboardXmlParser.ExtractAsCruiseServerSnapshot(xml);
  }
  private static string GetDisplayNameFromUri(Uri uri)
  {
   const int DefaultHttpPort = 80;
   if (uri.Port == DefaultHttpPort)
    return uri.Host;
   return uri.Host + ":" + uri.Port;
  }
 }
}
