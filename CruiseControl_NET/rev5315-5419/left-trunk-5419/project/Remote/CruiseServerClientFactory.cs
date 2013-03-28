using System;
using System.Collections.Generic;
using System.Text;
namespace ThoughtWorks.CruiseControl.Remote
{
    public static class CruiseServerClientFactory
    {
        public static CruiseServerClientBase GenerateClient(string address)
        {
            var serverUri = new Uri(address);
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
            var client = new CruiseServerClient(connection);
            return client;
        }
        public static CruiseServerClientBase GenerateClient(string address, string targetServer)
        {
            var client = GenerateClient(address);
            client.TargetServer = targetServer;
            return client;
        }
        public static CruiseServerClientBase GenerateHttpClient(string address)
        {
            var connection = new HttpConnection(address);
            var client = new CruiseServerClient(connection);
            return client;
        }
        public static CruiseServerClientBase GenerateHttpClient(string address, string targetServer)
        {
            var client = GenerateHttpClient(address);
            client.TargetServer = targetServer;
            return client;
        }
        public static CruiseServerClientBase GenerateRemotingClient(string address)
        {
            var connection = new RemotingConnection(address);
            var client = new CruiseServerClient(connection);
            return client;
        }
        public static CruiseServerClientBase GenerateRemotingClient(string address, string targetServer)
        {
            var client = GenerateRemotingClient(address);
            client.TargetServer = targetServer;
            return client;
        }
    }
}
