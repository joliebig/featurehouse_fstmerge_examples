using System;
using System.Xml.Serialization;
namespace ThoughtWorks.CruiseControl.Remote.Security
{
    [Serializable]
    public class EventTypeAuditFilter
        : AuditFilterBase
    {
        private SecurityEvent type;
        public EventTypeAuditFilter()
        {
        }
        public EventTypeAuditFilter(SecurityEvent eventType)
            : this(eventType, null) { }
        public EventTypeAuditFilter(SecurityEvent eventType, AuditFilterBase innerFilter)
            : base(innerFilter)
        {
            this.type = eventType;
        }
        [XmlAttribute("type")]
        public SecurityEvent EventType
        {
            get { return type; }
            set { type = value; }
        }
        protected override bool DoCheckFilter(AuditRecord record)
        {
            bool include = (this.type == record.EventType);
            return include;
        }
    }
}
