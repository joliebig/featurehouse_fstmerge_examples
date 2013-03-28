using System;
using System.Collections.Generic;
using System.Text;
using ThoughtWorks.CruiseControl.Remote.Messages;
namespace ThoughtWorks.CruiseControl.Remote
{
    public interface IServerConnection
    {
        string Type { get; }
        string ServerName { get; }
        bool IsBusy { get; }
        Response SendMessage(string action, ServerRequest request);
        void SendMessageAsync(string action, ServerRequest request);
        void SendMessageAsync(string action, ServerRequest request, object userState);
        void CancelAsync();
        void CancelAsync(object userState);
        event EventHandler<MessageReceivedEventArgs> SendMessageCompleted;
    }
}
