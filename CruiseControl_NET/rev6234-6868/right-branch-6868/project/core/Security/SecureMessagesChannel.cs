using Exortech.NetReflector;
using System;
namespace ThoughtWorks.CruiseControl.Core.Security
{
    [ReflectorType("encryptedChannel")]
    public class SecureMessagesChannel
        : IChannelSecurity
    {
        public virtual void Validate(object channelInformation)
        {
            var information = channelInformation as ChannelSecurityInformation;
            if (information == null) throw new SecurityException("Communications channel does not have any security");
            if (!information.IsEncrypted) throw new SecurityException("Message was not encrypted");
        }
    }
}
