using System;
namespace ThoughtWorks.CruiseControl.Remote
{
    public interface ICruiseServerClientFactory
    {
        CruiseServerClientBase GenerateClient(string address, ClientStartUpSettings settings);
        CruiseServerClientBase GenerateClient(string address, string targetServer, ClientStartUpSettings settings);
        CruiseServerClientBase GenerateClient(string address);
        CruiseServerClientBase GenerateClient(string address, string targetServer);
        CruiseServerClientBase GenerateHttpClient(string address, string targetServer);
        CruiseServerClientBase GenerateHttpClient(string address);
        CruiseServerClientBase GenerateHttpClient(string address, ClientStartUpSettings settings);
        CruiseServerClientBase GenerateHttpClient(string address, string targetServer, ClientStartUpSettings settings);
        CruiseServerClientBase GenerateRemotingClient(string address, ClientStartUpSettings settings);
        CruiseServerClientBase GenerateRemotingClient(string address, string targetServer, ClientStartUpSettings settings);
        CruiseServerClientBase GenerateRemotingClient(string address);
        CruiseServerClientBase GenerateRemotingClient(string address, string targetServer);
        void ResetCache();
        void ResetCache(string address);
    }
}
