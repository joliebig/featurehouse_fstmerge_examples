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
    public partial class AddressBook : Form
    {
        public AddressBook()
        {
            InitializeComponent();
            checkBox2.Checked = FireIRCCore.Settings.HighlightsEnabled;
            RefreshHighlights();
            ResourceManagement.ConvertToManagedResource(this);
        }
        private void checkBox2_CheckedChanged(object sender, EventArgs e)
        {
            listBox2.Enabled = checkBox2.Checked;
            button7.Enabled = checkBox2.Checked;
            button6.Enabled = checkBox2.Checked;
            button5.Enabled = checkBox2.Checked;
            FireIRCCore.Settings.HighlightsEnabled = checkBox2.Checked;
        }
        private void button1_Click(object sender, EventArgs e)
        {
            this.Close();
        }
        private void button7_Click(object sender, EventArgs e)
        {
            FireIRCCore.Settings.HighlightRegEx.Add(MessageService.ShowInputBox("Add Highlight", "Put the highlight text to highlight on", ""));
            RefreshHighlights();
        }
        void RefreshHighlights()
        {
            listBox2.Items.Clear();
            foreach (string i in FireIRCCore.Settings.HighlightRegEx)
            {
                listBox2.Items.Add(i);
            }
        }
        private void button6_Click(object sender, EventArgs e)
        {
            FireIRCCore.Settings.HighlightRegEx.Remove(listBox2.Text);
            FireIRCCore.Settings.HighlightRegEx.Add(MessageService.ShowInputBox("Add Highlight", "Put the highlight text to highlight on", listBox2.Text));
            RefreshHighlights();
        }
        private void button5_Click(object sender, EventArgs e)
        {
            FireIRCCore.Settings.HighlightRegEx.Remove(listBox2.Text);
            RefreshHighlights();
        }
        private void AddressBook_Load(object sender, EventArgs e)
        {
        }
    }
}
