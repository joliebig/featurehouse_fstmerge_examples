using System;
using System.IO;
using System.Text;
using System.Collections;
using System.Threading;
using System.Reflection;
using System.Net.Sockets;
namespace FireIRC.Extenciblility.IRCClasses
{
    public class IrcConnection
    {
        private string _VersionNumber;
        private string _VersionString;
        private string[] _AddressList = {"localhost"};
        private int _CurrentAddress;
        private int _Port = 6667;
        private StreamReader _Reader;
        private StreamWriter _Writer;
        private ReadThread _ReadThread;
        private WriteThread _WriteThread;
        private IdleWorkerThread _IdleWorkerThread;
        private IrcTcpClient _TcpClient;
        private Hashtable _SendBuffer = Hashtable.Synchronized(new Hashtable());
        private int _SendDelay = 200;
        private bool _IsRegistered;
        private bool _IsConnected;
        private bool _IsConnectionError;
        private int _ConnectTries;
        private bool _AutoRetry;
        private int _AutoRetryDelay = 30;
        private bool _AutoReconnect;
        private Encoding _Encoding = Encoding.Default;
        private int _SocketReceiveTimeout = 600;
        private int _SocketSendTimeout = 600;
        private int _IdleWorkerInterval = 60;
        private int _PingInterval = 60;
        private int _PingTimeout = 300;
        private DateTime _LastPingSent;
        private DateTime _LastPongReceived;
        private TimeSpan _Lag;
        public bool CriticalMode = false;
        public event ReadLineEventHandler OnReadLine;
        public event WriteLineEventHandler OnWriteLine;
        public event EventHandler OnConnecting;
        public event EventHandler OnConnected;
        public event EventHandler OnDisconnecting;
        public event EventHandler OnDisconnected;
        public event EventHandler OnConnectionError;
        public event AutoConnectErrorEventHandler OnAutoConnectError;
        protected bool IsConnectionError {
            get {
                lock (this) {
                    return _IsConnectionError;
                }
            }
            set {
                lock (this) {
                    _IsConnectionError = value;
                }
            }
        }
        public string Address {
            get {
                return _AddressList[_CurrentAddress];
            }
        }
        public string[] AddressList {
            get {
                return _AddressList;
            }
        }
        public int Port {
            get {
                return _Port;
            }
        }
        public bool AutoReconnect {
            get {
                return _AutoReconnect;
            }
            set {
                _AutoReconnect = value;
            }
        }
        public bool AutoRetry {
            get {
                return _AutoRetry;
            }
            set {
                _AutoRetry = value;
            }
        }
        public int AutoRetryDelay {
            get {
                return _AutoRetryDelay;
            }
            set {
                _AutoRetryDelay = value;
            }
        }
        public int SendDelay {
            get {
                return _SendDelay;
            }
            set {
                _SendDelay = value;
            }
        }
        public bool IsRegistered {
            get {
                return _IsRegistered;
            }
        }
        public bool IsConnected {
            get {
                return _IsConnected;
            }
        }
        public string VersionNumber {
            get {
                return _VersionNumber;
            }
        }
        public string VersionString {
            get {
                return _VersionString;
            }
        }
        public Encoding Encoding {
            get {
                return _Encoding;
            }
            set {
                _Encoding = value;
            }
        }
        public int SocketReceiveTimeout {
            get {
                return _SocketReceiveTimeout;
            }
            set {
                _SocketReceiveTimeout = value;
            }
        }
        public int SocketSendTimeout {
            get {
                return _SocketSendTimeout;
            }
            set {
                _SocketSendTimeout = value;
            }
        }
        public int IdleWorkerInterval {
            get {
                return _IdleWorkerInterval;
            }
            set {
                _IdleWorkerInterval = value;
            }
        }
        public int PingInterval {
            get {
                return _PingInterval;
            }
            set {
                _PingInterval = value;
            }
        }
        public int PingTimeout {
            get {
                return _PingTimeout;
            }
            set {
                _PingTimeout = value;
            }
        }
        public TimeSpan Lag {
            get {
                return _Lag;
            }
        }
        public IrcConnection()
        {
            _SendBuffer[Priority.High] = Queue.Synchronized(new Queue());
            _SendBuffer[Priority.AboveMedium] = Queue.Synchronized(new Queue());
            _SendBuffer[Priority.Medium] = Queue.Synchronized(new Queue());
            _SendBuffer[Priority.BelowMedium] = Queue.Synchronized(new Queue());
            _SendBuffer[Priority.Low] = Queue.Synchronized(new Queue());
            OnReadLine += new ReadLineEventHandler(_SimpleParser);
            OnConnectionError += new EventHandler(_OnConnectionError);
            _ReadThread = new ReadThread(this);
            _WriteThread = new WriteThread(this);
            _IdleWorkerThread = new IdleWorkerThread(this);
            Assembly assm = Assembly.GetAssembly(this.GetType());
            AssemblyName assm_name = assm.GetName(false);
            AssemblyProductAttribute pr = (AssemblyProductAttribute)assm.GetCustomAttributes(typeof(AssemblyProductAttribute), false)[0];
            _VersionNumber = assm_name.Version.ToString();
            _VersionString = pr.Product+" "+_VersionNumber;
        }
        public void Connect(string[] addresslist, int port)
        {
            if (_IsConnected) {
                throw new AlreadyConnectedException("Already connected to: "+Address+":"+Port);
            }
            _ConnectTries++;
            _AddressList = (string[])addresslist.Clone();
            _Port = port;
            if (OnConnecting != null) {
                OnConnecting(this, EventArgs.Empty);
            }
            try {
                System.Net.IPAddress ip = System.Net.Dns.Resolve(Address).AddressList[0];
                _TcpClient = new IrcTcpClient();
                _TcpClient.NoDelay = true;
                _TcpClient.Socket.SetSocketOption(SocketOptionLevel.Socket, SocketOptionName.KeepAlive, 1);
                _TcpClient.ReceiveTimeout = _SocketReceiveTimeout*1000;
                _TcpClient.SendTimeout = _SocketSendTimeout*1000;
                _TcpClient.Connect(ip, port);
                _Reader = new StreamReader(_TcpClient.GetStream(), _Encoding);
                _Writer = new StreamWriter(_TcpClient.GetStream(), _Encoding);
                if (_Encoding.GetPreamble().Length > 0) {
                    _Writer.WriteLine();
                }
                _ConnectTries = 0;
                IsConnectionError = false;
                _IsConnected = true;
                _ReadThread.Start();
                _WriteThread.Start();
                _IdleWorkerThread.Start();
                if (OnConnected != null) {
                    OnConnected(this, EventArgs.Empty);
                }
            } catch (Exception e) {
                if (_Reader != null) {
                    try {
                        _Reader.Close();
                    } catch (ObjectDisposedException) {
                    }
                }
                if (_Writer != null) {
                    try {
                        _Writer.Close();
                    } catch (ObjectDisposedException) {
                    }
                }
                if (_TcpClient != null) {
                    _TcpClient.Close();
                }
                _IsConnected = false;
                IsConnectionError = true;
                if (_AutoRetry &&
                    _ConnectTries <= 3) {
                    if (OnAutoConnectError != null) {
                        OnAutoConnectError(this, new AutoConnectErrorEventArgs(Address, Port, e));
                    }
                    Thread.Sleep(_AutoRetryDelay * 1000);
                    _NextAddress();
                    Connect(_AddressList, _Port);
                } else {
                    throw new CouldNotConnectException("Could not connect to: "+Address+":"+Port+" "+e.Message, e);
                }
            }
        }
        public void Connect(string address, int port)
        {
            Connect(new string[] {address}, port);
        }
        public void Reconnect()
        {
            Disconnect();
            Connect(_AddressList, _Port);
        }
        public void Disconnect()
        {
            if (!IsConnected) {
                throw new NotConnectedException("The connection could not be disconnected because there is no active connection");
            }
            if (OnDisconnecting != null) {
                OnDisconnecting(this, EventArgs.Empty);
            }
            _ReadThread.Stop();
            _WriteThread.Stop();
            _TcpClient.Close();
            _IsConnected = false;
            _IsRegistered = false;
            if (OnDisconnected != null) {
                OnDisconnected(this, EventArgs.Empty);
            }
        }
        public void Listen(bool blocking)
        {
            if (blocking) {
                while (IsConnected) {
                    ReadLine(true);
                }
            } else {
                while (ReadLine(false).Length > 0) {
                }
            }
        }
        public void Listen()
        {
            Listen(true);
        }
        public void ListenOnce(bool blocking)
        {
            ReadLine(blocking);
        }
        public void ListenOnce()
        {
            ListenOnce(true);
        }
        public string ReadLine(bool blocking)
        {
            string data = "";
            if (blocking) {
                while (IsConnected &&
                       !IsConnectionError &&
                       _ReadThread.Queue.Count == 0) {
                    Thread.Sleep(10);
                }
            }
            if (IsConnected &&
                _ReadThread.Queue.Count > 0) {
                data = (string)(_ReadThread.Queue.Dequeue());
            }
            if (data != null && data.Length > 0) {
                if (OnReadLine != null) {
                    OnReadLine(this, new ReadLineEventArgs(data));
                }
            }
            if (IsConnectionError &&
                OnConnectionError != null) {
                OnConnectionError(this, EventArgs.Empty);
            }
            return data;
        }
        public void WriteLine(string data, Priority priority)
        {
            if (CriticalMode == true)
            {
                if (!IsConnected)
                {
                    throw new NotConnectedException();
                }
                _WriteLine(data);
            }
            else
            {
                if (priority == Priority.Critical)
                {
                    if (!IsConnected)
                    {
                        throw new NotConnectedException();
                    }
                    _WriteLine(data);
                }
                else
                {
                    ((Queue)_SendBuffer[priority]).Enqueue(data);
                }
            }
        }
        public void WriteLine(string data)
        {
            WriteLine(data, Priority.Medium);
        }
        private bool _WriteLine(string data)
        {
            if (IsConnected) {
                try {
                    _Writer.Write(data+"\r\n");
                    _Writer.Flush();
                } catch (IOException) {
                    IsConnectionError = true;
                    return false;
                } catch (ObjectDisposedException) {
                    IsConnectionError = true;
                    return false;
                }
                if (OnWriteLine != null) {
                    OnWriteLine(this, new WriteLineEventArgs(data));
                }
                return true;
            }
            return false;
        }
        private void _NextAddress()
        {
            _CurrentAddress++;
            if (_CurrentAddress >= _AddressList.Length) {
                _CurrentAddress = 0;
            }
        }
        private void _SimpleParser(object sender, ReadLineEventArgs args)
        {
            string rawline = args.Line;
            string[] rawlineex = rawline.Split(new char[] {' '});
            string messagecode = "";
            if (rawline[0] == ':') {
                messagecode = rawlineex[1];
                ReplyCode replycode = ReplyCode.Null;
                try {
                    replycode = (ReplyCode)int.Parse(messagecode);
                } catch (FormatException) {
                }
                if (replycode != ReplyCode.Null) {
                    switch (replycode) {
                        case ReplyCode.Welcome:
                            _IsRegistered = true;
                            break;
                    }
                } else {
                    switch (rawlineex[1]) {
                        case "PONG":
                            DateTime now = DateTime.Now;
                            _LastPongReceived = now;
                            _Lag = now - _LastPingSent;
                            break;
                    }
                }
            } else {
                messagecode = rawlineex[0];
                switch (messagecode) {
                    case "ERROR":
                        IsConnectionError = true;
                    break;
                }
            }
        }
        private void _OnConnectionError(object sender, EventArgs e)
        {
            try {
                if (AutoReconnect) {
                    Reconnect();
                } else {
                    Disconnect();
                }
            } catch (ConnectionException) {
            }
        }
        private class ReadThread
        {
            private IrcConnection _Connection;
            private Thread _Thread;
            private Queue _Queue = Queue.Synchronized(new Queue());
            public Queue Queue {
                get {
                    return _Queue;
                }
            }
            public ReadThread(IrcConnection connection)
            {
                _Connection = connection;
            }
            public void Start()
            {
                _Thread = new Thread(new ThreadStart(_Worker));
                _Thread.Name = "ReadThread ("+_Connection.Address+":"+_Connection.Port+")";
                _Thread.IsBackground = true;
                _Thread.Start();
            }
            public void Stop()
            {
                _Thread.Abort();
                try {
                    _Connection._Reader.Close();
                } catch (ObjectDisposedException) {
                }
            }
            private void _Worker()
            {
                try {
                    string data = "";
                    try {
                        while (_Connection.IsConnected &&
                               ((data = _Connection._Reader.ReadLine()) != null)) {
                            _Queue.Enqueue(data);
                        }
                    } catch (IOException e) {
                    } finally {
                        _Connection.IsConnectionError = true;
                    }
                } catch (ThreadAbortException) {
                    Thread.ResetAbort();
                }
            }
        }
        private class WriteThread
        {
            private IrcConnection _Connection;
            private Thread _Thread;
            private int _HighCount;
            private int _AboveMediumCount;
            private int _MediumCount;
            private int _BelowMediumCount;
            private int _LowCount;
            private int _AboveMediumSentCount;
            private int _MediumSentCount;
            private int _BelowMediumSentCount;
            private int _AboveMediumThresholdCount = 4;
            private int _MediumThresholdCount = 2;
            private int _BelowMediumThresholdCount = 1;
            private int _BurstCount;
            public WriteThread(IrcConnection connection)
            {
                _Connection = connection;
            }
            public void Start()
            {
                _Thread = new Thread(new ThreadStart(_Worker));
                _Thread.Name = "WriteThread ("+_Connection.Address+":"+_Connection.Port+")";
                _Thread.IsBackground = true;
                _Thread.Start();
            }
            public void Stop()
            {
                _Thread.Abort();
                try {
                    _Connection._Writer.Close();
                } catch (ObjectDisposedException) {
                }
            }
            private void _Worker()
            {
                try {
                    try {
                        while (_Connection.IsConnected) {
                            _CheckBuffer();
                            Thread.Sleep(_Connection._SendDelay);
                        }
                    } catch (IOException e) {
                    } finally {
                        _Connection.IsConnectionError = true;
                    }
                } catch (ThreadAbortException) {
                    Thread.ResetAbort();
                }
            }
            private void _CheckBuffer()
            {
                if (!_Connection._IsRegistered) {
                    return;
                }
                _HighCount = ((Queue)_Connection._SendBuffer[Priority.High]).Count;
                _AboveMediumCount = ((Queue)_Connection._SendBuffer[Priority.AboveMedium]).Count;
                _MediumCount = ((Queue)_Connection._SendBuffer[Priority.Medium]).Count;
                _BelowMediumCount = ((Queue)_Connection._SendBuffer[Priority.BelowMedium]).Count;
                _LowCount = ((Queue)_Connection._SendBuffer[Priority.Low]).Count;
                if (_CheckHighBuffer() &&
                    _CheckAboveMediumBuffer() &&
                    _CheckMediumBuffer() &&
                    _CheckBelowMediumBuffer() &&
                    _CheckLowBuffer()) {
                    _AboveMediumSentCount = 0;
                    _MediumSentCount = 0;
                    _BelowMediumSentCount = 0;
                    _BurstCount = 0;
                }
                if (_BurstCount < 3) {
                    _BurstCount++;
                }
            }
            private bool _CheckHighBuffer()
            {
                if (_HighCount > 0) {
                    string data = (string)((Queue)_Connection._SendBuffer[Priority.High]).Dequeue();
                    if (_Connection._WriteLine(data) == false) {
                        ((Queue)_Connection._SendBuffer[Priority.High]).Enqueue(data);
                    }
                    if (_HighCount > 1) {
                        return false;
                    }
                }
                return true;
            }
            private bool _CheckAboveMediumBuffer()
            {
                if ((_AboveMediumCount > 0) &&
                    (_AboveMediumSentCount < _AboveMediumThresholdCount)) {
                    string data = (string)((Queue)_Connection._SendBuffer[Priority.AboveMedium]).Dequeue();
                    if (_Connection._WriteLine(data) == false) {
                        ((Queue)_Connection._SendBuffer[Priority.AboveMedium]).Enqueue(data);
                    }
                    _AboveMediumSentCount++;
                    if (_AboveMediumSentCount < _AboveMediumThresholdCount) {
                        return false;
                    }
                }
                return true;
            }
            private bool _CheckMediumBuffer()
            {
                if ((_MediumCount > 0) &&
                    (_MediumSentCount < _MediumThresholdCount)) {
                    string data = (string)((Queue)_Connection._SendBuffer[Priority.Medium]).Dequeue();
                    if (_Connection._WriteLine(data) == false) {
                        ((Queue)_Connection._SendBuffer[Priority.Medium]).Enqueue(data);
                    }
                    _MediumSentCount++;
                    if (_MediumSentCount < _MediumThresholdCount) {
                        return false;
                    }
                }
                return true;
            }
            private bool _CheckBelowMediumBuffer()
            {
                if ((_BelowMediumCount > 0) &&
                    (_BelowMediumSentCount < _BelowMediumThresholdCount)) {
                    string data = (string)((Queue)_Connection._SendBuffer[Priority.BelowMedium]).Dequeue();
                    if (_Connection._WriteLine(data) == false) {
                        ((Queue)_Connection._SendBuffer[Priority.BelowMedium]).Enqueue(data);
                    }
                    _BelowMediumSentCount++;
                    if (_BelowMediumSentCount < _BelowMediumThresholdCount) {
                        return false;
                    }
                }
                return true;
            }
            private bool _CheckLowBuffer()
            {
                if (_LowCount > 0) {
                    if ((_HighCount > 0) ||
                        (_AboveMediumCount > 0) ||
                        (_MediumCount > 0) ||
                        (_BelowMediumCount > 0)) {
                            return true;
                    }
                    string data = (string)((Queue)_Connection._SendBuffer[Priority.Low]).Dequeue();
                    if (_Connection._WriteLine(data) == false) {
                        ((Queue)_Connection._SendBuffer[Priority.Low]).Enqueue(data);
                    }
                    if (_LowCount > 1) {
                        return false;
                    }
                }
                return true;
            }
        }
        private class IdleWorkerThread
        {
            private IrcConnection _Connection;
            private Thread _Thread;
            public IdleWorkerThread(IrcConnection connection)
            {
                _Connection = connection;
            }
            public void Start()
            {
                DateTime now = DateTime.Now;
                _Connection._LastPingSent = now;
                _Connection._LastPongReceived = now;
                _Thread = new Thread(new ThreadStart(_Worker));
                _Thread.Name = "IdleWorkerThread ("+_Connection.Address+":"+_Connection.Port+")";
                _Thread.IsBackground = true;
                _Thread.Start();
            }
            public void Stop()
            {
                _Thread.Abort();
            }
            private void _Worker()
            {
                try {
                   while (_Connection.IsConnected ) {
                       if (_Connection.IsRegistered) {
                           DateTime now = DateTime.Now;
                           int last_ping_sent = (int)(now - _Connection._LastPingSent).TotalSeconds;
                           int last_pong_rcvd = (int)(now - _Connection._LastPongReceived).TotalSeconds;
                           if (last_ping_sent < _Connection._PingTimeout) {
                               if (last_pong_rcvd > _Connection._PingInterval) {
                                   _Connection.WriteLine(Rfc2812.Ping(_Connection.Address), Priority.Critical);
                                   _Connection._LastPingSent = now;
                                   _Connection._LastPongReceived = now;
                               }
                           } else {
                               _Connection.IsConnectionError = true;
                               break;
                           }
                       }
                       Thread.Sleep(_Connection._IdleWorkerInterval);
                   }
                } catch (ThreadAbortException) {
                    Thread.ResetAbort();
                }
            }
        }
    }
}
