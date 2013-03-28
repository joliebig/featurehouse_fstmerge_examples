namespace FireIRC.Extenciblility.IRCClasses
{
    public class ChannelUser
    {
        private string _Channel;
        private IrcUser _IrcUser;
        private bool _IsOp;
        private bool _IsVoice;
        internal ChannelUser(string channel, IrcUser ircuser)
        {
            _Channel = channel;
            _IrcUser = ircuser;
        }
        public string Channel {
            get {
                return _Channel;
            }
        }
        public bool IsIrcOp {
            get {
                return _IrcUser.IsIrcOp;
            }
        }
        public bool IsOp {
            get {
                return _IsOp;
            }
            set {
                _IsOp = value;
            }
        }
        public bool IsVoice {
            get {
                return _IsVoice;
            }
            set {
                _IsVoice = value;
            }
        }
        public bool IsAway {
            get {
                return _IrcUser.IsAway;
            }
        }
        public IrcUser IrcUser {
            get {
                return _IrcUser;
            }
        }
        public string Nick {
            get {
                return _IrcUser.Nick;
            }
        }
        public string Ident {
            get {
                return _IrcUser.Ident;
            }
        }
        public string Host {
            get {
                return _IrcUser.Host;
            }
        }
        public string Realname {
            get {
                return _IrcUser.Realname;
            }
        }
        public string Server {
            get {
                return _IrcUser.Server;
            }
        }
        public int HopCount {
            get {
                return _IrcUser.HopCount;
            }
        }
        public string[] JoinedChannels {
            get {
                return _IrcUser.JoinedChannels;
            }
        }
    }
}
