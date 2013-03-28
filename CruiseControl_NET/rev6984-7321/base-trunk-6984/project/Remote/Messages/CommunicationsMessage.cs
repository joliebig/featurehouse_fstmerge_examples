using System;
using System.Xml.Serialization;
namespace ThoughtWorks.CruiseControl.Remote.Messages
{
    [Serializable]
    public abstract class CommunicationsMessage
    {
        private DateTime timestamp = DateTime.Now;
        [NonSerialized]
        private object channelInformation;
        [XmlAttribute("timestamp")]
        public DateTime Timestamp
        {
            get { return timestamp; }
            set { timestamp = value; }
        }
        [XmlIgnore]
        public object ChannelInformation
        {
            get { return channelInformation; }
            set { channelInformation = value; }
        }
    }
}
