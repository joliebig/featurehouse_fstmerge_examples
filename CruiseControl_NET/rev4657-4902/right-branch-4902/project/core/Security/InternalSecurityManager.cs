using Exortech.NetReflector;
using System;
using System.Collections.Generic;
using System.Text;
using System.Text.RegularExpressions;
using ThoughtWorks.CruiseControl.Core.Util;
using ThoughtWorks.CruiseControl.Remote;
using ThoughtWorks.CruiseControl.Remote.Security;
using ThoughtWorks.CruiseControl.Core.Security.Auditing;
using ThoughtWorks.CruiseControl.Core.Config;
using System.Globalization;
namespace ThoughtWorks.CruiseControl.Core.Security
{
    [ReflectorType("internalSecurity")]
    public class InternalSecurityManager
        : SecurityManagerBase, IConfigurationValidation
    {
        private IAuthentication[] users;
        private Dictionary<string, IAuthentication> loadedUsers;
        private List<IAuthentication> wildCardUsers;
        private IPermission[] permissions;
        private Dictionary<string, IPermission> loadedPermissions;
        private bool isInitialised = false;
        [ReflectorProperty("users")]
        public IAuthentication[] Users
        {
            get { return users; }
            set { users = value; }
        }
        [ReflectorProperty("permissions")]
        public IPermission[] Permissions
        {
            get { return permissions; }
            set { permissions = value; }
        }
        public override void Initialise()
        {
            SessionCache.Initialise();
            loadedUsers = new Dictionary<string, IAuthentication>();
            wildCardUsers = new List<IAuthentication>();
            if (users != null)
            {
                foreach (IAuthentication user in users)
                {
                    user.Manager = this;
                    if (user.Identifier.Contains("*"))
                    {
                        wildCardUsers.Add(user);
                    }
                    else
                    {
                        loadedUsers.Add(user.Identifier.ToLower(CultureInfo.InvariantCulture), user);
                    }
                }
            }
            loadedPermissions = new Dictionary<string, IPermission>();
            if (permissions != null)
            {
                foreach (IPermission permission in permissions)
                {
                    permission.Manager = this;
                    loadedPermissions.Add(permission.Identifier.ToLower(CultureInfo.InvariantCulture), permission);
                }
            }
            isInitialised = true;
        }
        public override IAuthentication RetrieveUser(string identifier)
        {
            IAuthentication setting = null;
            if (!string.IsNullOrEmpty(identifier))
            {
                if (isInitialised)
                {
                    identifier = identifier.ToLower(CultureInfo.InvariantCulture);
                    if ((setting == null) && (loadedUsers.ContainsKey(identifier)))
                    {
                        setting = loadedUsers[identifier];
                    }
                    if (setting == null)
                    {
                        foreach (IAuthentication wildCard in wildCardUsers)
                        {
                            if (SecurityHelpers.IsWildCardMatch(wildCard.Identifier, identifier))
                            {
                                setting = wildCard;
                                break;
                            }
                        }
                    }
                }
                else
                {
                    if (setting == null)
                    {
                        foreach (IAuthentication securitySetting in users)
                        {
                            if (securitySetting.Identifier.Equals(identifier, StringComparison.InvariantCultureIgnoreCase))
                            {
                                setting = securitySetting;
                                break;
                            }
                            else if (securitySetting.Identifier.Contains("*"))
                            {
                                if (SecurityHelpers.IsWildCardMatch(securitySetting.Identifier, identifier))
                                {
                                    setting = securitySetting;
                                    break;
                                }
                            }
                        }
                    }
                }
            }
            return setting;
        }
        public override IPermission RetrievePermission(string identifier)
        {
            IPermission setting = null;
            if (!string.IsNullOrEmpty(identifier))
            {
                if (isInitialised)
                {
                    identifier = identifier.ToLower(CultureInfo.InvariantCulture);
                    if (loadedPermissions.ContainsKey(identifier))
                    {
                        setting = loadedPermissions[identifier];
                    }
                }
                else
                {
                    foreach (IPermission securitySetting in permissions)
                    {
                        if (securitySetting.Identifier.Equals(identifier, StringComparison.InvariantCultureIgnoreCase))
                        {
                            setting = securitySetting;
                            break;
                        }
                    }
                }
            }
            return setting;
        }
        public override List<UserDetails> ListAllUsers()
        {
            List<UserDetails> usersList = new List<UserDetails>();
            foreach (IAuthentication userDetails in users)
            {
                UserDetails user = new UserDetails();
                user.UserName = userDetails.UserName;
                user.DisplayName = userDetails.DisplayName;
                user.Type = userDetails.AuthenticationName;
                usersList.Add(user);
            }
            return usersList;
        }
        public override bool CheckServerPermission(string userName, SecurityPermission permission)
        {
            SecurityRight currentRight = SecurityRight.Inherit;
            foreach (IPermission permissionToCheck in permissions)
            {
                if (permissionToCheck.CheckUser(this, userName)) currentRight = permissionToCheck.CheckPermission(this, permission);
                if (currentRight != SecurityRight.Inherit) break;
            }
            if (currentRight == SecurityRight.Inherit) currentRight = DefaultRight;
            return (currentRight == SecurityRight.Allow);
        }
        public virtual void Validate(IConfiguration configuration)
        {
            foreach (IAuthentication user in users)
            {
                if (user is IConfigurationValidation) (user as IConfigurationValidation).Validate(configuration);
            }
            foreach (IPermission permission in permissions)
            {
                if (permission is IConfigurationValidation) (permission as IConfigurationValidation).Validate(configuration);
            }
        }
    }
}
