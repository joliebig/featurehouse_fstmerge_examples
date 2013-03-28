using System;
using System.Collections.Generic;
using System.Text;
using FireIRC.Extenciblility.IRCClasses;
using System.IO;
using System.Xml.Serialization;
using FireIRC.Extenciblility;
using OVT.Melissa.PluginSupport;
using System.Reflection;
using System.Resources;
using System.Drawing;
using System.Windows.Forms;
namespace OVT.FireIRC.Resources
{
    public class FireIRCCore
    {
        static bool usingMonoMode = false;
        static Dictionary<string, Client> clients = new Dictionary<string, Client>();
        static MainForm primaryForm = new MainForm();
        static Formatter formatter = new Formatter();
        static SettingsContainer sc = new SettingsContainer();
        static Dictionary<string, IAlias> aliases = new Dictionary<string, IAlias>();
        static Dictionary<string, IOnCommand> onCommands = new Dictionary<string, IOnCommand>();
        static Dictionary<string, ImageTheme> themes = new Dictionary<string, ImageTheme>();
        static ExEngine extEngine = new ExEngine();
        public static ExEngine ExtEngine
        {
            get { return FireIRCCore.extEngine; }
            set { FireIRCCore.extEngine = value; }
        }
        static public string ActiveChannel
        {
            get { return GetActiveChannelWindow().Channel; }
        }
        static public string ActiveServer
        {
            get { return GetActiveChannelWindow().Server; }
        }
        public static Dictionary<string, ImageTheme> Themes
        {
            get { return FireIRCCore.themes; }
            set { FireIRCCore.themes = value; }
        }
        public static Dictionary<string, IOnCommand> OnCommands
        {
            get { return FireIRCCore.onCommands; }
            set { FireIRCCore.onCommands = value; }
        }
        public static Dictionary<string, IAlias> Aliases
        {
            get { return FireIRCCore.aliases; }
            set { FireIRCCore.aliases = value; }
        }
        public static bool UsingMonoMode
        {
            get { return FireIRCCore.usingMonoMode; }
            set { FireIRCCore.usingMonoMode = value; }
        }
        public static Settings Settings
        {
            get { return FireIRCCore.sc.Settings; }
        }
        public static Formatter Formatter
        {
            get { return FireIRCCore.formatter; }
            set { FireIRCCore.formatter = value; }
        }
        static public MainForm PrimaryForm
        {
            get { return primaryForm; }
            set { primaryForm = value; }
        }
        static public ToolStripItemCollection CommandsMenu
        {
            get { return PrimaryForm.CommandMenu.DropDownItems; }
        }
        static public Dictionary<string, Client> Clients
        {
            get { return clients; }
            set { clients = value; }
        }
        static public ChannelWindow GetChannel(string s, string c)
        {
            ChannelWindow ren = null;
            foreach (ChannelWindow i in primaryForm.Panel.Documents)
            {
                if (i.Server == s)
                {
                    if (i.Channel == c.ToUpper())
                    {
                        ren = i;
                        break;
                    }
                }
            }
            return ren;
        }
        static public ChannelWindow GetActiveChannelWindow()
        {
            return (ChannelWindow)primaryForm.ActiveMdiChild;
        }
        static public ChannelWindow DoesWindowExist(string s, string c)
        {
            return DoesWindowExist(s, c, "Channel");
        }
        static public ChannelWindow DoesWindowExist(string s, string c, string mode)
        {
            if (_DoesWindowExist(s, c) == false)
            {
                ChannelWindow cw = new ChannelWindow(s, c);
                cw.Text = c;
                cw.TabText = c;
                if (mode == "Query") { FireIRCCore.OpenedQuery(s, c); cw.IsQueryWindow = true; }
                else if (mode == "Channel") { FireIRCCore.JoinedChannel(s, c); }
                cw.Show(PrimaryForm.Panel);
                return cw;
            }
            else { return GetChannel(s, c); }
        }
        static private bool _DoesWindowExist(string s, string c)
        {
            bool ren = false;
            foreach (ChannelWindow i in primaryForm.Panel.Documents)
            {
                if (i.Server == s)
                {
                    if (i.Channel == c.ToUpper())
                    {
                        ren = true;
                        break;
                    }
                }
            }
            return ren;
        }
        static public void WindowsDisconnected(string s)
        {
            try
            {
                foreach (ChannelWindow i in FireIRCCore.PrimaryForm.Panel.Documents)
                {
                    if (i.Server == s)
                    {
                        i.IsConnected = false;
                    }
                }
            }
            catch { WindowsDisconnected(s); }
        }
        static public void CloseServerWindows(string s)
        {
            try
            {
                foreach (ChannelWindow i in FireIRCCore.PrimaryForm.Panel.Documents)
                {
                    if (i.Server == s)
                    {
                        i.Close();
                    }
                }
            }
            catch { CloseServerWindows(s); }
        }
        static public void NickChange(string s, string on, string nn)
        {
            foreach (ChannelWindow i in FireIRCCore.PrimaryForm.Panel.Documents)
            {
                if (i.Server == s)
                {
                    try
                    {
                        i.NickChanged(on, nn);
                        i.Sort();
                        MessageInfo p = new MessageInfo(null, String.Format(OVT.FireIRC.Resources.Resources.ResourceManagement.r.GetString("Client.NickChange"), on, nn));
                        i.Write(Formatter.Format(Formatter.MessageType.IrcCommand, p, Clients[s].IrcClient.Address,null));
                    }
                    catch (KeyNotFoundException) { }
                    catch (NullReferenceException) { }
                }
            }
        }
        static public void UserQuit(string s, string u,string quitreason)
        {
            foreach (ChannelWindow i in FireIRCCore.PrimaryForm.Panel.Documents)
            {
                if (i.Server == s)
                {
                    try
                    {
                        if (i.UserList.ContainsKey(u) == true)
                        {
                            i.UserQuit(u);
                            i.Sort();
                            MessageInfo p = new MessageInfo(null, String.Format(OVT.FireIRC.Resources.Resources.ResourceManagement.r.GetString("Client.Quit"), u, quitreason));
                            i.Write(Formatter.Format(Formatter.MessageType.IrcCommand, p, Clients[s].IrcClient.Address, null));
                        }
                    }
                    catch (KeyNotFoundException) { }
                    catch (NullReferenceException) { }
                }
            }
        }
        static public void ExecuteLine(string line)
        {
            if (line.Split(' ')[0] == "connect")
            {
            }
        }
        static public void ExecuteLine(string server, string line)
        {
            string[] parms = line.Split(' ');
            if (parms[0] == "join")
            {
                string[] channels = line.Remove(0, 5).Split(',');
                string[] keys = new string[channels.GetLength(0)];
                int integer = 0;
                foreach (string i in channels)
                {
                    string[] spl = i.Split(' ');
                    if (spl.GetLength(0) != 1)
                    {
                        keys[integer] = spl[1];
                        channels[integer] = i.Split(' ')[0];
                    }
                    integer++;
                }
                Clients[server].IrcClient.RfcJoin(channels, keys);
            }
            else if (parms[0] == "msg")
            {
                Clients[server].IrcClient.SendMessage(SendType.Message, parms[1], line.Remove(0, parms[0].Length + parms[1].Length + 2));
            }
            else if (parms[0] == "describe")
            {
                Clients[server].IrcClient.SendMessage(SendType.Action, parms[1], line.Remove(0, parms[0].Length + parms[1].Length + 2));
            }
            else if (parms[0] == "part")
            {
                GetChannel(server, parms[1]).Close();
            }
            else if (parms[0] == "nick")
            {
                Clients[server].IrcClient.RfcNick(parms[1]);
            }
            else if (parms[0] == "kick")
            {
                clients[server].IrcClient.RfcKick(parms[1], parms[2], parms[3]);
            }
            else if (parms[0] == "kickban")
            {
                clients[server].IrcClient.RfcKick(parms[1], parms[2], parms[3]);
                clients[server].IrcClient.Ban(parms[1], parms[2]);
            }
            else if (parms[0] == "ban")
            {
                if (parms[1] == "+")
                {
                    clients[server].IrcClient.Ban(parms[2], parms[3]);
                }
                else if (parms[1] == "-")
                {
                    clients[server].IrcClient.Unban(parms[2], parms[3]);
                }
            }
            else if (parms[0] == "raw")
            {
                Clients[server].IrcClient.WriteLine(line.Remove(0, parms[0].Length + 1));
            }
            else if (parms[0] == "echo")
            {
                FireIRCCore.GetChannel(parms[1], parms[2]).Write(line.Remove(0, parms[0].Length + parms[1].Length + parms[2].Length + 3));
            }
            else if (parms[0] == "mode")
            {
                Clients[server].IrcClient.RfcMode(parms[1], line.Remove(0, parms[0].Length + parms[1].Length + 2));
            }
            else if (parms[0] == "ctcp")
            {
                Clients[server].IrcClient.SendMessage(SendType.CtcpRequest, parms[1], line.Remove(0, parms[0].Length + parms[1].Length + 2).ToUpper());
            }
            else if (parms[0] == "voice")
            {
                if (parms[1] == "+")
                {
                    Clients[server].IrcClient.Voice(parms[2], parms[3]);
                }
                else if (parms[1] == "-")
                {
                    Clients[server].IrcClient.Devoice(parms[2], parms[3]);
                }
            }
            else if (parms[0] == "hop")
            {
                if (parms[1] == "+")
                {
                    Clients[server].IrcClient.Halfop(parms[2], parms[3], Priority.High);
                }
                else if (parms[1] == "-")
                {
                    Clients[server].IrcClient.Dehalfop(parms[2], parms[3]);
                }
            }
            else if (parms[0] == "op")
            {
                if (parms[1] == "+")
                {
                    Clients[server].IrcClient.Op(parms[2], parms[3]);
                }
                else if (parms[1] == "-")
                {
                    Clients[server].IrcClient.Deop(parms[2], parms[3]);
                }
            }
            else if (parms[0] == "whois")
            {
                Clients[server].IrcClient.RfcWhois(parms[1]);
            }
            else if (parms[0] == "help")
            {
            }
            else if (parms[0] == "ns")
            {
                Clients[server].IrcClient.WriteLine(line);
            }
            else if (parms[0] == "cs")
            {
                Clients[server].IrcClient.WriteLine(line);
            }
            else if (parms[0] == "bs")
            {
                Clients[server].IrcClient.WriteLine(line);
            }
            else if (parms[0] == "hs")
            {
                Clients[server].IrcClient.WriteLine(line);
            }
            else if (parms[0] == "oper")
            {
                Clients[server].IrcClient.WriteLine(line);
            }
            else
            {
                try { Aliases[parms[0]].ExecuteAlias(parms, Clients[server].IrcClient); }
                catch (KeyNotFoundException) { }
            }
        }
        static public void UserModeChange(string s, string c, string u)
        {
            GetChannel(s, c).MoveUser(u);
        }
        static public void ServerConnected(string s)
        {
            PrimaryForm.ServerBrowser.Add(s, s, "irc_server", "irc_server");
            PrimaryForm.ServerBrowser[s].Nodes.Add("Channels", "Channels", "folder_icon", "folder_icon");
            PrimaryForm.ServerBrowser[s].Nodes.Add("Queries", "Queries", "folder_icon", "folder_icon");
            if (PrimaryForm.ServerBrowser[s].IsExpanded == false)
            {
                PrimaryForm.ServerBrowser[s].ExpandAll();
            }
        }
        static public void ServerDisconnected(string s)
        {
            PrimaryForm.ServerBrowser.RemoveByKey(s);
        }
        static public void PartedChannel(string s, string c)
        {
            PrimaryForm.ServerBrowser[s].Nodes["Channels"].Nodes.RemoveByKey(c);
        }
        static public void ClosedQuery(string s, string c)
        {
            PrimaryForm.ServerBrowser[s].Nodes["Queries"].Nodes.RemoveByKey(c);
        }
        static public void JoinedChannel(string s, string c)
        {
            PrimaryForm.ServerBrowser[s].Nodes["Channels"].Nodes.Add(c, c, "irc_channel", "irc_channel");
            if (PrimaryForm.ServerBrowser[s].Nodes["Channels"].IsExpanded == false)
            {
                PrimaryForm.ServerBrowser[s].Nodes["Channels"].ExpandAll();
            }
        }
        static public void OpenedQuery(string s, string n)
        {
            PrimaryForm.ServerBrowser[s].Nodes["Queries"].Nodes.Add(n, n, "irc_query", "irc_query");
            if (PrimaryForm.ServerBrowser[s].Nodes["Queries"].IsExpanded == false)
            {
                PrimaryForm.ServerBrowser[s].Nodes["Queries"].ExpandAll();
            }
        }
        public static void ReadSettings()
        {
            sc.ReadSettings();
            FireIRCCore.Settings.MainFont = PropertyService.Get<Font>("fireirc.font", SystemFonts.DefaultFont);
            FireIRCCore.Settings.Background = PropertyService.Get<Color>("fireirc.backcolor", Color.White);
            FireIRCCore.Settings.Forground = PropertyService.Get<Color>("fireirc.forecolor", Color.Black);
        }
        public static void SaveSettings()
        {
            PropertyService.Set<Font>("fireirc.font", FireIRCCore.Settings.MainFont);
            PropertyService.Set<Color>("fireirc.backcolor", FireIRCCore.Settings.Background);
            PropertyService.Set<Color>("fireirc.forecolor", FireIRCCore.Settings.Forground);
            sc.SaveSettings();
        }
        public static void Setup()
        {
            if (AddInTree.ExistsTreeNode("/FireIRC/Aliases") == true)
            {
                foreach (Codon c in AddInTree.GetTreeNode("/FireIRC/Aliases").Codons)
                {
                    object a = c.AddIn.CreateObject(c.Properties["class"]);
                    try
                    {
                        Aliases.Add(c.Id, (IAlias)a);
                    }
                    catch (ArgumentException) { }
                }
            }
            if (AddInTree.ExistsTreeNode("/FireIRC/OnLaunch") == true)
            {
                foreach (Codon c in AddInTree.GetTreeNode("/FireIRC/OnLaunch").Codons)
                {
                    OnLaunch a = (OnLaunch)c.AddIn.CreateObject(c.Properties["class"]);
                    try
                    {
                        a.Run();
                    }
                    catch (ArgumentException) { }
                }
            }
            if (AddInTree.ExistsTreeNode("/FireIRC/OnCommands") == true)
            {
                foreach (Codon c in AddInTree.GetTreeNode("/FireIRC/OnCommands").Codons)
                {
                    object a = c.AddIn.CreateObject(c.Properties["class"]);
                    try
                    {
                        OnCommands.Add(c.Id, (IOnCommand)a);
                    }
                    catch (ArgumentException) { }
                }
            }
            if (AddInTree.ExistsTreeNode("/FireIRC/Themes") == true)
            {
                foreach (Codon c in AddInTree.GetTreeNode("/FireIRC/Themes").Codons)
                {
                    object a = c.AddIn.CreateObject(c.Properties["class"]);
                    try
                    {
                        Themes.Add(c.Id, (ImageTheme)a);
                    }
                    catch (ArgumentException) { }
                }
            }
        }
        internal static void SaveStream()
        {
        }
    }
}
