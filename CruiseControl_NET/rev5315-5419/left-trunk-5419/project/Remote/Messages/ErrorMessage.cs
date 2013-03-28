using System;
using System.Collections.Generic;
using System.Text;
using System.Xml.Serialization;
namespace ThoughtWorks.CruiseControl.Remote.Messages
{
    [Serializable]
    [XmlRoot("errorMessage")]
    public class ErrorMessage
    {
        private string type;
        private string message;
        public ErrorMessage()
        {
        }
        public ErrorMessage(string message)
        {
            this.message = message;
        }
        public ErrorMessage(string message, string type)
        {
            this.type = type;
            this.message = message;
        }
        [XmlAttribute("type")]
        public string Type
        {
            get { return type; }
            set { type = value; }
        }
        [XmlText]
        public string Message
        {
            get { return message; }
            set { message = value; }
        }
    }
}
