using System;
using System.Collections.Generic;
using System.Text;
namespace ThoughtWorks.CruiseControl.Core.Security
{
    public abstract class ChannelSecurityInformation
    {
        public bool IsEncrypted { get; set; }
    }
}
