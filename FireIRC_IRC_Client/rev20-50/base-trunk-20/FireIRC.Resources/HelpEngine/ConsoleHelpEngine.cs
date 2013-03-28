using System;
using System.Collections.Generic;
using System.Text;
using OVT.FireIRC.Resources.PlugIn;
namespace OVT.FireIRC.Resources.HelpEngine
{
    public class ConsoleHelpEngine
    {
        public static void ShowHelp(string[] parms)
        {
            FireIRCCore.GetActiveChannelWindow().Write("=========================================================");
            FireIRCCore.GetActiveChannelWindow().Write("FireIRC IRC Client Help");
            FireIRCCore.GetActiveChannelWindow().Write("=========================================================");
            try
            {
                if (parms[1] == "/join") { FireIRCCore.GetActiveChannelWindow().Write("</join [Channel 1],{Channel 2}, {...}> This is a standard IRC command for joining a channel."); }
                else if (parms[1] == "/msg") { FireIRCCore.GetActiveChannelWindow().Write("</msg [Channel] [Message]> "); }
                else if (parms[1] == "/describe") { FireIRCCore.GetActiveChannelWindow().Write("</describe [Channel] [Message]> "); }
                else if (parms[1] == "/part") { FireIRCCore.GetActiveChannelWindow().Write("</part [Channel]> "); }
                else if (parms[1] == "/nick") { FireIRCCore.GetActiveChannelWindow().Write("</nick [New Nick]> "); }
                else if (parms[1] == "/kick") { FireIRCCore.GetActiveChannelWindow().Write("</kick [Channel] [User] [Reason]> "); }
                else if (parms[1] == "/kickban") { FireIRCCore.GetActiveChannelWindow().Write("</kickban [Channel] [User] [Reason]> "); }
                else if (parms[1] == "/ban") { FireIRCCore.GetActiveChannelWindow().Write("</ban [+|-] [Channel] [Mask]> "); }
                else if (parms[1] == "/raw") { FireIRCCore.GetActiveChannelWindow().Write("</raw [Raw Server Command]> "); }
                else if (parms[1] == "/echo") { FireIRCCore.GetActiveChannelWindow().Write("</echo [Server] [Window Name] [Text]> "); }
                else if (parms[1] == "/mode") { FireIRCCore.GetActiveChannelWindow().Write("</mode [Channel] [Modes]> "); }
                else if (parms[1] == "/voice") { FireIRCCore.GetActiveChannelWindow().Write("</voice [+|-] [Channel] [User]> "); }
                else if (parms[1] == "/hop") { FireIRCCore.GetActiveChannelWindow().Write("</hop [+|-] [Channel] [User]> "); }
                else if (parms[1] == "/op") { FireIRCCore.GetActiveChannelWindow().Write("</op [+|-] [Channel] [User]> "); }
            }
            catch (IndexOutOfRangeException)
            {
                FireIRCCore.GetActiveChannelWindow().Write("Commands");
                FireIRCCore.GetActiveChannelWindow().Write("/join");
                FireIRCCore.GetActiveChannelWindow().Write("/msg");
                FireIRCCore.GetActiveChannelWindow().Write("/describe");
                FireIRCCore.GetActiveChannelWindow().Write("/part");
                FireIRCCore.GetActiveChannelWindow().Write("/nick");
                FireIRCCore.GetActiveChannelWindow().Write("/kick");
                FireIRCCore.GetActiveChannelWindow().Write("/kickban");
                FireIRCCore.GetActiveChannelWindow().Write("/ban");
                FireIRCCore.GetActiveChannelWindow().Write("/raw");
                FireIRCCore.GetActiveChannelWindow().Write("/echo");
                FireIRCCore.GetActiveChannelWindow().Write("/mode");
                FireIRCCore.GetActiveChannelWindow().Write("/voice");
                FireIRCCore.GetActiveChannelWindow().Write("/hop");
                FireIRCCore.GetActiveChannelWindow().Write("/op");
                foreach (KeyValuePair<string, IAlias> i in FireIRCCore.Aliases)
                {
                    FireIRCCore.GetActiveChannelWindow().Write("/" + i.Key);
                }
            }
            FireIRCCore.GetActiveChannelWindow().Write("=========================================================");
            FireIRCCore.GetActiveChannelWindow().Write("For more help on a command type /help /[command]");
            FireIRCCore.GetActiveChannelWindow().Write("=========================================================");
        }
    }
}
