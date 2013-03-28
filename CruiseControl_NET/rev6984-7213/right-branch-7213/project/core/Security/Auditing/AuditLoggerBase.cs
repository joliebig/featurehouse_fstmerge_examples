using Exortech.NetReflector;
using System;
using ThoughtWorks.CruiseControl.Remote.Security;
namespace ThoughtWorks.CruiseControl.Core.Security.Auditing
{
    public abstract class AuditLoggerBase
    {
        private bool logSuccessfulEvents = true;
        private bool logFailureEvents = true;
        [ReflectorProperty("success", Required = false)]
        public bool LogSuccessfulEvents
        {
            get { return this.logSuccessfulEvents; }
            set { this.logSuccessfulEvents = value; }
        }
        [ReflectorProperty("failure", Required = false)]
        public bool LogFailureEvents
        {
            get { return this.logFailureEvents; }
            set { this.logFailureEvents = value; }
        }
        public virtual void LogEvent(string projectName, string userName, SecurityEvent eventType, SecurityRight eventRight, string message)
        {
            if ((eventRight == SecurityRight.Allow) && this.logSuccessfulEvents)
            {
                DoLogEvent(projectName, userName, eventType, eventRight, message);
            }
            else if ((eventRight == SecurityRight.Deny) && this.logFailureEvents)
            {
                DoLogEvent(projectName, userName, eventType, eventRight, message);
            }
            else if (eventRight == SecurityRight.Inherit)
            {
                DoLogEvent(projectName, userName, eventType, eventRight, message);
            }
        }
        public virtual void LogEvent(AuditRecord record)
        {
            LogEvent(record.ProjectName,
                record.UserName,
                record.EventType,
                record.SecurityRight,
                record.Message);
            record.TimeOfEvent = DateTime.Now;
        }
        protected abstract void DoLogEvent(string projectName, string userName, SecurityEvent eventType, SecurityRight eventRight, string message);
    }
}
