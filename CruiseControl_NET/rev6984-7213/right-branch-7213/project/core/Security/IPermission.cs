using System;
using ThoughtWorks.CruiseControl.Remote.Security;
namespace ThoughtWorks.CruiseControl.Core.Security
{
    public interface IPermission
        : ISecuritySetting
    {
        bool CheckUser(ISecurityManager manager, string userName);
        SecurityRight CheckPermission(ISecurityManager manager, SecurityPermission permission);
    }
}
