using System;
using System.Collections.Generic;
using System.Text;
using ThoughtWorks.CruiseControl.Core.Security.Auditing;
using ThoughtWorks.CruiseControl.Remote;
using ThoughtWorks.CruiseControl.Remote.Security;
namespace ThoughtWorks.CruiseControl.Core.Security
{
    public interface ISecurityManager
    {
        void Initialise();
        string Login(ISecurityCredentials credentials);
        void Logout(string sessionToken);
        bool ValidateSession(string sessionToken);
        string GetUserName(string sessionToken);
        string GetDisplayName(string sessionToken);
        IAuthentication RetrieveUser(string identifier);
        IPermission RetrievePermission(string identifier);
        void LogEvent(string projectName, string userName, SecurityEvent eventType, SecurityRight eventRight, string message);
        List<UserDetails> ListAllUsers();
        bool CheckServerPermission(string userName, SecurityPermission permission);
        List<AuditRecord> ReadAuditRecords(int startPosition, int numberOfRecords);
        List<AuditRecord> ReadAuditRecords(int startPosition, int numberOfRecords, IAuditFilter filter);
        void ChangePassword(string sessionToken, string oldPassword, string newPassword);
        void ResetPassword(string sessionToken, string userName, string newPassword);
        TComponent RetrieveComponent<TComponent>()
            where TComponent : class;
    }
}
