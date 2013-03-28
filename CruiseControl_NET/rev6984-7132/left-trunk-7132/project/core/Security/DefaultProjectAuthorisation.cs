using System;
using Exortech.NetReflector;
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
        [ReflectorProperty("defaultRight", Required = false)]
        public SecurityRight DefaultRight
        {
            get { return defaultRight; }
            set { defaultRight = value; }
        }
        [ReflectorProperty("permissions", Required = false)]
        public IPermission[] Permissions
        {
            get { return permissions; }
            set { permissions = value; }
        }
        public bool RequiresServerSecurity
        {
            get { return true; }
        }
        [ReflectorProperty("guest", Required = false)]
        public string GuestAccountName { get; set; }
        public bool RequiresSession(ISecurityManager manager)
        {
            return true;
        }
        public virtual bool CheckPermission(ISecurityManager manager,
            string userName,
            SecurityPermission permission,
            SecurityRight defaultRight)
        {
            SecurityRight currentRight = SecurityRight.Inherit;
            foreach (IPermission assertion in permissions)
            {
                if (assertion.CheckUser(manager, userName)) currentRight = assertion.CheckPermission(manager, permission);
                if (currentRight != SecurityRight.Inherit) break;
            }
            if (currentRight == SecurityRight.Inherit) currentRight = this.defaultRight;
            if (currentRight == SecurityRight.Inherit) currentRight = defaultRight;
            return (currentRight == SecurityRight.Allow);
        }
        public virtual void Validate(IConfiguration configuration, ConfigurationTrace parent, IConfigurationErrorProcesser errorProcesser)
        {
            foreach (IPermission permission in permissions)
            {
                if (permission is IConfigurationValidation)
                {
                    (permission as IConfigurationValidation).Validate(configuration, parent.Wrap(this), errorProcesser);
                }
            }
        }
    }
}
