using System;
using ThoughtWorks.CruiseControl.Remote.Security;
namespace ThoughtWorks.CruiseControl.Core.Security.Auditing
{
    public interface IAuditLogger
    {
        void LogEvent(string projectName, string userName, SecurityEvent eventType, SecurityRight eventRight, string message);
        void LogEvent(AuditRecord record);
    }
}
