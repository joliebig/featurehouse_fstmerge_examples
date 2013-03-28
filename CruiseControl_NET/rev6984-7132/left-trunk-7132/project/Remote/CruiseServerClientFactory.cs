using System;
using System.Collections.Generic;
namespace ThoughtWorks.CruiseControl.Remote
{
    public class CruiseServerClientFactory
        : ICruiseServerClientFactory
    {
        private Dictionary<string, ClientInitialiser> initialisers = new Dictionary<string, ClientInitialiser>();
        private Dictionary<string, CruiseServerClientBase> clients = new Dictionary<string, CruiseServerClientBase>();
        public CruiseServerClientFactory()
        {
            InitialiseDefaultTcpClient();
            InitialiseDefaultHttpClient();
            UseClientCaching = true;
        }
        public bool UseClientCaching { get; set; }
        public CruiseServerClientBase GenerateClient(string address)
        {
            var client = GenerateClient(address, new ClientStartUpSettings());
            return client;
        }
        public CruiseServerClientBase GenerateClient(string address, string targetServer)
        {
            var client = GenerateClient(address, targetServer, new ClientStartUpSettings());
            return client;
        }
        public CruiseServerClientBase GenerateClient(string address, ClientStartUpSettings settings)
        {
            var serverUri = new Uri(address);
            var transport = serverUri.Scheme.ToLower();
            if (initialisers.ContainsKey(transport))
            {
                if (UseClientCaching && clients.ContainsKey(address))
                {
                    return clients[address];
                }
                else
                {
                    var client = initialisers[transport](address, settings);
                    if (UseClientCaching) clients.Add(address, client);
                    return client;
                }
            }
            else
            {
                throw new ApplicationException("Unknown transport protocol");
            }
        }
        public CruiseServerClientBase GenerateClient(string address, string targetServer, ClientStartUpSettings settings)
        {
            var client = GenerateClient(address, settings);
            client.TargetServer = targetServer;
            return client;
        }
        public CruiseServerClientBase GenerateHttpClient(string address)
        {
            var client = GenerateHttpClient(address, new ClientStartUpSettings());
            return client;
        }
        public CruiseServerClientBase GenerateHttpClient(string address, string targetServer)
        {
            var client = GenerateHttpClient(address, targetServer, new ClientStartUpSettings());
            return client;
        }
        public CruiseServerClientBase GenerateHttpClient(string address, ClientStartUpSettings settings)
        {
            var client = clients.ContainsKey(address) ?
                clients[address] :
                null;
            if (client == null)
            {
                if (settings.BackwardsCompatable)
                {
                    client = new CruiseServerHttpClient(address);
                }
                else
                {
                    IServerConnection connection = new HttpConnection(address);
                    connection = BuildUpConnection(connection, settings);
                    client = new CruiseServerClient(connection);
                }
                if (UseClientCaching) clients.Add(address, client);
            }
            return client;
        }
        public CruiseServerClientBase GenerateHttpClient(string address, string targetServer, ClientStartUpSettings settings)
        {
            var client = GenerateHttpClient(address, settings);
            client.TargetServer = targetServer;
            return client;
        }
        public CruiseServerClientBase GenerateRemotingClient(string address)
        {
            var client = GenerateRemotingClient(address, new ClientStartUpSettings());
            return client;
        }
        public CruiseServerClientBase GenerateRemotingClient(string address, string targetServer)
        {
            var client = GenerateRemotingClient(address, targetServer, new ClientStartUpSettings());
            return client;
        }
        public CruiseServerClientBase GenerateRemotingClient(string address, ClientStartUpSettings settings)
        {
            var client = clients.ContainsKey(address) ?
                clients[address] :
                null;
            if (client == null)
            {
                if (settings.BackwardsCompatable)
                {
                    client = new CruiseServerRemotingClient(address);
                }
                else
                {
                    IServerConnection connection = new RemotingConnection(address);
                    connection = BuildUpConnection(connection, settings);
                    client = new CruiseServerClient(connection);
                }
                if (UseClientCaching) clients.Add(address, client);
            }
            return client;
        }
        public CruiseServerClientBase GenerateRemotingClient(string address, string targetServer, ClientStartUpSettings settings)
        {
            var client = GenerateRemotingClient(address, settings);
            client.TargetServer = targetServer;
            return client;
        }
        public void InitialiseDefaultHttpClient()
        {
            initialisers["http"] = (address, settings) =>
            {
                if (settings.BackwardsCompatable)
                {
                    return new CruiseServerHttpClient(address);
                }
                else
                {
                    IServerConnection connection = new HttpConnection(address);
                    connection = BuildUpConnection(connection, settings);
                    return new CruiseServerClient(connection);
                }
            };
        }
        public void InitialiseDefaultTcpClient()
        {
            initialisers["tcp"] = (address, settings) =>
            {
                if (settings.BackwardsCompatable)
                {
                    return new CruiseServerRemotingClient(address);
                }
                else
                {
                    IServerConnection connection = new RemotingConnection(address);
                    connection = BuildUpConnection(connection, settings);
                    return new CruiseServerClient(connection);
                }
            };
        }
        public void AddInitialiser(string transport, ClientInitialiser initialiser)
        {
            initialisers[transport.ToLower()] = initialiser;
        }
        public static IServerConnection BuildUpConnection(IServerConnection connection, ClientStartUpSettings settings)
        {
            if (settings.UseEncryption) connection = new EncryptingConnection(connection);
            return connection;
        }
        public virtual void ResetCache()
        {
            clients.Clear();
        }
        public virtual void ResetCache(string address)
        {
            clients.Remove(address);
        }
        public delegate CruiseServerClientBase ClientInitialiser(string address, ClientStartUpSettings settings);
    }
}
