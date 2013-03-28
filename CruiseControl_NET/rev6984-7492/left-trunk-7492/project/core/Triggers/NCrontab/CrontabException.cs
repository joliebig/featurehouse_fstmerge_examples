using System;
using System.Runtime.Serialization;
namespace ThoughtWorks.CruiseControl.Core.Triggers.NCrontab
{
    [ Serializable ]
    public class CrontabException : Exception
    {
        public CrontabException() :
            base("Crontab error.") {}
        public CrontabException(string message) :
            base(message) {}
        public CrontabException(string message, Exception innerException) :
            base(message, innerException) {}
        protected CrontabException(SerializationInfo info, StreamingContext context) :
            base(info, context) {}
    }
}
