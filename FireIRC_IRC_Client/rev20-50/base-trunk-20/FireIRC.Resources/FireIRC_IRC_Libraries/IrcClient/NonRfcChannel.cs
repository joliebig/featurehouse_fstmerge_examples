using System.Collections;
using System.Collections.Specialized;
namespace OVT.FireIRC.Resources.IRC
{
    public class NonRfcChannel : Channel
    {
        private Hashtable _Halfops = Hashtable.Synchronized(new Hashtable(new CaseInsensitiveHashCodeProvider(), new CaseInsensitiveComparer()));
        internal NonRfcChannel(string name) : base(name)
        {
        }
        public Hashtable Halfops {
            get {
                return (Hashtable)_Halfops.Clone();
            }
        }
        internal Hashtable UnsafeHalfops {
            get {
                return _Halfops;
            }
        }
    }
}
