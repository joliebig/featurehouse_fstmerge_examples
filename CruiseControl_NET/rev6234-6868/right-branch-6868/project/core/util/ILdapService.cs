using System;
using System.Collections.Generic;
using System.Text;
namespace ThoughtWorks.CruiseControl.Core.Util
{
    public interface ILdapService
    {
        string DomainName { get; set; }
        LdapUserInfo RetrieveUserInformation(string userNameToRetrieveFrom);
        bool Authenticate(string userName, string password, string domainName);
    }
}
