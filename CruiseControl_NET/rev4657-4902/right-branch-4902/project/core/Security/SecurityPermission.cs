using System;
using System.Collections.Generic;
using System.Text;
namespace ThoughtWorks.CruiseControl.Core.Security
{
    public enum SecurityPermission
    {
        SendMessage,
        ForceBuild,
        StartProject,
        StopProject,
        ViewSecurity,
    }
}
