using System;
using System.Collections.Generic;
using System.Text;
using System.Xml.Serialization;
namespace ThoughtWorks.CruiseControl.Remote.Messages
{
    [XmlRoot("messageMessage")]
    [Serializable]
    public class MessageRequest
        : ProjectRequest
    {
        private string message;
        private Message.MessageKind kind ;
        [XmlElement("message")]
        public string Message
        {
            get { return message; }
            set { message = value; }
        }
        [XmlElement("kind")]
        public Message.MessageKind Kind
        {
            get { return kind; }
            set { kind = value; }
        }
    }
}
