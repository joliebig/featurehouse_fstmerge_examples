using System;
using System.Collections.Generic;
using System.Text;
using FireIRC.Extenciblility;
using System.Windows.Forms;
using OVT.FireIRC.Resources;
using FireIRC.Extenciblility.IRCClasses;
using OVT.Melissa.PluginSupport;
namespace Chris.Plugin
{
    public class KickReasons : OnLaunch
    {
        public void Run()
        {
            ToolStripMenuItem k = new ToolStripMenuItem(OVT.FireIRC.Resources.Resources.ResourceManagement.r.GetString("FireIRC.SendNoticeSystem.SendNotice"));
            k.Click +=new EventHandler(k_Click);
            ToolStripMenuItem k2 = new ToolStripMenuItem(OVT.FireIRC.Resources.Resources.ResourceManagement.r.GetString("FireIRC.SendNoticeSystem.Kick"));
            k2.Click += new EventHandler(k2_Click);
            FireIRCCore.ExtEngine.NickList.Items.Add(k);
            FireIRCCore.ExtEngine.NickList.Items.Add(k2);
            FireIRCCore.ExtEngine.NickList.Items.Add("-");
            ToolStripMenuItem k3 = new ToolStripMenuItem(OVT.FireIRC.Resources.Resources.ResourceManagement.r.GetString("FireIRC.SendNoticeSystem.SetReasons"));
            k3.Click += new EventHandler(k3_Click);
            FireIRCCore.PrimaryForm.CommandMenu.DropDownItems.Add(k3);
        }
        void k3_Click(object sender, EventArgs e)
        {
            ReasonsOptions i = new ReasonsOptions();
            i.ShowDialog();
        }
        void k2_Click(object sender, EventArgs e)
        {
            NoticeWindow w = new NoticeWindow();
            if (w.ShowDialog() == DialogResult.OK)
            {
                foreach (ListViewItem i in FireIRCCore.GetActiveChannelWindow().SelectedNicks)
                {
                    FireIRCCore.Clients[FireIRCCore.ActiveServer].IrcClient.RfcKick(FireIRCCore.ActiveChannel, i.Text, w.Reason);
                }
            }
        }
        void k_Click(object sender, EventArgs e)
        {
            NoticeWindow w = new NoticeWindow();
            if (w.ShowDialog() == DialogResult.OK)
            {
                foreach (ListViewItem i in FireIRCCore.GetActiveChannelWindow().SelectedNicks)
                {
                    FireIRCCore.Clients[FireIRCCore.ActiveServer].IrcClient.SendMessage(SendType.Message, FireIRCCore.ActiveChannel, i.Text + " " + w.Reason);
                }
            }
        }
    }
}
