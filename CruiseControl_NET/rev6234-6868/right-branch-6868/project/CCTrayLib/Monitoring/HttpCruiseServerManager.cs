using System;
using System.Collections.Specialized;
using System.Xml;
using ThoughtWorks.CruiseControl.CCTrayLib.Configuration;
using ThoughtWorks.CruiseControl.Remote;
using ThoughtWorks.CruiseControl.Remote.Messages;
namespace ThoughtWorks.CruiseControl.CCTrayLib.Monitoring
{
 public class HttpCruiseServerManager : ICruiseServerManager
 {
        private readonly CruiseServerClientBase manager;
        private readonly BuildServer configuration;
  private readonly string displayName;
        private string sessionToken;
        public HttpCruiseServerManager(CruiseServerClientBase manager,
   BuildServer buildServer)
  {
            this.manager = manager;
            this.configuration = buildServer;
   displayName = GetDisplayNameFromUri(buildServer.Uri);
  }
        public BuildServer Configuration
  {
            get { return configuration; }
  }
        public string SessionToken
        {
            get { return sessionToken; }
        }
  public string DisplayName
  {
   get { return displayName; }
  }
  public void CancelPendingRequest(string projectName)
  {
   throw new NotImplementedException("Cancel pending not currently supported on servers monitored via HTTP");
  }
        public CruiseServerSnapshot GetCruiseServerSnapshot()
        {
            try
            {
                var snapshot = manager.GetCruiseServerSnapshot();
                return snapshot;
            }
            catch (System.Net.WebException)
            {
                return new CruiseServerSnapshot();
            }
        }
  private static string GetDisplayNameFromUri(Uri uri)
  {
   const int DefaultHttpPort = 80;
   if (uri.Port == DefaultHttpPort)
    return uri.Host;
   return uri.Host + ":" + uri.Port;
  }
        public bool Login()
        {
            if (configuration.SecurityType != null)
            {
                if (sessionToken != null) Logout();
                IAuthenticationMode authentication = ExtensionHelpers.RetrieveAuthenticationMode(configuration.SecurityType);
                authentication.Settings = configuration.SecuritySettings;
                var request = authentication.GenerateCredentials();
                if (manager.Login(request.Credentials))
                {
                    sessionToken = manager.SessionToken;
                }
                else
                {
                    sessionToken = null;
                }
                return (sessionToken != null);
            }
            else
            {
                return true;
            }
        }
        public void Logout()
        {
            if (sessionToken != null)
            {
                manager.Logout();
                sessionToken = null;
            }
        }
 }
}
