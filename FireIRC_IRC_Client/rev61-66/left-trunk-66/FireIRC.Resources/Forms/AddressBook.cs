using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using OVT.Melissa.PluginSupport;
using OVT.FireIRC.Resources.Resources;
using OVT.FireIRC.Resources.Forms;
namespace OVT.FireIRC.Resources
{
    public partial class AddressBook : Form
    {
        public AddressBook()
        {
            InitializeComponent();
            checkBox2.Checked = FireIRCCore.Settings.HighlightsEnabled;
            RefreshHighlights();
            RefreshIgnores();
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
            string m = GetText.ShowWindow("Add Highlight", "Put the highlight text to highlight on", listBox2.Text);
            if (m != "")
            {
                FireIRCCore.Settings.HighlightRegEx.Add(m);
                RefreshHighlights();
            }
        }
        void RefreshHighlights()
        {
            listBox2.Items.Clear();
            foreach (string i in FireIRCCore.Settings.HighlightRegEx)
            {
                listBox2.Items.Add(i);
            }
        }
        void RefreshIgnores()
        {
            listBox1.Items.Clear();
            foreach (string i in FireIRCCore.Settings.IgnoreList)
            {
                listBox1.Items.Add(i);
            }
        }
        private void button6_Click(object sender, EventArgs e)
        {
            string m = GetText.ShowWindow("Add Highlight", "Put the highlight text to highlight on", listBox2.Text);
            if (m != "")
            {
                FireIRCCore.Settings.HighlightRegEx.Remove(listBox2.Text);
                FireIRCCore.Settings.HighlightRegEx.Add(m);
                RefreshHighlights();
            }
        }
        private void button5_Click(object sender, EventArgs e)
        {
            FireIRCCore.Settings.HighlightRegEx.Remove(listBox2.Text);
            RefreshHighlights();
        }
        private void AddressBook_Load(object sender, EventArgs e)
        {
        }
        private void button4_Click(object sender, EventArgs e)
        {
            string m = GetText.ShowWindow("Add Ignore", "Host to Ignore", "");
            if (m != "")
            {
                FireIRCCore.Settings.IgnoreList.Add(m);
            }
            RefreshIgnores();
        }
        private void button3_Click(object sender, EventArgs e)
        {
            string m = GetText.ShowWindow("Edit Ignore", "Host to Ignore", listBox1.Text);
            if (m != null)
            {
                FireIRCCore.Settings.IgnoreList.Remove(listBox1.Text);
                FireIRCCore.Settings.IgnoreList.Add(m);
            }
            RefreshIgnores();
        }
        private void button2_Click(object sender, EventArgs e)
        {
            FireIRCCore.Settings.IgnoreList.Remove(listBox1.Text);
            RefreshIgnores();
        }
        private void listBox1_SelectedIndexChanged(object sender, EventArgs e)
        {
        }
    }
}
