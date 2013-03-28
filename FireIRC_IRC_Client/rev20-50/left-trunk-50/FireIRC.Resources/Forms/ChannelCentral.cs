using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using FireIRC.Extenciblility.IRCClasses;
using System.Collections;
using OVT.FireIRC.Resources.Resources;
namespace OVT.FireIRC.Resources
{
    public partial class ChannelCentral : Form
    {
        NonRfcChannel channel;
        string server;
        bool bootUp = true;
        public ChannelCentral(NonRfcChannel chan, string s)
        {
            InitializeComponent();
            channel = chan;
            server = s;
            Text = "FireIRC Channel Central " + chan.Name;
            textBox1.Text = chan.Topic;
            if (chan.Mode.Contains("i") == true) { checkBox2.Checked = true; }
            if (chan.Mode.Contains("m") == true) { checkBox1.Checked = true; }
            if (chan.Mode.Contains("p") == true) { checkBox3.Checked = true; }
            if (chan.Mode.Contains("s") == true) { checkBox4.Checked = true; }
            bootUp = false;
            ResourceManagement.ConvertToManagedResource(this);
        }
        private void ChannelCentral_Load(object sender, EventArgs e)
        {
            RefreshBans();
        }
        private void button1_Click(object sender, EventArgs e)
        {
            this.Close();
        }
        private void button2_Click(object sender, EventArgs e)
        {
            try { FireIRCCore.Clients[server].IrcClient.Unban(channel.Name, listBox1.SelectedItem.ToString()); }
            catch (NullReferenceException) { }
        }
        private void RefreshBans()
        {
            listBox1.Items.Clear();
            foreach (string i in channel.Bans)
            {
                listBox1.Items.Add(i);
            }
        }
        private void button3_Click(object sender, EventArgs e)
        {
            FireIRCCore.Clients[server].IrcClient.RfcTopic(channel.Name, textBox1.Text);
        }
        private void checkBox2_CheckedChanged(object sender, EventArgs e)
        {
            if (bootUp == false)
            {
                if (checkBox2.Checked == true) { FireIRCCore.Clients[server].IrcClient.RfcMode(channel.Name, "+i"); }
                else if (checkBox2.Checked == false) { FireIRCCore.Clients[server].IrcClient.RfcMode(channel.Name, "-i"); }
            }
        }
        private void checkBox1_CheckedChanged(object sender, EventArgs e)
        {
            if (bootUp == false)
            {
                if (checkBox1.Checked == true) { FireIRCCore.Clients[server].IrcClient.RfcMode(channel.Name, "+m"); }
                else if (checkBox1.Checked == false) { FireIRCCore.Clients[server].IrcClient.RfcMode(channel.Name, "-m"); }
            }
        }
        private void checkBox3_CheckedChanged(object sender, EventArgs e)
        {
            if (bootUp == false)
            {
                if (checkBox3.Checked == true) { FireIRCCore.Clients[server].IrcClient.RfcMode(channel.Name, "+p"); }
                else if (checkBox3.Checked == false) { FireIRCCore.Clients[server].IrcClient.RfcMode(channel.Name, "-p"); }
            }
        }
        private void checkBox4_CheckedChanged(object sender, EventArgs e)
        {
            if (bootUp == false)
            {
                if (checkBox4.Checked == true) { FireIRCCore.Clients[server].IrcClient.RfcMode(channel.Name, "+s"); }
                else if (checkBox4.Checked == false) { FireIRCCore.Clients[server].IrcClient.RfcMode(channel.Name, "-s"); }
            }
        }
    }
}
