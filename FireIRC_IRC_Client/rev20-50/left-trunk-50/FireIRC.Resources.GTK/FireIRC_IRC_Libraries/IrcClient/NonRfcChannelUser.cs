namespace FireIRC.Resources.IRC
{
    public class NonRfcChannelUser : ChannelUser
    {
        private bool _IsHalfop;
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
