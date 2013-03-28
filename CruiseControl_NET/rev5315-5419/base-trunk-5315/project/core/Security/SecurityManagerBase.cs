using Exortech.NetReflector;
using System;
using System.Collections.Generic;
using System.Text;
using System.Text.RegularExpressions;
using ThoughtWorks.CruiseControl.Core.Util;
using ThoughtWorks.CruiseControl.Remote;
using ThoughtWorks.CruiseControl.Remote.Security;
using ThoughtWorks.CruiseControl.Core.Security.Auditing;
using ThoughtWorks.CruiseControl.Core.Config;
using System.Globalization;
namespace ThoughtWorks.CruiseControl.Core.Security
{
    public abstract class SecurityManagerBase
        : ISecurityManager
    {
        private const string displayNameKey = "DisplayName";
        private SecurityRight defaultRight = SecurityRight.Deny;
        private ISessionCache sessionCache = new InMemorySessionCache();
        private IAuditLogger[] loggers = new IAuditLogger[0];
        private IAuditReader reader;
        [ReflectorProperty("cache", InstanceTypeKey = "type", Required = false)]
        public ISessionCache SessionCache
        {
            get { return sessionCache; }
            set { sessionCache = value; }
        }
        [ReflectorProperty("defaultRight", Required = false)]
        public SecurityRight DefaultRight
        {
            get { return defaultRight; }
            set
            {
                if (value == SecurityRight.Inherit) throw new ArgumentOutOfRangeException("DefaultRight", "DefaultRight must be either Allow or Deny");
                defaultRight = value;
            }
        }
        [ReflectorProperty("audit", Required=false)]
        public IAuditLogger[] AuditLoggers
        {
            get { return loggers; }
            set { loggers = value; }
        }
        [ReflectorProperty("auditReader", InstanceTypeKey = "type", Required = false)]
        public IAuditReader AuditReader
        {
            get { return reader; }
            set { reader = value; }
        }
        public abstract void Initialise();
        public abstract IAuthentication RetrieveUser(string identifier);
        public abstract IPermission RetrievePermission(string identifier);
        public virtual string Login(ISecurityCredentials credentials)
        {
            string sessionToken = null;
            string identifier = credentials.Identifier;
            IAuthentication authentication = RetrieveUser(identifier);
            string userName = credentials.Identifier;
            string displayName = null;
            if (authentication != null)
            {
                userName = authentication.GetUserName(credentials);
                if (authentication.Authenticate(credentials))
                {
                    sessionToken = sessionCache.AddToCache(userName);
                    displayName = authentication.GetDisplayName(credentials);
                    sessionCache.StoreSessionValue(sessionToken, displayNameKey, displayName);
                }
            }
            if (sessionToken != null)
            {
                Log.Debug(string.Format("{0} [{1}] has logged in", displayName, userName));
                LogEvent(null, userName, SecurityEvent.Login, SecurityRight.Allow, null);
            }
            else
            {
                Log.Warning(string.Format("Login failure: {0} has failed to login", userName));
                LogEvent(null, userName, SecurityEvent.Login, SecurityRight.Deny, null);
            }
            return sessionToken;
        }
        public virtual void Logout(string sessionToken)
        {
            string userName = sessionCache.RetrieveFromCache(sessionToken);
            if (!string.IsNullOrEmpty(userName))
            {
                sessionCache.RemoveFromCache(sessionToken);
                Log.Debug(string.Format("{0} has logged out", userName));
                LogEvent(null, userName, SecurityEvent.Logout, SecurityRight.Allow, null);
            }
            else
            {
                LogEvent(null, null, SecurityEvent.Logout, SecurityRight.Deny, "Session has already been logged out");
            }
        }
        public virtual bool ValidateSession(string sessionToken)
        {
            if (sessionToken == null) return false;
            string userName = sessionCache.RetrieveFromCache(sessionToken);
            return (userName != null);
        }
        public virtual string GetUserName(string sessionToken)
        {
            if (sessionToken == null) return null;
            string userName = sessionCache.RetrieveFromCache(sessionToken);
            return userName;
        }
        public virtual string GetDisplayName(string sessionToken)
        {
            if (sessionToken == null) return null;
            string displayName = sessionCache.RetrieveSessionValue(sessionToken, displayNameKey) as string;
            return displayName;
        }
        public virtual void LogEvent(string projectName, string userName, SecurityEvent eventType, SecurityRight eventRight, string message)
        {
            if (loggers != null)
            {
                foreach (IAuditLogger logger in loggers)
                {
                    logger.LogEvent(projectName, userName, eventType, eventRight, message);
                }
            }
        }
        public abstract List<UserDetails> ListAllUsers();
        public abstract bool CheckServerPermission(string userName, SecurityPermission permission);
        public virtual List<AuditRecord> ReadAuditRecords(int startPosition, int numberOfRecords)
        {
            List<AuditRecord> records = new List<AuditRecord>();
            if (reader != null) records = reader.Read(startPosition, numberOfRecords);
            return records;
        }
        public virtual List<AuditRecord> ReadAuditRecords(int startPosition, int numberOfRecords, IAuditFilter filter)
        {
            List<AuditRecord> records = new List<AuditRecord>();
            if (reader != null) records = reader.Read(startPosition, numberOfRecords, filter);
            return records;
        }
        public virtual void ChangePassword(string sessionToken, string oldPassword, string newPassword)
        {
            throw new NotImplementedException("Password management is not allowed for this security manager");
        }
        public virtual void ResetPassword(string sessionToken, string userName, string newPassword)
        {
            throw new NotImplementedException("Password management is not allowed for this security manager");
        }
        public TComponent RetrieveComponent<TComponent>()
            where TComponent : class
        {
            return null;
        }
    }
}
