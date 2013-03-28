using System;
using System.IO;
using System.Net;
using System.Threading;
using System.Net.Sockets;
namespace FireIRC.Extenciblility.IRCClasses
{
    public class DccSend : DccConnection
    {
        private Stream _File;
        private long _Filesize;
        private string _Filename;
        private bool _DirectionUp;
        private long _SentBytes;
        private DccSpeed _Speed;
        private byte[] _Buffer = new byte[8192];
        public long SentBytes {
            get {
                return _SentBytes;
            }
        }
        internal DccSend(IrcFeatures irc, string user, IPAddress externalIpAdress, Stream file, string filename, long filesize, DccSpeed speed, bool passive, Priority priority) : base()
        {
            this.Irc = irc;
            _DirectionUp = true;
            _File = file;
            _Filesize = filesize;
            _Filename = filename;
            _Speed = speed;
            User = user;
            if(passive) {
                irc.SendMessage(SendType.CtcpRequest, user, "DCC SEND \"" + filename + "\" " + HostToDccInt(externalIpAdress).ToString() + " 0 " + filesize + " " + session, priority);
            } else {
                DccServer = new TcpListener(new IPEndPoint(IPAddress.Any, 0));
                DccServer.Start();
                LocalEndPoint = (IPEndPoint)DccServer.LocalEndpoint;
                irc.SendMessage(SendType.CtcpRequest, user, "DCC SEND \"" + filename + "\" " + HostToDccInt(externalIpAdress).ToString() + " " + LocalEndPoint.Port + " " + filesize, priority);
            }
        }
        internal DccSend(IrcFeatures irc, IPAddress externalIpAdress, CtcpEventArgs e) : base()
        {
            this.Irc = irc;
            _DirectionUp = false;
            User = e.Data.Nick;
            if (e.Data.MessageArray.Length > 4) {
                long ip, filesize = 0; int port = 0;
                bool okIP = long.TryParse(e.Data.MessageArray[3], out ip);
                bool okPo = int.TryParse(e.Data.MessageArray[4], out port);
                if (e.Data.MessageArray.Length > 5) {
                    bool okFs = long.TryParse(FilterMarker(e.Data.MessageArray[5]), out filesize);
                    _Filesize = filesize;
                    _Filename = e.Data.MessageArray[2].Trim(new char[] {'\"'});
                }
                if (okIP && okPo) {
                    RemoteEndPoint = new IPEndPoint(IPAddress.Parse(DccIntToHost(ip)), port);
                    DccSendRequestEvent(new DccSendRequestEventArgs(this, e.Data.MessageArray[2], filesize));
                    return;
                } else {
                    irc.SendMessage(SendType.CtcpReply, e.Data.Nick, "ERRMSG DCC Send Parameter Error");
                }
            } else {
                irc.SendMessage(SendType.CtcpReply, e.Data.Nick, "ERRMSG DCC Send not enough parameters");
            }
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
                    if (reject)
                        return;
                }
            }
            DccSendStartEvent(new DccEventArgs(this));
            int bytes;
            if(_DirectionUp) {
                do{
                    while (Connection.Available > 0) {
                        switch(_Speed)
                        {
                            case DccSpeed.Rfc:
                                Connection.GetStream().Read(_Buffer, 0, _Buffer.Length);
                                break;
                            case DccSpeed.RfcSendAhead:
                                Connection.GetStream().Read(_Buffer, 0, _Buffer.Length);
                                break;
                            case DccSpeed.Turbo:
                                break;
                        }
                    }
                    bytes = _File.Read(_Buffer, 0, _Buffer.Length);
                    try {
                        Connection.GetStream().Write(_Buffer, 0, (int)bytes);
                    } catch (IOException) {
                        bytes = 0;
                    }
                    _SentBytes += bytes;
                    if (bytes > 0) {
                        DccSendSentBlockEvent(new DccSendEventArgs(this, _Buffer, bytes));
                        Console.Write(".");
                    }
                } while(bytes > 0);
            } else {
                while((bytes = Connection.GetStream().Read(_Buffer,0,_Buffer.Length))>0) {
                    _File.Write(_Buffer, 0, bytes);
                    _SentBytes += bytes;
                    if (_Speed != DccSpeed.Turbo)
                        Connection.GetStream().Write(getAck(_SentBytes),0,4);
                    DccSendReceiveBlockEvent(new DccSendEventArgs(this, _Buffer, bytes));
                }
            }
            isValid = false;
            isConnected = false;
            Console.WriteLine("--> Filetrangsfer Endet / Bytes sent: " + _SentBytes + " of " + _Filesize);
            DccSendStopEvent(new DccEventArgs(this));
        }
        public bool AcceptRequest(Stream file, long offset)
        {
            if (isConnected)
                return false;
            try {
                if (file!=null)
                    _File = file;
                if(RemoteEndPoint.Port==0) {
                    DccServer = new TcpListener(new IPEndPoint(IPAddress.Any, 0));
                    DccServer.Start();
                    LocalEndPoint = (IPEndPoint)DccServer.LocalEndpoint;
                    Irc.SendMessage(SendType.CtcpRequest, User, "DCC SEND \"" + _Filename + "\" " + HostToDccInt(ExternalIPAdress).ToString() + " " + LocalEndPoint.Port + " " + _Filesize);
                } else {
                    if(offset==0) {
                        Connection = new TcpClient();
                        Connection.Connect(RemoteEndPoint);
                        isConnected = true;
                    } else {
                        if(_File.CanSeek) {
                            _File.Seek(offset, SeekOrigin.Begin);
                            _SentBytes = offset;
                            Irc.SendMessage(SendType.CtcpRequest, User, "DCC RESUME \"" + _Filename + "\" " + RemoteEndPoint.Port + " " + offset);
                        } else {
                            _SentBytes = offset;
                            Irc.SendMessage(SendType.CtcpRequest, User, "DCC RESUME \"" + _Filename + "\" " + RemoteEndPoint.Port + " " + offset);
                        }
                    }
                }
                return true;
            } catch(Exception) {
                isValid = false;
                isConnected = false;
                return false;
            }
        }
        internal bool TryResume(CtcpEventArgs e) {
            if (User == e.Data.Nick) {
                if ((e.Data.MessageArray.Length > 4) && (_Filename == e.Data.MessageArray[2].Trim(new char[] {'\"'}))) {
                    long offset = 0;
                    long.TryParse(FilterMarker(e.Data.MessageArray[4]), out offset);
                    if (_File.CanSeek) {
                        if (e.Data.MessageArray.Length > 5) {
                            Irc.SendMessage(SendType.CtcpRequest, e.Data.Nick, "DCC ACCEPT " + e.Data.MessageArray[2] + " " + e.Data.MessageArray[3] + " " + e.Data.MessageArray[4] + " " + FilterMarker(e.Data.MessageArray[5]));
                        } else {
                            Irc.SendMessage(SendType.CtcpRequest, e.Data.Nick, "DCC ACCEPT " + e.Data.MessageArray[2] + " " + e.Data.MessageArray[3] + " " + FilterMarker(e.Data.MessageArray[4]));
                        }
                        _File.Seek(offset, SeekOrigin.Begin);
                        _SentBytes = offset;
                        return true;
                    } else {
                        Irc.SendMessage(SendType.CtcpRequest, e.Data.Nick, "ERRMSG DCC File not seekable");
                    }
                }
            }
            return false;
        }
        internal bool TryAccept(CtcpEventArgs e) {
            if (User == e.Data.Nick) {
                if ((e.Data.MessageArray.Length > 4) && (_Filename == e.Data.MessageArray[2].Trim(new char[] {'\"'}))) {
                    return this.AcceptRequest(null, 0);
                }
            }
            return false;
        }
        internal bool SetRemote(CtcpEventArgs e) {
            long ip;
            int port = 0;
            bool okIP = long.TryParse(e.Data.MessageArray[3], out ip);
            bool okPo = int.TryParse(e.Data.MessageArray[4], out port);
            if (okIP && okPo) {
                RemoteEndPoint = new IPEndPoint(IPAddress.Parse(DccIntToHost(ip)), port);
                return true;
            }
            return false;
        }
    }
}
