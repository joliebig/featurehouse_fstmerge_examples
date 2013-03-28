using System;
namespace ThoughtWorks.CruiseControl.Remote.Security
{
    public static class AuditFilters
    {
        public static AuditFilterBase ByProject(string projectName)
        {
            return new ProjectAuditFilter(projectName);
        }
        public static AuditFilterBase ByUser(string userName)
        {
            return new UserAuditFilter(userName);
        }
        public static AuditFilterBase ByEventType(SecurityEvent eventType)
        {
            return new EventTypeAuditFilter(eventType);
        }
        public static AuditFilterBase ByRight(SecurityRight right)
        {
            return new SecurityRightAuditFilter(right);
        }
        public static AuditFilterBase ByDateRange(DateTime startDate, DateTime endDate)
        {
            return new DateRangeAuditFilter(startDate, endDate);
        }
        public static AuditFilterBase Combine(params AuditFilterBase[] filters)
        {
            return new CombinationAuditFilter(filters);
        }
    }
}
