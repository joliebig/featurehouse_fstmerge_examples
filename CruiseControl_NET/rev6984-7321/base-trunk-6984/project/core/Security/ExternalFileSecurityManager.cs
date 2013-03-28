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
using System.IO;
using System.Xml;
using ThoughtWorks.CruiseControl.Remote.Messages;
namespace ThoughtWorks.CruiseControl.Core.Security
{
    [ReflectorType("externalFileSecurity")]
    public class ExternalFileSecurityManager
        : SecurityManagerBase, IConfigurationValidation
    {
        private const string CONFIG_ASSEMBLY_PATTERN = "ccnet.*.plugin.dll";
        private string[] files;
        private Dictionary<string, IAuthentication> loadedUsers;
        private List<IAuthentication> wildCardUsers;
        private Dictionary<string, IPermission> loadedPermissions;
        private bool isInitialised = false;
        private NetReflectorTypeTable typeTable;
        private NetReflectorReader reflectionReader;
        private Dictionary<string, string> settingFileMap;
  private readonly IExecutionEnvironment executionEnvironment;
  public ExternalFileSecurityManager() : this(new ExecutionEnvironment())
  {}
  public ExternalFileSecurityManager(IExecutionEnvironment executionEnvironment)
  {
   this.executionEnvironment = executionEnvironment;
  }
        [ReflectorProperty("files")]
        public string[] Files
        {
            get { return files; }
            set { files = value; }
        }
        public override void Initialise()
        {
            if (!isInitialised)
            {
                typeTable = new NetReflectorTypeTable();
                typeTable.Add(AppDomain.CurrentDomain);
                typeTable.Add(Directory.GetCurrentDirectory(), CONFIG_ASSEMBLY_PATTERN);
                typeTable.InvalidNode += delegate(InvalidNodeEventArgs args)
                {
                    throw new Exception(args.Message);
                };
                reflectionReader = new NetReflectorReader(typeTable);
                SessionCache.Initialise();
                loadedUsers = new Dictionary<string, IAuthentication>();
                wildCardUsers = new List<IAuthentication>();
                loadedPermissions = new Dictionary<string, IPermission>();
                settingFileMap = new Dictionary<string, string>();
                foreach (string fileName in files)
                {
                    LoadFile(fileName);
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
            }
            return setting;
        }
        public override List<UserDetails> ListAllUsers()
        {
            List<UserDetails> usersList = new List<UserDetails>();
            foreach (IAuthentication userDetails in loadedUsers.Values)
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
            foreach (IPermission permissionToCheck in loadedPermissions.Values)
            {
                if (permissionToCheck.CheckUser(this, userName)) currentRight = permissionToCheck.CheckPermission(this, permission);
                if (currentRight != SecurityRight.Inherit) break;
            }
            if (currentRight == SecurityRight.Inherit) currentRight = GetDefaultRight(permission);
            return (currentRight == SecurityRight.Allow);
        }
        public virtual void Validate(IConfiguration configuration, ConfigurationTrace parent, IConfigurationErrorProcesser errorProcesser)
        {
            var isInitialised = false;
            try
            {
                Initialise();
                isInitialised = true;
            }
            catch (Exception error)
            {
                errorProcesser.ProcessError(error);
            }
            if (isInitialised)
            {
                foreach (IAuthentication user in this.loadedUsers.Values)
                {
                    if (user is IConfigurationValidation)
                    {
                        (user as IConfigurationValidation).Validate(configuration, parent.Wrap(this), errorProcesser);
                    }
                }
                foreach (IPermission permission in this.loadedPermissions.Values)
                {
                    if (permission is IConfigurationValidation)
                    {
                        (permission as IConfigurationValidation).Validate(configuration, parent.Wrap(this), errorProcesser);
                    }
                }
            }
        }
        public override void ChangePassword(string sessionToken, string oldPassword, string newPassword)
        {
            string userName = GetUserName(sessionToken);
            if (string.IsNullOrEmpty(userName)) throw new SessionInvalidException();
            IAuthentication user = RetrieveUser(userName);
            if (user == null) throw new SessionInvalidException();
            LoginRequest credientals = new LoginRequest(userName);
            credientals.AddCredential(LoginRequest.PasswordCredential, oldPassword);
            if (!user.Authenticate(credientals))
            {
                LogEvent(null, userName, SecurityEvent.ChangePassword, SecurityRight.Deny, "Old password is incorrect");
                throw new SecurityException("Old password is incorrect");
            }
            LogEvent(null, userName, SecurityEvent.ChangePassword, SecurityRight.Allow, null);
            user.ChangePassword(newPassword);
            UpdateSetting(user);
        }
        public override void ResetPassword(string sessionToken, string userName, string newPassword)
        {
            string currentUser = GetUserName(sessionToken);
            if (string.IsNullOrEmpty(currentUser)) throw new SessionInvalidException();
            if (!CheckServerPermission(currentUser, SecurityPermission.ModifySecurity))
            {
                LogEvent(null, currentUser, SecurityEvent.ResetPassword, SecurityRight.Deny, null);
                throw new PermissionDeniedException("Reset password");
            }
            LogEvent(null, currentUser, SecurityEvent.ResetPassword, SecurityRight.Allow,
                string.Format("Reset password for '{0}'", userName));
            IAuthentication user = RetrieveUser(userName);
            if (user == null) throw new SessionInvalidException();
            user.ChangePassword(newPassword);
            UpdateSetting(user);
        }
        private void LoadFile(string fileName)
        {
            XmlDocument sourceDocument = new XmlDocument();
   sourceDocument.Load(executionEnvironment.EnsurePathIsRooted(fileName));
            foreach (XmlElement setting in sourceDocument.DocumentElement.SelectNodes("*"))
            {
                object loadedItem = reflectionReader.Read(setting);
                if (loadedItem is IPermission)
                {
                    IPermission permission = loadedItem as IPermission;
                    permission.Manager = this;
                    string identifier = permission.Identifier.ToLower(CultureInfo.InvariantCulture);
                    if (loadedPermissions.ContainsKey(identifier)) loadedPermissions.Remove(identifier);
                    loadedPermissions.Add(identifier, permission);
                    LinkIdentifierWithFile(fileName, identifier);
                }
                else if (loadedItem is IAuthentication)
                {
                    IAuthentication authentication = loadedItem as IAuthentication;
                    authentication.Manager = this;
                    string identifier = authentication.Identifier.ToLower(CultureInfo.InvariantCulture);
                    if (loadedUsers.ContainsKey(identifier)) loadedUsers.Remove(identifier);
                    if (authentication.Identifier.Contains("*"))
                    {
                        wildCardUsers.Add(authentication);
                    }
                    else
                    {
                        loadedUsers.Add(identifier, authentication);
                    }
                    LinkIdentifierWithFile(fileName, identifier);
                }
                else
                {
                    throw new Exception("Unknown security item: " + setting.OuterXml);
                }
            }
        }
        private void LinkIdentifierWithFile(string fileName, string identifier)
        {
            if (settingFileMap.ContainsKey(identifier))
            {
                settingFileMap[identifier] = fileName;
            }
            else
            {
                settingFileMap.Add(identifier, fileName);
            }
        }
        private void UpdateSetting(ISecuritySetting setting)
        {
            string fileName = settingFileMap[setting.Identifier];
            XmlDocument sourceDocument = new XmlDocument();
   sourceDocument.Load(executionEnvironment.EnsurePathIsRooted(fileName));
            foreach (XmlElement settingEl in sourceDocument.DocumentElement.SelectNodes("*"))
            {
                object loadedItem = reflectionReader.Read(settingEl);
                if (loadedItem is ISecuritySetting)
                {
                    string identifier = (loadedItem as ISecuritySetting).Identifier;
                    if (identifier == setting.Identifier)
                    {
                        StringWriter buffer = new StringWriter();
                        new ReflectorTypeAttribute(settingEl.Name).Write(new XmlTextWriter(buffer), setting);
                        XmlElement element = sourceDocument.CreateElement("changed");
                        element.InnerXml = buffer.ToString();
                        settingEl.ParentNode.ReplaceChild(element.FirstChild, settingEl);
                        break;
                    }
                }
            }
            sourceDocument.Save(fileName);
        }
    }
}
