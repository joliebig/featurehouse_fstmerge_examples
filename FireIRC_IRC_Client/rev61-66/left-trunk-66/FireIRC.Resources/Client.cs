using System;
using System.Collections.Generic;
using System.Text;
using FireIRC.Extenciblility.IRCClasses;
using System.Threading;
using FireIRC.Extenciblility;
using System.Text.RegularExpressions;
using OVT.Melissa.SharpDevelop;
using OVT.Melissa.PluginSupport;
using System.Collections.Specialized;
using System.Windows.Forms;
using OVT.FireIRC.Resources.Resources;
namespace OVT.FireIRC.Resources
{
    public class Client
    {
        IrcClient client = new IrcClient();
        Perform pd;
        NickInformation ninfo;
        public NickInformation Ninfo
        {
            get { return ninfo; }
            set { ninfo = value; }
        }
        public Thread _;
        public StringCollection accepted = new StringCollection();
        public StringCollection denied = new StringCollection();
        string[] nicks;
        public string[] Nicks
        {
            get { return nicks; }
            set { nicks = value; }
        }
        public IrcClient IrcClient
        {
            get { return client; }
            set { client = value; }
        }
        public Client(string server, int port, string[] nick, string usrname, string name, Perform p, NickInformation n)
        {
            ninfo = n;
            ChannelWindow w = new ChannelWindow(server, "server");
            pd = p;
            client.SupportNonRfc = true;
            client.ActiveChannelSyncing = true;
            client.CtcpVersion = "Shion Version: " + VersionInformation.ToString() + " (" + Environment.OSVersion.ToString() + ")";
            client.OnErrorMessage += new IrcEventHandler(client_OnErrorMessage);
            client.OnChannelAction += new ActionEventHandler(client_OnChannelAction);
            client.OnChannelMessage += new IrcEventHandler(client_OnChannelMessage);
            client.OnChannelNotice += new IrcEventHandler(client_OnChannelNotice);
            client.OnQueryAction += new ActionEventHandler(client_OnQueryAction);
            client.OnQueryMessage += new IrcEventHandler(client_OnQueryMessage);
            client.OnQueryNotice += new IrcEventHandler(client_OnQueryNotice);
            client.OnTopic += new TopicEventHandler(client_OnTopic);
            client.OnTopicChange += new TopicChangeEventHandler(client_OnTopicChange);
            client.OnOp += new OpEventHandler(client_OnOp);
            client.OnDeop += new DeopEventHandler(client_OnDeop);
            client.OnHalfop += new HalfopEventHandler(client_OnHalfop);
            client.OnDehalfop += new DehalfopEventHandler(client_OnDehalfop);
            client.OnVoice += new VoiceEventHandler(client_OnVoice);
            client.OnDevoice += new DevoiceEventHandler(client_OnDevoice);
            client.OnInvite += new InviteEventHandler(client_OnInvite);
            client.OnJoin += new JoinEventHandler(client_OnJoin);
            client.OnPart += new PartEventHandler(client_OnPart);
            client.OnKick += new KickEventHandler(client_OnKick);
            client.OnBan += new BanEventHandler(client_OnBan);
            client.OnUnban += new UnbanEventHandler(client_OnUnban);
            client.OnNames += new NamesEventHandler(client_OnNames);
            client.OnMotd += new MotdEventHandler(client_OnMotd);
            client.OnNickChange += new NickChangeEventHandler(client_OnNickChange);
            client.OnConnected += new EventHandler(client_OnConnected);
            client.OnQuit += new QuitEventHandler(client_OnQuit);
            client.OnRegistered += new EventHandler(client_OnRegistered);
            client.OnChannelModeChange += new IrcEventHandler(client_OnChannelModeChange);
            client.OnWriteLine += new WriteLineEventHandler(client_OnWriteLine);
            client.OnRawMessage += new IrcEventHandler(client_OnRawMessage);
            client.OnCtcpReply += new CtcpEventHandler(client_OnCtcpReply);
            client.Connect(server, port);
            client.Login(nick, name, 0, usrname);
            w.Text = client.Address;
            w.TabText = client.Address;
            w.IsServerWindow = true;
            w.Show(FireIRCCore.PrimaryForm.Panel);
            FireIRCCore.ServerConnected(server);
            _ = new Thread(new ThreadStart(client.Listen));
            _.Start();
            nicks = nick;
        }
        void client_OnCtcpReply(object sender, CtcpEventArgs e)
        {
            FireIRCCore.PrimaryForm.Invoke(new Action(delegate
            {
                MessageInfo i = new MessageInfo(e.Data.Nick, e.CtcpParameter);
                FireIRCCore.GetActiveChannelWindow().Write(FireIRCCore.Formatter.Format(Formatter.MessageType.Message, i, client.Address, "server"));
            }));
        }
        void client_OnRawMessage(object sender, IrcEventArgs e)
        {
            try
            {
                FireIRCCore.PrimaryForm.Invoke(new Action(delegate
                {
                    if (e.Data.ReplyCode == ReplyCode.WhoIsUser)
                    {
                        MessageInfo i = new MessageInfo(null, e.Data.RawMessage.Remove(0, e.Data.RawMessageArray[0].Length + e.Data.RawMessageArray[1].Length + e.Data.RawMessageArray[2].Length + e.Data.RawMessageArray[3].Length + 4));
                        FireIRCCore.GetActiveChannelWindow().Write(FireIRCCore.Formatter.Format(Formatter.MessageType.WhoIs, i, client.Address, "server"));
                    }
                    else if (e.Data.ReplyCode == ReplyCode.WhoIsServer)
                    {
                        MessageInfo i = new MessageInfo(null, e.Data.RawMessage.Remove(0, e.Data.RawMessageArray[0].Length + e.Data.RawMessageArray[1].Length + e.Data.RawMessageArray[2].Length + e.Data.RawMessageArray[3].Length + 4));
                        FireIRCCore.GetActiveChannelWindow().Write(FireIRCCore.Formatter.Format(Formatter.MessageType.WhoIs, i, client.Address, "server"));
                    }
                    else if (e.Data.ReplyCode == ReplyCode.WhoIsOperator)
                    {
                        MessageInfo i = new MessageInfo(null, e.Data.RawMessage.Remove(0, e.Data.RawMessageArray[0].Length + e.Data.RawMessageArray[1].Length + e.Data.RawMessageArray[2].Length + e.Data.RawMessageArray[3].Length + 4));
                        FireIRCCore.GetActiveChannelWindow().Write(FireIRCCore.Formatter.Format(Formatter.MessageType.WhoIs, i, client.Address, "server"));
                    }
                    else if (e.Data.ReplyCode == ReplyCode.WhoIsIdle)
                    {
                        MessageInfo i = new MessageInfo(null, e.Data.RawMessage.Remove(0, e.Data.RawMessageArray[0].Length + e.Data.RawMessageArray[1].Length + e.Data.RawMessageArray[2].Length + e.Data.RawMessageArray[3].Length + 4));
                        FireIRCCore.GetActiveChannelWindow().Write(FireIRCCore.Formatter.Format(Formatter.MessageType.WhoIs, i, client.Address, "server"));
                    }
                    else if (e.Data.ReplyCode == ReplyCode.WhoIsChannels)
                    {
                        MessageInfo i = new MessageInfo(null, e.Data.RawMessage.Remove(0, e.Data.RawMessageArray[0].Length + e.Data.RawMessageArray[1].Length + e.Data.RawMessageArray[2].Length + e.Data.RawMessageArray[3].Length + 4));
                        FireIRCCore.GetActiveChannelWindow().Write(FireIRCCore.Formatter.Format(Formatter.MessageType.WhoIs, i, client.Address, "server"));
                    }
                }));
            }
            catch (InvalidOperationException) { }
        }
        void client_OnErrorMessage(object sender, IrcEventArgs e)
        {
            try
            {
                FireIRCCore.PrimaryForm.Invoke(new Action(delegate
                {
                    MessageInfo mi = new MessageInfo(null, e.Data.Message);
                    FireIRCCore.GetActiveChannelWindow().Write(FireIRCCore.Formatter.Format(Formatter.MessageType.IrcError, mi, client.Address, "Server"));
                }));
            }
            catch (InvalidOperationException) { }
        }
        void client_OnWriteLine(object sender, WriteLineEventArgs e)
        {
            try
            {
                FireIRCCore.PrimaryForm.Invoke(new Action(delegate
                {
                    string[] split = e.Line.Split(' ');
                    if (split[0] == "PRIVMSG")
                    {
                        string where = split[1];
                        split[0] = "";
                        split[1] = "";
                        string blah = String.Format("[{0}] {1}", where, String.Join(" ", split).Remove(0, 3));
                        MessageInfo mi = new MessageInfo(client.Nickname, blah);
                        FireIRCCore.GetActiveChannelWindow().Write(FireIRCCore.Formatter.Format(Formatter.MessageType.MessageOut, mi, client.Address, where));
                    }
                    else if (split[0] == "NOTICE")
                    {
                        string where = split[1];
                        split[0] = "";
                        split[1] = "";
                        string blah = String.Format("[{0}] {1}", where, String.Join(" ", split).Remove(0, 3));
                        MessageInfo mi = new MessageInfo(client.Nickname, blah);
                        FireIRCCore.GetActiveChannelWindow().Write(FireIRCCore.Formatter.Format(Formatter.MessageType.MessageOut, mi, client.Address, where));
                    }
                }));
            }
            catch (NullReferenceException) { }
            catch (InvalidOperationException) { }
        }
        void client_OnChannelModeChange(object sender, IrcEventArgs e)
        {
            if (FireIRCCore.UsingMonoMode == false)
            {
                FireIRCCore.PrimaryForm.Invoke(new Action(delegate
                {
                    FireIRCCore.GetChannel(client.Address, e.Data.Channel).ChannelModes = e.Data.Message;
                }));
            }
        }
        void client_OnRegistered(object sender, EventArgs e)
        {
            foreach (string i in pd.Perf)
            {
                string line;
                if (i.ToCharArray()[0] == '/') { line = i.Substring(1); }
                else { line = i; }
                FireIRCCore.ExecuteLine(client.Address, line);
            }
            IrcClient.SendMessage(SendType.Message, "NickServ", "identify " + Ninfo.NickServPass);
        }
        void client_OnQuit(object sender, QuitEventArgs e)
        {
            if (FireIRCCore.UsingMonoMode == false)
            {
                FireIRCCore.PrimaryForm.Invoke(new Action(delegate
                {
                    FireIRCCore.UserQuit(client.Address, e.Who, e.QuitMessage);
                }));
            }
        }
        void client_OnConnected(object sender, EventArgs e)
        {
            AppDomain.CurrentDomain.UnhandledException += new UnhandledExceptionEventHandler(ExceptionHandler.CurrentDomain_UnhandledException);
        }
        void client_OnNickChange(object sender, NickChangeEventArgs e)
        {
            if (FireIRCCore.UsingMonoMode == false)
            {
                FireIRCCore.PrimaryForm.Invoke(new Action(delegate
                {
                    FireIRCCore.NickChange(client.Address, e.OldNickname, e.NewNickname);
                }));
            }
        }
        void client_OnMotd(object sender, MotdEventArgs e)
        {
            if (FireIRCCore.UsingMonoMode == false)
            {
                FireIRCCore.PrimaryForm.Invoke(new Action(delegate
                {
                    FireIRCCore.GetChannel(client.Address, "server").Write(e.MotdMessage);
                }));
            }
        }
        void client_OnNames(object sender, NamesEventArgs e)
        {
            if (FireIRCCore.UsingMonoMode == false)
            {
                FireIRCCore.PrimaryForm.Invoke(new Action(delegate
                {
                    FireIRCCore.GetChannel(client.Address, e.Channel).NameList(e.UserList);
                }));
            }
        }
        void client_OnUnban(object sender, UnbanEventArgs e)
        {
            if (FireIRCCore.UsingMonoMode == false)
            {
                FireIRCCore.PrimaryForm.Invoke(new Action(delegate
                {
                    MessageInfo mi = new MessageInfo(null, String.Format(OVT.FireIRC.Resources.Resources.ResourceManagement.r.GetString("Client.Unbanned"), e.Who, e.Hostmask));
                    FireIRCCore.GetChannel(client.Address, e.Channel).Write(FireIRCCore.Formatter.Format(Formatter.MessageType.IrcCommand, mi, client.Address, e.Channel));
                }));
            }
        }
        void client_OnBan(object sender, BanEventArgs e)
        {
            if (FireIRCCore.UsingMonoMode == false)
            {
                FireIRCCore.PrimaryForm.Invoke(new Action(delegate
                {
                    MessageInfo mi = new MessageInfo(null, String.Format(OVT.FireIRC.Resources.Resources.ResourceManagement.r.GetString("Client.Banned"), e.Who, e.Hostmask));
                    FireIRCCore.GetChannel(client.Address, e.Channel).Write(FireIRCCore.Formatter.Format(Formatter.MessageType.IrcCommand, mi, client.Address, e.Channel));
                }));
            }
        }
        void client_OnKick(object sender, KickEventArgs e)
        {
            if (FireIRCCore.UsingMonoMode == false)
            {
                FireIRCCore.PrimaryForm.Invoke(new Action(delegate
                {
                    if (e.Whom != client.Nickname)
                    {
                        MessageInfo mi = new MessageInfo(null, String.Format(OVT.FireIRC.Resources.Resources.ResourceManagement.r.GetString("Client.Kicked"), e.Who, e.Whom, e.KickReason));
                        FireIRCCore.GetChannel(client.Address, e.Channel).Write(FireIRCCore.Formatter.Format(Formatter.MessageType.IrcCommand, mi, client.Address, e.Data.Channel));
                    }
                    else
                    {
                        MessageInfo mi = new MessageInfo(null, String.Format(OVT.FireIRC.Resources.Resources.ResourceManagement.r.GetString("Client.Kicked"), e.Who, e.Whom, e.KickReason));
                        FireIRCCore.GetChannel(client.Address, e.Channel).Write(FireIRCCore.Formatter.Format(Formatter.MessageType.IrcCommand, mi, client.Address, e.Data.Channel));
                        FireIRCCore.GetChannel(client.Address, e.Channel).IsConnected = false;
                        FireIRCCore.GetChannel(client.Address, e.Channel).UserList.Clear();
                    }
                }));
            }
        }
        void client_OnPart(object sender, PartEventArgs e)
        {
            if (FireIRCCore.UsingMonoMode == false)
            {
                FireIRCCore.PrimaryForm.Invoke(new Action(delegate
                {
                    if (e.Who != client.Nickname)
                    {
                        MessageInfo mi = new MessageInfo(null, String.Format(OVT.FireIRC.Resources.Resources.ResourceManagement.r.GetString("Client.Parted"), e.Who, e.PartMessage));
                        FireIRCCore.GetChannel(client.Address, e.Channel).Write(FireIRCCore.Formatter.Format(Formatter.MessageType.IrcCommand, mi, client.Address, e.Data.Channel));
                        FireIRCCore.GetChannel(client.Address, e.Channel).UserPart(e.Who);
                    }
                    else
                    {
                        FireIRCCore.PartedChannel(client.Address, e.Channel);
                    }
                }));
            }
        }
        void client_OnJoin(object sender, JoinEventArgs e)
        {
            if (FireIRCCore.UsingMonoMode == false)
            {
                FireIRCCore.PrimaryForm.Invoke(new Action(delegate
                {
                    FireIRCCore.DoesWindowExist(client.Address, e.Channel);
                    MessageInfo mi = new MessageInfo(null, String.Format(OVT.FireIRC.Resources.Resources.ResourceManagement.r.GetString("Client.Joined"), e.Who, e.Channel));
                    if (e.Who != client.Nickname) { FireIRCCore.GetChannel(client.Address, e.Channel).UserJoin(e.Who); }
                    FireIRCCore.GetChannel(client.Address, e.Channel).Write(FireIRCCore.Formatter.Format(Formatter.MessageType.IrcCommand, mi, client.Address, e.Data.Channel));
                }));
            }
        }
        void client_OnInvite(object sender, InviteEventArgs e)
        {
            if (FireIRCCore.UsingMonoMode == false)
            {
                FireIRCCore.PrimaryForm.Invoke(new Action(delegate
                {
                    MessageInfo mi = new MessageInfo(null, String.Format(OVT.FireIRC.Resources.Resources.ResourceManagement.r.GetString("Client.Invite"), e.Who, e.Channel));
                    FireIRCCore.GetActiveChannelWindow().Write(FireIRCCore.Formatter.Format(Formatter.MessageType.IrcCommand, mi, client.Address, null));
                }));
            }
        }
        void client_OnDevoice(object sender, DevoiceEventArgs e)
        {
            if (FireIRCCore.UsingMonoMode == false)
            {
                FireIRCCore.PrimaryForm.Invoke(new Action(delegate
                {
                    MessageInfo mi = new MessageInfo(null, String.Format(OVT.FireIRC.Resources.Resources.ResourceManagement.r.GetString("Client.LostVoice"), e.Whom, e.Who));
                    FireIRCCore.GetChannel(client.Address, e.Channel).Write(FireIRCCore.Formatter.Format(Formatter.MessageType.IrcCommand, mi, client.Address, e.Data.Channel));
                    FireIRCCore.UserModeChange(client.Address, e.Channel, e.Whom);
                }));
            }
        }
        void client_OnVoice(object sender, VoiceEventArgs e)
        {
            if (FireIRCCore.UsingMonoMode == false)
            {
                FireIRCCore.PrimaryForm.Invoke(new Action(delegate
                {
                    MessageInfo mi = new MessageInfo(null, String.Format(OVT.FireIRC.Resources.Resources.ResourceManagement.r.GetString("Client.GivenVoice"), e.Whom, e.Who));
                    FireIRCCore.GetChannel(client.Address, e.Channel).Write(FireIRCCore.Formatter.Format(Formatter.MessageType.IrcCommand, mi, client.Address, e.Data.Channel));
                    FireIRCCore.UserModeChange(client.Address, e.Channel, e.Whom);
                }));
            }
        }
        void client_OnDehalfop(object sender, DehalfopEventArgs e)
        {
            if (FireIRCCore.UsingMonoMode == false)
            {
                FireIRCCore.PrimaryForm.Invoke(new Action(delegate
                {
                    MessageInfo mi = new MessageInfo(null, String.Format(OVT.FireIRC.Resources.Resources.ResourceManagement.r.GetString("Client.LostHop"), e.Whom, e.Who));
                    FireIRCCore.GetChannel(client.Address, e.Channel).Write(FireIRCCore.Formatter.Format(Formatter.MessageType.IrcCommand, mi, client.Address, e.Data.Channel));
                    FireIRCCore.UserModeChange(client.Address, e.Channel, e.Whom);
                }));
            }
        }
        void client_OnHalfop(object sender, HalfopEventArgs e)
        {
            if (FireIRCCore.UsingMonoMode == false)
            {
                FireIRCCore.PrimaryForm.Invoke(new Action(delegate
                {
                    MessageInfo mi = new MessageInfo(null, String.Format(OVT.FireIRC.Resources.Resources.ResourceManagement.r.GetString("Client.GivenHop"), e.Whom, e.Who));
                    FireIRCCore.GetChannel(client.Address, e.Channel).Write(FireIRCCore.Formatter.Format(Formatter.MessageType.IrcCommand, mi, client.Address, e.Data.Channel));
                    FireIRCCore.UserModeChange(client.Address, e.Channel, e.Whom);
                }));
            }
        }
        void client_OnDeop(object sender, DeopEventArgs e)
        {
            FireIRCCore.PrimaryForm.Invoke(new Action(delegate
            {
                MessageInfo mi = new MessageInfo(null, String.Format(OVT.FireIRC.Resources.Resources.ResourceManagement.r.GetString("Client.LostOp"), e.Whom, e.Who));
                FireIRCCore.GetChannel(client.Address, e.Channel).Write(FireIRCCore.Formatter.Format(Formatter.MessageType.IrcCommand, mi, client.Address, e.Data.Channel));
                FireIRCCore.UserModeChange(client.Address, e.Channel, e.Whom);
            }));
        }
        void client_OnOp(object sender, OpEventArgs e)
        {
            FireIRCCore.PrimaryForm.Invoke(new Action(delegate
            {
                MessageInfo mi = new MessageInfo(null, String.Format(OVT.FireIRC.Resources.Resources.ResourceManagement.r.GetString("Client.GivenOp"), e.Whom, e.Who));
                FireIRCCore.GetChannel(client.Address, e.Channel).Write(FireIRCCore.Formatter.Format(Formatter.MessageType.IrcCommand, mi, client.Address, e.Data.Channel));
                FireIRCCore.UserModeChange(client.Address, e.Channel, e.Whom);
            }));
        }
        void client_OnTopicChange(object sender, TopicChangeEventArgs e)
        {
            FireIRCCore.PrimaryForm.Invoke(new Action(delegate
            {
                MessageInfo mi = new MessageInfo(null, String.Format(OVT.FireIRC.Resources.Resources.ResourceManagement.r.GetString("Client.TopicChange"), e.NewTopic, e.Who));
                FireIRCCore.GetChannel(client.Address, e.Channel).Write(FireIRCCore.Formatter.Format(Formatter.MessageType.IrcCommand, mi, client.Address, e.Data.Channel));
                FireIRCCore.GetChannel(client.Address, e.Channel).Topic = e.NewTopic;
            }));
        }
        void client_OnTopic(object sender, TopicEventArgs e)
        {
            FireIRCCore.PrimaryForm.Invoke(new Action(delegate
            {
                MessageInfo mi = new MessageInfo(null, String.Format(OVT.FireIRC.Resources.Resources.ResourceManagement.r.GetString("Client.TopicIs"), e.Topic));
                FireIRCCore.GetChannel(client.Address, e.Channel).Write(FireIRCCore.Formatter.Format(Formatter.MessageType.IrcCommand, mi, client.Address, e.Data.Channel));
                FireIRCCore.GetChannel(client.Address, e.Channel).Topic = e.Topic;
            }));
        }
        void client_OnQueryNotice(object sender, IrcEventArgs e)
        {
            if (FireIRCCore.Settings.IgnoreList.Contains(e.Data.Host) != true)
            {
                try
                {
                    FireIRCCore.PrimaryForm.Invoke(new Action(delegate
                    {
                        try
                        {
                            MessageInfo mi = new MessageInfo(e.Data.Nick, e.Data.Message);
                            FireIRCCore.GetChannel(client.Address, "server").Write(FireIRCCore.Formatter.Format(Formatter.MessageType.NoticeMessage, mi, client.Address, e.Data.Channel));
                            DoExtencibility(new ExClass(MessageForm.QueryNotice, e.Data.Nick, e.Data.Channel, e.Data.Message, client));
                        }
                        catch (NullReferenceException) { }
                    }));
                }
                catch (InvalidOperationException) { }
            }
        }
        void client_OnQueryMessage(object sender, IrcEventArgs e)
        {
            if (FireIRCCore.Settings.IgnoreList.Contains(e.Data.Host) != true)
            {
                FireIRCCore.PrimaryForm.Invoke(new Action(delegate
                {
                    FireIRCCore.DoesWindowExist(client.Address, e.Data.Nick, "Query");
                    if (FireIRCCore.Settings.UseAcceptDenyPMSystem == true)
                    {
                        if (denied.Contains(e.Data.Nick) == false)
                        {
                            if (accepted.Contains(e.Data.Nick) == false)
                            {
                                client.SendMessage(SendType.Message, e.Data.Nick, ResourceManagement.r.GetString("Client.PMWait"));
                                if (MessageBox.Show("Do you want to accept this pm from " + e.Data.Nick, "Private Message", MessageBoxButtons.YesNo, MessageBoxIcon.Question) == DialogResult.Yes)
                                {
                                    client.SendMessage(SendType.Message, e.Data.Nick, ResourceManagement.r.GetString("Client.PMAccepted"));
                                    MessageInfo mi = new MessageInfo(e.Data.Nick, e.Data.Message);
                                    FireIRCCore.GetChannel(client.Address, e.Data.Nick).Write(FireIRCCore.Formatter.Format(Formatter.MessageType.Message, mi, client.Address, e.Data.Channel));
                                    DoExtencibility(new ExClass(MessageForm.QueryMessage, e.Data.Nick, e.Data.Channel, e.Data.Message, client));
                                    accepted.Add(e.Data.Nick);
                                }
                                else
                                {
                                    client.SendMessage(SendType.Message, e.Data.Nick, ResourceManagement.r.GetString("Client.PMDenied"));
                                    denied.Add(e.Data.Nick);
                                }
                            }
                            else
                            {
                                MessageInfo mi = new MessageInfo(e.Data.Nick, e.Data.Message);
                                FireIRCCore.GetChannel(client.Address, e.Data.Nick).Write(FireIRCCore.Formatter.Format(Formatter.MessageType.Message, mi, client.Address, e.Data.Channel));
                                DoExtencibility(new ExClass(MessageForm.QueryMessage, e.Data.Nick, e.Data.Channel, e.Data.Message, client));
                            }
                        }
                    }
                    else
                    {
                        MessageInfo mi = new MessageInfo(e.Data.Nick, e.Data.Message);
                        FireIRCCore.GetChannel(client.Address, e.Data.Nick).Write(FireIRCCore.Formatter.Format(Formatter.MessageType.Message, mi, client.Address, e.Data.Channel));
                        DoExtencibility(new ExClass(MessageForm.QueryMessage, e.Data.Nick, e.Data.Channel, e.Data.Message, client));
                    }
                }));
            }
        }
        void client_OnQueryAction(object sender, ActionEventArgs e)
        {
            if (FireIRCCore.Settings.IgnoreList.Contains(e.Data.Host) != true)
            {
                FireIRCCore.PrimaryForm.Invoke(new Action(delegate
                {
                    FireIRCCore.DoesWindowExist(client.Address, e.Data.Nick, "Query");
                    if (FireIRCCore.Settings.UseAcceptDenyPMSystem == true)
                    {
                        if (denied.Contains(e.Data.Nick) == false)
                        {
                            if (accepted.Contains(e.Data.Nick) == false)
                            {
                                client.SendMessage(SendType.Message, e.Data.Nick, ResourceManagement.r.GetString("Client.PMWait"));
                                if (MessageBox.Show("Do you want to accept this pm from " + e.Data.Nick, "Private Message", MessageBoxButtons.YesNo, MessageBoxIcon.Question) == DialogResult.Yes)
                                {
                                    client.SendMessage(SendType.Message, e.Data.Nick, ResourceManagement.r.GetString("Client.PMAccepted"));
                                    MessageInfo mi = new MessageInfo(e.Data.Nick, e.ActionMessage);
                                    FireIRCCore.GetChannel(client.Address, e.Data.Nick).Write(FireIRCCore.Formatter.Format(Formatter.MessageType.ActionMessage, mi, client.Address, e.Data.Channel));
                                    DoExtencibility(new ExClass(MessageForm.QueryAction, e.Data.Nick, e.Data.Channel, e.ActionMessage, client));
                                    accepted.Add(e.Data.Nick);
                                }
                                else
                                {
                                    client.SendMessage(SendType.Message, e.Data.Nick, ResourceManagement.r.GetString("Client.PMDenied"));
                                    denied.Add(e.Data.Nick);
                                }
                            }
                            else
                            {
                                MessageInfo mi = new MessageInfo(e.Data.Nick, e.ActionMessage);
                                FireIRCCore.GetChannel(client.Address, e.Data.Nick).Write(FireIRCCore.Formatter.Format(Formatter.MessageType.ActionMessage, mi, client.Address, e.Data.Channel));
                                DoExtencibility(new ExClass(MessageForm.QueryAction, e.Data.Nick, e.Data.Channel, e.ActionMessage, client));
                            }
                        }
                    }
                    else
                    {
                        MessageInfo mi = new MessageInfo(e.Data.Nick, e.ActionMessage);
                        FireIRCCore.GetChannel(client.Address, e.Data.Nick).Write(FireIRCCore.Formatter.Format(Formatter.MessageType.ActionMessage, mi, client.Address, e.Data.Channel));
                        DoExtencibility(new ExClass(MessageForm.QueryAction, e.Data.Nick, e.Data.Channel, e.ActionMessage, client));
                    }
                }));
            }
        }
        void client_OnChannelNotice(object sender, IrcEventArgs e)
        {
            if (FireIRCCore.Settings.IgnoreList.Contains(e.Data.Host) != true)
            {
                FireIRCCore.PrimaryForm.Invoke(new Action(delegate
                {
                    MessageInfo mi = new MessageInfo(e.Data.Nick, e.Data.Message);
                    FireIRCCore.GetChannel(client.Address, e.Data.Channel).Write(FireIRCCore.Formatter.Format(Formatter.MessageType.NoticeMessage, mi, client.Address, e.Data.Channel));
                    DoExtencibility(new ExClass(MessageForm.ChannelNotice, e.Data.Nick, e.Data.Channel, e.Data.Message, client));
                }));
            }
        }
        void client_OnChannelMessage(object sender, IrcEventArgs e)
        {
            if (FireIRCCore.Settings.IgnoreList.Contains(e.Data.Host) != true)
            {
                try
                {
                    FireIRCCore.PrimaryForm.Invoke(new Action(delegate
                    {
                        MessageInfo mi = new MessageInfo(e.Data.Nick, e.Data.Message);
                        FireIRCCore.GetChannel(client.Address, e.Data.Channel).Write(FireIRCCore.Formatter.Format(Formatter.MessageType.Message, mi, client.Address, e.Data.Channel));
                        DoExtencibility(new ExClass(MessageForm.ChannelMessage, e.Data.Nick, e.Data.Channel, e.Data.Message, client));
                        if (FireIRCCore.Settings.HighlightsEnabled == true)
                        {
                            string highlightstring = "";
                            foreach (string i in FireIRCCore.Settings.HighlightRegEx)
                            {
                                highlightstring += i + "|";
                            }
                            highlightstring = highlightstring.Remove(highlightstring.Length - 1, 1);
                            Regex ed = new Regex("^(.*)" + highlightstring + "(.*)$", RegexOptions.IgnoreCase);
                            if (ed.IsMatch(e.Data.Message) == true)
                            {
                                System.Media.SystemSounds.Beep.Play();
                                FireIRCCore.PrimaryForm.Tip("Highlighted", "You were highlighted in " + e.Data.Channel + " By " + e.Data.Nick);
                                FireIRCCore.PrimaryForm.AppendToLog(MainForm.LogType.HighlightLog, client.Address + " " + e.Data.Channel + " " + FireIRCCore.Formatter.Format(Formatter.MessageType.Message, mi, client.Address, e.Data.Channel));
                            }
                        }
                    }));
                }
                catch (InvalidOperationException) { }
            }
        }
        void client_OnChannelAction(object sender, ActionEventArgs e)
        {
            if (FireIRCCore.Settings.IgnoreList.Contains(e.Data.Host) != true)
            {
                FireIRCCore.PrimaryForm.Invoke(new Action(delegate
                {
                    MessageInfo mi = new MessageInfo(e.Data.Nick, e.ActionMessage);
                    FireIRCCore.GetChannel(client.Address, e.Data.Channel).Write(FireIRCCore.Formatter.Format(Formatter.MessageType.ActionMessage, mi, client.Address, e.Data.Channel));
                    DoExtencibility(new ExClass(MessageForm.ChannelAction, e.Data.Nick, e.Data.Channel, e.ActionMessage, client));
                }));
            }
        }
        void DoExtencibility(ExClass e)
        {
            foreach (KeyValuePair<string, IOnCommand> i in FireIRCCore.OnCommands)
            {
                i.Value.ExecuteOnCommand(client, e, new Dictionary<string, string>());
            }
        }
    }
}
