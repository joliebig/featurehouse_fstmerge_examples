using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using OVT.Melissa.PluginSupport;
using OVT.FireIRC.Resources.Resources;
namespace OVT.FireIRC.Resources
{
    public partial class Options : Form
    {
        bool fontsupdated = false;
        public Options()
        {
            InitializeComponent();
            if (PropertyService.Get("language") == "en-US") { comboBox1.Text = "English"; }
            else if (PropertyService.Get("language") == "nb-NO") { comboBox1.Text = "Norwegian"; }
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
            checkBox1.Checked = FireIRCCore.Settings.UseExperimentalInterface;
            checkBox2.Checked = FireIRCCore.Settings.UseAcceptDenyPMSystem;
            propertyGrid1.SelectedObject = FireIRCCore.Settings;
            ResourceManagement.ConvertToManagedResource(this);
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
        private void checkBox1_CheckedChanged(object sender, EventArgs e)
        {
            FireIRCCore.Settings.UseExperimentalInterface = checkBox1.Checked;
        }
        private void comboBox1_SelectedIndexChanged(object sender, EventArgs e)
        {
            if (comboBox1.Text == "English") { PropertyService.Set<string>("language", "en-US"); }
            else if (comboBox1.Text == "Norwegian") { PropertyService.Set<string>("language", "nb-NO"); }
        }
        private void generalTab_Click(object sender, EventArgs e)
        {
        }
        private void checkBox2_CheckedChanged(object sender, EventArgs e)
        {
            FireIRCCore.Settings.UseAcceptDenyPMSystem = checkBox2.Checked;
        }
        private void propertyGrid1_Click(object sender, EventArgs e)
        {
        }
    }
}
