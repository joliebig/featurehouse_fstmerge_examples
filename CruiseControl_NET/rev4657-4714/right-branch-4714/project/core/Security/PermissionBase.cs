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
        : IConfigurationValidation
    {
        private string refId;
        private SecurityRight defaultRight = SecurityRight.Inherit;
        private SecurityRight sendMessage = SecurityRight.Inherit;
        private SecurityRight forceBuild = SecurityRight.Inherit;
        private SecurityRight startProject = SecurityRight.Inherit;
        private SecurityRight stopProject = SecurityRight.Inherit;
        private SecurityRight viewSecurity = SecurityRight.Inherit;
        private ISecurityManager manager;
        [ReflectorProperty("ref", Required = false)]
        public string RefId
        {
            get { return refId; }
            set { refId = value; }
        }
        [ReflectorProperty("defaultRight", Required = false)]
        [DefaultValue(SecurityRight.Inherit)]
        public SecurityRight DefaultRight
        {
            get { return defaultRight; }
            set { defaultRight = value; }
        }
        [ReflectorProperty("sendMessage", Required = false)]
        [DefaultValue(SecurityRight.Inherit)]
        public SecurityRight SendMessageRight
        {
            get { return sendMessage; }
            set { sendMessage = value; }
        }
        [ReflectorProperty("forceBuild", Required = false)]
        [DefaultValue(SecurityRight.Inherit)]
        public SecurityRight ForceBuildRight
        {
            get { return forceBuild; }
            set { forceBuild = value; }
        }
        [ReflectorProperty("startProject", Required = false)]
        [DefaultValue(SecurityRight.Inherit)]
        public SecurityRight StartProjectRight
        {
            get { return startProject; }
            set { startProject = value; }
        }
        [ReflectorProperty("stopProject", Required = false)]
        [DefaultValue(SecurityRight.Inherit)]
        public SecurityRight StopProjectRight
        {
            get { return stopProject; }
            set { stopProject = value; }
        }
        [ReflectorProperty("viewSecurity", Required = false)]
        [DefaultValue(SecurityRight.Inherit)]
        public SecurityRight ViewSecurityRight
        {
            get { return viewSecurity; }
            set { viewSecurity = value; }
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
        public virtual void Validate(IConfiguration configuration)
        {
            if (!string.IsNullOrEmpty(refId))
            {
                IPermission refPermission = configuration.SecurityManager.RetrievePermission(refId);
                if (refPermission == null)
                {
                    throw new BadReferenceException(refId);
                }
            }
        }
        protected abstract bool CheckUserActual(ISecurityManager manager, string userName);
        protected virtual SecurityRight CheckPermissionActual(ISecurityManager manager, SecurityPermission permission)
        {
            switch (permission)
            {
                case SecurityPermission.ForceBuild:
                    return forceBuild;
                case SecurityPermission.SendMessage:
                    return sendMessage;
                case SecurityPermission.StopProject:
                    return stopProject;
                case SecurityPermission.StartProject:
                    return startProject;
                case SecurityPermission.ViewSecurity:
                    return viewSecurity;
                default:
                    return defaultRight;
            }
        }
    }
}
