using System;
using System.Collections.Generic;
using System.Text;
using Exortech.NetReflector;
using ThoughtWorks.CruiseControl.Remote;
namespace ThoughtWorks.CruiseControl.Core.Tasks
{
    [ReflectorType("cruiseServerControl")]
    public class CruiseServerControlTask
        : TaskBase
    {
        [ReflectorProperty("server", Required = false)]
        public string Server { get; set; }
        [ReflectorProperty("actions", Required = true)]
        public CruiseServerControlTaskAction[] Actions { get; set; }
        public ICruiseServerClientFactory ClientFactory { get; set; }
        protected override bool Execute(IIntegrationResult result)
        {
            if (ClientFactory == null) ClientFactory = new CruiseServerClientFactory();
            var client = ClientFactory.GenerateClient(Server ?? "tcp://localhost:21234");
            foreach (var action in Actions)
            {
                switch (action.Type)
                {
                    case CruiseServerControlTaskActionType.StartProject:
                        client.StartProject(action.Project);
                        break;
                    case CruiseServerControlTaskActionType.StopProject:
                        client.StopProject(action.Project);
                        break;
                }
            }
            return true;
        }
    }
}
