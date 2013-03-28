using System;
using System.Text;
using System.Xml;
using System.Xml.Serialization;
namespace ThoughtWorks.CruiseControl.Remote.Messages
{
    [XmlRoot("serverMessage")]
    [Serializable]
    public class ServerRequest
    {
        private string identifier = Guid.NewGuid().ToString();
        private string serverName;
        private string sessionToken;
        private DateTime timestamp = DateTime.Now;
        private string sourceName = Environment.MachineName;
        public ServerRequest()
        {
        }
        public ServerRequest(string sessionToken)
        {
            this.sessionToken = sessionToken;
        }
        [XmlAttribute("identifier")]
        public string Identifier
        {
            get { return identifier; }
            set { identifier = value; }
        }
        [XmlAttribute("server")]
        public string ServerName
        {
            get { return serverName; }
            set { serverName = value; }
        }
        [XmlAttribute("source")]
        public string SourceName
        {
            get { return sourceName; }
            set { sourceName = value; }
        }
        [XmlAttribute("session")]
        public string SessionToken
        {
            get { return sessionToken; }
            set { sessionToken = value; }
        }
        [XmlAttribute("timestamp")]
        public DateTime Timestamp
        {
            get { return timestamp; }
            set { timestamp = value; }
        }
        public override bool Equals(object obj)
        {
            if (obj is ServerRequest)
            {
                return string.Equals((obj as ServerRequest).identifier, identifier);
            }
            else
            {
                return false;
            }
        }
        public override int GetHashCode()
        {
            return identifier.GetHashCode();
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
    }
}
