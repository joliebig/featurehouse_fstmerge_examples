using System;
using System.Collections.Generic;
using System.Text;
using FireIRC.Extenciblility.IRCClasses;
namespace FireIRC.Extenciblility
{
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
        public ExClass(MessageForm _on, string _nick, string _chan, string _message, IrcClient _client)
        {
            on = _on;
            nick = _nick;
            chan = _chan;
            message = _message;
        }
    }
}
