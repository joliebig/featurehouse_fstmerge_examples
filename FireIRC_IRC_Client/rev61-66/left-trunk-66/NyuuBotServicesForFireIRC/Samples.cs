using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using FireIRC.Extenciblility;
using FireIRC.Extenciblility.IRCClasses;
namespace NyuuBotServicesForFireIRC
{
    public class Test : IOnCommand
    {
        public void ExecuteOnCommand(IrcClient irc, ExClass exClass, Dictionary<string, string> PVars)
        {
            if (exClass.on == MessageForm.ChannelMessage)
            {
                if (exClass.message == "!test")
                {
                    irc.SendMessage(SendType.Message, exClass.chan, "Test Complete");
                }
            }
        }
    }
}
