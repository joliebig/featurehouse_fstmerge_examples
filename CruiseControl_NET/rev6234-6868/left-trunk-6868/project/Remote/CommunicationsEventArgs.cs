using System;
using System.Collections.Generic;
using System.Text;
using ThoughtWorks.CruiseControl.Remote.Messages;
namespace ThoughtWorks.CruiseControl.Remote
{
    public class CommunicationsEventArgs
        : EventArgs
    {
        public CommunicationsEventArgs(string action, CommunicationsMessage message)
        {
            this.Message = message;
            this.Action = action;
        }
        public CommunicationsMessage Message { get; private set; }
        public string Action { get; private set; }
    }
}
