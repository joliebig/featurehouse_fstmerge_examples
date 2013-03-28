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
        [XmlElement("message")]
        public string Message
        {
            get { return message; }
            set { message = value; }
        }
    }
}
