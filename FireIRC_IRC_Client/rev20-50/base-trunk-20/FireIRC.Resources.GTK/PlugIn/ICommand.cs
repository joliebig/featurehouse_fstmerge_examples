using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using FireIRC.Resources.IRC;
namespace FireIRC.Resources.GTK.PlugIn
{
    public interface ICommand
    {
        void ExecuteOnCommand(IrcClient irc, Client.ExClass exClass, Dictionary<string, string> PVars);
    }
}
