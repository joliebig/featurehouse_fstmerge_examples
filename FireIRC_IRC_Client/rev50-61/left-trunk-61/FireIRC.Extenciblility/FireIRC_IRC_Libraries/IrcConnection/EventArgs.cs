using System;
using System.Collections.Specialized;
namespace FireIRC.Extenciblility.IRCClasses
{
    public class ReadLineEventArgs : EventArgs
    {
        private string _Line;
        public string Line {
            get {
                return _Line;
            }
        }
        internal ReadLineEventArgs(string line)
        {
            _Line = line;
        }
    }
    public class WriteLineEventArgs : EventArgs
    {
        private string _Line;
        public string Line {
            get {
                return _Line;
            }
        }
        internal WriteLineEventArgs(string line)
        {
            _Line = line;
        }
    }
    public class AutoConnectErrorEventArgs : EventArgs
    {
        private Exception _Exception;
        private string _Address;
        private int _Port;
        public Exception Exception {
            get {
                return _Exception;
            }
        }
        public string Address {
            get {
                return _Address;
            }
        }
        public int Port {
            get {
                return _Port;
            }
        }
        internal AutoConnectErrorEventArgs(string address, int port, Exception ex)
        {
            _Address = address;
            _Port = port;
            _Exception = ex;
        }
    }
}
