using System;
using System.Collections;
using System.Collections.Specialized;
using System.Text.RegularExpressions;
namespace OVT.FireIRC.Resources.IRC
{
    public class IrcClient : IrcCommands
    {
        private string _Nickname = string.Empty;
        private string[] _NicknameList;
        private int _CurrentNickname;
        private string _Realname = string.Empty;
        private string _Usermode = string.Empty;
        private int _IUsermode;
        private string _Username = string.Empty;
        private string _Password = string.Empty;
        private bool _IsAway;
        private string _CtcpVersion;
        private bool _ActiveChannelSyncing;
        private bool _PassiveChannelSyncing;
        private bool _AutoJoinOnInvite;
        private bool _AutoRejoin;
        private StringDictionary _AutoRejoinChannels = new StringDictionary();
        private bool _AutoRejoinChannelsWithKeys;
        private bool _AutoRejoinOnKick;
        private bool _AutoRelogin;
        private bool _AutoNickHandling = true;
        private bool _SupportNonRfc;
        private bool _SupportNonRfcLocked;
        private StringCollection _Motd = new StringCollection();
        private bool _MotdReceived;
        private Array _ReplyCodes = Enum.GetValues(typeof(ReplyCode));
        private StringCollection _JoinedChannels = new StringCollection();
        private Hashtable _Channels = Hashtable.Synchronized(new Hashtable(new CaseInsensitiveHashCodeProvider(), new CaseInsensitiveComparer()));
        private Hashtable _IrcUsers = Hashtable.Synchronized(new Hashtable(new CaseInsensitiveHashCodeProvider(), new CaseInsensitiveComparer()));
        private static Regex _ReplyCodeRegex = new Regex("^:[^ ]+? ([0-9]{3}) .+$", RegexOptions.Compiled);
        private static Regex _PingRegex = new Regex("^PING :.*", RegexOptions.Compiled);
        private static Regex _ErrorRegex = new Regex("^ERROR :.*", RegexOptions.Compiled);
        private static Regex _ActionRegex = new Regex("^:.*? PRIVMSG (.).* :"+"\x1"+"ACTION .*"+"\x1"+"$", RegexOptions.Compiled);
        private static Regex _CtcpRequestRegex = new Regex("^:.*? PRIVMSG .* :"+"\x1"+".*"+"\x1"+"$", RegexOptions.Compiled);
        private static Regex _MessageRegex = new Regex("^:.*? PRIVMSG (.).* :.*$", RegexOptions.Compiled);
        private static Regex _CtcpReplyRegex = new Regex("^:.*? NOTICE .* :"+"\x1"+".*"+"\x1"+"$", RegexOptions.Compiled);
        private static Regex _NoticeRegex = new Regex("^:.*? NOTICE (.).* :.*$", RegexOptions.Compiled);
        private static Regex _InviteRegex = new Regex("^:.*? INVITE .* .*$", RegexOptions.Compiled);
        private static Regex _JoinRegex = new Regex("^:.*? JOIN .*$", RegexOptions.Compiled);
        private static Regex _TopicRegex = new Regex("^:.*? TOPIC .* :.*$", RegexOptions.Compiled);
        private static Regex _NickRegex = new Regex("^:.*? NICK .*$", RegexOptions.Compiled);
        private static Regex _KickRegex = new Regex("^:.*? KICK .* .*$", RegexOptions.Compiled);
        private static Regex _PartRegex = new Regex("^:.*? PART .*$", RegexOptions.Compiled);
        private static Regex _ModeRegex = new Regex("^:.*? MODE (.*) .*$", RegexOptions.Compiled);
        private static Regex _QuitRegex = new Regex("^:.*? QUIT :.*$", RegexOptions.Compiled);
        public event EventHandler OnRegistered;
        public event PingEventHandler OnPing;
        public event PongEventHandler OnPong;
        public event IrcEventHandler OnRawMessage;
        public event ErrorEventHandler OnError;
        public event IrcEventHandler OnErrorMessage;
        public event JoinEventHandler OnJoin;
        public event NamesEventHandler OnNames;
        public event PartEventHandler OnPart;
        public event QuitEventHandler OnQuit;
        public event KickEventHandler OnKick;
        public event AwayEventHandler OnAway;
        public event IrcEventHandler OnUnAway;
        public event IrcEventHandler OnNowAway;
        public event InviteEventHandler OnInvite;
        public event BanEventHandler OnBan;
        public event UnbanEventHandler OnUnban;
        public event OpEventHandler OnOp;
        public event DeopEventHandler OnDeop;
        public event HalfopEventHandler OnHalfop;
        public event DehalfopEventHandler OnDehalfop;
        public event VoiceEventHandler OnVoice;
        public event DevoiceEventHandler OnDevoice;
        public event WhoEventHandler OnWho;
        public event MotdEventHandler OnMotd;
        public event TopicEventHandler OnTopic;
        public event TopicChangeEventHandler OnTopicChange;
        public event NickChangeEventHandler OnNickChange;
        public event IrcEventHandler OnModeChange;
        public event IrcEventHandler OnUserModeChange;
        public event IrcEventHandler OnChannelModeChange;
        public event IrcEventHandler OnChannelMessage;
        public event ActionEventHandler OnChannelAction;
        public event IrcEventHandler OnChannelNotice;
        public event IrcEventHandler OnChannelActiveSynced;
        public event IrcEventHandler OnChannelPassiveSynced;
        public event IrcEventHandler OnQueryMessage;
        public event ActionEventHandler OnQueryAction;
        public event IrcEventHandler OnQueryNotice;
        public event CtcpEventHandler OnCtcpRequest;
        public event CtcpEventHandler OnCtcpReply;
        public bool ActiveChannelSyncing {
            get {
                return _ActiveChannelSyncing;
            }
            set {
                _ActiveChannelSyncing = value;
            }
        }
        public bool PassiveChannelSyncing {
            get {
                return _PassiveChannelSyncing;
            }
        }
        public string CtcpVersion {
            get {
                return _CtcpVersion;
            }
            set {
                _CtcpVersion = value;
            }
        }
        public bool AutoJoinOnInvite {
            get {
                return _AutoJoinOnInvite;
            }
            set {
                _AutoJoinOnInvite = value;
            }
        }
        public bool AutoRejoin {
            get {
                return _AutoRejoin;
            }
            set {
                _AutoRejoin = value;
            }
        }
        public bool AutoRejoinOnKick {
            get {
                return _AutoRejoinOnKick;
            }
            set {
                _AutoRejoinOnKick = value;
            }
        }
        public bool AutoRelogin {
            get {
                return _AutoRelogin;
            }
            set {
                _AutoRelogin = value;
            }
        }
        public bool AutoNickHandling {
            get {
                return _AutoNickHandling;
            }
            set {
                _AutoNickHandling = value;
            }
        }
        public bool SupportNonRfc {
            get {
                return _SupportNonRfc;
            }
            set {
                if (_SupportNonRfcLocked) {
                    return;
                }
                _SupportNonRfc = value;
            }
        }
        public string Nickname {
            get {
                return _Nickname;
            }
        }
        public string[] NicknameList {
            get {
                return _NicknameList;
            }
        }
        public string Realname {
            get {
                return _Realname;
            }
        }
        public string Username {
            get {
                return _Username;
            }
        }
        public string Usermode {
            get {
                return _Usermode;
            }
        }
        public int IUsermode {
            get {
                return _IUsermode;
            }
        }
        public bool IsAway {
            get {
                return _IsAway;
            }
        }
        public string Password {
            get {
                return _Password;
            }
        }
        public StringCollection JoinedChannels {
            get {
                return _JoinedChannels;
            }
        }
        public StringCollection Motd {
            get {
                return _Motd;
            }
        }
        public IrcClient()
        {
            OnReadLine += new ReadLineEventHandler(_Worker);
            OnDisconnected += new EventHandler(_OnDisconnected);
            OnConnectionError += new EventHandler(_OnConnectionError);
        }
        public new void Connect(string[] addresslist, int port)
        {
            _SupportNonRfcLocked = true;
            base.Connect(addresslist, port);
        }
        public void Reconnect(bool login, bool channels)
        {
            if (channels) {
                _StoreChannelsToRejoin();
            }
            base.Reconnect();
            if (login) {
                _CurrentNickname = 0;
                Login(_NicknameList, Realname, IUsermode, Username, Password);
            }
            if (channels) {
                _RejoinChannels();
            }
        }
        public void Reconnect(bool login)
        {
            Reconnect(login, true);
        }
        public void Login(string[] nicklist, string realname, int usermode, string username, string password)
        {
            _NicknameList = (string[])nicklist.Clone();
            _Nickname = _NicknameList[0].Replace(" ", "");
            _Realname = realname;
            _IUsermode = usermode;
            if (username != null && username.Length > 0) {
                _Username = username.Replace(" ", "");
            } else {
                _Username = Environment.UserName.Replace(" ", "");
            }
            if (password != null && password.Length > 0) {
                _Password = password;
                RfcPass(Password, Priority.Critical);
            }
            RfcNick(Nickname, Priority.Critical);
            RfcUser(Username, IUsermode, Realname, Priority.Critical);
        }
        public void Login(string[] nicklist, string realname, int usermode, string username)
        {
            Login(nicklist, realname, usermode, username, "");
        }
        public void Login(string[] nicklist, string realname, int usermode)
        {
            Login(nicklist, realname, usermode, "", "");
        }
        public void Login(string[] nicklist, string realname)
        {
            Login(nicklist, realname, 0, "", "");
        }
        public void Login(string nick, string realname, int usermode, string username, string password)
        {
            Login(new string[] {nick, nick+"_", nick+"__"}, realname, usermode, username, password);
        }
        public void Login(string nick, string realname, int usermode, string username)
        {
            Login(new string[] {nick, nick+"_", nick+"__"}, realname, usermode, username, "");
        }
        public void Login(string nick, string realname, int usermode)
        {
            Login(new string[] {nick, nick+"_", nick+"__"}, realname, usermode, "", "");
        }
        public void Login(string nick, string realname)
        {
            Login(new string[] {nick, nick+"_", nick+"__"}, realname, 0, "", "");
        }
        public bool IsMe(string nickname)
        {
            return (Nickname == nickname);
        }
        public bool IsJoined(string channelname)
        {
            return IsJoined(channelname, Nickname);
        }
        public bool IsJoined(string channelname, string nickname)
        {
            if (channelname == null) {
                throw new System.ArgumentNullException("channelname");
            }
            if (nickname == null) {
                throw new System.ArgumentNullException("nickname");
            }
            Channel channel = GetChannel(channelname);
            if (channel != null &&
                channel.UnsafeUsers != null &&
                channel.UnsafeUsers.ContainsKey(nickname)) {
                return true;
            }
            return false;
        }
        public IrcUser GetIrcUser(string nickname)
        {
            if (nickname == null) {
                throw new System.ArgumentNullException("nickname");
            }
            return (IrcUser)_IrcUsers[nickname];
        }
        public ChannelUser GetChannelUser(string channelname, string nickname)
        {
            if (channelname == null) {
                throw new System.ArgumentNullException("channel");
            }
            if (nickname == null) {
                throw new System.ArgumentNullException("nickname");
            }
            Channel channel = GetChannel(channelname);
            if (channel != null) {
                return (ChannelUser)channel.UnsafeUsers[nickname];
            } else {
                return null;
            }
        }
        public Channel GetChannel(string channelname)
        {
            if (channelname == null) {
                throw new System.ArgumentNullException("channelname");
            }
            return (Channel)_Channels[channelname];
        }
        public string[] GetChannels()
        {
            string[] channels = new string[_Channels.Values.Count];
            int i = 0;
            foreach (Channel channel in _Channels.Values) {
                channels[i++] = channel.Name;
            }
            return channels;
        }
        public IrcMessageData MessageParser(string rawline)
        {
            string line;
            string[] linear;
            string messagecode;
            string from_;
            string nick = null;
            string ident = null;
            string host = null;
            string channel = null;
            string message = null;
            ReceiveType type;
            ReplyCode replycode;
            int exclamationpos;
            int atpos;
            int colonpos;
            if (rawline[0] == ':') {
                line = rawline.Substring(1);
            } else {
                line = rawline;
            }
            linear = line.Split(new char[] {' '});
            from_ = linear[0];
            messagecode = linear[1];
            exclamationpos = from_.IndexOf("!");
            atpos = from_.IndexOf("@");
            colonpos = line.IndexOf(" :");
            if (colonpos != -1) {
                colonpos += 1;
            }
            if (exclamationpos != -1) {
                nick = from_.Substring(0, exclamationpos);
            }
            if ((atpos != -1) &&
                (exclamationpos != -1)) {
                ident = from_.Substring(exclamationpos+1, (atpos - exclamationpos)-1);
            }
            if (atpos != -1) {
                host = from_.Substring(atpos+1);
            }
            try {
                replycode = (ReplyCode)int.Parse(messagecode);
            } catch (FormatException) {
                replycode = ReplyCode.Null;
            }
            type = _GetMessageType(rawline);
            if (colonpos != -1) {
                message = line.Substring(colonpos+1);
            }
            switch (type) {
                case ReceiveType.Join:
                case ReceiveType.Kick:
                case ReceiveType.Part:
                case ReceiveType.TopicChange:
                case ReceiveType.ChannelModeChange:
                case ReceiveType.ChannelMessage:
                case ReceiveType.ChannelAction:
                case ReceiveType.ChannelNotice:
                    channel = linear[2];
                break;
                case ReceiveType.Who:
                case ReceiveType.Topic:
                case ReceiveType.Invite:
                case ReceiveType.BanList:
                case ReceiveType.ChannelMode:
                    channel = linear[3];
                break;
                case ReceiveType.Name:
                    channel = linear[4];
                break;
            }
            if ((channel != null) &&
                (channel[0] == ':')) {
                    channel = channel.Substring(1);
            }
            IrcMessageData data;
            data = new IrcMessageData(this, from_, nick, ident, host, channel, message, rawline, type, replycode);
            return data;
        }
        private void _Worker(object sender, ReadLineEventArgs e)
        {
            _HandleEvents(MessageParser(e.Line));
        }
        private void _OnDisconnected(object sender, EventArgs e)
        {
            if (AutoRejoin) {
                _StoreChannelsToRejoin();
            }
            _SyncingCleanup();
        }
        private void _OnConnectionError(object sender, EventArgs e)
        {
            if (AutoRelogin) {
                Login(_NicknameList, Realname, IUsermode, Username, Password);
            }
            if (AutoRejoin) {
                _RejoinChannels();
            }
        }
        private void _StoreChannelsToRejoin()
        {
            if (ActiveChannelSyncing || PassiveChannelSyncing) {
                foreach (Channel channel in _Channels.Values) {
                    if (channel.Key.Length > 0) {
                        _AutoRejoinChannels.Add(channel.Name, channel.Key);
                        _AutoRejoinChannelsWithKeys = true;
                    } else {
                        _AutoRejoinChannels.Add(channel.Name, "nokey");
                    }
                }
            } else {
                foreach (string channel in _JoinedChannels) {
                    _AutoRejoinChannels.Add(channel, "nokey");
                }
            }
        }
        private void _RejoinChannels()
        {
            int chan_count = _AutoRejoinChannels.Count;
            string[] names = new string[chan_count];
            _AutoRejoinChannels.Keys.CopyTo(names, 0);
            if (_AutoRejoinChannelsWithKeys) {
                string[] keys = new string[chan_count];
                _AutoRejoinChannels.Values.CopyTo(keys, 0);
                RfcJoin(names, keys, Priority.High);
            } else {
                RfcJoin(names, Priority.High);
            }
            _AutoRejoinChannelsWithKeys = false;
            _AutoRejoinChannels.Clear();
        }
        private void _SyncingCleanup()
        {
            _JoinedChannels.Clear();
            if (ActiveChannelSyncing) {
                _Channels.Clear();
                _IrcUsers.Clear();
            }
            _IsAway = false;
            _MotdReceived = false;
            _Motd.Clear();
        }
        private string _NextNickname()
        {
            _CurrentNickname++;
            if (_CurrentNickname >= _NicknameList.Length) {
                _CurrentNickname--;
            }
            return NicknameList[_CurrentNickname];
        }
        private ReceiveType _GetMessageType(string rawline)
        {
            Match found = _ReplyCodeRegex.Match(rawline);
            if (found.Success) {
                string code = found.Groups[1].Value;
                ReplyCode replycode = (ReplyCode)int.Parse(code);
                if (Array.IndexOf(_ReplyCodes, replycode) == -1) {
                    return ReceiveType.Unknown;
                }
                switch (replycode) {
                    case ReplyCode.Welcome:
                    case ReplyCode.YourHost:
                    case ReplyCode.Created:
                    case ReplyCode.MyInfo:
                    case ReplyCode.Bounce:
                        return ReceiveType.Login;
                    case ReplyCode.LuserClient:
                    case ReplyCode.LuserOp:
                    case ReplyCode.LuserUnknown:
                    case ReplyCode.LuserMe:
                    case ReplyCode.LuserChannels:
                        return ReceiveType.Info;
                    case ReplyCode.MotdStart:
                    case ReplyCode.Motd:
                    case ReplyCode.EndOfMotd:
                        return ReceiveType.Motd;
                    case ReplyCode.NamesReply:
                    case ReplyCode.EndOfNames:
                        return ReceiveType.Name;
                    case ReplyCode.WhoReply:
                    case ReplyCode.EndOfWho:
                        return ReceiveType.Who;
                    case ReplyCode.ListStart:
                    case ReplyCode.List:
                    case ReplyCode.ListEnd:
                        return ReceiveType.List;
                    case ReplyCode.BanList:
                    case ReplyCode.EndOfBanList:
                        return ReceiveType.BanList;
                    case ReplyCode.Topic:
                    case ReplyCode.NoTopic:
                        return ReceiveType.Topic;
                    case ReplyCode.WhoIsUser:
                    case ReplyCode.WhoIsServer:
                    case ReplyCode.WhoIsOperator:
                    case ReplyCode.WhoIsIdle:
                    case ReplyCode.WhoIsChannels:
                    case ReplyCode.EndOfWhoIs:
                        return ReceiveType.WhoIs;
                    case ReplyCode.WhoWasUser:
                    case ReplyCode.EndOfWhoWas:
                        return ReceiveType.WhoWas;
                    case ReplyCode.UserModeIs:
                        return ReceiveType.UserMode;
                    case ReplyCode.ChannelModeIs:
                        return ReceiveType.ChannelMode;
                    default:
                        if (((int)replycode >= 400) &&
                            ((int)replycode <= 599)) {
                            return ReceiveType.ErrorMessage;
                        } else {
                            return ReceiveType.Unknown;
                        }
                }
            }
            found = _PingRegex.Match(rawline);
            if (found.Success) {
                return ReceiveType.Unknown;
            }
            found = _ErrorRegex.Match(rawline);
            if (found.Success) {
                return ReceiveType.Error;
            }
            found = _ActionRegex.Match(rawline);
            if (found.Success) {
                switch (found.Groups[1].Value) {
                    case "#":
                    case "!":
                    case "&":
                    case "+":
                        return ReceiveType.ChannelAction;
                    default:
                        return ReceiveType.QueryAction;
                }
            }
            found = _CtcpRequestRegex.Match(rawline);
            if (found.Success) {
                return ReceiveType.CtcpRequest;
            }
            found = _MessageRegex.Match(rawline);
            if (found.Success) {
                switch (found.Groups[1].Value) {
                    case "#":
                    case "!":
                    case "&":
                    case "+":
                        return ReceiveType.ChannelMessage;
                    default:
                        return ReceiveType.QueryMessage;
                }
            }
            found = _CtcpReplyRegex.Match(rawline);
            if (found.Success) {
                return ReceiveType.CtcpReply;
            }
            found = _NoticeRegex.Match(rawline);
            if (found.Success) {
                switch (found.Groups[1].Value) {
                    case "#":
                    case "!":
                    case "&":
                    case "+":
                        return ReceiveType.ChannelNotice;
                    default:
                        return ReceiveType.QueryNotice;
                }
            }
            found = _InviteRegex.Match(rawline);
            if (found.Success) {
                return ReceiveType.Invite;
            }
            found = _JoinRegex.Match(rawline);
            if (found.Success) {
                return ReceiveType.Join;
            }
            found = _TopicRegex.Match(rawline);
            if (found.Success) {
                return ReceiveType.TopicChange;
            }
            found = _NickRegex.Match(rawline);
            if (found.Success) {
                return ReceiveType.NickChange;
            }
            found = _KickRegex.Match(rawline);
            if (found.Success) {
                return ReceiveType.Kick;
            }
            found = _PartRegex.Match(rawline);
            if (found.Success) {
                return ReceiveType.Part;
            }
            found = _ModeRegex.Match(rawline);
            if (found.Success) {
                if (found.Groups[1].Value == _Nickname) {
                    return ReceiveType.UserModeChange;
                } else {
                    return ReceiveType.ChannelModeChange;
                }
            }
            found = _QuitRegex.Match(rawline);
            if (found.Success) {
                return ReceiveType.Quit;
            }
            return ReceiveType.Unknown;
        }
        private void _HandleEvents(IrcMessageData ircdata)
        {
            if (OnRawMessage != null) {
                OnRawMessage(this, new IrcEventArgs(ircdata));
            }
            string code;
            code = ircdata.RawMessageArray[0];
            switch (code) {
                case "PING":
                    _Event_PING(ircdata);
                break;
                case "ERROR":
                    _Event_ERROR(ircdata);
                break;
            }
            code = ircdata.RawMessageArray[1];
            switch (code) {
                case "PRIVMSG":
                    _Event_PRIVMSG(ircdata);
                break;
                case "NOTICE":
                    _Event_NOTICE(ircdata);
                break;
                case "JOIN":
                    _Event_JOIN(ircdata);
                break;
                case "PART":
                    _Event_PART(ircdata);
                break;
                case "KICK":
                    _Event_KICK(ircdata);
                break;
                case "QUIT":
                    _Event_QUIT(ircdata);
                break;
                case "TOPIC":
                    _Event_TOPIC(ircdata);
                break;
                case "NICK":
                    _Event_NICK(ircdata);
                break;
                case "INVITE":
                    _Event_INVITE(ircdata);
                break;
                case "MODE":
                    _Event_MODE(ircdata);
                break;
                case "PONG":
                    _Event_PONG(ircdata);
                break;
            }
            if (ircdata.ReplyCode != ReplyCode.Null) {
                switch (ircdata.ReplyCode) {
                    case ReplyCode.Welcome:
                        _Event_RPL_WELCOME(ircdata);
                    break;
                    case ReplyCode.Topic:
                        _Event_RPL_TOPIC(ircdata);
                    break;
                    case ReplyCode.NoTopic:
                        _Event_RPL_NOTOPIC(ircdata);
                    break;
                    case ReplyCode.NamesReply:
                        _Event_RPL_NAMREPLY(ircdata);
                    break;
                    case ReplyCode.EndOfNames:
                        _Event_RPL_ENDOFNAMES(ircdata);
                    break;
                    case ReplyCode.WhoReply:
                        _Event_RPL_WHOREPLY(ircdata);
                    break;
                    case ReplyCode.ChannelModeIs:
                        _Event_RPL_CHANNELMODEIS(ircdata);
                    break;
                    case ReplyCode.BanList:
                        _Event_RPL_BANLIST(ircdata);
                    break;
                    case ReplyCode.EndOfBanList:
                        _Event_RPL_ENDOFBANLIST(ircdata);
                    break;
                    case ReplyCode.Motd:
                        _Event_RPL_MOTD(ircdata);
                    break;
                    case ReplyCode.EndOfMotd:
                        _Event_RPL_ENDOFMOTD(ircdata);
                    break;
                    case ReplyCode.Away:
                        _Event_RPL_AWAY(ircdata);
                    break;
                    case ReplyCode.UnAway:
                        _Event_RPL_UNAWAY(ircdata);
                    break;
                    case ReplyCode.NowAway:
                        _Event_RPL_NOWAWAY(ircdata);
                    break;
                    case ReplyCode.ErrorNicknameInUse:
                        _Event_ERR_NICKNAMEINUSE(ircdata);
                    break;
                }
            }
            if (ircdata.Type == ReceiveType.ErrorMessage) {
                _Event_ERR(ircdata);
            }
        }
        private bool _RemoveIrcUser(string nickname)
        {
            if (GetIrcUser(nickname).JoinedChannels.Length == 0) {
                _IrcUsers.Remove(nickname);
                return true;
            }
            return false;
        }
        private void _RemoveChannelUser(string channelname, string nickname)
        {
            Channel chan = GetChannel(channelname);
            chan.UnsafeUsers.Remove(nickname);
            chan.UnsafeOps.Remove(nickname);
            chan.UnsafeVoices.Remove(nickname);
            if (SupportNonRfc) {
                NonRfcChannel nchan = (NonRfcChannel)chan;
                nchan.UnsafeHalfops.Remove(nickname);
            }
        }
        private void _InterpretChannelMode(IrcMessageData ircdata, string mode, string parameter)
        {
            string[] parameters = parameter.Split(new char[] {' '});
            bool add = false;
            bool remove = false;
            int modelength = mode.Length;
            string temp;
            Channel channel = null;
            if (ActiveChannelSyncing) {
                channel = GetChannel(ircdata.Channel);
            }
            IEnumerator parametersEnumerator = parameters.GetEnumerator();
            parametersEnumerator.MoveNext();
            for (int i = 0; i < modelength; i++) {
                switch(mode[i]) {
                    case '-':
                        add = false;
                        remove = true;
                    break;
                    case '+':
                        add = true;
                        remove = false;
                    break;
                    case 'o':
                    case 'a':
                    case 'q':
                        temp = (string)parametersEnumerator.Current;
                        parametersEnumerator.MoveNext();
                        if (add) {
                            if (ActiveChannelSyncing) {
                                if (GetChannelUser(ircdata.Channel, temp) != null) {
                                    try {
                                        channel.UnsafeOps.Add(temp, GetIrcUser(temp));
                                    } catch (ArgumentException) {
                                    }
                                    ChannelUser cuser = GetChannelUser(ircdata.Channel, temp);
                                    cuser.IsOp = true;
                                } else {
                                }
                            }
                            if (OnOp != null) {
                                OnOp(this, new OpEventArgs(ircdata, ircdata.Channel, ircdata.Nick, temp));
                            }
                        }
                        if (remove) {
                            if (ActiveChannelSyncing) {
                                if (GetChannelUser(ircdata.Channel, temp) != null) {
                                    channel.UnsafeOps.Remove(temp);
                                    ChannelUser cuser = GetChannelUser(ircdata.Channel, temp);
                                    cuser.IsOp = false;
                                } else {
                                }
                            }
                            if (OnDeop != null) {
                                OnDeop(this, new DeopEventArgs(ircdata, ircdata.Channel, ircdata.Nick, temp));
                            }
                        }
                    break;
                    case 'h':
                        if (SupportNonRfc) {
                            temp = (string)parametersEnumerator.Current;
                            parametersEnumerator.MoveNext();
                            if (add) {
                                if (ActiveChannelSyncing) {
                                    if (GetChannelUser(ircdata.Channel, temp) != null) {
                                        try {
                                            ((NonRfcChannel)channel).UnsafeHalfops.Add(temp, GetIrcUser(temp));
                                        } catch (ArgumentException) {
                                        }
                                        NonRfcChannelUser cuser = (NonRfcChannelUser)GetChannelUser(ircdata.Channel, temp);
                                        cuser.IsHalfop = true;
                                    } else {
                                    }
                                }
                                if (OnHalfop != null) {
                                    OnHalfop(this, new HalfopEventArgs(ircdata, ircdata.Channel, ircdata.Nick, temp));
                                }
                            }
                            if (remove) {
                                if (ActiveChannelSyncing) {
                                    if (GetChannelUser(ircdata.Channel, temp) != null) {
                                        ((NonRfcChannel)channel).UnsafeHalfops.Remove(temp);
                                        NonRfcChannelUser cuser = (NonRfcChannelUser)GetChannelUser(ircdata.Channel, temp);
                                        cuser.IsHalfop = false;
                                    } else {
                                    }
                                }
                                if (OnDehalfop != null) {
                                    OnDehalfop(this, new DehalfopEventArgs(ircdata, ircdata.Channel, ircdata.Nick, temp));
                                }
                            }
                        }
                    break;
                    case 'v':
                        temp = (string)parametersEnumerator.Current;
                        parametersEnumerator.MoveNext();
                        if (add) {
                            if (ActiveChannelSyncing) {
                                if (GetChannelUser(ircdata.Channel, temp) != null) {
                                    try {
                                        channel.UnsafeVoices.Add(temp, GetIrcUser(temp));
                                    } catch (ArgumentException) {
                                    }
                                    ChannelUser cuser = GetChannelUser(ircdata.Channel, temp);
                                    cuser.IsVoice = true;
                                } else {
                                }
                            }
                            if (OnVoice != null) {
                                OnVoice(this, new VoiceEventArgs(ircdata, ircdata.Channel, ircdata.Nick, temp));
                            }
                        }
                        if (remove) {
                            if (ActiveChannelSyncing) {
                                if (GetChannelUser(ircdata.Channel, temp) != null) {
                                    channel.UnsafeVoices.Remove(temp);
                                    ChannelUser cuser = GetChannelUser(ircdata.Channel, temp);
                                    cuser.IsVoice = false;
                                } else {
                                }
                            }
                            if (OnDevoice != null) {
                                OnDevoice(this, new DevoiceEventArgs(ircdata, ircdata.Channel, ircdata.Nick, temp));
                            }
                        }
                    break;
                    case 'b':
                        temp = (string)parametersEnumerator.Current;
                        parametersEnumerator.MoveNext();
                        if (add) {
                            if (ActiveChannelSyncing) {
                                try {
                                    channel.Bans.Add(temp);
                                } catch (ArgumentException) {
                                }
                            }
                            if (OnBan != null) {
                               OnBan(this, new BanEventArgs(ircdata, ircdata.Channel, ircdata.Nick, temp));
                            }
                        }
                        if (remove) {
                            if (ActiveChannelSyncing) {
                                channel.Bans.Remove(temp);
                            }
                            if (OnUnban != null) {
                                OnUnban(this, new UnbanEventArgs(ircdata, ircdata.Channel, ircdata.Nick, temp));
                            }
                        }
                    break;
                    case 'l':
                        temp = (string)parametersEnumerator.Current;
                        parametersEnumerator.MoveNext();
                        if (add) {
                            if (ActiveChannelSyncing) {
                                try {
                                    channel.UserLimit = int.Parse(temp);
                                } catch (FormatException) {
                                }
                            }
                        }
                        if (remove) {
                            if (ActiveChannelSyncing) {
                                channel.UserLimit = 0;
                            }
                        }
                    break;
                        case 'k':
                            temp = (string)parametersEnumerator.Current;
                            parametersEnumerator.MoveNext();
                            if (add) {
                                if (ActiveChannelSyncing) {
                                    channel.Key = temp;
                                }
                            }
                            if (remove) {
                                if (ActiveChannelSyncing) {
                                    channel.Key = "";
                                }
                            }
                        break;
                        default:
                            if (add) {
                                if (ActiveChannelSyncing) {
                                    channel.Mode += mode[i];
                                }
                            }
                            if (remove) {
                                if (ActiveChannelSyncing) {
                                    channel.Mode = channel.Mode.Replace(mode[i], new char());
                                }
                            }
                        break;
                    }
                }
        }
        private void _Event_PING(IrcMessageData ircdata)
        {
            string server = ircdata.RawMessageArray[1].Substring(1);
            RfcPong(server, Priority.Critical);
            if (OnPing != null) {
                OnPing(this, new PingEventArgs(ircdata, server));
            }
        }
        private void _Event_PONG(IrcMessageData ircdata)
        {
            if (OnPong != null) {
                OnPong(this, new PongEventArgs(ircdata, ircdata.Irc.Lag));
            }
        }
        private void _Event_ERROR(IrcMessageData ircdata)
        {
            string message = ircdata.Message;
            if (OnError != null) {
                OnError(this, new ErrorEventArgs(ircdata, message));
            }
        }
        private void _Event_JOIN(IrcMessageData ircdata)
        {
            string who = ircdata.Nick;
            string channelname = ircdata.Channel;
            if (IsMe(who)) {
                _JoinedChannels.Add(channelname);
            }
            if (ActiveChannelSyncing) {
                Channel channel;
                if (IsMe(who)) {
                    if (SupportNonRfc) {
                        channel = new NonRfcChannel(channelname);
                    } else {
                        channel = new Channel(channelname);
                    }
                    _Channels.Add(channelname, channel);
                    RfcMode(channelname);
                    RfcWho(channelname);
                    Ban(channelname);
                } else {
                    RfcWho(who);
                }
                channel = GetChannel(channelname);
                IrcUser ircuser = GetIrcUser(who);
                if (ircuser == null) {
                    ircuser = new IrcUser(who, this);
                    ircuser.Ident = ircdata.Ident;
                    ircuser.Host = ircdata.Host;
                    _IrcUsers.Add(who, ircuser);
                }
                ChannelUser channeluser;
                if (SupportNonRfc) {
                    channeluser = new NonRfcChannelUser(channelname, ircuser);
                } else {
                    channeluser = new ChannelUser(channelname, ircuser);
                }
                channel.UnsafeUsers.Add(who, channeluser);
            }
            if (OnJoin != null) {
                OnJoin(this, new JoinEventArgs(ircdata, channelname, who));
            }
        }
        private void _Event_PART(IrcMessageData ircdata)
        {
            string who = ircdata.Nick;
            string channel = ircdata.Channel;
            string partmessage = ircdata.Message;
            if (IsMe(who)) {
                _JoinedChannels.Remove(channel);
            }
            if (ActiveChannelSyncing) {
                if (IsMe(who)) {
                    _Channels.Remove(channel);
                } else {
                    _RemoveChannelUser(channel, who);
                    _RemoveIrcUser(who);
                }
            }
            if (OnPart != null) {
                OnPart(this, new PartEventArgs(ircdata, channel, who, partmessage));
            }
        }
        private void _Event_KICK(IrcMessageData ircdata)
        {
            string channelname = ircdata.Channel;
            string who = ircdata.Nick;
            string whom = ircdata.RawMessageArray[3];
            string reason = ircdata.Message;
            bool isme = IsMe(whom);
            if (isme) {
                _JoinedChannels.Remove(channelname);
            }
            if (ActiveChannelSyncing) {
                if (isme) {
                    Channel channel = GetChannel(channelname);
                    _Channels.Remove(channelname);
                    if (_AutoRejoinOnKick) {
                        RfcJoin(channel.Name, channel.Key);
                    }
                } else {
                    _RemoveChannelUser(channelname, whom);
                    _RemoveIrcUser(whom);
                }
            } else {
                if (isme && AutoRejoinOnKick) {
                    RfcJoin(channelname);
                }
            }
            if (OnKick != null) {
                OnKick(this, new KickEventArgs(ircdata, channelname, who, whom, reason));
            }
        }
        private void _Event_QUIT(IrcMessageData ircdata)
        {
            string who = ircdata.Nick;
            string reason = ircdata.Message;
            if (ActiveChannelSyncing) {
                IrcUser user = GetIrcUser(who);
                if (user != null) {
                    string[] joined_channels = user.JoinedChannels;
                    if (joined_channels != null) {
                        foreach (string channel in joined_channels) {
                            _RemoveChannelUser(channel, who);
                        }
                        _RemoveIrcUser(who);
                    }
                }
            }
            if (OnQuit != null) {
                OnQuit(this, new QuitEventArgs(ircdata, who, reason));
            }
        }
        private void _Event_PRIVMSG(IrcMessageData ircdata)
        {
            if (ircdata.Type == ReceiveType.CtcpRequest) {
                if (ircdata.Message.StartsWith("\x1"+"PING")) {
                    if (ircdata.Message.Length > 7) {
                        SendMessage(SendType.CtcpReply, ircdata.Nick, "PING "+ircdata.Message.Substring(6, (ircdata.Message.Length-7)));
              } else {
                        SendMessage(SendType.CtcpReply, ircdata.Nick, "PING");
              }
                } else if (ircdata.Message.StartsWith("\x1"+"VERSION")) {
                    string versionstring;
                    if (_CtcpVersion == null) {
                        versionstring = VersionString;
                    } else {
                        versionstring = _CtcpVersion;
                    }
                    SendMessage(SendType.CtcpReply, ircdata.Nick, "VERSION "+versionstring);
                } else if (ircdata.Message.StartsWith("\x1"+"CLIENTINFO")) {
                    SendMessage(SendType.CtcpReply, ircdata.Nick, "CLIENTINFO PING VERSION CLIENTINFO");
                }
            }
            switch (ircdata.Type) {
                case ReceiveType.ChannelMessage:
                    if (OnChannelMessage != null) {
                        OnChannelMessage(this, new IrcEventArgs(ircdata));
                    }
                    break;
                case ReceiveType.ChannelAction:
                    if (OnChannelAction != null) {
                        string action = ircdata.Message.Substring(7, ircdata.Message.Length-8);
                        OnChannelAction(this, new ActionEventArgs(ircdata, action));
                    }
                    break;
                case ReceiveType.QueryMessage:
                    if (OnQueryMessage != null) {
                        OnQueryMessage(this, new IrcEventArgs(ircdata));
                    }
                    break;
                case ReceiveType.QueryAction:
                    if (OnQueryAction != null) {
                        string action = ircdata.Message.Substring(7, ircdata.Message.Length-8);
                        OnQueryAction(this, new ActionEventArgs(ircdata, action));
                    }
                    break;
                case ReceiveType.CtcpRequest:
                    if (OnCtcpRequest != null) {
                        int space_pos = ircdata.Message.IndexOf(' ');
                        string cmd = "";
                        string param = "";
                        if (space_pos != -1) {
                            cmd = ircdata.Message.Substring(1, space_pos - 1);
                            param = ircdata.Message.Substring(space_pos + 1,
                                        ircdata.Message.Length - space_pos - 2);
                        } else {
                            cmd = ircdata.Message.Substring(1, ircdata.Message.Length - 2);
                        }
                        OnCtcpRequest(this, new CtcpEventArgs(ircdata, cmd, param));
                    }
                    break;
            }
        }
        private void _Event_NOTICE(IrcMessageData ircdata)
        {
            switch (ircdata.Type) {
                case ReceiveType.ChannelNotice:
                    if (OnChannelNotice != null) {
                        OnChannelNotice(this, new IrcEventArgs(ircdata));
                    }
                    break;
                case ReceiveType.QueryNotice:
                    if (OnQueryNotice != null) {
                        OnQueryNotice(this, new IrcEventArgs(ircdata));
                    }
                    break;
                case ReceiveType.CtcpReply:
                    if (OnCtcpReply != null) {
                        int space_pos = ircdata.Message.IndexOf(' ');
                        string cmd = "";
                        string param = "";
                        if (space_pos != -1) {
                            cmd = ircdata.Message.Substring(1, space_pos - 1);
                            param = ircdata.Message.Substring(space_pos + 1,
                                        ircdata.Message.Length - space_pos - 2);
                        } else {
                            cmd = ircdata.Message.Substring(1, ircdata.Message.Length - 2);
                        }
                        OnCtcpReply(this, new CtcpEventArgs(ircdata, cmd, param));
                    }
                    break;
            }
        }
        private void _Event_TOPIC(IrcMessageData ircdata)
        {
            string who = ircdata.Nick;
            string channel = ircdata.Channel;
            string newtopic = ircdata.Message;
            if (ActiveChannelSyncing &&
                IsJoined(channel)) {
                GetChannel(channel).Topic = newtopic;
            }
            if (OnTopicChange != null) {
                OnTopicChange(this, new TopicChangeEventArgs(ircdata, channel, who, newtopic));
            }
        }
        private void _Event_NICK(IrcMessageData ircdata)
        {
            string oldnickname = ircdata.Nick;
            string newnickname = ircdata.Message;
            if (IsMe(ircdata.Nick)) {
                _Nickname = newnickname;
            }
            if (ActiveChannelSyncing) {
                IrcUser ircuser = GetIrcUser(oldnickname);
                if (ircuser != null) {
                    string[] joinedchannels = ircuser.JoinedChannels;
                    ircuser.Nick = newnickname;
                    _IrcUsers.Remove(oldnickname);
                    _IrcUsers.Add(newnickname, ircuser);
                    Channel channel;
                    ChannelUser channeluser;
                    foreach (string channelname in joinedchannels) {
                        channel = GetChannel(channelname);
                        channeluser = GetChannelUser(channelname, oldnickname);
                        channel.UnsafeUsers.Remove(oldnickname);
                        channel.UnsafeUsers.Add(newnickname, channeluser);
                        if (channeluser.IsOp) {
                            channel.UnsafeOps.Remove(oldnickname);
                            channel.UnsafeOps.Add(newnickname, channeluser);
                        }
                        if (SupportNonRfc && ((NonRfcChannelUser)channeluser).IsHalfop) {
                            NonRfcChannel nchannel = (NonRfcChannel)channel;
                            nchannel.UnsafeHalfops.Remove(oldnickname);
                            nchannel.UnsafeHalfops.Add(newnickname, channeluser);
                        }
                        if (channeluser.IsVoice) {
                            channel.UnsafeVoices.Remove(oldnickname);
                            channel.UnsafeVoices.Add(newnickname, channeluser);
                        }
                    }
                }
            }
            if (OnNickChange != null) {
                OnNickChange(this, new NickChangeEventArgs(ircdata, oldnickname, newnickname));
            }
        }
        private void _Event_INVITE(IrcMessageData ircdata)
        {
            string channel = ircdata.Channel;
            string inviter = ircdata.Nick;
            if (AutoJoinOnInvite) {
                if (channel.Trim() != "0") {
                    RfcJoin(channel);
                }
            }
            if (OnInvite != null) {
                OnInvite(this, new InviteEventArgs(ircdata, channel, inviter));
            }
        }
        private void _Event_MODE(IrcMessageData ircdata)
        {
            if (IsMe(ircdata.RawMessageArray[2])) {
                _Usermode = ircdata.RawMessageArray[3].Substring(1);
            } else {
                string mode = ircdata.RawMessageArray[3];
                string parameter = String.Join(" ", ircdata.RawMessageArray, 4, ircdata.RawMessageArray.Length-4);
                _InterpretChannelMode(ircdata, mode, parameter);
            }
            if ((ircdata.Type == ReceiveType.UserModeChange) &&
                (OnUserModeChange != null)) {
                OnUserModeChange(this, new IrcEventArgs(ircdata));
            }
            if ((ircdata.Type == ReceiveType.ChannelModeChange) &&
                (OnChannelModeChange != null)) {
                OnChannelModeChange(this, new IrcEventArgs(ircdata));
            }
            if (OnModeChange != null) {
                OnModeChange(this, new IrcEventArgs(ircdata));
            }
        }
        private void _Event_RPL_CHANNELMODEIS(IrcMessageData ircdata)
        {
            if (ActiveChannelSyncing &&
                IsJoined(ircdata.Channel)) {
                string mode = ircdata.RawMessageArray[4];
                string parameter = String.Join(" ", ircdata.RawMessageArray, 5, ircdata.RawMessageArray.Length-5);
                _InterpretChannelMode(ircdata, mode, parameter);
            }
        }
        private void _Event_RPL_WELCOME(IrcMessageData ircdata)
        {
            _Nickname = ircdata.RawMessageArray[2];
            if (OnRegistered != null) {
                OnRegistered(this, EventArgs.Empty);
            }
        }
        private void _Event_RPL_TOPIC(IrcMessageData ircdata)
        {
            string topic = ircdata.Message;
            string channel = ircdata.Channel;
            if (ActiveChannelSyncing &&
                IsJoined(channel)) {
                GetChannel(channel).Topic = topic;
            }
            if (OnTopic != null) {
                OnTopic(this, new TopicEventArgs(ircdata, channel, topic));
            }
        }
        private void _Event_RPL_NOTOPIC(IrcMessageData ircdata)
        {
            string channel = ircdata.Channel;
            if (ActiveChannelSyncing &&
                IsJoined(channel)) {
                GetChannel(channel).Topic = "";
            }
            if (OnTopic != null) {
                OnTopic(this, new TopicEventArgs(ircdata, channel, ""));
            }
        }
        private void _Event_RPL_NAMREPLY(IrcMessageData ircdata)
        {
            string channelname = ircdata.Channel;
            string[] userlist = ircdata.MessageArray;
            if (ActiveChannelSyncing &&
                IsJoined(channelname)) {
                string nickname;
                bool op;
                bool halfop;
                bool voice;
                foreach (string user in userlist) {
                    if (user.Length <= 0) {
                        continue;
                    }
                    op = false;
                    halfop = false;
                    voice = false;
                    switch (user[0]) {
                        case '@':
                            op = true;
                            nickname = user.Substring(1).Replace("~", "").Replace("&", "").Replace("@", "").Replace("%", "").Replace("+", "");
                        break;
                        case '+':
                            voice = true;
                            nickname = user.Substring(1).Replace("~", "").Replace("&", "").Replace("@", "").Replace("%", "").Replace("+", "");
                        break;
                        case '&':
                            op = true;
                            nickname = user.Substring(1).Replace("~", "").Replace("&", "").Replace("@", "").Replace("%", "").Replace("+", "");
                        break;
                        case '%':
                            halfop = true;
                            nickname = user.Substring(1).Replace("~", "").Replace("&", "").Replace("@", "").Replace("%", "").Replace("+", "");
                        break;
                        case '~':
                            op = true;
                            nickname = user.Substring(1).Replace("~","").Replace("&","").Replace("@", "").Replace("%", "").Replace("+", "");
                        break;
                        default:
                            nickname = user;
                        break;
                    }
                    IrcUser ircuser = GetIrcUser(nickname);
                    ChannelUser channeluser = GetChannelUser(channelname, nickname);
                    if (ircuser == null) {
                        ircuser = new IrcUser(nickname, this);
                        _IrcUsers.Add(nickname, ircuser);
                    }
                    if (channeluser == null) {
                        if (SupportNonRfc) {
                            channeluser = new NonRfcChannelUser(channelname, ircuser);
                        } else {
                            channeluser = new ChannelUser(channelname, ircuser);
                        }
                        Channel channel = GetChannel(channelname);
                        channel.UnsafeUsers.Add(nickname, channeluser);
                        if (op) {
                            channel.UnsafeOps.Add(nickname, channeluser);
                        }
                        if (SupportNonRfc && halfop) {
                            ((NonRfcChannel)channel).UnsafeHalfops.Add(nickname, channeluser);
                        }
                        if (voice) {
                            channel.UnsafeVoices.Add(nickname, channeluser);
                        }
                    }
                    channeluser.IsOp = op;
                    channeluser.IsVoice = voice;
                    if (SupportNonRfc) {
                        ((NonRfcChannelUser)channeluser).IsHalfop = halfop;
                    }
                }
            }
            if (OnNames != null) {
                OnNames(this, new NamesEventArgs(ircdata, channelname, userlist));
            }
        }
        private void _Event_RPL_ENDOFNAMES(IrcMessageData ircdata)
        {
            string channelname = ircdata.RawMessageArray[3];
            if (ActiveChannelSyncing &&
                IsJoined(channelname)) {
                if (OnChannelPassiveSynced != null) {
                    OnChannelPassiveSynced(this, new IrcEventArgs(ircdata));
                }
            }
        }
        private void _Event_RPL_AWAY(IrcMessageData ircdata)
        {
            string who = ircdata.RawMessageArray[3];
            string awaymessage = ircdata.Message;
            if (ActiveChannelSyncing) {
                IrcUser ircuser = GetIrcUser(who);
                if (ircuser != null) {
                    ircuser.IsAway = true;
                }
            }
            if (OnAway != null) {
                OnAway(this, new AwayEventArgs(ircdata, who, awaymessage));
            }
        }
        private void _Event_RPL_UNAWAY(IrcMessageData ircdata)
        {
            _IsAway = false;
            if (OnUnAway != null) {
                OnUnAway(this, new IrcEventArgs(ircdata));
            }
        }
        private void _Event_RPL_NOWAWAY(IrcMessageData ircdata)
        {
            _IsAway = true;
            if (OnNowAway != null) {
                OnNowAway(this, new IrcEventArgs(ircdata));
            }
        }
        private void _Event_RPL_WHOREPLY(IrcMessageData ircdata)
        {
            string channel = ircdata.Channel;
            string ident = ircdata.RawMessageArray[4];
            string host = ircdata.RawMessageArray[5];
            string server = ircdata.RawMessageArray[6];
            string nick = ircdata.RawMessageArray[7];
            string usermode = ircdata.RawMessageArray[8];
            string realname = ircdata.Message.Substring(2);
            int hopcount = 0;
            string temp = ircdata.RawMessageArray[9].Substring(1);
            try {
                hopcount = int.Parse(temp);
            } catch (FormatException) {
            }
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
            if (ActiveChannelSyncing &&
                IsJoined(channel)) {
                IrcUser ircuser = GetIrcUser(nick);
                ChannelUser channeluser = GetChannelUser(channel, nick);
                if (ircuser != null) {
                    ircuser.Ident = ident;
                    ircuser.Host = host;
                    ircuser.Server = server;
                    ircuser.Nick = nick;
                    ircuser.HopCount = hopcount;
                    ircuser.Realname = realname;
                    ircuser.IsAway = away;
                    ircuser.IsIrcOp = ircop;
                    switch (channel[0]) {
                        case '#':
                        case '!':
                        case '&':
                        case '+':
                            if (channeluser != null) {
                                channeluser.IsOp = op;
                                channeluser.IsVoice = voice;
                            }
                        break;
                    }
                }
            }
            if (OnWho != null) {
                OnWho(this, new WhoEventArgs(ircdata, channel, nick, ident, host, realname, away, op, voice, ircop, server, hopcount));
            }
        }
        private void _Event_RPL_MOTD(IrcMessageData ircdata)
        {
            if (!_MotdReceived) {
                _Motd.Add(ircdata.Message);
            }
            if (OnMotd != null) {
                OnMotd(this, new MotdEventArgs(ircdata, ircdata.Message));
            }
        }
        private void _Event_RPL_ENDOFMOTD(IrcMessageData ircdata)
        {
            _MotdReceived = true;
        }
        private void _Event_RPL_BANLIST(IrcMessageData ircdata)
        {
        }
        private void _Event_RPL_ENDOFBANLIST(IrcMessageData ircdata)
        {
            string channelname = ircdata.Channel;
            if (ActiveChannelSyncing &&
                IsJoined(channelname)) {
                Channel channel = GetChannel(channelname);
                channel.ActiveSyncStop = DateTime.Now;
                if (OnChannelActiveSynced != null) {
                    OnChannelActiveSynced(this, new IrcEventArgs(ircdata));
                }
            }
        }
        private void _Event_ERR(IrcMessageData ircdata)
        {
            if (OnErrorMessage != null) {
                OnErrorMessage(this, new IrcEventArgs(ircdata));
            }
        }
        private void _Event_ERR_NICKNAMEINUSE(IrcMessageData ircdata)
        {
            if (!AutoNickHandling) {
                return;
            }
            string nickname;
            if (_CurrentNickname == NicknameList.Length-1) {
                Random rand = new Random();
                int number = rand.Next(999);
                if (Nickname.Length > 5) {
                    nickname = Nickname.Substring(0, 5)+number;
                } else {
                    nickname = Nickname.Substring(0, Nickname.Length-1)+number;
                }
            } else {
                nickname = _NextNickname();
            }
            RfcNick(nickname, Priority.Critical);
        }
        public void NickServIdentify(string password)
        {
            RfcPrivmsg("NickServ", "IDENTIFY " + password);
        }
    }
}
