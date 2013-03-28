using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using OVT.Melissa.PluginSupport;
namespace OVT.FireIRC.Resources
{
    public partial class Options : Form
    {
        bool fontsupdated = false;
        public Options()
        {
            InitializeComponent();
            if (AddInTree.ExistsTreeNode("/FireIRC/Themes") == true)
            {
                foreach (Codon c in AddInTree.GetTreeNode("/FireIRC/Themes").Codons)
                {
                    try
                    {
                        listBox1.Items.Add(c.Id);
                    }
                    catch (ArgumentException) { }
                }
            }
            open.Text = ResourceService.GetString("Global.Open");
            ok.Text = ResourceService.GetString("Global.OK");
            serverEditorGroup.Text = ResourceService.GetString("FireIRC.OptionsDialog.ServerEditor");
            ServerEditorNotice.Text = ResourceService.GetString("FireIRC.OptionsDialog.ServerEditorNotice");
            tabPage2.Text = ResourceService.GetString("FireIRC.Tabs.Server");
            tabPage3.Text = ResourceService.GetString("FireIRC.Tabs.Colors");
            tabPage4.Text = ResourceService.GetString("FireIRC.Tabs.Themes");
            this.Text = ResourceService.GetString("FireIRC.Tools.Options");
            label3.Text = ResourceService.GetString("FireIRC.ThemeNotice");
            button7.Text = ResourceService.GetString("FireIRC.SetTheme");
            button3.Text = ResourceService.GetString("FireIRC.Colors.Font");
            button4.Text = ResourceService.GetString("FireIRC.Colors.Background");
            button5.Text = ResourceService.GetString("FireIRC.Colors.Foreground");
            groupBox1.Text = ResourceService.GetString("FireIRC.Colors.SetFont");
            groupBox2.Text = ResourceService.GetString("FireIRC.Colors.SetColors");
            label1.Text = ResourceService.GetString("FireIRC.Colors.SetFontNotice");
            label2.Text = ResourceService.GetString("FireIRC.Colors.SetColorsNotice");
            generalTab.Text = ResourceService.GetString("FireIRC.Tabs.General");
        }
        public Options(string Tab)
        {
            InitializeComponent();
            if (Tab == "Colors") { tabControl1.SelectedTab = tabPage3; }
            if (AddInTree.ExistsTreeNode("/FireIRC/Themes") == true)
            {
                foreach (Codon c in AddInTree.GetTreeNode("/FireIRC/Themes").Codons)
                {
                    try
                    {
                        listBox1.Items.Add(c.Id);
                    }
                    catch (ArgumentException) { }
                }
            }
        }
        private void button4_Click(object sender, EventArgs e)
        {
            ColorDialog d = new ColorDialog();
            d.Color = FireIRCCore.Settings.Background;
            if (d.ShowDialog() == DialogResult.OK) { FireIRCCore.Settings.Background = d.Color; fontsupdated = true; }
        }
        private void button5_Click(object sender, EventArgs e)
        {
            ColorDialog d = new ColorDialog();
            d.Color = FireIRCCore.Settings.Forground;
            if (d.ShowDialog() == DialogResult.OK) { FireIRCCore.Settings.Forground = d.Color; fontsupdated = true; }
        }
        private void button3_Click(object sender, EventArgs e)
        {
            FontDialog d = new FontDialog();
            d.Font = FireIRCCore.Settings.MainFont;
            if (d.ShowDialog() == DialogResult.OK) { FireIRCCore.Settings.MainFont = d.Font; fontsupdated = true; }
        }
        private void button1_Click(object sender, EventArgs e)
        {
            if (fontsupdated == true)
            {
                FireIRCCore.PrimaryForm.UpdateWindowFont();
                foreach (ChannelWindow c in FireIRCCore.PrimaryForm.MdiChildren)
                {
                    c.UpdateWindowFont();
                }
            }
            this.Close();
        }
        private void button2_Click(object sender, EventArgs e)
        {
            this.Close();
        }
        private void Options_Load(object sender, EventArgs e)
        {
        }
        private void button2_Click_1(object sender, EventArgs e)
        {
            Server_Editor p = new Server_Editor();
            p.ShowDialog();
        }
        private void button7_Click(object sender, EventArgs e)
        {
            FireIRCCore.Settings.PrimaryTheme = listBox1.Text;
            FireIRCCore.PrimaryForm.UpdateImages();
            foreach (ChannelWindow c in FireIRCCore.PrimaryForm.MdiChildren)
            {
                c.UpdateIcon();
            }
        }
    }
}
