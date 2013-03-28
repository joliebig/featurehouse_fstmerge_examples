using Exortech.NetReflector;
using System;
using ThoughtWorks.CruiseControl.Remote.Security;
namespace ThoughtWorks.CruiseControl.Core.Security
{
    [ReflectorType("rolePermission")]
    public class RolePermission
        : PermissionBase, IPermission
    {
        private string roleName;
        private UserName[] users = new UserName[0];
        public RolePermission() { }
        public RolePermission(string roleName, SecurityRight defaultRight, SecurityRight sendMessage, SecurityRight forceBuild, SecurityRight startProject, params UserName[] users)
        {
            this.roleName = roleName;
            base.DefaultRight = defaultRight;
            base.SendMessageRight = sendMessage;
            base.ForceBuildRight = forceBuild;
            base.StartProjectRight = startProject;
            this.users = users;
        }
        public string Identifier
        {
            get { return roleName; }
        }
        [ReflectorProperty("name", Required = true)]
        public string RoleName
        {
            get { return roleName; }
            set { roleName = value; }
        }
        [ReflectorArray("users", Required=false)]
        public UserName[] Users
        {
            get { return users; }
            set { users = value; }
        }
        protected override bool CheckUserActual(ISecurityManager manager, string userName)
        {
            bool userFound = false;
            foreach (UserName user in users)
            {
                if (string.Equals(userName, user.Name, StringComparison.InvariantCultureIgnoreCase))
                {
                    userFound = true;
                    break;
                }
            }
            return userFound;
        }
    }
}
