using System;
using System.Collections.Generic;
using System.Text;
using ThoughtWorks.CruiseControl.Remote.Security;
using Exortech.NetReflector;
using System.ComponentModel;
namespace ThoughtWorks.CruiseControl.Core.Security
{
    [ReflectorType("permissions")]
    public class Permissions
    {
        private SecurityRight defaultRight = SecurityRight.Inherit;
        private SecurityRight sendMessage = SecurityRight.Inherit;
        private SecurityRight forceAbortBuild = SecurityRight.Inherit;
        private SecurityRight startStopProject = SecurityRight.Inherit;
        private SecurityRight changeProjectConfiguration = SecurityRight.Inherit;
        private SecurityRight viewSecurity = SecurityRight.Inherit;
        private SecurityRight modifySecurity = SecurityRight.Inherit;
        private SecurityRight viewProject = SecurityRight.Inherit;
        private SecurityRight viewConfiguration = SecurityRight.Inherit;
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
            get { return forceAbortBuild; }
            set { forceAbortBuild = value; }
        }
        [ReflectorProperty("startProject", Required = false)]
        [DefaultValue(SecurityRight.Inherit)]
        public SecurityRight StartProjectRight
        {
            get { return startStopProject; }
            set { startStopProject = value; }
        }
        [ReflectorProperty("changeProject", Required = false)]
        [DefaultValue(SecurityRight.Inherit)]
        public SecurityRight ChangeProjectRight
        {
            get { return changeProjectConfiguration; }
            set { changeProjectConfiguration = value; }
        }
        [ReflectorProperty("viewSecurity", Required = false)]
        [DefaultValue(SecurityRight.Inherit)]
        public SecurityRight ViewSecurityRight
        {
            get { return viewSecurity; }
            set { viewSecurity = value; }
        }
        [ReflectorProperty("modifySecurity", Required = false)]
        [DefaultValue(SecurityRight.Inherit)]
        public SecurityRight ModifySecurityRight
        {
            get { return modifySecurity; }
            set { modifySecurity = value; }
        }
        [ReflectorProperty("viewProject", Required = false)]
        [DefaultValue(SecurityRight.Inherit)]
        public SecurityRight ViewProjectRight
        {
            get { return viewProject; }
            set { viewProject = value; }
        }
        [ReflectorProperty("viewConfiguration", Required = false)]
        [DefaultValue(SecurityRight.Inherit)]
        public SecurityRight ViewConfigurationRight
        {
            get { return viewConfiguration; }
            set { viewConfiguration = value; }
        }
        public SecurityRight GetPermission(SecurityPermission permission)
        {
            var right = SecurityRight.Inherit;
            switch (permission)
            {
                case SecurityPermission.ViewProject:
                    right = ViewProjectRight;
                    break;
                case SecurityPermission.ViewConfiguration:
                    right = ViewConfigurationRight;
                    break;
                case SecurityPermission.ForceAbortBuild:
                    right = ForceBuildRight;
                    break;
                case SecurityPermission.SendMessage:
                    right = SendMessageRight;
                    break;
                case SecurityPermission.StartStopProject:
                    right = StartProjectRight;
                    break;
                case SecurityPermission.ChangeProjectConfiguration:
                    right = ChangeProjectRight;
                    break;
                case SecurityPermission.ViewSecurity:
                    right = ViewSecurityRight;
                    break;
                case SecurityPermission.ModifySecurity:
                    right = ModifySecurityRight;
                    break;
            }
            if (right == SecurityRight.Inherit) right = DefaultRight;
            return right;
        }
    }
}
