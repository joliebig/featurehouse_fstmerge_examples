using System;
using System.Collections.Generic;
using System.Text;
using OVT.FireIRC.Resources.IRC;
namespace OVT.FireIRC.Resources.PlugIn
{
    public class FireIRCExtend
    {
        public virtual void ExecuteOnCommand(IrcClient irc, Client.ExClass exClass, Dictionary<string, string> PVars) { }
        public virtual void ExecuteAlias(string[] parms, IrcClient client) { }
    }
}
