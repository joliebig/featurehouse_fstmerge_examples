using System;
namespace FireIRC.Extenciblility.IRCClasses
{
    public class WhoInfo
    {
        private string f_Channel;
        private string f_Ident;
        private string f_Host;
        private string f_Server;
        private string f_Nick;
        private int f_HopCount;
        private string f_Realname;
        private bool f_IsAway;
        private bool f_IsOp;
        private bool f_IsVoice;
        private bool f_IsIrcOp;
        public string Channel {
            get {
                return f_Channel;
            }
        }
        public string Ident {
            get {
                return f_Ident;
            }
        }
        public string Host {
            get {
                return f_Host;
            }
        }
        public string Server {
            get {
                return f_Server;
            }
        }
        public string Nick {
            get {
                return f_Nick;
            }
        }
        public int HopCount {
            get {
                return f_HopCount;
            }
        }
        public string Realname {
            get {
                return f_Realname;
            }
        }
        public bool IsAway {
            get {
                return f_IsAway;
            }
        }
        public bool IsOp {
            get {
                return f_IsOp;
            }
        }
        public bool IsVoice {
            get {
                return f_IsVoice;
            }
        }
        public bool IsIrcOp {
            get {
                return f_IsIrcOp;
            }
        }
        private WhoInfo()
        {
        }
        public static WhoInfo Parse(IrcMessageData data)
        {
            WhoInfo whoInfo = new WhoInfo();
            whoInfo.f_Channel = data.RawMessageArray[3];
            whoInfo.f_Ident = data.RawMessageArray[4];
            whoInfo.f_Host = data.RawMessageArray[5];
            whoInfo.f_Server = data.RawMessageArray[6];
            whoInfo.f_Nick = data.RawMessageArray[7];
            whoInfo.f_Realname = String.Join(" ", data.MessageArray, 1, data.MessageArray.Length - 1);
            int hopcount = 0;
            string hopcountStr = data.MessageArray[0];
            try {
                hopcount = int.Parse(hopcountStr);
            } catch (FormatException ex) {
            }
            string usermode = data.RawMessageArray[8];
            bool op = false;
            bool voice = false;
            bool ircop = false;
            bool away = false;
            int usermodelength = usermode.Length;
            for (int i = 0; i < usermodelength; i++) {
                switch (usermode[i]) {
                    case 'H':
                        away = false;
                    break;
                    case 'G':
                        away = true;
                    break;
                    case '@':
                        op = true;
                    break;
                    case '+':
                        voice = true;
                    break;
                    case '*':
                        ircop = true;
                    break;
                }
            }
            whoInfo.f_IsAway = away;
            whoInfo.f_IsOp = op;
            whoInfo.f_IsVoice = voice;
            whoInfo.f_IsIrcOp = ircop;
            return whoInfo;
        }
    }
}
