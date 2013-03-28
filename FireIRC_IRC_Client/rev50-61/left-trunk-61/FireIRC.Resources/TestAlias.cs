using System;
using System.Collections.Generic;
using System.Text;
using FireIRC.Extenciblility;
using System.Windows.Forms;
using FireIRC.Extenciblility.IRCClasses;
using OVT.FireIRC.Resources;
using OVT.Melissa.PluginSupport;
namespace PluginTest
{
    public class GenericNickStuff : OnLaunch
    {
        public void Run()
        {
            ToolStripMenuItem banusers = new ToolStripMenuItem(OVT.FireIRC.Resources.Resources.ResourceManagement.r.GetString("FireIRC.NickList.Ban"));
            banusers.Click += new EventHandler(banusers_Click);
            ToolStripMenuItem unbanusers = new ToolStripMenuItem(OVT.FireIRC.Resources.Resources.ResourceManagement.r.GetString("FireIRC.NickList.Unban"));
            unbanusers.Click += new EventHandler(unbanusers_Click);
            ToolStripMenuItem kick = new ToolStripMenuItem(OVT.FireIRC.Resources.Resources.ResourceManagement.r.GetString("FireIRC.NickList.Kick"));
            kick.Click += new EventHandler(kick_Click);
            ToolStripMenuItem op = new ToolStripMenuItem(OVT.FireIRC.Resources.Resources.ResourceManagement.r.GetString("FireIRC.NickList.Op"));
            op.Click += new EventHandler(op_Click);
            ToolStripMenuItem deop = new ToolStripMenuItem(OVT.FireIRC.Resources.Resources.ResourceManagement.r.GetString("FireIRC.NickList.DeOp"));
            deop.Click += new EventHandler(deop_Click);
            ToolStripMenuItem dehop = new ToolStripMenuItem(OVT.FireIRC.Resources.Resources.ResourceManagement.r.GetString("FireIRC.NickList.DeHop"));
            dehop.Click += new EventHandler(dehop_Click);
            ToolStripMenuItem devoice = new ToolStripMenuItem(OVT.FireIRC.Resources.Resources.ResourceManagement.r.GetString("FireIRC.NickList.DeVoice"));
            devoice.Click += new EventHandler(devoice_Click);
            ToolStripMenuItem hop = new ToolStripMenuItem(OVT.FireIRC.Resources.Resources.ResourceManagement.r.GetString("FireIRC.NickList.Hop"));
            hop.Click += new EventHandler(hop_Click);
            ToolStripMenuItem voice = new ToolStripMenuItem(OVT.FireIRC.Resources.Resources.ResourceManagement.r.GetString("FireIRC.NickList.Voice"));
            voice.Click += new EventHandler(voice_Click);
           FireIRCCore.ExtEngine.NickList.Items.Add(banusers);
            FireIRCCore.ExtEngine.NickList.Items.Add(unbanusers);
            FireIRCCore.ExtEngine.NickList.Items.Add(kick);
            FireIRCCore.ExtEngine.NickList.Items.Add("-");
            FireIRCCore.ExtEngine.NickList.Items.Add(op);
            FireIRCCore.ExtEngine.NickList.Items.Add(hop);
            FireIRCCore.ExtEngine.NickList.Items.Add(voice);
            FireIRCCore.ExtEngine.NickList.Items.Add(deop);
            FireIRCCore.ExtEngine.NickList.Items.Add(dehop);
            FireIRCCore.ExtEngine.NickList.Items.Add(devoice);
        }
        void voice_Click(object sender, EventArgs e)
        {
            foreach (ListViewItem i in FireIRCCore.GetActiveChannelWindow().SelectedNicks)
            {
                FireIRCCore.Clients[FireIRCCore.ActiveServer].IrcClient.Voice(FireIRCCore.ActiveChannel, i.Text);
            }
        }
        void hop_Click(object sender, EventArgs e)
        {
            foreach (ListViewItem i in FireIRCCore.GetActiveChannelWindow().SelectedNicks)
            {
                FireIRCCore.Clients[FireIRCCore.ActiveServer].IrcClient.Halfop(FireIRCCore.ActiveChannel, i.Text, Priority.High);
            }
        }
        void devoice_Click(object sender, EventArgs e)
        {
            foreach (ListViewItem i in FireIRCCore.GetActiveChannelWindow().SelectedNicks)
            {
                FireIRCCore.Clients[FireIRCCore.ActiveServer].IrcClient.Devoice(FireIRCCore.ActiveChannel, i.Text);
            }
        }
        void dehop_Click(object sender, EventArgs e)
        {
            foreach (ListViewItem i in FireIRCCore.GetActiveChannelWindow().SelectedNicks)
            {
                FireIRCCore.Clients[FireIRCCore.ActiveServer].IrcClient.Dehalfop(FireIRCCore.ActiveChannel, i.Text);
            }
        }
        void deop_Click(object sender, EventArgs e)
        {
            foreach (ListViewItem i in FireIRCCore.GetActiveChannelWindow().SelectedNicks)
            {
                FireIRCCore.Clients[FireIRCCore.ActiveServer].IrcClient.Deop(FireIRCCore.ActiveChannel, i.Text);
            }
        }
        void op_Click(object sender, EventArgs e)
        {
            foreach (ListViewItem i in FireIRCCore.GetActiveChannelWindow().SelectedNicks)
            {
                FireIRCCore.Clients[FireIRCCore.ActiveServer].IrcClient.Op(FireIRCCore.ActiveChannel, i.Text);
            }
        }
        void kick_Click(object sender, EventArgs e)
        {
            foreach (ListViewItem i in FireIRCCore.GetActiveChannelWindow().SelectedNicks)
            {
                FireIRCCore.Clients[FireIRCCore.ActiveServer].IrcClient.RfcKick(FireIRCCore.ActiveChannel, i.Text);
            }
        }
        void unbanusers_Click(object sender, EventArgs e)
        {
            foreach (ListViewItem i in FireIRCCore.GetActiveChannelWindow().SelectedNicks)
            {
                FireIRCCore.Clients[FireIRCCore.ActiveServer].IrcClient.Unban(FireIRCCore.ActiveChannel, i.Text);
            }
        }
        void banusers_Click(object sender, EventArgs e)
        {
            foreach (ListViewItem i in FireIRCCore.GetActiveChannelWindow().SelectedNicks)
            {
                FireIRCCore.Clients[FireIRCCore.ActiveServer].IrcClient.Ban(FireIRCCore.ActiveChannel, i.Text);
            }
        }
    }
}
