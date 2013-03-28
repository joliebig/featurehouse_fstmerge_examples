using System;
using System.Collections.Generic;
using System.Text;
using ThoughtWorks.CruiseControl.Remote;
using ThoughtWorks.CruiseControl.Remote.Messages;
namespace ThoughtWorks.CruiseControl.Core.Security
{
    public interface IAuthentication
        : ISecuritySetting
    {
        bool Authenticate(LoginRequest credentials);
        string GetUserName(LoginRequest credentials);
        string GetDisplayName(LoginRequest credentials);
        string UserName { get; }
        string DisplayName { get; }
        string AuthenticationName { get; }
        void ChangePassword(string newPassword);
    }
}
