using System;
using System.Collections.Generic;
using System.Text;
using FireIRC.Extenciblility.IRCClasses;
namespace FireIRC.Extenciblility
{
    public interface IAlias
    {
        void ExecuteAlias(string[] parms, IrcClient client);
    }
}
