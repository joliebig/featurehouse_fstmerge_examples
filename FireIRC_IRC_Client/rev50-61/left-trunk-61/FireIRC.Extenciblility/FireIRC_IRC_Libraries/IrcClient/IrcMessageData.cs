namespace FireIRC.Extenciblility.IRCClasses
{
    public class IrcMessageData
    {
        private IrcClient _Irc;
        private string _From;
        private string _Nick;
        private string _Ident;
        private string _Host;
        private string _Channel;
        private string _Message;
        private string[] _MessageArray;
        private string _RawMessage;
        private string[] _RawMessageArray;
        private ReceiveType _Type;
        private ReplyCode _ReplyCode;
        public IrcClient Irc {
            get {
                return _Irc;
            }
        }
        public string From {
            get {
                return _From;
            }
        }
        public string Nick {
            get {
                return _Nick;
            }
        }
        public string Ident {
            get {
                return _Ident;
            }
        }
        public string Host {
            get {
                return _Host;
            }
        }
        public string Channel {
            get {
                return _Channel;
            }
        }
        public string Message {
            get {
                return _Message;
            }
        }
        public string[] MessageArray {
            get {
                return _MessageArray;
            }
        }
        public string RawMessage {
            get {
                return _RawMessage;
            }
        }
        public string[] RawMessageArray {
            get {
                return _RawMessageArray;
            }
        }
        public ReceiveType Type {
            get {
                return _Type;
            }
        }
        public ReplyCode ReplyCode {
            get {
                return _ReplyCode;
            }
        }
        public IrcMessageData(IrcClient ircclient, string from_, string nick, string ident, string host, string channel, string message, string rawmessage, ReceiveType type, ReplyCode replycode)
        {
            _Irc = ircclient;
            _RawMessage = rawmessage;
            _RawMessageArray = rawmessage.Split(new char[] {' '});
            _Type = type;
            _ReplyCode = replycode;
            _From = from_;
            _Nick = nick;
            _Ident = ident;
            _Host = host;
            _Channel = channel;
            if (message != null) {
                _Message = message;
                _MessageArray = message.Split(new char[] {' '});
            }
        }
    }
}
