using System;
using System.Collections.Generic;
namespace FireIRC.Extenciblility.IRCClasses
{
    public class DccEventArgs : EventArgs
    {
        private DccConnection _dcc;
        public DccConnection dcc {
            get {
                return _dcc;
            }
        }
        internal DccEventArgs(DccConnection dcc)
        {
            this._dcc = dcc;
        }
    }
    public class DccChatEventArgs : DccEventArgs
    {
        private string _Message;
        public string Message {
            get {
                return _Message;
            }
        }
        private string[] _MessageArray;
        public string[] MessageArray {
            get {
                return _MessageArray;
            }
        }
        internal DccChatEventArgs(DccConnection dcc, string messageLine) : base(dcc)
        {
            char[] whiteSpace = {' '};
            this._Message = messageLine;
            this._MessageArray = messageLine.Split(new char[] {' '});
        }
    }
    public class DccSendEventArgs : DccEventArgs
    {
        private byte[] _Package;
        public byte[] Package {
            get {
                return _Package;
            }
        }
        private int _PackageSize;
        public int PackageSize {
            get {
                return _PackageSize;
            }
        }
        internal DccSendEventArgs(DccConnection dcc, byte[] package, int packageSize) : base(dcc)
        {
            this._Package = package;
            this._PackageSize = packageSize;
        }
    }
    public class DccSendRequestEventArgs : DccEventArgs
    {
        private string _Filename;
        public string Filename {
            get {
                return _Filename;
            }
        }
        private long _Filesize;
        public long Filesize {
            get {
                return _Filesize;
            }
        }
        internal DccSendRequestEventArgs(DccConnection dcc, string filename, long filesize) : base(dcc)
        {
            this._Filename = filename;
            this._Filesize = filesize;
        }
    }
}
