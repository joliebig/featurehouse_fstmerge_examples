namespace ThoughtWorks.CruiseControl.Core.Distribution
{
    using System;
    using System.Collections.Generic;
    using System.ServiceModel;
    using System.Threading;
    using Exortech.NetReflector;
    using ThoughtWorks.CruiseControl.Core.Distribution.Messages;
    using ThoughtWorks.CruiseControl.Core.Util;
    using ThoughtWorks.CruiseControl.Remote;
    using System.Xml;
    [ReflectorType("remoteMachineAgent")]
    [ServiceBehavior(ConcurrencyMode = ConcurrencyMode.Single,
        InstanceContextMode = InstanceContextMode.Single)]
    public class RemoteBuildAgent
        : IBuildAgent, IDisposable, IRemoteBuildService
    {
        private ServiceHost serviceHost;
        private Dictionary<string, BuildStatusInformation> statusList = new Dictionary<string, BuildStatusInformation>();
        private Dictionary<string, IProject> projects = new Dictionary<string, IProject>();
        private object lockObject = new object();
        private int activeCount = 0;
        private int idCounter = 0;
        public RemoteBuildAgent()
        {
            this.NumberOfBuilds = 5;
        }
        public INetReflectorConfigurationReader ConfigurationReader { get; set; }
        [ReflectorProperty("address")]
        public string Address { get; set; }
        [ReflectorProperty("allowed", Required = false)]
        public int NumberOfBuilds { get; set; }
        public ILogger Logger { get; set; }
        public void Initialise()
        {
            this.RetrieveLogger().Info("Starting build agent at " + this.Address);
            this.serviceHost = new ServiceHost(this);
            var binding = new NetTcpBinding();
            this.serviceHost.AddServiceEndpoint(
                typeof(IRemoteBuildService),
                binding,
                this.Address);
            this.serviceHost.Open();
        }
        public void Terminate()
        {
            this.Dispose();
        }
        public void Dispose()
        {
            if (this.serviceHost != null)
            {
                this.RetrieveLogger().Info("Stopping build agent at " + this.Address);
                try
                {
                    this.serviceHost.Close();
                }
                catch
                {
                }
                finally
                {
                    this.serviceHost = null;
                }
            }
        }
        public CheckIfBuildCanRunResponse CheckIfBuildCanRun(CheckIfBuildCanRunRequest request)
        {
            this.RetrieveLogger().Debug(
                "Checking if project '" +
                request.ProjectName +
                "' can run");
            var canBuild = false;
            this.RunInLock(() => canBuild = this.activeCount < this.NumberOfBuilds);
            var response = new CheckIfBuildCanRunResponse()
            {
                CanBuild = canBuild
            };
            return response;
        }
        public StartBuildResponse StartBuild(StartBuildRequest request)
        {
            Thread.CurrentThread.Name = "R==>" + request.ProjectName;
            string id = null;
            var info = new BuildStatusInformation();
            this.RunInLock(() =>
            {
                id = this.idCounter.ToString();
                this.idCounter++;
                this.activeCount++;
                this.statusList.Add(id, info);
            });
            IProject project = null;
            var crypto = new DefaultCryptoFunctions();
            var hash = crypto.GenerateHash(request.ProjectDefinition);
            if (!this.projects.TryGetValue(hash, out project))
            {
                this.RetrieveLogger().Debug(
                    "Deserialising project definition for '" +
                    request.ProjectName +
                    "'");
                var document = new XmlDocument();
                document.LoadXml(request.ProjectDefinition);
                project = this.ConfigurationReader.ParseElement(document.DocumentElement) as Project;
                this.projects.Add(hash, project);
            }
            var projectRequest = new IntegrationRequest(
                request.BuildCondition,
                request.Source,
                request.UserName);
            projectRequest.BuildValues = request.BuildValues ??
                new Dictionary<string, string>();
            this.RetrieveLogger().Debug(
                "Starting project '" +
                request.ProjectName +
                "'");
            ThreadPool.QueueUserWorkItem(s =>
            {
                Thread.CurrentThread.Name = "R==>" + request.ProjectName;
                try
                {
                    info.Result = project.Integrate(projectRequest) ??
                        new IntegrationResult()
                        {
                            Status = IntegrationStatus.Success
                        };
                    if (info.Result.Status == IntegrationStatus.Unknown)
                    {
                        info.Result.Status = IntegrationStatus.Success;
                    }
                }
                catch
                {
                    info.Result = new IntegrationResult()
                    {
                        Status = IntegrationStatus.Exception
                    };
                }
                this.RetrieveLogger().Debug(
                    "Project '" +
                    request.ProjectName +
                    "' has completed");
                this.RunInLock(() =>
                {
                    this.activeCount--;
                });
            });
            var response = new StartBuildResponse()
            {
                BuildIdentifier = id
            };
            return response;
        }
        public CancelBuildResponse CancelBuild(CancelBuildRequest request)
        {
            throw new NotImplementedException();
        }
        public RetrieveBuildStatusResponse RetrieveBuildStatus(RetrieveBuildStatusRequest request)
        {
            var buildStatus = IntegrationStatus.Unknown;
            this.RunInLock(() =>
            {
                BuildStatusInformation info;
                if (this.statusList.TryGetValue(request.BuildIdentifier, out info))
                {
                    buildStatus = info.Result == null ?
                        IntegrationStatus.Unknown :
                        info.Result.Status;
                }
            });
            var response = new RetrieveBuildStatusResponse()
            {
                Status = buildStatus
            };
            return response;
        }
        private bool RunInLock(Action action)
        {
            if (Monitor.TryEnter(this.lockObject, 5000))
            {
                try
                {
                    action();
                    return true;
                }
                finally
                {
                    Monitor.Exit(this.lockObject);
                }
            }
            else
            {
                return false;
            }
        }
        private ILogger RetrieveLogger()
        {
            if (this.Logger == null)
            {
                this.Logger = new DefaultLogger();
            }
            return this.Logger;
        }
        private class BuildStatusInformation
        {
            public IIntegrationResult Result { get; set; }
        }
    }
}
