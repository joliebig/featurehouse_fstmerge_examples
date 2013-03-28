using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using OVT.FireIRC.Resources.IRC;
using OVT.Melissa.ImageFunctions;
using System.CodeDom.Compiler;
namespace OVT.FireIRC.Resources
{
    public partial class ChannelWindow : Form
    {
        string channel, server;
        bool isServerWindow, isQueryWindow, isConnected, isConsole;
        List<string> Opers, Half_Opers, Voice, Normal, WriteStrings = new List<string>();
        float j = 0;
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
                if (value == true) { panel1.Visible = false; splitter1.Visible = false; listView1.Visible = false; label1.Visible = false; }
                else { panel1.Visible = true; splitter1.Visible = true; listView1.Visible = true; label1.Visible = true; }
                isQueryWindow = value;
            }
        }
        public bool IsServerWindow
        {
            get { return isServerWindow; }
            set
            {
                if (value == true) { panel1.Visible = false; splitter1.Visible = false; listView1.Visible = false; label1.Visible = false; }
                else { panel1.Visible = true; splitter1.Visible = true; listView1.Visible = true; label1.Visible = true; }
                Icon = Icon.FromHandle(new Bitmap(FireIRCCore.PrimaryForm.TreeViewImages.Images["irc_server"]).GetHicon());
                isServerWindow = value;
            }
        }
        public bool IsConsole
        {
            get { return isConsole; }
            set
            {
                if (value == true) { panel1.Visible = false; splitter1.Visible = false; listView1.Visible = false; label1.Visible = false; }
                else { panel1.Visible = true; splitter1.Visible = true; listView1.Visible = true; label1.Visible = true; }
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
            richTextBox1.AppendText(MessageParser.ParseToGraphic(s) + "\n");
            richTextBox1.ScrollToCaret();
        }
        private void panel2_Paint(object sender, PaintEventArgs e)
        {
            Graphics g = e.Graphics;
            g.Clear(FireIRCCore.Settings.Background);
            j = g.MeasureString("X", FireIRCCore.Settings.MainFont).Height + 2;
            foreach (string i in Reverse(g))
            {
                g.DrawString(i, FireIRCCore.Settings.MainFont, new SolidBrush(FireIRCCore.Settings.Forground), new PointF(0, panel2.Height - j));
                j += g.MeasureString("X", FireIRCCore.Settings.MainFont).Height + 2;
            }
        }
        private List<string> Reverse(Graphics g)
        {
            List<string> Rever = new List<string>();
            foreach (string i in WriteStrings)
            {
                foreach (string y in ReturnSplitLineArray(i, g))
                {
                    Rever.Add(y);
                }
            }
            Rever.Reverse();
            return Rever;
        }
        public List<string> ReturnSplitLineArray(string line, Graphics g)
        {
            string temp = "";
            List<string> array = new List<string>();
            foreach (char i in line.ToCharArray())
            {
                if (g.MeasureString(temp, FireIRCCore.Settings.MainFont).Width <= panel2.Width)
                {
                    temp += i;
                }
                else
                {
                    array.Add(temp);
                    temp = " " + i;
                }
            }
            array.Add(temp);
            return array;
        }
        public ChannelWindow(string s, string c)
        {
            InitializeComponent();
            MdiParent = FireIRCCore.PrimaryForm;
            server = s;
            channel = c;
            isConnected = true;
            UpdateWindowFont();
            Icon = Icon.FromHandle(new Bitmap(FireIRCCore.PrimaryForm.TreeViewImages.Images["irc_channel"]).GetHicon());
            listView1.SmallImageList = FireIRCCore.Themes[FireIRCCore.Settings.PrimaryTheme].ModeIcons;
        }
        public void UpdateWindowFont()
        {
            richTextBox1.Font = FireIRCCore.Settings.MainFont;
            richTextBox1.ForeColor = FireIRCCore.Settings.Forground;
            richTextBox1.BackColor = FireIRCCore.Settings.Background;
            listView1.BackColor = FireIRCCore.Settings.Background;
            listView1.ForeColor = FireIRCCore.Settings.Forground;
            listView1.Font = FireIRCCore.Settings.MainFont;
        }
        private void ChannelWindow_Resize(object sender, EventArgs e)
        {
            panel2.Refresh();
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
                    if (e.CloseReason == CloseReason.UserClosing) { FireIRCCore.CloseServerWindows(server); }
                }
                else if (isQueryWindow == true) { FireIRCCore.ClosedQuery(server, channel); }
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
                    else { FireIRCCore.ExecuteLine(server, textBox1.Text.Remove(0, 1)); }
                }
                else
                {
                    if (IsConsole != true) { FireIRCCore.Clients[server].IrcClient.SendMessage(global::OVT.FireIRC.Resources.IRC.SendType.Message, channel, textBox1.Text); }
                    else
                    {
                        try
                        {
                        }
                        catch (Exception ex) { Write(ex.ToString()); }
                    }
                }
                textBox1.Items.Add(textBox1.Text);
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
            UserList.Clear();
            foreach (string i in Opers)
            {
                UserList.Add(i, i, "Operator");
            }
            foreach (string i in Half_Opers)
            {
                UserList.Add(i, i, "HalfOp");
            }
            foreach (string i in Voice)
            {
                UserList.Add(i, i, "Voice");
            }
            foreach (string i in Normal)
            {
                UserList.Add(i, i, "Normal");
            }
        }
        public void MoveUser(string nick)
        {
            string highestMode = "N";
            NonRfcChannelUser user = (NonRfcChannelUser)FireIRCCore.Clients[server].IrcClient.GetChannelUser(channel, nick);
            if (user.IsVoice == true) { highestMode = "V"; }
            if (user.IsHalfop == true) { highestMode = "H"; }
            if (user.IsOp == true) { highestMode = "O"; }
            if (highestMode == "O") { listView1.Items[nick].ImageKey = "Operator"; }
            else if (highestMode == "H") { listView1.Items[nick].ImageKey = "HalfOp"; }
            else if (highestMode == "V") { listView1.Items[nick].ImageKey = "Voice"; }
            else if (highestMode == "N") { listView1.Items[nick].ImageKey = "Normal"; }
        }
        public void UserPart(string nick)
        {
            listView1.Items.RemoveByKey(nick);
        }
        public void NickChanged(string oldnick, string newnick)
        {
            listView1.Items[oldnick].Text = newnick;
            listView1.Items[oldnick].Name = newnick;
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
                    if (i.ToCharArray()[0] == '@' || i.ToCharArray()[0] == '~' || i.ToCharArray()[0] == '&') { listView1.Items.Add(i, i, "Operator"); }
                    else if (i.ToCharArray()[0] == '%') { listView1.Items.Add(i, i, "HalfOp"); }
                    else if (i.ToCharArray()[0] == '+') { listView1.Items.Add(i, i, "Voice"); }
                    else { listView1.Items.Add(i, i, "Normal"); }
                }
                catch (IndexOutOfRangeException) { }
            }
            listView1.ArrangeIcons();
        }
        private void richTextBox1_LinkClicked(object sender, LinkClickedEventArgs e)
        {
            Link l = new Link(e.LinkText);
            l.ShowDialog();
        }
        private void label1_Click(object sender, EventArgs e)
        {
        }
        private void label1_DoubleClick(object sender, EventArgs e)
        {
            if (splitter1.Visible == true)
            {
                splitter1.Visible = false;
                listView1.Visible = false;
                label1.Text = "<";
            }
            else
            {
                splitter1.Visible = true;
                listView1.Visible = true;
                label1.Text = ">";
            }
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
        private void textBox1_SelectedIndexChanged(object sender, EventArgs e)
        {
        }
    }
}
