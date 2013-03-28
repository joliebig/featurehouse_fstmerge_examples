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
        List<Perform> Performs = new List<Perform>();
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
            string newstring = GetText.ShowWindow("Add Nick", "Name of the new Nick", "");
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
            textBox1.Text = get.NickServPass;
            Performs = get.Performs;
            get_Performs();
        }
        private void button2_Click(object sender, EventArgs e)
        {
            string m = GetText.ShowWindow("Add Identity", "Name of the new Identity", "");
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
            get.NickServPass = textBox1.Text;
            get.Performs = Performs;
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
            try { listView1.SelectedItems[0].Text = GetText.ShowWindow("Edit Nick", "Name of the new Nick", listView1.SelectedItems[0].Text); }
            catch (ArgumentException) { }
        }
        private void button8_Click(object sender, EventArgs e)
        {
            GetGroup OHYES = new GetGroup();
            if (OHYES.ShowDialog() == DialogResult.OK)
            {
                Perform p = new Perform();
                p.Server = OHYES.group;
                p.Perf = new string[] { };
                Performs.Add(p);
                get_Performs();
            }
        }
        Perform get_Perform(string group)
        {
            Perform ren = null;
            foreach (Perform p in Performs)
            {
                if (p.Server == group)
                {
                    ren = p;
                    break;
                }
            }
            return ren;
        }
        void set_Perform(string group, string[] perf)
        {
            Performs.Remove(get_Perform(group));
            Perform a = new Perform();
            a.Perf = perf;
            a.Server = group;
            Performs.Add(a);
        }
        void get_Performs()
        {
            comboBox2.Items.Clear();
            foreach (Perform p in Performs)
            {
                comboBox2.Items.Add(p.Server);
            }
        }
        private void comboBox2_SelectedIndexChanged(object sender, EventArgs e)
        {
            textBox2.Lines = get_Perform(comboBox2.Text).Perf;
        }
        private void button10_Click(object sender, EventArgs e)
        {
            set_Perform(comboBox2.Text, textBox2.Lines);
        }
    }
}
