using System;
using System.Runtime.Serialization;
namespace ThoughtWorks.CruiseControl.Remote
{
    [Serializable]
    public class CommunicationsException
        : ApplicationException
    {
        public CommunicationsException() : base("A communications error has occurred.") { }
        public CommunicationsException(string s) : base(s) { }
        public CommunicationsException(string s, Exception e) : base(s, e) { }
        public CommunicationsException(SerializationInfo info, StreamingContext context) : base(info, context) { }
    }
}
