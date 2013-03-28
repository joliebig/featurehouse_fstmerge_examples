using System;
using System.Collections.Generic;
using System.Text;
using ThoughtWorks.CruiseControl.Remote.Security;
namespace ThoughtWorks.CruiseControl.Core.Security
{
    public interface IProjectAuthorisation
    {
        bool RequiresServerSecurity { get; }
        string GuestAccountName { get; }
        bool RequiresSession(ISecurityManager manager);
        bool CheckPermission(ISecurityManager manager,
            string userName,
            SecurityPermission permission,
            SecurityRight defaultRight);
    }
}
