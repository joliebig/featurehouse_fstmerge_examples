using System;
namespace FireIRC.Extenciblility.IRCClasses
{
    public class BanInfo
    {
        private string f_Channel;
        private string f_Mask;
        public string Channel {
            get {
                return f_Channel;
            }
        }
        public string Mask {
            get {
                return f_Mask;
            }
        }
        private BanInfo()
        {
        }
        public static BanInfo Parse(IrcMessageData data)
        {
            BanInfo info = new BanInfo();
            info.f_Channel = data.RawMessageArray[3];
            info.f_Mask= data.RawMessageArray[4];
            return info;
        }
    }
}
