using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using FireIRC.Resources.IRC;
namespace FireIRC.Resources.GTK.PlugIn
{
    public interface IAlias
    {
        void ExecuteAlias(string[] parms, IrcClient client);
    }
}
