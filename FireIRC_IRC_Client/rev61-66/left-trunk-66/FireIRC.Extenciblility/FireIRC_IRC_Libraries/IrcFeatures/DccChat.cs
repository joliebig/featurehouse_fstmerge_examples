using System;
using System.IO;
using System.Net;
using System.Threading;
using System.Net.Sockets;
namespace FireIRC.Extenciblility.IRCClasses
{
    public class DccChat : DccConnection
    {
        private StreamReader _sr;
        private StreamWriter _sw;
        private int _lines;
        public int Lines {
            get {
                return _lines;
            }
        }
        internal DccChat(IrcFeatures irc, string user, IPAddress externalIpAdress, bool passive, Priority priority) : base()
        {
            this.Irc = irc;
            this.ExternalIPAdress = externalIpAdress;
            this.User = user;
            if(passive) {
                irc.SendMessage(SendType.CtcpRequest, user, "DCC CHAT chat " + HostToDccInt(externalIpAdress).ToString() + " 0 " + session, priority);
                this.Disconnect();
            } else {
                DccServer = new TcpListener(new IPEndPoint(IPAddress.Any, 0));
                DccServer.Start();
                LocalEndPoint = (IPEndPoint)DccServer.LocalEndpoint;
                irc.SendMessage(SendType.CtcpRequest, user, "DCC CHAT chat " + HostToDccInt(externalIpAdress).ToString() + " " + LocalEndPoint.Port, priority);
            }
        }
        internal DccChat(IrcFeatures irc, IPAddress externalIpAdress, CtcpEventArgs e) : base ()
        {
            this.Irc = irc;
            this.ExternalIPAdress = externalIpAdress;
            this.User = e.Data.Nick;
            long ip; int port;
            if (e.Data.MessageArray.Length > 4) {
                bool okIP = long.TryParse(e.Data.MessageArray[3], out ip);
                bool okPo = int.TryParse(FilterMarker(e.Data.MessageArray[4]), out port);
                if((e.Data.MessageArray[2]=="chat") && okIP && okPo) {
                    RemoteEndPoint = new IPEndPoint(IPAddress.Parse(DccIntToHost(ip)), port);
                    if (e.Data.MessageArray.Length > 5 && e.Data.MessageArray[5] != "T") {
                       this.AcceptRequest();
                       return;
                    }
                    DccChatRequestEvent(new DccEventArgs(this));
                    return;
                } else {
                    irc.SendMessage(SendType.CtcpReply, e.Data.Nick, "ERRMSG DCC Chat Parameter Error");
                }
            } else {
                irc.SendMessage(SendType.CtcpReply, e.Data.Nick, "ERRMSG DCC Chat not enough parameters");
            }
            this.isValid = false;
        }
        internal override void InitWork(Object stateInfo)
        {
            if (!Valid)
                return;
            if (DccServer != null) {
                Connection = DccServer.AcceptTcpClient();
                RemoteEndPoint = (IPEndPoint)Connection.Client.RemoteEndPoint;
                DccServer.Stop();
                isConnected = true;
            } else {
                while(!isConnected) {
                    Thread.Sleep(500);
                    if (reject) {
                        isValid = false;
                        return;
                    }
                }
            }
            DccChatStartEvent(new DccEventArgs(this));
            _sr = new StreamReader(Connection.GetStream(), Irc.Encoding);
            _sw = new StreamWriter(Connection.GetStream(), Irc.Encoding);
            _sw.AutoFlush = true;
            string line;
            while(((line = _sr.ReadLine())!=null) && (isConnected)) {
                DccChatReceiveLineEvent(new DccChatEventArgs(this, line));
                _lines++;
            }
            isValid = false;
            isConnected = false;
            DccChatStopEvent(new DccEventArgs(this));
        }
        public bool AcceptRequest()
        {
            if (isConnected)
                return false;
            try {
                if(RemoteEndPoint.Port==0) {
                    DccServer = new TcpListener(new IPEndPoint(IPAddress.Any, 0));
                    DccServer.Start();
                    LocalEndPoint = (IPEndPoint)DccServer.LocalEndpoint;
                    Irc.SendMessage(SendType.CtcpRequest, User, "DCC CHAT chat " + HostToDccInt(ExternalIPAdress).ToString() + " " + LocalEndPoint.Port);
                } else {
                    Connection = new TcpClient();
                    Connection.Connect(RemoteEndPoint);
                    isConnected = true;
                }
                return true;
            } catch(Exception) {
                isValid = false;
                isConnected = false;
                return false;
            }
        }
        public void WriteLine(string message) {
            if(isConnected) {
                _sw.WriteLine(message);
                _lines++;
                DccChatSentLineEvent(new DccChatEventArgs(this, message));
            } else {
                throw new NotConnectedException("DCC Chat is not Connected");
            }
        }
    }
}
