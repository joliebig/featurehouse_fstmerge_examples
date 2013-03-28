using Exortech.NetReflector;
using System;
using System.Collections.Generic;
using System.Text;
using ThoughtWorks.CruiseControl.Remote;
using ThoughtWorks.CruiseControl.Remote.Security;
namespace ThoughtWorks.CruiseControl.Core.Security
{
    [ReflectorType("simpleUser")]
    public class UserNameAuthentication
        : IAuthentication
    {
        private const string userNameCredential = "username";
        private string userName;
        private string displayName;
        private ISecurityManager manager;
        public UserNameAuthentication() { }
        public UserNameAuthentication(string userName)
        {
            this.userName = userName;
        }
        public string Identifier
        {
            get { return userName; }
        }
        [ReflectorProperty("name")]
        public string UserName
        {
            get { return userName; }
            set { userName = value; }
        }
        [ReflectorProperty("display", Required = false)]
        public string DisplayName
        {
            get { return displayName; }
            set { displayName = value; }
        }
        public string AuthenticationName
        {
            get { return "Simple"; }
        }
        public ISecurityManager Manager
        {
            get { return manager; }
            set { manager = value; }
        }
        public bool Authenticate(ISecurityCredentials credentials)
        {
            string userName = credentials[userNameCredential];
            bool isValid = !string.IsNullOrEmpty(userName);
            if (isValid) isValid = SecurityHelpers.IsWildCardMatch(this.userName, userName);
            return isValid;
        }
        public string GetUserName(ISecurityCredentials credentials)
        {
            string userName = credentials[userNameCredential];
            return userName;
        }
        public string GetDisplayName(ISecurityCredentials credentials)
        {
            string nameToReturn = displayName;
            if (string.IsNullOrEmpty(displayName)) nameToReturn = GetUserName(credentials);
            return nameToReturn;
        }
        public void ChangePassword(string newPassword)
        {
        }
    }
}
