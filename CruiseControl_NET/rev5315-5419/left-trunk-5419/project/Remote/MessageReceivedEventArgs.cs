using System;
using System.Collections.Generic;
using System.Text;
using ThoughtWorks.CruiseControl.Remote.Messages;
using System.ComponentModel;
namespace ThoughtWorks.CruiseControl.Remote
{
    public class MessageReceivedEventArgs
        : AsyncCompletedEventArgs
    {
        private readonly Response response;
        public MessageReceivedEventArgs(Response response, Exception error, bool cancelled, object userState)
            : base(error, cancelled, userState)
        {
            this.response = response;
        }
        public Response Response
        {
            get { return response; }
        }
    }
}
