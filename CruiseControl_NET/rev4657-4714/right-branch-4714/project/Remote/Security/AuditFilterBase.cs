using System;
namespace ThoughtWorks.CruiseControl.Remote.Security
{
    [Serializable]
    public abstract class AuditFilterBase
        : IAuditFilter
    {
        private IAuditFilter innerFilter;
        public AuditFilterBase() { }
        public AuditFilterBase(IAuditFilter inner)
        {
            this.innerFilter = inner;
        }
        public virtual bool CheckFilter(AuditRecord record)
        {
            bool include = DoCheckFilter(record);
            if (include && (innerFilter != null)) include = innerFilter.CheckFilter(record);
            return include;
        }
        public virtual IAuditFilter ByProject(string projectName)
        {
            return new ProjectAuditFilter(projectName, this);
        }
        public virtual IAuditFilter ByUser(string userName)
        {
            return new UserAuditFilter(userName, this);
        }
        public virtual IAuditFilter ByEventType(SecurityEvent eventType)
        {
            return new EventTypeAuditFilter(eventType, this);
        }
        public virtual IAuditFilter ByRight(SecurityRight right)
        {
            return new SecurityRightAuditFilter(right, this);
        }
        public virtual IAuditFilter ByDateRange(DateTime startDate, DateTime endDate)
        {
            return new DateRangeAuditFilter(startDate, endDate, this);
        }
        protected abstract bool DoCheckFilter(AuditRecord record);
    }
}
