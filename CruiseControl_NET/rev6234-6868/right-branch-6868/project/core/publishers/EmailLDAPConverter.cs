using System;
using Exortech.NetReflector;
namespace ThoughtWorks.CruiseControl.Core.Publishers
{
    [ReflectorType("ldapConverter")]
    public class EmailLDAPConverter : IEmailConverter
    {
        private string domainName = string.Empty;
        private string ldap_Mail = "mail";
        private string ldap_QueryField = "MailNickName";
        private string ldap_LogOnUser = string.Empty;
        private string ldap_LogOnPassword = string.Empty;
        [ReflectorProperty("domainName", Required = true)]
        public string DomainName
        {
            get { return domainName; }
            set { domainName = value; }
        }
        [ReflectorProperty("ldapQueryField", Required = false)]
        public string LdapQueryField
        {
            get { return ldap_QueryField; }
            set { ldap_QueryField = value; }
        }
        [ReflectorProperty("ldapLogOnUser", Required = false)]
        public string LdapLogOnUser
        {
            get { return ldap_LogOnUser; }
            set { ldap_LogOnUser = value; }
        }
        [ReflectorProperty("ldapLogOnPassword", Required = false)]
        public string LdapLogOnPassword
        {
            get { return ldap_LogOnPassword; }
            set { ldap_LogOnPassword = value; }
        }
        public EmailLDAPConverter()
        {
        }
        public string Convert(string username)
        {
            string LDAPPath = @"LDAP://" + domainName;
            string LDAPFilter = @"(&(objectClass=user)(SAMAccountName=" + username + "))";
            string[] LDAPProperties = { ldap_Mail, ldap_QueryField };
            System.DirectoryServices.DirectoryEntry domain;
            if (ldap_LogOnUser.Length > 0 )
            {
                domain = new System.DirectoryServices.DirectoryEntry(LDAPPath,ldap_LogOnUser,ldap_LogOnPassword);
            }
            else
            {
                domain = new System.DirectoryServices.DirectoryEntry(LDAPPath);
            }
            System.DirectoryServices.DirectorySearcher searcher = new System.DirectoryServices.DirectorySearcher(domain);
            System.DirectoryServices.SearchResult result;
            searcher.Filter = LDAPFilter;
            searcher.PropertiesToLoad.AddRange(LDAPProperties);
            result = searcher.FindOne();
            searcher.Dispose();
            if (result != null)
            {
                return result.Properties[ldap_Mail][0].ToString();
            }
            else
            {
                Core.Util.Log.Debug(string.Format("No email adress found for user {0} in domain {1}",username,domainName));
                return null;
            }
        }
    }
}
