using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using System.Collections.Specialized;
using OVT.Melissa.PluginSupport;
namespace OVT.FireIRC.Resources.Forms
{
    public partial class Identities : Form
    {
        public Identities()
        {
            InitializeComponent();
            OVT.FireIRC.Resources.Resources.ResourceManagement.ConvertToManagedResource(this);
        }
        private void button5_Click(object sender, EventArgs e)
        {
            if (comboBox1.Text != "Default Identity")
            {
                FireIRCCore.Settings.NickInfo.Remove(FireIRCCore.Settings.ReturnNickInformation(comboBox1.Text));
                GetIdentities();
            }
        }
        private void Identities_Load(object sender, EventArgs e)
        {
            GetIdentities();
            comboBox1.Text = "Default Identity";
        }
        private void button3_Click(object sender, EventArgs e)
        {
            string newstring = MessageService.ShowInputBox("Add Nick", "Name of the new Nick", "");
            if (newstring != "")
            {
                listView1.Items.Add(newstring);
            }
        }
        public void GetIdentities()
        {
            comboBox1.Items.Clear();
            foreach (NickInformation i in FireIRCCore.Settings.NickInfo)
            {
                comboBox1.Items.Add(i.Identity);
            }
        }
        private void comboBox1_SelectedIndexChanged(object sender, EventArgs e)
        {
            NickInformation get = FireIRCCore.Settings.ReturnNickInformation(comboBox1.Text);
            listView1.Items.Clear();
            foreach(string i in get.Nicks)
            {
                listView1.Items.Add(i);
            }
            textBox4.Text = get.UserName;
            textBox5.Text = get.RealName;
        }
        private void button2_Click(object sender, EventArgs e)
        {
            string m = MessageService.ShowInputBox("Add Identity", "Name of the new Identity", "");
            if (m != null)
            {
                NickInformation n = new NickInformation();
                n.Identity = m;
                FireIRCCore.Settings.NickInfo.Add(n);
                GetIdentities();
            }
            else
            {
                MessageService.ShowError("Please type a name for the Identity");
            }
        }
        private void button6_Click(object sender, EventArgs e)
        {
            save();
        }
        private void save()
        {
            StringCollection a = new StringCollection();
            NickInformation get = FireIRCCore.Settings.ReturnNickInformation(comboBox1.Text);
            get.UserName = textBox4.Text;
            get.RealName = textBox5.Text;
            foreach(ListViewItem i in listView1.Items)
            {
                a.Add(i.Text);
            }
            get.Nicks = new string[listView1.Items.Count];
            a.CopyTo(get.Nicks, 0);
        }
        private void button4_Click(object sender, EventArgs e)
        {
            try { listView1.Items.Remove(listView1.SelectedItems[0]); }
            catch (ArgumentException) { }
        }
        private void button1_Click(object sender, EventArgs e)
        {
            save();
            this.Close();
        }
        private void button7_Click(object sender, EventArgs e)
        {
            try { listView1.SelectedItems[0].Text = MessageService.ShowInputBox("Edit Nick", "Name of the new Nick", listView1.SelectedItems[0].Text); }
            catch (ArgumentException) { }
        }
    }
}
