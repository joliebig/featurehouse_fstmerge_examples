namespace ThoughtWorks.CruiseControl.Remote
{
    public static class CruiseServerClientFactoryExtensions
    {
        public static CruiseServerClientBase GenerateWcfClient(this CruiseServerClientFactory factory, string address)
        {
            var client = GenerateWcfClient(factory, address, new ClientStartUpSettings());
            return client;
        }
        public static CruiseServerClientBase GenerateWcfClient(this CruiseServerClientFactory factory, string address, string targetServer)
        {
            var client = GenerateWcfClient(factory, address, targetServer, new ClientStartUpSettings());
            return client;
        }
        public static CruiseServerClientBase GenerateWcfClient(this CruiseServerClientFactory factory, string address, ClientStartUpSettings settings)
        {
            CruiseServerClientBase client;
            var connection = new WcfConnection(address);
            client = new CruiseServerClient(connection);
            return client;
        }
        public static CruiseServerClientBase GenerateWcfClient(this CruiseServerClientFactory factory, string address, string targetServer, ClientStartUpSettings settings)
        {
            var client = GenerateWcfClient(factory, address, settings);
            client.TargetServer = targetServer;
            return client;
        }
        public static void SwitchHtppToWcf(this CruiseServerClientFactory factory)
        {
            factory.AddInitialiser("http", (address, settings) =>
            {
                var client = new CruiseServerClient(new WcfConnection(address));
                return client;
            });
        }
    }
}
