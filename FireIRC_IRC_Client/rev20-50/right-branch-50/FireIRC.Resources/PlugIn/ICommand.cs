using System;
using System.Collections.Generic;
using System.Text;
using OVT.FireIRC.Resources.IRC;
namespace OVT.FireIRC.Resources.PlugIn
{
    public interface IOnCommand
    {
        void ExecuteOnCommand(IrcClient irc, Client.ExClass exClass, Dictionary<string, string> PVars);
    }
}
