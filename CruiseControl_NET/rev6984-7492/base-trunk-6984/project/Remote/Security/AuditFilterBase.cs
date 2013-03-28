using System;
using System.Xml.Serialization;
namespace ThoughtWorks.CruiseControl.Remote.Security
{
    [Serializable]
    [XmlInclude(typeof(CombinationAuditFilter))]
    [XmlInclude(typeof(DateRangeAuditFilter))]
    [XmlInclude(typeof(EventTypeAuditFilter))]
    [XmlInclude(typeof(ProjectAuditFilter))]
    [XmlInclude(typeof(SecurityRightAuditFilter))]
    [XmlInclude(typeof(UserAuditFilter))]
    public abstract class AuditFilterBase
    {
        private AuditFilterBase innerFilter;
        public AuditFilterBase() { }
        public AuditFilterBase(AuditFilterBase inner)
        {
            this.innerFilter = inner;
        }
        public virtual bool CheckFilter(AuditRecord record)
        {
            bool include = DoCheckFilter(record);
            if (include && (innerFilter != null)) include = innerFilter.CheckFilter(record);
            return include;
        }
        public virtual AuditFilterBase ByProject(string projectName)
        {
            return new ProjectAuditFilter(projectName, this);
        }
        public virtual AuditFilterBase ByUser(string userName)
        {
            return new UserAuditFilter(userName, this);
        }
        public virtual AuditFilterBase ByEventType(SecurityEvent eventType)
        {
            return new EventTypeAuditFilter(eventType, this);
        }
        public virtual AuditFilterBase ByRight(SecurityRight right)
        {
            return new SecurityRightAuditFilter(right, this);
        }
        public virtual AuditFilterBase ByDateRange(DateTime startDate, DateTime endDate)
        {
            return new DateRangeAuditFilter(startDate, endDate, this);
        }
        protected abstract bool DoCheckFilter(AuditRecord record);
    }
}
