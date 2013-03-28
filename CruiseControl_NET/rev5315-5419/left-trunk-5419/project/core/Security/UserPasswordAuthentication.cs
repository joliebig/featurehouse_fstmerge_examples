using Exortech.NetReflector;
using System;
using System.Collections.Generic;
using System.Text;
using ThoughtWorks.CruiseControl.Remote;
using ThoughtWorks.CruiseControl.Remote.Messages;
namespace ThoughtWorks.CruiseControl.Core.Security
{
    [ReflectorType("passwordUser")]
    public class UserPasswordAuthentication
        : IAuthentication
    {
        private const string userNameCredential = "username";
        private const string passwordCredential = "password";
        private string userName;
        private string password;
        private string displayName;
        private ISecurityManager manager;
        public UserPasswordAuthentication() { }
        public UserPasswordAuthentication(string userName, string password)
        {
            this.userName = userName;
            this.password = password;
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
        [ReflectorProperty("password")]
        public string Password
        {
            get { return password; }
            set { password = value; }
        }
        public string AuthenticationName
        {
            get { return "Password"; }
        }
        [ReflectorProperty("display", Required=false)]
        public string DisplayName
        {
            get { return displayName; }
            set { displayName = value; }
        }
        public ISecurityManager Manager
        {
            get { return manager; }
            set { manager = value; }
        }
        public bool Authenticate(LoginRequest credentials)
        {
            string userName = GetUserName(credentials);
            string password = NameValuePair.FindNamedValue(credentials.Credentials,
                LoginRequest.PasswordCredential);
            bool isValid = !string.IsNullOrEmpty(userName) && !string.IsNullOrEmpty(password);
            if (isValid)
            {
                isValid = SecurityHelpers.IsWildCardMatch(userName, this.userName) &&
                 string.Equals(password, this.password, StringComparison.InvariantCulture);
            }
            return isValid;
        }
        public string GetUserName(LoginRequest credentials)
        {
            string userName = NameValuePair.FindNamedValue(credentials.Credentials,
                LoginRequest.UserNameCredential);
            return userName;
        }
        public string GetDisplayName(LoginRequest credentials)
        {
            string nameToReturn = displayName;
            if (string.IsNullOrEmpty(displayName)) nameToReturn = GetUserName(credentials);
            return nameToReturn;
        }
        public void ChangePassword(string newPassword)
        {
            password = newPassword;
        }
    }
}
