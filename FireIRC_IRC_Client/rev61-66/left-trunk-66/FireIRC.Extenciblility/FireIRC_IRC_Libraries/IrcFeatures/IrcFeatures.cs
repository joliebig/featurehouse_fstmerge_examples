using System;
using System.IO;
using System.Net;
using System.Threading;
using System.Collections.Generic;
using System.Collections.ObjectModel;
namespace FireIRC.Extenciblility.IRCClasses
{
    public class IrcFeatures : IrcClient
    {
        public IPAddress ExternalIpAdress {
            get {
                return _ExternalIpAdress;
            }
            set {
                _ExternalIpAdress = value;
            }
        }
        public ReadOnlyCollection<DccConnection> DccConnections {
            get {
                return new ReadOnlyCollection<DccConnection>(_DccConnections);
            }
        }
        public Dictionary<string, ctcpDelagte> CtcpDelegates {
            get {
                return _CtcpDelagtes;
            }
        }
        public string CtcpUserInfo
        {
            get {
                return _CtcpUserInfo;
            }
            set {
                _CtcpUserInfo = value;
            }
        }
        public string CtcpUrl
        {
            get {
                return _CtcpUrl;
            }
            set {
                _CtcpUrl = value;
            }
        }
        public string CtcpSource
        {
            get {
                return _CtcpSource;
            }
            set {
                _CtcpSource = value;
            }
        }
        private IPAddress _ExternalIpAdress;
        private List<DccConnection> _DccConnections = new List<DccConnection>();
        private Dictionary<string, ctcpDelagte> _CtcpDelagtes = new Dictionary<string, ctcpDelagte>(StringComparer.CurrentCultureIgnoreCase);
        private string _CtcpUserInfo;
        private string _CtcpUrl;
        private string _CtcpSource;
        internal DccSpeed Speed = DccSpeed.RfcSendAhead;
        public event DccConnectionHandler OnDccChatRequestEvent;
        public void DccChatRequestEvent(DccEventArgs e) {
            if (OnDccChatRequestEvent!=null) {OnDccChatRequestEvent(this, e); }
        }
        public event DccSendRequestHandler OnDccSendRequestEvent;
        public void DccSendRequestEvent(DccSendRequestEventArgs e) {
            if (OnDccSendRequestEvent!=null) {OnDccSendRequestEvent(this, e); }
        }
        public event DccConnectionHandler OnDccChatStartEvent;
        public void DccChatStartEvent(DccEventArgs e) {
            if (OnDccChatStartEvent!=null) {OnDccChatStartEvent(this, e); }
        }
        public event DccConnectionHandler OnDccSendStartEvent;
        public void DccSendStartEvent(DccEventArgs e) {
            if (OnDccSendStartEvent!=null) {OnDccSendStartEvent(this, e); }
        }
        public event DccChatLineHandler OnDccChatReceiveLineEvent;
        public void DccChatReceiveLineEvent(DccChatEventArgs e) {
            if (OnDccChatReceiveLineEvent!=null) {OnDccChatReceiveLineEvent(this, e); }
        }
        public event DccSendPacketHandler OnDccSendReceiveBlockEvent;
        public void DccSendReceiveBlockEvent(DccSendEventArgs e) {
            if (OnDccSendReceiveBlockEvent!=null) {OnDccSendReceiveBlockEvent(this, e); }
        }
        public event DccChatLineHandler OnDccChatSentLineEvent;
        public void DccChatSentLineEvent(DccChatEventArgs e) {
            if (OnDccChatSentLineEvent!=null) {OnDccChatSentLineEvent(this, e); }
        }
        public event DccSendPacketHandler OnDccSendSentBlockEvent;
        internal void DccSendSentBlockEvent(DccSendEventArgs e) {
            if (OnDccSendSentBlockEvent!=null) {OnDccSendSentBlockEvent(this, e); }
        }
        public event DccConnectionHandler OnDccChatStopEvent;
        public void DccChatStopEvent(DccEventArgs e) {
            if (OnDccChatStopEvent!=null) {OnDccChatStopEvent(this, e); }
        }
        public event DccConnectionHandler OnDccSendStopEvent;
        public void DccSendStopEvent(DccEventArgs e) {
            if (OnDccSendStopEvent!=null) {OnDccSendStopEvent(this, e); }
        }
        public IrcFeatures() : base()
        {
            this.OnCtcpRequest += new CtcpEventHandler(this.CtcpRequestsHandler);
            _CtcpDelagtes.Add("version", this.CtcpVersionDelegate);
            _CtcpDelagtes.Add("clientinfo", this.CtcpClientInfoDelegate);
            _CtcpDelagtes.Add("time", this.CtcpTimeDelegate);
            _CtcpDelagtes.Add("userinfo", this.CtcpUserInfoDelegate);
            _CtcpDelagtes.Add("url", this.CtcpUrlDelegate);
            _CtcpDelagtes.Add("source", this.CtcpSourceDelegate);
            _CtcpDelagtes.Add("finger", this.CtcpFingerDelegate);
            _CtcpDelagtes.Add("dcc", this.CtcpDccDelegate);
            _CtcpDelagtes.Add("ping", this.CtcpPingDelegate);
        }
        public void InitDccChat(string user) {
            this.InitDccChat(user, false);
        }
        public void InitDccChat(string user, bool passive) {
            this.InitDccChat(user, passive, Priority.Medium);
        }
        public void InitDccChat(string user, bool passive, Priority priority) {
            DccChat chat = new DccChat(this, user, _ExternalIpAdress, passive, priority);
            _DccConnections.Add(chat);
            ThreadPool.QueueUserWorkItem(new WaitCallback(chat.InitWork));
            RemoveInvalidDccConnections();
        }
        public void SendFile(string user, string filepath)
        {
            FileInfo fi = new FileInfo(filepath);
            if (fi.Exists) {
                this.SendFile(user, new FileStream(filepath, FileMode.Open), fi.Name, fi.Length, DccSpeed.RfcSendAhead, false, Priority.Medium);
            }
        }
        public void SendFile(string user, string filepath, bool passive)
        {
            FileInfo fi = new FileInfo(filepath);
            if (fi.Exists) {
                this.SendFile(user, new FileStream(filepath, FileMode.Open), fi.Name, fi.Length, DccSpeed.RfcSendAhead, passive, Priority.Medium);
            }
        }
        public void SendFile(string user, Stream file, string filename, long filesize) {
            this.SendFile(user, file, filename, filesize, DccSpeed.RfcSendAhead, false);
        }
        public void SendFile(string user, Stream file, string filename, long filesize, DccSpeed speed, bool passive) {
            this.SendFile(user, file, filename, filesize, speed, passive, Priority.Medium);
        }
        public void SendFile(string user, Stream file, string filename, long filesize, DccSpeed speed, bool passive, Priority priority) {
            DccSend send = new DccSend(this, user, _ExternalIpAdress, file, filename, filesize, speed, passive, priority);
            _DccConnections.Add(send);
            ThreadPool.QueueUserWorkItem(new WaitCallback(send.InitWork));
            RemoveInvalidDccConnections();
        }
        private void CtcpRequestsHandler(object sender, CtcpEventArgs e)
        {
            if (_CtcpDelagtes.ContainsKey(e.CtcpCommand)) {
                _CtcpDelagtes[e.CtcpCommand].Invoke(e);
            } else {
            }
            RemoveInvalidDccConnections();
        }
        private void CtcpVersionDelegate(CtcpEventArgs e)
        {
            SendMessage(SendType.CtcpReply, e.Data.Nick, "VERSION " + ((CtcpVersion==null)?VersionString:CtcpVersion));
        }
        private void CtcpClientInfoDelegate(CtcpEventArgs e)
        {
            string clientInfo = "CLIENTINFO";
            foreach(KeyValuePair<string, ctcpDelagte> kvp in _CtcpDelagtes) {
                clientInfo = clientInfo+" "+kvp.Key.ToUpper();
            }
            SendMessage(SendType.CtcpReply, e.Data.Nick, clientInfo);
        }
        private void CtcpPingDelegate(CtcpEventArgs e)
        {
            if (e.Data.Message.Length > 7) {
                SendMessage(SendType.CtcpReply, e.Data.Nick, "PING "+e.Data.Message.Substring(6, (e.Data.Message.Length-7)));
            } else {
                SendMessage(SendType.CtcpReply, e.Data.Nick, "PING");
            }
        }
        private void CtcpRfcPingDelegate(CtcpEventArgs e)
        {
            if (e.Data.Message.Length > 7) {
                SendMessage(SendType.CtcpReply, e.Data.Nick, "PONG "+e.Data.Message.Substring(6, (e.Data.Message.Length-7)));
            } else {
                SendMessage(SendType.CtcpReply, e.Data.Nick, "PONG");
            }
        }
        private void CtcpTimeDelegate(CtcpEventArgs e)
        {
            SendMessage(SendType.CtcpReply, e.Data.Nick, "TIME " + DateTime.Now.ToString("r"));
        }
        private void CtcpUserInfoDelegate(CtcpEventArgs e)
        {
            SendMessage(SendType.CtcpReply, e.Data.Nick, "USERINFO " + ((CtcpUserInfo==null)?"No user info given.":CtcpUserInfo));
        }
        private void CtcpUrlDelegate(CtcpEventArgs e)
        {
            SendMessage(SendType.CtcpReply, e.Data.Nick, "URL " + ((CtcpUrl==null)?"http://www.google.com":CtcpUrl));
        }
        private void CtcpSourceDelegate(CtcpEventArgs e)
        {
            SendMessage(SendType.CtcpReply, e.Data.Nick, "SOURCE " + ((CtcpSource==null)?"http://smartirc4net.meebey.net":CtcpSource));
        }
        private void CtcpFingerDelegate(CtcpEventArgs e)
        {
            SendMessage(SendType.CtcpReply, e.Data.Nick, "FINGER Don't touch little Helga there! " );
        }
        private void CtcpDccDelegate(CtcpEventArgs e)
        {
            if (e.Data.MessageArray.Length < 2) {
                SendMessage(SendType.CtcpReply, e.Data.Nick, "ERRMSG DCC missing parameters");
            } else {
                switch(e.Data.MessageArray[1]) {
                    case "CHAT":
                        DccChat chat = new DccChat(this, _ExternalIpAdress, e);
                        _DccConnections.Add(chat);
                        ThreadPool.QueueUserWorkItem(new WaitCallback(chat.InitWork));
                        break;
                    case "SEND":
                        if (e.Data.MessageArray.Length > 6 && (FilterMarker(e.Data.MessageArray[6]) != "T")) {
                            long session = -1;
                            long.TryParse(FilterMarker(e.Data.MessageArray[6]), out session);
                            foreach(DccConnection dc in _DccConnections) {
                                if(dc.isSession(session)) {
                                    ((DccSend)dc).SetRemote(e);
                                    ((DccSend)dc).AcceptRequest(null, 0);
                                       return;
                                }
                            }
                            SendMessage(SendType.CtcpReply, e.Data.Nick, "ERRMSG Invalid passive DCC");
                        } else {
                            DccSend send = new DccSend(this, _ExternalIpAdress, e);
                            _DccConnections.Add(send);
                            ThreadPool.QueueUserWorkItem(new WaitCallback(send.InitWork));
                        }
                        break;
                    case "RESUME":
                        foreach(DccConnection dc in _DccConnections) {
                            if((dc is DccSend) && (((DccSend)dc).TryResume(e))) {
                                return;
                            }
                        }
                        SendMessage(SendType.CtcpReply, e.Data.Nick, "ERRMSG Invalid DCC RESUME");
                        break;
                    case "ACCEPT":
                        foreach(DccConnection dc in _DccConnections) {
                            if((dc is DccSend) && (((DccSend)dc).TryAccept(e))) {
                                return;
                            }
                        }
                        SendMessage(SendType.CtcpReply, e.Data.Nick, "ERRMSG Invalid DCC ACCEPT");
                        break;
                    case "XMIT":
                        SendMessage(SendType.CtcpReply, e.Data.Nick, "ERRMSG DCC XMIT not implemented");
                        break;
                    default:
                        SendMessage(SendType.CtcpReply, e.Data.Nick, "ERRMSG DCC "+e.CtcpParameter+" unavailable");
                        break;
                }
            }
        }
        private void RemoveInvalidDccConnections()
        {
            List<DccConnection> invalidDc= new List<DccConnection>();
            foreach (DccConnection dc in _DccConnections) {
                if ((!dc.Valid) && (!dc.Connected)) {
                    invalidDc.Add(dc);
                }
            }
            foreach (DccConnection dc in invalidDc) {
                _DccConnections.Remove(dc);
            }
        }
        private string FilterMarker(string msg)
        {
            string result = "";
            foreach(char c in msg)
            {
                if (c!=IrcConstants.CtcpChar)
                  result += c;
            }
            return result;
        }
    }
}
