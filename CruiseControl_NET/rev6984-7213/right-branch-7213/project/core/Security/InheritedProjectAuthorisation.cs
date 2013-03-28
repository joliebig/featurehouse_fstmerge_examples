using System;
using Exortech.NetReflector;
using ThoughtWorks.CruiseControl.Core.Config;
using ThoughtWorks.CruiseControl.Remote.Security;
namespace ThoughtWorks.CruiseControl.Core.Security
{
    [ReflectorType("inheritedProjectSecurity")]
    public class InheritedProjectAuthorisation
        : IProjectAuthorisation
    {
        public InheritedProjectAuthorisation() { }
        public bool RequiresServerSecurity
        {
            get { return false; }
        }
        [ReflectorProperty("guest", Required = false)]
        public string GuestAccountName { get; set; }
        public bool RequiresSession(ISecurityManager manager)
        {
            return manager.RequiresSession;
        }
        public virtual bool CheckPermission(ISecurityManager manager,
            string userName,
            SecurityPermission permission,
            SecurityRight defaultRight)
        {
            return manager.CheckServerPermission(userName, permission);
        }
    }
}
