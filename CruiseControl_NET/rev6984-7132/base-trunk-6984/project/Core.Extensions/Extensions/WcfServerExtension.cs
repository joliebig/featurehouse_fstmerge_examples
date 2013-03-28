using System;
using System.ServiceModel;
using ThoughtWorks.CruiseControl.Core.Util;
using ThoughtWorks.CruiseControl.Remote;
namespace ThoughtWorks.CruiseControl.Core.Extensions
{
    public class WcfServerExtension
        : ICruiseServerExtension, IDisposable
    {
        private ServiceHost wcfServiceHost;
        private ICruiseServer cruiseServer;
        public bool IsRunning
        {
            get { return (wcfServiceHost.State == CommunicationState.Opened); }
        }
        public void Initialise(ICruiseServer server, ExtensionConfiguration extensionConfig)
        {
            if (server == null) throw new ArgumentNullException("server");
            cruiseServer = server;
            wcfServiceHost = new ServiceHost(new CruiseControlImplementation(cruiseServer));
        }
        public void Start()
        {
            if ((wcfServiceHost.State != CommunicationState.Opened) &&
                (wcfServiceHost.State != CommunicationState.Opening))
            {
                Log.Info("Opening service host");
                wcfServiceHost.Open();
                Log.Debug("Service host opened");
            }
        }
        public void Stop()
        {
            if ((wcfServiceHost.State != CommunicationState.Closed) &&
                (wcfServiceHost.State != CommunicationState.Closing) &&
                (wcfServiceHost.State != CommunicationState.Faulted))
            {
                Log.Info("Closing service host");
                wcfServiceHost.Close();
                Log.Debug("Service host closed");
            }
        }
        public void Abort()
        {
            if ((wcfServiceHost.State != CommunicationState.Closed) &&
                (wcfServiceHost.State != CommunicationState.Closing) &&
                (wcfServiceHost.State != CommunicationState.Faulted))
            {
                Log.Info("Aborting service host");
                wcfServiceHost.Abort();
                Log.Debug("Service host aborted");
            }
        }
        public void Dispose()
        {
            Abort();
        }
    }
}
