using System;
using System.Xml.Serialization;
namespace ThoughtWorks.CruiseControl.Remote.Security
{
    [Serializable]
    public class AuditRecord
    {
        private DateTime timeOfEvent;
        private string projectName;
        private string userName;
        private SecurityEvent eventType;
        private SecurityRight eventRight;
        private string message;
        [XmlAttribute("time")]
        public DateTime TimeOfEvent
        {
            get { return timeOfEvent; }
            set { timeOfEvent = value; }
        }
        [XmlAttribute("project")]
        public string ProjectName
        {
            get { return projectName; }
            set { projectName = value; }
        }
        [XmlAttribute("user")]
        public string UserName
        {
            get { return userName; }
            set { userName = value; }
        }
        [XmlAttribute("event")]
        public SecurityEvent EventType
        {
            get { return eventType; }
            set { eventType = value; }
        }
        [XmlAttribute("right")]
        public SecurityRight SecurityRight
        {
            get { return eventRight; }
            set { eventRight = value; }
        }
        [XmlElement("message")]
        public string Message
        {
            get { return message; }
            set { message = value; }
        }
    }
}
