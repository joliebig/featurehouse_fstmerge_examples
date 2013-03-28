using System;
using System.Collections;
using System.Collections.Specialized;
namespace OVT.FireIRC.Resources.IRC
{
    public class Channel
    {
        private string _Name;
        private string _Key = String.Empty;
        private Hashtable _Users = Hashtable.Synchronized(new Hashtable(new CaseInsensitiveHashCodeProvider(), new CaseInsensitiveComparer()));
        private Hashtable _Ops = Hashtable.Synchronized(new Hashtable(new CaseInsensitiveHashCodeProvider(), new CaseInsensitiveComparer()));
        private Hashtable _Voices = Hashtable.Synchronized(new Hashtable(new CaseInsensitiveHashCodeProvider(), new CaseInsensitiveComparer()));
        private StringCollection _Bans = new StringCollection();
        private string _Topic = String.Empty;
        private int _UserLimit;
        private string _Mode = String.Empty;
        private DateTime _ActiveSyncStart;
        private DateTime _ActiveSyncStop;
        private TimeSpan _ActiveSyncTime;
        internal Channel(string name)
        {
            _Name = name;
            _ActiveSyncStart = DateTime.Now;
        }
        public string Name {
            get {
                return _Name;
            }
        }
        public string Key {
            get {
                return _Key;
            }
            set {
                _Key = value;
            }
        }
        public Hashtable Users {
            get {
                return (Hashtable)_Users.Clone();
            }
        }
        internal Hashtable UnsafeUsers {
            get {
                return _Users;
            }
        }
        public Hashtable Ops {
            get {
                return (Hashtable)_Ops.Clone();
            }
        }
        internal Hashtable UnsafeOps {
            get {
                return _Ops;
            }
        }
        public Hashtable Voices {
            get {
                return (Hashtable)_Voices.Clone();
            }
        }
        internal Hashtable UnsafeVoices {
            get {
                return _Voices;
            }
        }
        public StringCollection Bans {
            get {
                return _Bans;
            }
        }
        public string Topic {
            get {
                return _Topic;
            }
            set {
                _Topic = value;
            }
        }
        public int UserLimit {
            get {
                return _UserLimit;
            }
            set {
                _UserLimit = value;
            }
        }
        public string Mode {
            get {
                return _Mode;
            }
            set {
                _Mode = value;
            }
        }
        public DateTime ActiveSyncStart {
            get {
                return _ActiveSyncStart;
            }
        }
        public DateTime ActiveSyncStop {
            get {
                return _ActiveSyncStop;
            }
            set {
                _ActiveSyncStop = value;
                _ActiveSyncTime = _ActiveSyncStop.Subtract(_ActiveSyncStart);
            }
        }
        public TimeSpan ActiveSyncTime {
            get {
                return _ActiveSyncTime;
            }
        }
    }
}
