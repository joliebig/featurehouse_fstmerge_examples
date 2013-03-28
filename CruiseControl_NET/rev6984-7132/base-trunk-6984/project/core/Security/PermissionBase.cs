using Exortech.NetReflector;
using System;
using System.Collections.Generic;
using System.Text;
using ThoughtWorks.CruiseControl.Core.Config;
using ThoughtWorks.CruiseControl.Remote.Security;
using System.ComponentModel;
namespace ThoughtWorks.CruiseControl.Core.Security
{
    public abstract class PermissionBase
        : Permissions, IConfigurationValidation
    {
        private string refId;
        private ISecurityManager manager;
        [ReflectorProperty("ref", Required = false)]
        public string RefId
        {
            get { return refId; }
            set { refId = value; }
        }
        public ISecurityManager Manager
        {
            get { return manager; }
            set { manager = value; }
        }
        public virtual bool CheckUser(ISecurityManager manager, string userName)
        {
            if (string.IsNullOrEmpty(refId))
            {
                return CheckUserActual(manager, userName);
            }
            else
            {
                IPermission refPermission = manager.RetrievePermission(refId);
                if (refPermission == null)
                {
                    throw new BadReferenceException(refId);
                }
                else
                {
                    return refPermission.CheckUser(manager, userName);
                }
            }
        }
        public virtual SecurityRight CheckPermission(ISecurityManager manager, SecurityPermission permission)
        {
            if (string.IsNullOrEmpty(refId))
            {
                return CheckPermissionActual(manager, permission);
            }
            else
            {
                IPermission refPermission = manager.RetrievePermission(refId);
                if (refPermission == null)
                {
                    throw new BadReferenceException(refId);
                }
                else
                {
                    return refPermission.CheckPermission(manager, permission);
                }
            }
        }
        public virtual void Validate(IConfiguration configuration, ConfigurationTrace parent, IConfigurationErrorProcesser errorProcesser)
        {
            if (!string.IsNullOrEmpty(refId))
            {
                IPermission refPermission = configuration.SecurityManager.RetrievePermission(refId);
                if (refPermission == null)
                {
                    errorProcesser.ProcessError(new BadReferenceException(refId));
                }
            }
        }
        protected abstract bool CheckUserActual(ISecurityManager manager, string userName);
        protected virtual SecurityRight CheckPermissionActual(ISecurityManager manager, SecurityPermission permission)
        {
            return GetPermission(permission);
        }
    }
}
