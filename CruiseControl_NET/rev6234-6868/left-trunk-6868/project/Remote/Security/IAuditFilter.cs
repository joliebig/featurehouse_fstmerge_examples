using System;
namespace ThoughtWorks.CruiseControl.Remote.Security
{
    public interface IAuditFilter
    {
        bool CheckFilter(AuditRecord record);
        IAuditFilter ByProject(string projectName);
        IAuditFilter ByUser(string userName);
        IAuditFilter ByEventType(SecurityEvent eventType);
        IAuditFilter ByRight(SecurityRight right);
        IAuditFilter ByDateRange(DateTime startDate, DateTime endDate);
    }
}
