using System;
namespace ThoughtWorks.CruiseControl.Remote.Security
{
    public static class AuditFilters
    {
        public static IAuditFilter ByProject(string projectName)
        {
            return new ProjectAuditFilter(projectName);
        }
        public static IAuditFilter ByUser(string userName)
        {
            return new UserAuditFilter(userName);
        }
        public static IAuditFilter ByEventType(SecurityEvent eventType)
        {
            return new EventTypeAuditFilter(eventType);
        }
        public static IAuditFilter ByRight(SecurityRight right)
        {
            return new SecurityRightAuditFilter(right);
        }
        public static IAuditFilter ByDateRange(DateTime startDate, DateTime endDate)
        {
            return new DateRangeAuditFilter(startDate, endDate);
        }
        public static IAuditFilter Combine(params IAuditFilter[] filters)
        {
            return new CombinationAuditFilter(filters);
        }
    }
}
