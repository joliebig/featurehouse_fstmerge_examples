namespace ThoughtWorks.CruiseControl.Core.Publishers
{
    using System.Collections.Generic;
    using Exortech.NetReflector;
    using ThoughtWorks.CruiseControl.Core.Tasks;
    using ThoughtWorks.CruiseControl.Core.Util;
    using ThoughtWorks.CruiseControl.Remote;
    [ReflectorType("forcebuild")]
 public class ForceBuildPublisher
        : TaskBase
 {
        private readonly ICruiseServerClientFactory factory;
        private string BuildForcerName="BuildForcer";
        public ForceBuildPublisher()
            : this(new CruiseServerClientFactory())
  {}
        public ForceBuildPublisher(ICruiseServerClientFactory factory)
  {
   this.factory = factory;
            this.ServerUri = string.Format("tcp://localhost:21234/{0}", RemoteCruiseServer.ManagerUri);
            this.IntegrationStatus = IntegrationStatus.Success;
  }
        [ReflectorProperty("project")]
        public string Project { get; set; }
        [ReflectorProperty("enforcerName", Required = false)]
        public string EnforcerName
        {
            get { return BuildForcerName; }
            set { BuildForcerName = value; }
        }
        [ReflectorProperty("serverUri", Required = false)]
        public string ServerUri { get; set; }
        [ReflectorProperty("integrationStatus", Required = false)]
        public IntegrationStatus IntegrationStatus { get; set; }
        [ReflectorProperty("security", Required = false)]
        public NameValuePair[] SecurityCredentials { get; set; }
        [ReflectorProperty("parameters", Required = false)]
        public NameValuePair[] Parameters { get; set; }
        public ILogger Logger { get; set; }
        protected override bool Execute(IIntegrationResult result)
  {
   if (IntegrationStatus != result.Status) return false;
            var logger = Logger ?? new DefaultLogger();
            result.BuildProgressInformation.SignalStartRunTask(!string.IsNullOrEmpty(Description) ? Description : "Running for build publisher");
            var loggedIn = false;
            logger.Debug("Generating client for url '{0}'", ServerUri);
            var client = factory.GenerateClient(ServerUri);
            if ((SecurityCredentials != null) && (SecurityCredentials.Length > 0))
            {
                logger.Debug("Logging in");
                if (client.Login(new List<NameValuePair>(SecurityCredentials)))
                {
                    loggedIn = true;
                    logger.Debug("Logged on server, session token is " + client.SessionToken);
                }
                else
                {
                    logger.Warning("Unable to login to remote server");
                }
            }
            logger.Info("Sending ForceBuild request to '{0}' on '{1}'", Project, ServerUri);
            client.ForceBuild(Project, new List<NameValuePair>(Parameters ?? new NameValuePair[0]));
            if (loggedIn)
            {
                logger.Debug("Logging out");
                client.Logout();
            }
            return true;
  }
 }
}
