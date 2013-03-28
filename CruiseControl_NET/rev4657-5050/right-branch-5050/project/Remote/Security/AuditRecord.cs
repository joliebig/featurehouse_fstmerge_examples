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
        public DateTime TimeOfEvent
        {
            get { return timeOfEvent; }
            set { timeOfEvent = value; }
        }
        public string ProjectName
        {
            get { return projectName; }
            set { projectName = value; }
        }
        public string UserName
        {
            get { return userName; }
            set { userName = value; }
        }
        public SecurityEvent EventType
        {
            get { return eventType; }
            set { eventType = value; }
        }
        public SecurityRight SecurityRight
        {
            get { return eventRight; }
            set { eventRight = value; }
        }
        public string Message
        {
            get { return message; }
            set { message = value; }
        }
    }
}
