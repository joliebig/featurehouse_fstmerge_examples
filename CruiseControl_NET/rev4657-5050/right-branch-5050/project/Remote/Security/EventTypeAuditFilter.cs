using System;
namespace ThoughtWorks.CruiseControl.Remote.Security
{
    [Serializable]
    public class EventTypeAuditFilter
        : AuditFilterBase
    {
        private SecurityEvent type;
        public EventTypeAuditFilter(SecurityEvent eventType)
            : this(eventType, null) { }
        public EventTypeAuditFilter(SecurityEvent eventType, IAuditFilter innerFilter)
            : base(innerFilter)
        {
            this.type = eventType;
        }
        protected override bool DoCheckFilter(AuditRecord record)
        {
            bool include = (this.type == record.EventType);
            return include;
        }
    }
}
