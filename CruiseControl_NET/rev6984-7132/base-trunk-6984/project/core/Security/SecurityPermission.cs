using System;
using System.Collections.Generic;
using System.Text;
namespace ThoughtWorks.CruiseControl.Core.Security
{
    public enum SecurityPermission
    {
        SendMessage,
        ForceAbortBuild,
        StartStopProject,
        ChangeProjectConfiguration,
        ViewSecurity,
        ModifySecurity,
        ViewProject,
        ViewConfiguration,
    }
}
