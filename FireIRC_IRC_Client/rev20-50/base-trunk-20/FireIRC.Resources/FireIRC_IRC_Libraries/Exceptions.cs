using System;
using System.Runtime.Serialization;
namespace OVT.FireIRC.Resources.IRC
{
    [Serializable()]
    public class SmartIrc4netException : ApplicationException
    {
        public SmartIrc4netException() : base()
        {
        }
        public SmartIrc4netException(string message) : base(message)
        {
        }
        public SmartIrc4netException(string message, Exception e) : base(message, e)
        {
        }
        protected SmartIrc4netException(SerializationInfo info, StreamingContext context) : base(info, context)
        {
        }
    }
    [Serializable()]
    public class ConnectionException : SmartIrc4netException
    {
        public ConnectionException() : base()
        {
        }
        public ConnectionException(string message) : base(message)
        {
        }
        public ConnectionException(string message, Exception e) : base(message, e)
        {
        }
        protected ConnectionException(SerializationInfo info, StreamingContext context) : base(info, context)
        {
        }
    }
    [Serializable()]
    public class CouldNotConnectException : ConnectionException
    {
        public CouldNotConnectException() : base()
        {
        }
        public CouldNotConnectException(string message) : base(message)
        {
        }
        public CouldNotConnectException(string message, Exception e) : base(message, e)
        {
        }
        protected CouldNotConnectException(SerializationInfo info, StreamingContext context) : base(info, context)
        {
        }
    }
    [Serializable()]
    public class NotConnectedException : ConnectionException
    {
        public NotConnectedException() : base()
        {
        }
        public NotConnectedException(string message) : base(message)
        {
        }
        public NotConnectedException(string message, Exception e) : base(message, e)
        {
        }
        protected NotConnectedException(SerializationInfo info, StreamingContext context) : base(info, context)
        {
        }
    }
    [Serializable()]
    public class AlreadyConnectedException : ConnectionException
    {
        public AlreadyConnectedException() : base()
        {
        }
        public AlreadyConnectedException(string message) : base(message)
        {
        }
        public AlreadyConnectedException(string message, Exception e) : base(message, e)
        {
        }
        protected AlreadyConnectedException(SerializationInfo info, StreamingContext context) : base(info, context)
        {
        }
    }
}
