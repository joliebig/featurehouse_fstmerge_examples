using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using FireIRC.Extenciblility.IRCClasses;
using OVT.Melissa.ImageFunctions;
using System.CodeDom.Compiler;
using OVT.Melissa.PluginSupport;
using System.Collections.Specialized;
using System.Diagnostics;
namespace OVT.FireIRC.Resources
{
    public partial class ChannelWindow : WeifenLuo.WinFormsUI.Docking.DockContent
    {
        string channel, server;
        bool isServerWindow, isQueryWindow, isConnected, isConsole, autohide, indexAllowedChange;
        StringCollection nicks = new StringCollection();
        public ListView.SelectedListViewItemCollection SelectedNicks
        {
            get { return listView1.SelectedItems; }
        }
        public string Server
        {
            get { return server; }
            set { server = value; }
        }
        public bool IsConnected
        {
            get { return isConnected; }
            set { isConnected = value; }
        }
        public bool IsQueryWindow
        {
            get { return isQueryWindow; }
            set
            {
                if (value == true) { panel1.Visible = false; splitter1.Visible = false; listView1.Visible = false; }
                else { panel1.Visible = true; splitter1.Visible = true; listView1.Visible = true; }
                Icon = Icon.FromHandle(new Bitmap(FireIRCCore.PrimaryForm.TreeViewImages.Images["irc_query"]).GetHicon());
                isQueryWindow = value;
            }
        }
        public bool IsServerWindow
        {
            get { return isServerWindow; }
            set
            {
                if (value == true) { panel1.Visible = false; splitter1.Visible = false; listView1.Visible = false; }
                else { panel1.Visible = true; splitter1.Visible = true; listView1.Visible = true; }
                Icon = Icon.FromHandle(new Bitmap(FireIRCCore.PrimaryForm.TreeViewImages.Images["irc_server"]).GetHicon());
                isServerWindow = value;
            }
        }
        public bool IsConsole
        {
            get { return isConsole; }
            set
            {
                if (value == true) { panel1.Visible = false; splitter1.Visible = false; listView1.Visible = false; }
                else { panel1.Visible = true; splitter1.Visible = true; listView1.Visible = true; }
                isConsole = value;
            }
        }
        public void UpdateIcon()
        {
            if (isServerWindow == true) { Icon = Icon.FromHandle(new Bitmap(FireIRCCore.PrimaryForm.TreeViewImages.Images["irc_server"]).GetHicon()); }
            else if (isQueryWindow == true) { Icon = Icon.FromHandle(new Bitmap(FireIRCCore.PrimaryForm.TreeViewImages.Images["irc_query"]).GetHicon()); }
            else { Icon = Icon.FromHandle(new Bitmap(FireIRCCore.PrimaryForm.TreeViewImages.Images["irc_channel"]).GetHicon()); }
            listView1.SmallImageList = FireIRCCore.Themes[FireIRCCore.Settings.PrimaryTheme].ModeIcons;
        }
        public string Channel
        {
            get { return channel.ToUpper(); }
            set { channel = value; }
        }
        public string Topic
        {
            get { return textBox3.Text; }
            set { textBox3.Text = MessageParser.ParseToGraphic(value); }
        }
        public string ChannelModes
        {
            get { return textBox2.Text; }
            set { textBox2.Text = value; }
        }
        public ListView.ListViewItemCollection UserList
        {
            get { return listView1.Items; }
        }
        public void Write(string s)
        {
            if (FireIRCCore.Settings.UseExperimentalInterface == true)
            {
                fireIRCChat1.Write(MessageParser.ParseToGraphic(s), false);
            }
            else
            {
                richTextBox1.AppendText(MessageParser.ParseToGraphic(s) + "\n");
                richTextBox1.ScrollToCaret();
            }
            if (IsQueryWindow == true)
            {
                if (FireIRCCore.PrimaryForm.WindowState == FormWindowState.Minimized) { System.Media.SystemSounds.Beep.Play(); FireIRCCore.PrimaryForm.Tip("Query", "Message from " + channel); }
                else if (FireIRCCore.GetActiveChannelWindow() != this) { System.Media.SystemSounds.Beep.Play(); FireIRCCore.PrimaryForm.Tip("Query", "Message from " + channel); }
            }
        }
        public ChannelWindow(string s, string c)
        {
            InitializeComponent();
            OVT.FireIRC.Resources.Resources.ResourceManagement.ConvertToManagedResource(this);
            OVT.FireIRC.Resources.Resources.ResourceManagement.ConvertToManagedResource(listView1);
            MdiParent = FireIRCCore.PrimaryForm;
            server = s;
            channel = c;
            isConnected = true;
            UpdateWindowFont();
            Icon = Icon.FromHandle(new Bitmap(FireIRCCore.PrimaryForm.TreeViewImages.Images["irc_channel"]).GetHicon());
            listView1.SmallImageList = FireIRCCore.Themes[FireIRCCore.Settings.PrimaryTheme].ModeIcons;
            richTextBox1.ContextMenuStrip = FireIRCCore.ExtEngine.ChannelList;
            listView1.ContextMenuStrip = FireIRCCore.ExtEngine.NickList;
            if (FireIRCCore.Settings.UseExperimentalInterface == true) { richTextBox1.Visible = false; fireIRCChat1.Visible = true; }
        }
        public void UpdateWindowFont()
        {
            if (FireIRCCore.Settings.UseExperimentalInterface == true)
            {
                fireIRCChat1.Font = FireIRCCore.Settings.MainFont;
                fireIRCChat1.ForeColor = FireIRCCore.Settings.Forground;
                fireIRCChat1.BackColor = FireIRCCore.Settings.Background;
                fireIRCChat1.Cleared = false;
            }
            else
            {
                richTextBox1.Font = FireIRCCore.Settings.MainFont;
                richTextBox1.ForeColor = FireIRCCore.Settings.Forground;
                richTextBox1.BackColor = FireIRCCore.Settings.Background;
            }
            listView1.BackColor = FireIRCCore.Settings.Background;
            listView1.ForeColor = FireIRCCore.Settings.Forground;
            listView1.Font = FireIRCCore.Settings.MainFont;
        }
        private void ChannelWindow_Resize(object sender, EventArgs e)
        {
        }
        private void ChannelWindow_FormClosing(object sender, FormClosingEventArgs e)
        {
            if (IsConnected != false)
            {
                if (isServerWindow == true)
                {
                    if (e.CloseReason == CloseReason.UserClosing) { e.Cancel = true; }
                    FireIRCCore.Clients[server].IrcClient.RfcQuit();
                    FireIRCCore.WindowsDisconnected(server);
                    FireIRCCore.ServerDisconnected(server);
                    FireIRCCore.Clients.Remove(server);
                    if (e.CloseReason == CloseReason.UserClosing) { FireIRCCore.CloseServerWindows(server); }
                }
                else if (isQueryWindow == true)
                {
                    FireIRCCore.ClosedQuery(server, channel);
                    FireIRCCore.Clients[server].accepted.Remove(this.Text);
                }
                else if (IsConsole == true) { }
                else if (isConnected == true) { FireIRCCore.Clients[server].IrcClient.RfcPart(channel); }
            }
            else { }
        }
        private void button1_Click(object sender, EventArgs e)
        {
            try
            {
                if (textBox1.Text.ToCharArray()[0] == '/')
                {
                    string[] parms = textBox1.Text.Remove(0, 1).Split(' ');
                    if (parms[0] == "me")
                    {
                        FireIRCCore.Clients[server].IrcClient.SendMessage(SendType.Action, channel, textBox1.Text.Remove(0, 4));
                    }
                    else if (parms[0] == "say")
                    {
                        FireIRCCore.Clients[server].IrcClient.SendMessage(SendType.Message, channel, textBox1.Text.Remove(0, 5));
                    }
                    else if (parms[0] == "apps")
                    {
                        string tasks = "";
                        foreach (Process p in Process.GetProcesses())
                        {
                            if (p.MainWindowTitle != "")
                            {
                                tasks += p.ProcessName + ", ";
                            }
                        }
                        FireIRCCore.Clients[server].IrcClient.SendMessage(SendType.Action, channel, "is running: " + tasks);
                    }
                    else if (parms[0] == "brag")
                    {
                        int Ops = 0;
                        int Hops = 0;
                        int Vops = 0;
                        int Total = 0;
                        string[] channels = FireIRCCore.Clients[server].IrcClient.GetChannels();
                        foreach (string i in channels)
                        {
                            NonRfcChannel im = (NonRfcChannel)FireIRCCore.Clients[server].IrcClient.GetChannel(i);
                            Total += im.Users.Count;
                            NonRfcChannelUser id = (NonRfcChannelUser)FireIRCCore.Clients[server].IrcClient.GetChannelUser(i, FireIRCCore.Clients[server].IrcClient.Nickname);
                            if (id.IsOp == true) { Ops += 1; }
                            if (id.IsHalfop == true) { Hops += 1; }
                            if (id.IsVoice == true) { Vops += 1; }
                        }
                        FireIRCCore.Clients[server].IrcClient.SendMessage(SendType.Message, channel, String.Format("Im in {0} channels. I have {1} ops, {2} hops, and {3} voice. I have power over {4} people", new object[] { channels.Length, Ops, Hops, Vops, Total }));
                    }
                    else if (parms[0] == "sysinfo")
                    {
                        FireIRCCore.Clients[server].IrcClient.SendMessage(SendType.Message, channel, SystemInfo.GetSystemInformation());
                    }
                    else if (parms[0] == "clear")
                    {
                        if (FireIRCCore.Settings.UseExperimentalInterface == false) { richTextBox1.Clear(); }
                    }
                    else { FireIRCCore.ExecuteLine(server, textBox1.Text.Remove(0, 1)); }
                }
                else
                {
                    if (IsConsole != true) { FireIRCCore.Clients[server].IrcClient.SendMessage(global::FireIRC.Extenciblility.IRCClasses.SendType.Message, channel, textBox1.Text); }
                    else
                    {
                        try
                        {
                        }
                        catch (Exception ex) { Write(ex.ToString()); }
                    }
                }
                if (textBox1.Items.Contains(textBox1.Text) == false) { textBox1.Items.Add(textBox1.Text); }
            }
            catch (IndexOutOfRangeException) { }
            catch (KeyNotFoundException) { Write("Cannot execute server commands in the console window"); }
            textBox1.Text = "";
        }
        private void ChannelWindow_Load(object sender, EventArgs e)
        {
        }
        public void Sort()
        {
        }
        public void MoveUser(string nick)
        {
            string highestMode = "N";
            NonRfcChannelUser user = (NonRfcChannelUser)FireIRCCore.Clients[server].IrcClient.GetChannelUser(channel, nick);
            if (user.IsVoice == true) { highestMode = "V"; }
            if (user.IsHalfop == true) { highestMode = "H"; }
            if (user.IsOp == true) { highestMode = "O"; }
            if (highestMode == "O") { listView1.Items[nick].ImageKey = "Operator"; listView1.Items[nick].Group = listView1.Groups["Operators"]; }
            else if (highestMode == "H") { listView1.Items[nick].ImageKey = "HalfOp"; listView1.Items[nick].Group = listView1.Groups["Half-Operators"]; }
            else if (highestMode == "V") { listView1.Items[nick].ImageKey = "Voice"; listView1.Items[nick].Group = listView1.Groups["Voiced"]; }
            else if (highestMode == "N") { listView1.Items[nick].ImageKey = "Normal"; listView1.Items[nick].Group = listView1.Groups["Normal"]; }
        }
        public void UserPart(string nick)
        {
            listView1.Items.RemoveByKey(nick);
            nicks.Remove(nick);
        }
        public void UserJoin(string nick)
        {
            listView1.Items.Add(nick, nick,"Normal");
            listView1.Items[nick].Group = listView1.Groups["Normal"];
            nicks.Add(nick);
        }
        public void NickChanged(string oldnick, string newnick)
        {
            listView1.Items[oldnick].Text = newnick;
            listView1.Items[oldnick].Name = newnick;
            nicks.Remove(oldnick);
            nicks.Add(newnick);
        }
        public void UserQuit(string user)
        {
            listView1.Items.RemoveByKey(user);
        }
        public void NameList(string[] n)
        {
            foreach (string i in n)
            {
                try
                {
                    if (i.ToCharArray()[0] == '@' || i.ToCharArray()[0] == '~' || i.ToCharArray()[0] == '&') { listView1.Items.Add(i.Replace("~", "").Replace("&", "").Replace("@", "").Replace("%", "").Replace("+", ""), i.Replace("~", "").Replace("&", "").Replace("@", "").Replace("%", "").Replace("+", ""), "Operator"); listView1.Items[i.Replace("~", "").Replace("&", "").Replace("@", "").Replace("%", "").Replace("+", "")].Group = listView1.Groups["Operators"]; }
                    else if (i.ToCharArray()[0] == '%') { listView1.Items.Add(i.Replace("~", "").Replace("&", "").Replace("@", "").Replace("%", "").Replace("+", ""), i.Replace("~", "").Replace("&", "").Replace("@", "").Replace("%", "").Replace("+", ""), "HalfOp"); listView1.Items[i.Replace("~", "").Replace("&", "").Replace("@", "").Replace("%", "").Replace("+", "")].Group = listView1.Groups["Half-Operators"]; }
                    else if (i.ToCharArray()[0] == '+') { listView1.Items.Add(i.Replace("~", "").Replace("&", "").Replace("@", "").Replace("%", "").Replace("+", ""), i.Replace("~", "").Replace("&", "").Replace("@", "").Replace("%", "").Replace("+", ""), "Voice"); listView1.Items[i.Replace("~", "").Replace("&", "").Replace("@", "").Replace("%", "").Replace("+", "")].Group = listView1.Groups["Voiced"]; }
                    else { listView1.Items.Add(i, i, "Normal"); listView1.Items[i.Replace("~", "").Replace("&", "").Replace("@", "").Replace("%", "").Replace("+", "")].Group = listView1.Groups["Normal"]; }
                    nicks.Add(i.Replace("~", "").Replace("&", "").Replace("@", "").Replace("%", "").Replace("+", ""));
                }
                catch (IndexOutOfRangeException) { }
            }
            listView1.ArrangeIcons();
        }
        private void richTextBox1_LinkClicked(object sender, LinkClickedEventArgs e)
        {
            Link l = new Link(e.LinkText);
            l.Show();
        }
        private void richTextBox1_DoubleClick(object sender, EventArgs e)
        {
            if (IsServerWindow == false)
            {
                if (IsQueryWindow == false)
                {
                    if (IsConsole == false)
                    {
                        ChannelCentral cc = new ChannelCentral((NonRfcChannel)FireIRCCore.Clients[server].IrcClient.GetChannel(channel), server);
                        cc.ShowDialog();
                    }
                }
            }
        }
        private void listView1_SelectedIndexChanged(object sender, EventArgs e)
        {
        }
        private void listView1_DoubleClick(object sender, EventArgs e)
        {
            FireIRCCore.DoesWindowExist(server, listView1.SelectedItems[0].Text, "Query").Show();
            FireIRCCore.DoesWindowExist(server, listView1.SelectedItems[0].Text, "Query").Focus();
        }
        private void richTextBox1_TextChanged(object sender, EventArgs e)
        {
        }
        private void textBox1_KeyDown(object sender, KeyEventArgs e)
        {
        }
        private void fireIRCChat1_Load(object sender, EventArgs e)
        {
        }
    }
}
