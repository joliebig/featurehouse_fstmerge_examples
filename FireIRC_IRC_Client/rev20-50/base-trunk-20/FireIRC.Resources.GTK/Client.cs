using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using FireIRC.Resources.IRC;
using System.Threading;
namespace FireIRC.Resources.GTK
{
    public class Client
    {
        IrcClient client = new IrcClient();
        public IrcClient IrcClient
        {
            get { return client; }
            set { client = value; }
        }
        public Client(string server, int port, string[] nick, string usrname, string name)
        {
        }
        void DoExtencibility(ExClass e)
        {
        }
        public enum MessageForm
        {
            ChannelMessage,
            ChannelNotice,
            ChannelAction,
            QueryMessage,
            QueryNotice,
            QueryAction,
        }
        public class ExClass
        {
            public MessageForm on;
            public string nick;
            public string chan;
            public string message;
            public IrcClient client;
            public ExClass(MessageForm _on, string _nick, string _chan, string _message, IrcClient _client)
            {
                on = _on;
                nick = _nick;
                chan = _chan;
                message = _message;
                client = _client;
            }
        }
    }
}
