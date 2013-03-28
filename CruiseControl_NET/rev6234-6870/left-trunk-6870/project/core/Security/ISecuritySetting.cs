using System;
using System.Collections.Generic;
using System.Text;
namespace ThoughtWorks.CruiseControl.Core.Security
{
    public interface ISecuritySetting
    {
        string Identifier { get; }
        ISecurityManager Manager { get; set; }
    }
}
