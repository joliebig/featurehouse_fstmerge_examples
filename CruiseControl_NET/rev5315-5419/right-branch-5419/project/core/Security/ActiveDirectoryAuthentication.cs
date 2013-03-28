using Exortech.NetReflector;
using System;
using System.Collections.Generic;
using System.DirectoryServices;
using System.DirectoryServices.ActiveDirectory;
using System.Text;
using ThoughtWorks.CruiseControl.Remote;
using System.Runtime.InteropServices;
using ThoughtWorks.CruiseControl.Remote.Messages;
namespace ThoughtWorks.CruiseControl.Core.Security
{
    [ReflectorType("ldapUser")]
    public class ActiveDirectoryAuthentication
        : IAuthentication
    {
        private const string userNameCredential = "username";
        private string userName;
        private string domainName;
        private ISecurityManager manager;
        public ActiveDirectoryAuthentication() { }
        public ActiveDirectoryAuthentication(string userName)
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
        public string DisplayName
        {
            get { return null; }
        }
        public string AuthenticationName
        {
            get { return "LDAP"; }
        }
        [ReflectorProperty("domain")]
        public string DomainName
        {
            get { return domainName; }
            set { domainName = value; }
        }
        public ISecurityManager Manager
        {
            get { return manager; }
            set { manager = value; }
        }
        public bool Authenticate(LoginRequest credentials)
        {
            string userName = GetUserName(credentials);
            bool isValid = !string.IsNullOrEmpty(userName);
            if (isValid)
            {
                string displayName = FindUser(userName);
                isValid = (displayName != null);
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
            string userName = GetUserName(credentials);
            string nameToReturn = FindUser(userName);
            if (string.IsNullOrEmpty(nameToReturn)) nameToReturn = userName;
            return nameToReturn;
        }
        public void ChangePassword(string newPassword)
        {
        }
        private string FindUser(string userName)
        {
            if (string.IsNullOrEmpty(userName)) return null;
            DirectoryEntry domain = new DirectoryEntry("LDAP://OU=Domain,DC=" + domainName);
            domain.AuthenticationType = AuthenticationTypes.Secure;
            DirectorySearcher searcher = new DirectorySearcher(domain);
            searcher.Filter = "(SAMAccountName=" + userName + ")";
            searcher.PropertiesToLoad.Add("displayName");
            try
            {
                SearchResult result = searcher.FindOne();
                if (result != null)
                {
                    return result.Properties["displayname"][0].ToString();
                }
                else
                {
                    return null;
                }
            }
            catch (COMException)
            {
                return null;
            }
        }
    }
}
