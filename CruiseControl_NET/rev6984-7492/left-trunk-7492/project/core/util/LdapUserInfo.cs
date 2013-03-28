using System;
using System.Collections.Generic;
using System.Text;
namespace ThoughtWorks.CruiseControl.Core.Util
{
    public class LdapUserInfo
    {
        public string MailAddress {get;set;}
        public string Name { get; set; }
        public string SurName { get; set; }
        public string CommonName { get; set; }
        public string GivenName { get; set; }
        public string DisplayName { get; set; }
        public string MailNickName { get; set; }
    }
}
