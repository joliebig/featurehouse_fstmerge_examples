using System;
using System.Collections.Generic;
using System.Text;
using ThoughtWorks.CruiseControl.Remote.Messages;
namespace ThoughtWorks.CruiseControl.Remote
{
    public abstract class ServerConnectionBase
    {
        public event EventHandler<CommunicationsEventArgs> RequestSending;
        public event EventHandler<CommunicationsEventArgs> ResponseReceived;
        protected virtual void FireRequestSending(string action, ServerRequest request)
        {
            if (RequestSending != null)
            {
                RequestSending(this, new CommunicationsEventArgs(action, request));
            }
        }
        protected virtual void FireResponseReceived(string action, Response response)
        {
            if (ResponseReceived != null)
            {
                ResponseReceived(this, new CommunicationsEventArgs(action, response));
            }
        }
    }
}
