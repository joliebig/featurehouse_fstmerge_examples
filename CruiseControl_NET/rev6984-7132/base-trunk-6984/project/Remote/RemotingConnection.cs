using System;
using System.Runtime.Remoting;
using ThoughtWorks.CruiseControl.Remote.Messages;
namespace ThoughtWorks.CruiseControl.Remote
{
    public class RemotingConnection
        : ServerConnectionBase, IServerConnection, IDisposable
    {
        private const string managerUri = "CruiseManager.rem";
        private const string serverClientUri = "CruiseServerClient.rem";
        private readonly Uri serverAddress;
        private IMessageProcessor client;
        private bool isBusy;
        public RemotingConnection(string serverAddress)
            : this(new Uri(serverAddress))
        {
        }
        public RemotingConnection(Uri serverAddress)
        {
            UriBuilder builder = new UriBuilder(serverAddress);
            if (builder.Port == -1) builder.Port = 21234;
            this.serverAddress = new Uri(builder.Uri, "/CruiseManager.rem");
        }
        public string Type
        {
            get { return ".NET Remoting"; }
        }
        public string ServerName
        {
            get { return serverAddress.Host; }
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
            try
            {
                InitialiseRemoting();
                FireRequestSending(action, request);
                Response result = client.ProcessMessage(action, request);
                FireResponseReceived(action, result);
                return result;
            }
            catch (Exception error)
            {
                throw new CommunicationsException(error.Message);
            }
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
                InitialiseRemoting();
                Response result = client.ProcessMessage(action, request);
                if (SendMessageCompleted != null)
                {
                    MessageReceivedEventArgs args = new MessageReceivedEventArgs(result, null, false, userState);
                    SendMessageCompleted(this, args);
                }
            }
            catch (Exception error)
            {
                if (SendMessageCompleted != null)
                {
                    MessageReceivedEventArgs args = new MessageReceivedEventArgs(null, error, false, userState);
                    SendMessageCompleted(this, args);
                }
            }
            finally
            {
                isBusy = false;
            }
        }
        public virtual void CancelAsync()
        {
            CancelAsync(null);
        }
        public void CancelAsync(object userState)
        {
        }
        public virtual void Dispose()
        {
            client = null;
        }
        public event EventHandler<MessageReceivedEventArgs> SendMessageCompleted;
        private void InitialiseRemoting()
        {
            if (client == null)
            {
                var actualUri = serverAddress.AbsoluteUri;
                if (actualUri.EndsWith(managerUri, StringComparison.InvariantCultureIgnoreCase))
                {
                    actualUri = actualUri.Substring(0, actualUri.Length - managerUri.Length) + serverClientUri;
                }
                client = RemotingServices.Connect(typeof(IMessageProcessor),
                    actualUri) as IMessageProcessor;
            }
        }
    }
}
