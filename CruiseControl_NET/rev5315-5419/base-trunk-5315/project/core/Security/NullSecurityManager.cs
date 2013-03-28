using Exortech.NetReflector;
using System;
using System.Collections.Generic;
using System.Text;
using ThoughtWorks.CruiseControl.Core.Security.Auditing;
using ThoughtWorks.CruiseControl.Remote;
using ThoughtWorks.CruiseControl.Remote.Security;
namespace ThoughtWorks.CruiseControl.Core.Security
{
    [ReflectorType("nullSecurity")]
    public class NullSecurityManager
        : ISecurityManager
    {
        public void Initialise()
        {
        }
        public string Login(ISecurityCredentials credentials)
        {
            string userName = credentials["username"];
            return userName;
        }
        public void Logout(string sessionToken)
        {
        }
        public bool ValidateSession(string sessionToken)
        {
            return true;
        }
        public string GetUserName(string sessionToken)
        {
            return sessionToken == null ? string.Empty : sessionToken;
        }
        public string GetDisplayName(string sessionToken)
        {
            return sessionToken == null ? string.Empty : sessionToken;
        }
        public IAuthentication RetrieveUser(string identifier)
        {
            return null;
        }
        public IPermission RetrievePermission(string identifier)
        {
            return null;
        }
        public void LogEvent(string projectName, string userName, SecurityEvent eventType, SecurityRight eventRight, string message)
        {
        }
        public virtual List<UserDetails> ListAllUsers()
        {
            return new List<UserDetails>();
        }
        public virtual List<SecurityCheckDiagnostics> DiagnoseSecurityPermissions(string userName, params string[] projectNames)
        {
            return new List<SecurityCheckDiagnostics>();
        }
        public virtual bool CheckServerPermission(string userName, SecurityPermission permission)
        {
            return true;
        }
        public virtual List<AuditRecord> ReadAuditRecords(int startPosition, int numberOfRecords)
        {
            List<AuditRecord> records = new List<AuditRecord>();
            return records;
        }
        public virtual List<AuditRecord> ReadAuditRecords(int startPosition, int numberOfRecords, IAuditFilter filter)
        {
            List<AuditRecord> records = new List<AuditRecord>();
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
