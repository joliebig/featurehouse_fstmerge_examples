using System.Collections.Specialized;
namespace FireIRC.Extenciblility.IRCClasses
{
    public class IrcUser
    {
        private IrcClient _IrcClient;
        private string _Nick = null;
        private string _Ident = null;
        private string _Host = null;
        private string _Realname = null;
        private bool _IsIrcOp = false;
        private bool _IsAway = false;
        private string _Server = null;
        private int _HopCount = -1;
        internal IrcUser(string nickname, IrcClient ircclient)
        {
            _IrcClient = ircclient;
            _Nick = nickname;
        }
        public string Nick {
            get {
                return _Nick;
            }
            set {
                _Nick = value;
            }
        }
        public string Ident {
            get {
                return _Ident;
            }
            set {
                _Ident = value;
            }
        }
        public string Host {
            get {
                return _Host;
            }
            set {
                _Host = value;
            }
        }
        public string Realname {
            get {
                return _Realname;
            }
            set {
                _Realname = value;
            }
        }
        public bool IsIrcOp {
            get {
                return _IsIrcOp;
            }
            set {
                _IsIrcOp = value;
            }
        }
        public bool IsAway {
            get {
                return _IsAway;
            }
            set {
                _IsAway = value;
            }
        }
        public string Server {
            get {
                return _Server;
            }
            set {
                _Server = value;
            }
        }
        public int HopCount {
            get {
                return _HopCount;
            }
            set {
                _HopCount = value;
            }
        }
        public string[] JoinedChannels {
            get {
                Channel channel;
                string[] result;
                string[] channels = _IrcClient.GetChannels();
                StringCollection joinedchannels = new StringCollection();
                foreach (string channelname in channels) {
                    channel = _IrcClient.GetChannel(channelname);
                    if (channel.UnsafeUsers.ContainsKey(_Nick)) {
                        joinedchannels.Add(channelname);
                    }
                }
                result = new string[joinedchannels.Count];
                joinedchannels.CopyTo(result, 0);
                return result;
            }
        }
    }
}
