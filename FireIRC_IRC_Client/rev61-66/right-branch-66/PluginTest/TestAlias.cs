using System;
using System.Collections.Generic;
using System.Text;
using FireIRC.Extenciblility;
using System.Windows.Forms;
using FireIRC.Extenciblility.IRCClasses;
using OVT.FireIRC.Resources;
namespace PluginTest
{
    public class Test : IAlias, IOnCommand
    {
        public void ExecuteAlias(string[] parms, IrcClient client)
        {
            MessageBox.Show("Special Alias WOOT");
        }
        public void ExecuteOnCommand(IrcClient irc, ExClass exClass, Dictionary<string, string> PVars)
        {
            if (exClass.on == MessageForm.ChannelMessage)
            {
                if (exClass.message == "Test")
                {
                    if (exClass.nick == "DrHouse")
                    {
                        irc.SendMessage(SendType.Message, exClass.chan, "Test Successful");
                    }
                }
            }
        }
    }
    public class Fyou : IAlias
    {
        public void ExecuteAlias(string[] parms, IrcClient client)
        {
            client.SendMessage(SendType.Message, FireIRCCore.ActiveChannel, "ââââââ-/Â´Â¯/)");
            client.SendMessage(SendType.Message, FireIRCCore.ActiveChannel, "ââââââ/â/");
            client.SendMessage(SendType.Message, FireIRCCore.ActiveChannel, "âââââ- /â/");
            client.SendMessage(SendType.Message, FireIRCCore.ActiveChannel, "âââââ-/---/");
            client.SendMessage(SendType.Message, FireIRCCore.ActiveChannel, "âââ-/Â´Â¯/' --'/Â´Â¯Â¯`Â·Â¸");
            client.SendMessage(SendType.Message, FireIRCCore.ActiveChannel, "ââ-/'/--/â./.ââ/Â¨Â¯\\");
            client.SendMessage(SendType.Message, FireIRCCore.ActiveChannel, "ââ('(âÂ´âÂ´â Â¯./'â')");
            client.SendMessage(SendType.Message, FireIRCCore.ActiveChannel, "ââ-\\ââ--ââ-'â-/");
            client.SendMessage(SendType.Message, FireIRCCore.ActiveChannel, "ââ--\\ââââ _.Â·Â´");
            client.SendMessage(SendType.Message, FireIRCCore.ActiveChannel, "âââ\\ââââ----- Fuck You " + parms[1]);
        }
    }
    public class Pyou : IAlias
    {
        public void ExecuteAlias(string[] parms, IrcClient client)
        {
            client.SendMessage(SendType.Message, FireIRCCore.ActiveChannel, "ââââââ-/Â´Â¯/)");
            client.SendMessage(SendType.Message, FireIRCCore.ActiveChannel, "ââââââ/â/");
            client.SendMessage(SendType.Message, FireIRCCore.ActiveChannel, "âââââ- /â/");
            client.SendMessage(SendType.Message, FireIRCCore.ActiveChannel, "âââââ-/---/");
            client.SendMessage(SendType.Message, FireIRCCore.ActiveChannel, "âââ-/Â´Â¯/' --'/Â´Â¯Â¯`Â·Â¸");
            client.SendMessage(SendType.Message, FireIRCCore.ActiveChannel, "ââ-/'/--/â./.ââ/Â¨Â¯\\");
            client.SendMessage(SendType.Message, FireIRCCore.ActiveChannel, "ââ('(âÂ´âÂ´â Â¯./'â')");
            client.SendMessage(SendType.Message, FireIRCCore.ActiveChannel, "ââ-\\ââ--ââ-'â-/");
            client.SendMessage(SendType.Message, FireIRCCore.ActiveChannel, "ââ--\\ââââ _.Â·Â´");
            client.SendMessage(SendType.Message, FireIRCCore.ActiveChannel, "âââ\\ââââ----- Pack You " + parms[1]);
        }
    }
}
