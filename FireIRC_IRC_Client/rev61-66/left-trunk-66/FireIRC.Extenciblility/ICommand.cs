using System;
using System.Collections.Generic;
using System.Text;
using FireIRC.Extenciblility.IRCClasses;
namespace FireIRC.Extenciblility
{
    public interface IOnCommand
    {
        void ExecuteOnCommand(IrcClient irc, ExClass exClass, Dictionary<string, string> PVars);
    }
}
