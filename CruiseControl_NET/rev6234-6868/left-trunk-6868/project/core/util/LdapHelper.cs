using System;
using System.Collections.Generic;
using System.Text;
using System.DirectoryServices;
namespace ThoughtWorks.CruiseControl.Core.Util
{
    public class LdapHelper : ILdapService
    {
        public LdapHelper()
            : this(null, null, null)
        { }
        public LdapHelper(string domainName)
            : this(domainName, null, null)
        { }
        public LdapHelper(string domainName, string logonUser, string logOnPassword)
        {
            DomainName = domainName;
            LdapLogonUserName = logonUser;
            LdapLogonPassword = logOnPassword;
            LdapFieldMailAddress = "mail";
            LdapFieldSurName = "sn";
            LdapFieldName = "name";
            LdapFieldCommonName = "cn";
            LdapFieldGivenName = "givenname";
            LdapFieldDisplayName = "displayname";
            LdapFieldMailNickName = "mailnickname";
        }
        public string DomainName { get; set; }
        public string LdapLogonUserName { get; set; }
        public string LdapLogonPassword { get; set; }
        public string LdapFieldMailAddress { get; set; }
        public string LdapFieldSurName { get; set; }
        public string LdapFieldName { get; set; }
        public string LdapFieldCommonName { get; set; }
        private string LdapFieldGivenName { get; set; }
        public string LdapFieldDisplayName { get; set; }
        public string LdapFieldMailNickName { get; set; }
        public LdapUserInfo RetrieveUserInformation(string userNameToRetrieveFrom)
        {
            System.DirectoryServices.DirectorySearcher LdapSearcher = new System.DirectoryServices.DirectorySearcher();
            System.DirectoryServices.SearchResult LdapResult = default(System.DirectoryServices.SearchResult);
            string filter = "(&(objectClass=user)(SAMAccountName=" + userNameToRetrieveFrom + "))";
            System.DirectoryServices.DirectoryEntry Ldap = default(System.DirectoryServices.DirectoryEntry);
            try
            {
                if (LdapLogonUserName == null)
                {
                    Ldap = new System.DirectoryServices.DirectoryEntry("LDAP://" + DomainName);
                }
                else
                {
                    Ldap = new System.DirectoryServices.DirectoryEntry("LDAP://" + DomainName, LdapLogonUserName, LdapLogonPassword);
                }
            }
            catch (Exception e)
            {
                Util.Log.Trace(e.ToString());
                throw new Exception("Problem connecting to LDAP service", e);
            }
            LdapSearcher.SearchRoot = Ldap;
            LdapSearcher.PropertiesToLoad.Add(LdapFieldMailAddress);
            LdapSearcher.PropertiesToLoad.Add(LdapFieldName);
            LdapSearcher.PropertiesToLoad.Add(LdapFieldSurName);
            LdapSearcher.PropertiesToLoad.Add(LdapFieldCommonName);
            LdapSearcher.PropertiesToLoad.Add(LdapFieldGivenName);
            LdapSearcher.PropertiesToLoad.Add(LdapFieldDisplayName);
            LdapSearcher.PropertiesToLoad.Add(LdapFieldMailNickName);
            LdapSearcher.Filter = filter;
            LdapResult = LdapSearcher.FindOne();
            LdapSearcher.Dispose();
            LdapUserInfo result = new LdapUserInfo();
            if ((LdapResult != null))
            {
                result.CommonName = (string)LdapResult.GetDirectoryEntry().Properties[LdapFieldCommonName].Value;
                result.DisplayName = (string)LdapResult.GetDirectoryEntry().Properties[LdapFieldDisplayName].Value;
                result.GivenName = (string)LdapResult.GetDirectoryEntry().Properties[LdapFieldGivenName].Value;
                result.MailAddress = (string)LdapResult.GetDirectoryEntry().Properties[LdapFieldMailAddress].Value;
                result.MailNickName = (string)LdapResult.GetDirectoryEntry().Properties[LdapFieldMailNickName].Value;
                result.Name = (string)LdapResult.GetDirectoryEntry().Properties[LdapFieldName].Value;
                result.SurName = (string)LdapResult.GetDirectoryEntry().Properties[LdapFieldSurName].Value;
            }
            return result;
        }
        public bool Authenticate(string userName, string password, string domainName)
        {
            if (string.IsNullOrEmpty(userName) || string.IsNullOrEmpty(domainName))
                return false;
            AuthenticationTypes atAuthentType = AuthenticationTypes.Secure;
            bool isAuthenticUser = false;
            using (DirectoryEntry deDirEntry = new DirectoryEntry("LDAP://" + domainName, userName, password, atAuthentType))
             {
                try
                {
                    if ( string.IsNullOrEmpty(deDirEntry.Name) )
                        isAuthenticUser = false;
                    else
                        isAuthenticUser = true;
                }
                catch
                {
                    isAuthenticUser = false;
                }
             }
            return isAuthenticUser;
        }
    }
}
