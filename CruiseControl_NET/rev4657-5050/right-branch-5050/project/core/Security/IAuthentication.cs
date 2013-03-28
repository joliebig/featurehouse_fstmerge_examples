using System;
using System.Collections.Generic;
using System.Text;
using ThoughtWorks.CruiseControl.Remote;
using ThoughtWorks.CruiseControl.Remote.Security;
namespace ThoughtWorks.CruiseControl.Core.Security
{
    public interface IAuthentication
        : ISecuritySetting
    {
        bool Authenticate(ISecurityCredentials credentials);
        string GetUserName(ISecurityCredentials credentials);
        string GetDisplayName(ISecurityCredentials credentials);
        string UserName { get; }
        string DisplayName { get; }
        string AuthenticationName { get; }
        void ChangePassword(string newPassword);
    }
}
