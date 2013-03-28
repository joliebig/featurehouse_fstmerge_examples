namespace FireIRC.Extenciblility.IRCClasses
{
    public class NonRfcChannelUser : ChannelUser
    {
        private bool _IsHalfop;
        private bool _IsOwner;
        private bool _IsAdmin;
        internal NonRfcChannelUser(string channel, IrcUser ircuser) : base(channel, ircuser)
        {
        }
        public bool IsHalfop {
            get {
                return _IsHalfop;
            }
            set {
                _IsHalfop = value;
            }
        }
    }
}
