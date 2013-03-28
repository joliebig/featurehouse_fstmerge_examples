using Exortech.NetReflector;
using System;
using System.Collections.Generic;
using System.Text;
using ThoughtWorks.CruiseControl.Remote.Security;
namespace ThoughtWorks.CruiseControl.Core.Security
{
    [ReflectorType("nullProjectSecurity")]
    public class NullProjectAuthorisation
        : IProjectAuthorisation
    {
        public NullProjectAuthorisation() { }
        public bool RequiresServerSecurity
        {
            get { return false; }
        }
        public string GuestAccountName
        {
            get { return null; }
        }
        public bool RequiresSession(ISecurityManager manager)
        {
            return false;
        }
        public virtual bool CheckPermission(ISecurityManager manager,
            string userName,
            SecurityPermission permission,
            SecurityRight defaultRight)
        {
            return true;
        }
    }
}
