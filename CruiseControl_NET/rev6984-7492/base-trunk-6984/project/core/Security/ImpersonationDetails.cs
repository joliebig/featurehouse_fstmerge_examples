using Exortech.NetReflector;
using System;
using ThoughtWorks.CruiseControl.Core.Util;
namespace ThoughtWorks.CruiseControl.Core.Security
{
    [ReflectorType("impersonation")]
    public class ImpersonationDetails
    {
        public ImpersonationDetails()
        {
        }
        public ImpersonationDetails(string domainName, string userName, string password)
        {
            DomainName = domainName;
            UserName = userName;
            Password = password;
        }
        [ReflectorProperty("domain")]
        public string DomainName { get; set; }
        [ReflectorProperty("user")]
        public string UserName { get; set; }
        [ReflectorProperty("password", typeof(PrivateStringSerialiserFactory))]
        public PrivateString Password { get; set; }
        public IDisposable Impersonate()
        {
            var impersonation = new Impersonation(DomainName, UserName, Password.PrivateValue);
            impersonation.Impersonate();
            return impersonation;
        }
    }
}
