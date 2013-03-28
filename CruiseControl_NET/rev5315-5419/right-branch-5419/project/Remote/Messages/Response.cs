using System;
using System.Collections.Generic;
using System.Text;
using System.Xml;
using System.Xml.Serialization;
namespace ThoughtWorks.CruiseControl.Remote.Messages
{
    [XmlRoot("response")]
    [Serializable]
    public class Response
    {
        private List<ErrorMessage> errorMessages;
        private string requestIdentifier;
        private ResponseResult result = ResponseResult.Failure;
        private DateTime timestamp = DateTime.Now;
        public Response()
        {
            errorMessages = new List<ErrorMessage>();
            timestamp = DateTime.Now;
        }
        public Response(ServerRequest request)
            : this()
        {
            requestIdentifier = request.Identifier;
        }
        public Response(Response response)
        {
            errorMessages = response.errorMessages;
            requestIdentifier = response.requestIdentifier;
            result = response.result;
            timestamp = response.timestamp;
        }
        [XmlElement("error")]
        public List<ErrorMessage> ErrorMessages
        {
            get { return errorMessages; }
        }
        [XmlAttribute("identifier")]
        public string RequestIdentifier
        {
            get { return requestIdentifier; }
            set { requestIdentifier = value; }
        }
        [XmlAttribute("result")]
        public ResponseResult Result
        {
            get { return result; }
            set { result = value; }
        }
        [XmlAttribute("timestamp")]
        public DateTime Timestamp
        {
            get { return timestamp; }
            set { timestamp = value; }
        }
        public override bool Equals(object obj)
        {
            if (obj is Response)
            {
                Response other = obj as Response;
                return string.Equals(other.requestIdentifier, requestIdentifier) &&
                    DateTime.Equals(other.timestamp, timestamp);
            }
            else
            {
                return false;
            }
        }
        public override int GetHashCode()
        {
            return (requestIdentifier ?? string.Empty).GetHashCode() &
                timestamp.GetHashCode();
        }
        public override string ToString()
        {
            XmlSerializer serialiser = new XmlSerializer(this.GetType());
            StringBuilder builder = new StringBuilder();
            XmlWriterSettings settings = new XmlWriterSettings();
            settings.Encoding = UTF8Encoding.UTF8;
            settings.Indent = false;
            settings.OmitXmlDeclaration = true;
            XmlWriter writer = XmlWriter.Create(builder, settings);
            serialiser.Serialize(writer, this);
            return builder.ToString();
        }
        public virtual string ConcatenateErrors()
        {
            List<string> errorMessages = new List<string>();
            foreach (ErrorMessage error in ErrorMessages)
            {
                errorMessages.Add(error.Message);
            }
            return string.Join(Environment.NewLine, errorMessages.ToArray());
        }
    }
}
