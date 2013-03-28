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
        public CommunicationsException(string s, string type)
            : base(s)
        {
            ErrorType = type;
        }
        public CommunicationsException(string s, Exception e, string type) : base(s, e)
        {
            ErrorType = type;
        }
        public CommunicationsException(SerializationInfo info, StreamingContext context) : base(info, context) { }
        public string ErrorType { get; private set; }
    }
}
