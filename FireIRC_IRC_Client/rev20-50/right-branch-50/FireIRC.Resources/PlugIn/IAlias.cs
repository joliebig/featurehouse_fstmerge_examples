using System;
using System.Collections.Generic;
using System.Text;
using OVT.FireIRC.Resources.IRC;
namespace OVT.FireIRC.Resources.PlugIn
{
    public interface IAlias
    {
        void ExecuteAlias(string[] parms, IrcClient client);
    }
}
