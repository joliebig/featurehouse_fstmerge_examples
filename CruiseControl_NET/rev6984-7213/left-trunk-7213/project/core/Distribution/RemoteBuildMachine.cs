namespace ThoughtWorks.CruiseControl.Core.Distribution
{
    using System;
    using System.ServiceModel;
    using Exortech.NetReflector;
    using ThoughtWorks.CruiseControl.Core.Util;
    using System.ServiceModel.Channels;
    using ThoughtWorks.CruiseControl.Core.Distribution.Messages;
    [ReflectorType("remoteMachine")]
    public class RemoteBuildMachine
        : IBuildMachine
    {
        private ChannelFactory<IRemoteBuildService> factory;
        [ReflectorProperty("name")]
        public string Name { get; set; }
        [ReflectorProperty("address")]
        public string Address { get; set; }
        public ILogger Logger { get; set; }
        public void Initialise()
        {
            this.RetrieveLogger().Info("Initialising remote build machine connection to " + this.Address);
            var address = new EndpointAddress(this.Address);
            var binding = new NetTcpBinding();
            this.factory = new ChannelFactory<IRemoteBuildService>(binding, address);
        }
        public void Terminate()
        {
            this.RetrieveLogger().Info("Terminating remote build machine connection to " + this.Address);
        }
        public bool CanBuild(IProject project)
        {
            var request = new CheckIfBuildCanRunRequest()
            {
                ProjectName = project.Name
            };
            var response = this.SendMessage(s => s.CheckIfBuildCanRun(request));
            return response.CanBuild;
        }
        public RemoteBuildRequest StartBuild(
            IProject project,
            IIntegrationResult result,
            Action<RemoteBuildRequest> buildCompleted)
        {
            var request = new StartBuildRequest()
            {
                ProjectDefinition = project.ConfigurationXml,
                ProjectName = project.Name,
                BuildCondition = result.IntegrationRequest.BuildCondition,
                BuildValues = result.IntegrationRequest.BuildValues,
                Source = result.IntegrationRequest.Source,
                UserName = result.IntegrationRequest.UserName
            };
            var response = this.SendMessage(s => s.StartBuild(request));
            var buildRequest = new RemoteBuildRequest(
                this,
                response.BuildIdentifier,
                i =>
                {
                    var statusRequest = new RetrieveBuildStatusRequest()
                    {
                        BuildIdentifier = i
                    };
                    return this.SendMessage(s => s.RetrieveBuildStatus(statusRequest));
                },
                r => buildCompleted(r));
            return buildRequest;
        }
        public void CancelBuild(string identifier)
        {
            var request = new CancelBuildRequest()
            {
                BuildIdentifier = identifier
            };
            this.SendMessage(s => s.CancelBuild(request));
        }
        private ILogger RetrieveLogger()
        {
            if (this.Logger == null)
            {
                this.Logger = new DefaultLogger();
            }
            return this.Logger;
        }
        private TResponse SendMessage<TResponse>(Func<IRemoteBuildService, TResponse> action)
        {
            var remoteService = factory.CreateChannel();
            var remoteServiceClient = remoteService as IChannel;
            remoteServiceClient.Open();
            try
            {
                return action(remoteService);
            }
            finally
            {
                if (remoteServiceClient.State == CommunicationState.Opened)
                {
                    remoteServiceClient.Close();
                }
            }
        }
    }
}
