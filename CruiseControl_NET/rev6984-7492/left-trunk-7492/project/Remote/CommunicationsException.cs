namespace ThoughtWorks.CruiseControl.Remote
{
    using System;
    using System.Runtime.Serialization;
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
            this.ErrorType = type;
        }
        public CommunicationsException(string s, Exception e, string type) : base(s, e)
        {
            this.ErrorType = type;
        }
        public CommunicationsException(SerializationInfo info, StreamingContext context)
            : base(info, context)
        {
            this.ErrorType = info.GetString("_errorType");
        }
        public string ErrorType { get; private set; }
        public override void GetObjectData(SerializationInfo info, StreamingContext context)
        {
            info.AddValue("_errorType", this.ErrorType);
            base.GetObjectData(info, context);
        }
    }
}
