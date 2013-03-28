using System;
using System.Collections.Generic;
using System.Text;
namespace ThoughtWorks.CruiseControl.Remote
{
    public static class CruiseServerClientFactory
    {
        public static CruiseServerClient GenerateClient(string address)
        {
            Uri serverUri = new Uri(address);
            IServerConnection connection = null;
            switch (serverUri.Scheme.ToLower())
            {
                case "http":
                    connection = new HttpConnection(address);
                    break;
                case "tcp":
                    connection = new RemotingConnection(address);
                    break;
                default:
                    throw new ApplicationException("Unknown transport protocol");
            }
            CruiseServerClient client = new CruiseServerClient(connection);
            return client;
        }
        public static CruiseServerClient GenerateClient(string address, string targetServer)
        {
            CruiseServerClient client = GenerateClient(address);
            client.TargetServer = targetServer;
            return client;
        }
        public static CruiseServerClient GenerateHttpClient(string address)
        {
            IServerConnection connection = new HttpConnection(address);
            CruiseServerClient client = new CruiseServerClient(connection);
            return client;
        }
        public static CruiseServerClient GenerateHttpClient(string address, string targetServer)
        {
            CruiseServerClient client = GenerateHttpClient(address);
            client.TargetServer = targetServer;
            return client;
        }
        public static CruiseServerClient GenerateRemotingClient(string address)
        {
            IServerConnection connection = new RemotingConnection(address);
            CruiseServerClient client = new CruiseServerClient(connection);
            return client;
        }
        public static CruiseServerClient GenerateRemotingClient(string address, string targetServer)
        {
            CruiseServerClient client = GenerateRemotingClient(address);
            client.TargetServer = targetServer;
            return client;
        }
    }
}
