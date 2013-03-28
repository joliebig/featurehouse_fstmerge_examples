using System.DirectoryServices;
using System.Runtime.InteropServices;
using Exortech.NetReflector;
using ThoughtWorks.CruiseControl.Remote;
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
        private Util.ILdapService ldapService;
        public ActiveDirectoryAuthentication()
        {
            ldapService = new Util.LdapHelper();
        }
        public ActiveDirectoryAuthentication(string userName, Util.ILdapService ldap)
        {
            this.UserName = userName;
            ldapService = ldap;
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
            string retrievedUserName = GetUserName(credentials);
            string retrievedPassword = GetPassword(credentials);
            if (string.IsNullOrEmpty(retrievedUserName))
                return false;
            return ldapService.Authenticate(retrievedUserName, retrievedPassword, DomainName);
        }
        public string GetUserName(LoginRequest credentials)
        {
            Util.Log.Trace("Getting username from_ credentials");
            string dummy = NameValuePair.FindNamedValue(credentials.Credentials, LoginRequest.UserNameCredential);
            Util.Log.Trace("found username {0}", dummy);
            return dummy;
        }
        public string GetPassword(LoginRequest credentials)
        {
            return NameValuePair.FindNamedValue(credentials.Credentials, LoginRequest.PasswordCredential);
        }
        public string GetDisplayName(LoginRequest credentials)
        {
            string userName = GetUserName(credentials);
            string nameToReturn = userName;
            ldapService.DomainName = DomainName;
            Util.LdapUserInfo lu = ldapService.RetrieveUserInformation(userName);
            if (!string.IsNullOrEmpty(lu.DisplayName))
            {
                nameToReturn = lu.DisplayName;
            }
            return nameToReturn;
        }
        public void ChangePassword(string newPassword)
        {
        }
    }
}
