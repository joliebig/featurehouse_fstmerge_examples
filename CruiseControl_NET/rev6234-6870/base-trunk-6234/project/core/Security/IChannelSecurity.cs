using System;
using System.Collections.Generic;
using System.Text;
namespace ThoughtWorks.CruiseControl.Core.Security
{
    public interface IChannelSecurity
    {
        void Validate(object channelInformation);
    }
}
