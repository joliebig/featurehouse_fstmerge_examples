using System;
using System.Collections.Generic;
namespace ThoughtWorks.CruiseControl.Remote.Security
{
    [Serializable]
    public class SecurityCheckDiagnostics
    {
        private string permissionName;
        private string projectName;
        private string userName;
        private bool isAllowed;
        public string Permission
        {
            get { return permissionName; }
            set { permissionName = value; }
        }
        public string Project
        {
            get { return projectName; }
            set { projectName = value; }
        }
        public string User
        {
            get { return userName; }
            set { userName = value; }
        }
        public bool IsAllowed
        {
            get { return isAllowed; }
            set { isAllowed = value; }
        }
    }
}
