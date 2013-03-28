using System;
using System.ServiceModel;
using ThoughtWorks.CruiseControl.Remote.Messages;
namespace ThoughtWorks.CruiseControl.Remote
{
    public class WcfConnection
        : ServerConnectionBase, IServerConnection, IDisposable
    {
        private Uri serverAddress;
        private CruiseControlContractClient client;
        private bool isBusy;
        public WcfConnection(string serverAddress)
            : this(new Uri(serverAddress))
        {
        }
        public WcfConnection(Uri serverAddress)
        {
            this.serverAddress = serverAddress;
        }
        public string Type
        {
            get { return "Windows Communication Foundation"; }
        }
        public string ServerName
        {
            get { return client.Endpoint.ListenUri.Host; }
        }
        public bool IsBusy
        {
            get { return isBusy; }
        }
        public virtual string Address
        {
            get { return serverAddress.AbsoluteUri; }
        }
        public virtual Response SendMessage(string action, ServerRequest request)
        {
            InitialiseClient();
            FireRequestSending(action, request);
            var result = client.ProcessMessage(action, request);
            FireResponseReceived(action, result);
            return result;
        }
        public virtual void SendMessageAsync(string action, ServerRequest request)
        {
            SendMessageAsync(action, request, null);
        }
        public virtual void SendMessageAsync(string action, ServerRequest request, object userState)
        {
            if (isBusy) throw new InvalidOperationException();
            try
            {
                isBusy = true;
                InitialiseClient();
                IAsyncResult async = null;
                async = client.BeginProcessMessage(action, request, (result) =>
                {
                    if (SendMessageCompleted != null)
                    {
                        var response = client.EndProcessMessage(async);
                        var args = new MessageReceivedEventArgs(response, null, false, userState);
                        SendMessageCompleted(this, args);
                    }
                    isBusy = false;
                }, userState);
            }
            catch (Exception error)
            {
                if (SendMessageCompleted != null)
                {
                    var args = new MessageReceivedEventArgs(null, error, false, userState);
                    SendMessageCompleted(this, args);
                }
                isBusy = false;
            }
        }
        public virtual void CancelAsync()
        {
            CancelAsync(null);
        }
        public void CancelAsync(object userState)
        {
            if (isBusy) client.Abort();
        }
        public virtual void Dispose()
        {
            if (client != null)
            {
                client.Close();
                client = null;
            }
        }
        public event EventHandler<MessageReceivedEventArgs> SendMessageCompleted;
        private void InitialiseClient()
        {
            if ((client != null) && (client.State != CommunicationState.Opened))
            {
                Dispose();
            }
            if (client == null)
            {
                client = new CruiseControlContractClient(new BasicHttpBinding(),
                    new EndpointAddress(serverAddress));
                client.Open();
            }
        }
    }
}
