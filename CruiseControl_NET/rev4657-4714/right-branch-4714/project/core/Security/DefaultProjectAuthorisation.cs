using Exortech.NetReflector;
using System;
using System.Collections.Generic;
using System.Text;
using ThoughtWorks.CruiseControl.Core.Config;
using ThoughtWorks.CruiseControl.Remote.Security;
namespace ThoughtWorks.CruiseControl.Core.Security
{
    [ReflectorType("defaultProjectSecurity")]
    public class DefaultProjectAuthorisation
        : IProjectAuthorisation, IConfigurationValidation
    {
        private SecurityRight defaultRight = SecurityRight.Inherit;
        private IPermission[] permissions = new IPermission[0];
        public DefaultProjectAuthorisation() { }
        public DefaultProjectAuthorisation(SecurityRight defaultRight, params IPermission[] assertions)
        {
            this.defaultRight = defaultRight;
            this.permissions = assertions;
        }
        public bool RequiresSession
        {
            get { return true; }
        }
        [ReflectorProperty("defaultRight", Required=false)]
        public SecurityRight DefaultRight
        {
            get { return defaultRight; }
            set
            {
                if (value == SecurityRight.Inherit) throw new ArgumentOutOfRangeException("DefaultRight must be either Allow or Deny");
                defaultRight = value;
            }
        }
        [ReflectorProperty("permissions", Required = false)]
        public IPermission[] Permissions
        {
            get { return permissions; }
            set { permissions = value; }
        }
        public virtual bool CheckPermission(ISecurityManager manager, string userName, SecurityPermission permission)
        {
            SecurityRight currentRight = SecurityRight.Inherit;
            foreach (IPermission assertion in permissions)
            {
                if (assertion.CheckUser(manager, userName)) currentRight = assertion.CheckPermission(manager, permission);
                if (currentRight != SecurityRight.Inherit) break;
            }
            if (currentRight == SecurityRight.Inherit) currentRight = defaultRight;
            return (currentRight == SecurityRight.Allow);
        }
        public virtual void Validate(IConfiguration configuration)
        {
            foreach (IPermission permission in permissions)
            {
                if (permission is IConfigurationValidation)
                {
                    (permission as IConfigurationValidation).Validate(configuration);
                }
            }
        }
    }
}
