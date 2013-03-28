using System;
namespace FireIRC.Extenciblility.IRCClasses
{
    public class ChannelInfo
    {
        private string f_Channel;
        private int f_UserCount;
        private string f_Topic;
        public string Channel {
            get {
                return f_Channel;
            }
        }
        public int UserCount {
            get {
                return f_UserCount;
            }
        }
        public string Topic {
            get {
                return f_Topic;
            }
        }
        internal ChannelInfo(string channel, int userCount, string topic)
        {
            f_Channel = channel;
            f_UserCount = userCount;
            f_Topic = topic;
        }
    }
}
