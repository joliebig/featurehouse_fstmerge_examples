using System;
using System.Collections.Generic;
using System.Text;
namespace ThoughtWorks.CruiseControl.Core.Security
{
    public interface IProjectAuthorisation
    {
        bool RequiresSession { get; }
        bool CheckPermission(ISecurityManager manager, string userName, SecurityPermission permission);
    }
}
