using System;
using System.ServiceModel;
using ThoughtWorks.CruiseControl.Remote;
using ThoughtWorks.CruiseControl.Remote.Messages;
namespace ThoughtWorks.CruiseControl.Core.Extensions
{
    [ServiceBehavior(InstanceContextMode = InstanceContextMode.Single)]
    public class CruiseControlImplementation
        : ICruiseControlContract
    {
        private ICruiseServer cruiseServer;
        public CruiseControlImplementation(ICruiseServer server)
        {
            if (server == null) throw new ArgumentNullException("server");
            cruiseServer = server;
        }
        public Response ProcessMessage(string action, ServerRequest message)
        {
            var response = cruiseServer.CruiseServerClient.ProcessMessage(action, message);
            return response;
        }
    }
}
