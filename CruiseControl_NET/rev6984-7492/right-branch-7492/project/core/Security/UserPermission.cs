using Exortech.NetReflector;
using System;
using ThoughtWorks.CruiseControl.Remote.Security;
namespace ThoughtWorks.CruiseControl.Core.Security
{
    [ReflectorType("userPermission")]
    public class UserPermission
        : PermissionBase, IPermission
    {
        private string userName;
        public UserPermission() { }
        public UserPermission(string userName, SecurityRight defaultRight, SecurityRight sendMessage, SecurityRight forceBuild, SecurityRight startProject)
        {
            this.userName = userName;
            base.DefaultRight = defaultRight;
            base.SendMessageRight = sendMessage;
            base.ForceBuildRight = forceBuild;
            base.StartProjectRight = startProject;
        }
        public string Identifier
        {
            get { return userName; }
        }
        [ReflectorProperty("user", Required = true)]
        public string UserName
        {
            get { return userName; }
            set { userName = value; }
        }
        protected override bool CheckUserActual(ISecurityManager manager, string userName)
        {
            return string.Equals(userName, this.userName, StringComparison.InvariantCultureIgnoreCase);
        }
    }
}
