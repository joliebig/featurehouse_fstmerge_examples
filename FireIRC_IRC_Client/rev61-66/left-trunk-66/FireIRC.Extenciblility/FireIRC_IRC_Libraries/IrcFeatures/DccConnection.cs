using System;
using System.IO;
using System.Net;
using System.Threading;
using System.Net.Sockets;
namespace FireIRC.Extenciblility.IRCClasses
{
    public class DccConnection
    {
        protected IrcFeatures Irc;
        protected TcpListener DccServer;
        protected TcpClient Connection;
        protected IPEndPoint LocalEndPoint;
        protected IPEndPoint RemoteEndPoint;
        protected IPAddress ExternalIPAdress;
        protected DateTime Timeout;
        protected string User;
        protected bool isConnected = false;
        protected bool isValid = true;
        protected bool reject = false;
        protected long session;
        private class Session
        {
            private static long next;
            internal static long Next {
                get {
                    return ++next;
                }
            }
        }
        public bool Connected{
            get {
                return isConnected;
            }
        }
        public bool Valid{
            get {
                return isValid && (isConnected || (DateTime.Now < Timeout));
            }
        }
        public string Nick {
            get {
                return User;
            }
        }
        public event DccConnectionHandler OnDccChatRequestEvent;
        protected virtual void DccChatRequestEvent(DccEventArgs e) {
            if (OnDccChatRequestEvent!=null) {OnDccChatRequestEvent(this, e); }
              Irc.DccChatRequestEvent(e);
        }
        public event DccSendRequestHandler OnDccSendRequestEvent;
        protected virtual void DccSendRequestEvent(DccSendRequestEventArgs e) {
            if (OnDccSendRequestEvent!=null) {OnDccSendRequestEvent(this, e); }
              Irc.DccSendRequestEvent(e);
        }
        public event DccConnectionHandler OnDccChatStartEvent;
        protected virtual void DccChatStartEvent(DccEventArgs e) {
            if (OnDccChatStartEvent!=null) {OnDccChatStartEvent(this, e); }
              Irc.DccChatStartEvent(e);
        }
        public event DccConnectionHandler OnDccSendStartEvent;
        protected virtual void DccSendStartEvent(DccEventArgs e) {
            if (OnDccSendStartEvent!=null) {OnDccSendStartEvent(this, e); }
            Irc.DccSendStartEvent(e);
        }
        public event DccChatLineHandler OnDccChatReceiveLineEvent;
        protected virtual void DccChatReceiveLineEvent(DccChatEventArgs e) {
            if (OnDccChatReceiveLineEvent!=null) {OnDccChatReceiveLineEvent(this, e); }
            Irc.DccChatReceiveLineEvent(e);
        }
        public event DccSendPacketHandler OnDccSendReceiveBlockEvent;
        protected virtual void DccSendReceiveBlockEvent(DccSendEventArgs e) {
            if (OnDccSendReceiveBlockEvent!=null) {OnDccSendReceiveBlockEvent(this, e); }
            Irc.DccSendReceiveBlockEvent(e);
        }
        public event DccChatLineHandler OnDccChatSentLineEvent;
        protected virtual void DccChatSentLineEvent(DccChatEventArgs e) {
            if (OnDccChatSentLineEvent!=null) {OnDccChatSentLineEvent(this, e); }
            Irc.DccChatSentLineEvent(e);
        }
        public event DccSendPacketHandler OnDccSendSentBlockEvent;
        protected virtual void DccSendSentBlockEvent(DccSendEventArgs e) {
            if (OnDccSendSentBlockEvent!=null) {OnDccSendSentBlockEvent(this, e); }
            Irc.DccSendSentBlockEvent(e);
        }
        public event DccConnectionHandler OnDccChatStopEvent;
        protected virtual void DccChatStopEvent(DccEventArgs e) {
            if (OnDccChatStopEvent!=null) {OnDccChatStopEvent(this, e); }
            Irc.DccChatStopEvent(e);
        }
        public event DccConnectionHandler OnDccSendStopEvent;
        protected virtual void DccSendStopEvent(DccEventArgs e) {
            if (OnDccSendStopEvent!=null) {OnDccSendStopEvent(this, e); }
            Irc.DccSendStopEvent(e);
        }
        internal DccConnection()
        {
            session = Session.Next;
            Timeout = DateTime.Now.AddSeconds(120);
        }
        internal virtual void InitWork(Object stateInfo)
        {
            throw new NotSupportedException();
        }
        internal bool isSession(long session)
        {
            return (session == this.session);
        }
        public void RejectRequest()
        {
            Irc.SendMessage(SendType.CtcpReply, User, "ERRMSG DCC Rejected");
            reject = true;
            isValid = false;
        }
        public void Disconnect()
        {
            isConnected = false;
            isValid = false;
        }
        public override string ToString()
        {
            return "DCC Session " + session + " of " + this.GetType().ToString() + " is " + ((isConnected)?"connected to "+RemoteEndPoint.Address.ToString():"not connected") + "[" + this.User + "]";
        }
        protected long HostToDccInt(IPAddress ip)
        {
            long temp = (ip.Address & 0xff) << 24;
            temp |= (ip.Address & 0xff00) << 8;
            temp |= (ip.Address >> 8) & 0xff00;
            temp |= (ip.Address >> 24) & 0xff;
            return temp;
        }
        protected string DccIntToHost(long ip)
        {
            IPEndPoint ep = new IPEndPoint(ip, 80);
            char[] sep = { '.' };
            string[] ipparts = ep.Address.ToString().Split(sep);
            return ipparts[3] + "." + ipparts[2] + "." + ipparts[1] + "." + ipparts[0];
        }
        protected byte[] getAck(long SentBytes)
        {
            byte[] acks = new byte[4];
            acks[0] = (byte)((SentBytes >>24 ) % 256);
            acks[1] = (byte)((SentBytes >>16 ) % 256);
            acks[2] = (byte)((SentBytes >>8 ) % 256);
            acks[3] = (byte)((SentBytes ) % 256);
            return acks;
        }
        protected string FilterMarker(string msg)
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
