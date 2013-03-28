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
    public partial class Server_Editor : Form
    {
        public Server_Editor()
        {
            InitializeComponent();
            ResourceManagement.ConvertToManagedResource(this);
            get_Servers();
            get_Performs();
        }
        private void button2_Click(object sender, EventArgs e)
        {
            GetGroup OHYES = new GetGroup();
            if (OHYES.ShowDialog() == DialogResult.OK)
            {
                Perform p = new Perform();
                p.Server = OHYES.group;
                p.Perf = new string[] { };
                FireIRCCore.Settings.Performs.Add(p);
                get_Performs();
            }
        }
        void get_Servers()
        {
            treeView1.Nodes.Clear();
            foreach (Server i in FireIRCCore.Settings.Servers)
            {
                try
                {
                    treeView1.Nodes[i.GroupName].Nodes.Add(i.Name);
                }
                catch (NullReferenceException)
                {
                    treeView1.Nodes.Add(i.GroupName, i.GroupName);
                    treeView1.Nodes[i.GroupName].Nodes.Add(i.Name);
                }
            }
        }
        void get_Performs()
        {
            comboBox1.Items.Clear();
            foreach (Perform p in FireIRCCore.Settings.Performs)
            {
                comboBox1.Items.Add(p.Server);
            }
        }
        Perform get_Perform(string group)
        {
            Perform ren = null;
            foreach (Perform p in FireIRCCore.Settings.Performs)
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
            FireIRCCore.Settings.Performs.Remove(get_Perform(group));
            Perform a = new Perform();
            a.Perf = perf;
            a.Server = group;
            FireIRCCore.Settings.Performs.Add(a);
        }
        private void comboBox1_SelectedIndexChanged(object sender, EventArgs e)
        {
            textBox1.Lines = get_Perform(comboBox1.Text).Perf;
        }
        private void button3_Click(object sender, EventArgs e)
        {
            set_Perform(comboBox1.Text, textBox1.Lines);
        }
        private void button1_Click(object sender, EventArgs e)
        {
            this.Close();
        }
        private void Server_Editor_Load(object sender, EventArgs e)
        {
        }
        private void treeView1_AfterSelect(object sender, TreeViewEventArgs e)
        {
            try
            {
                Server bleh = getServer(e.Node.Parent.Text, e.Node.Text);
                textBox2.Text = bleh.Name;
                linkLabel1.Text = bleh.Name;
                textBox3.Text = bleh.Port.ToString();
                textBox4.Text = bleh.HostName;
                textBox5.Text = bleh.GroupName;
                linkLabel2.Text = bleh.GroupName;
            }
            catch (NullReferenceException) {
                textBox2.Text = "";
                linkLabel1.Text = "";
                textBox3.Text = "";
                textBox4.Text = "";
                textBox5.Text = "";
                linkLabel2.Text = "";
            }
        }
        Server getServer(string group, string name)
        {
            Server s = null;
            foreach (Server i in FireIRCCore.Settings.Servers)
            {
                if (i.GroupName == group)
                {
                    if (i.Name == name)
                    {
                        s = i;
                        break;
                    }
                }
            }
            return s;
        }
        void SaveServer(string ogroup, string oname, int port, string host, string group, string name )
        {
            foreach (Server i in FireIRCCore.Settings.Servers)
            {
                if (i.GroupName == ogroup)
                {
                    if (i.Name == oname)
                    {
                        i.Name = name;
                        linkLabel1.Text = name;
                        i.GroupName = group;
                        linkLabel2.Text = group;
                        i.Port = port;
                        i.HostName = host;
                        break;
                    }
                }
            }
            get_Servers();
        }
        private void button4_Click(object sender, EventArgs e)
        {
            try
            {
                SaveServer(linkLabel2.Text, linkLabel1.Text, Convert.ToInt32(textBox3.Text), textBox4.Text, textBox5.Text, textBox2.Text);
            }
            catch { }
        }
        private void button7_Click(object sender, EventArgs e)
        {
            FireIRCCore.Settings.Servers.Remove(getServer(treeView1.SelectedNode.Parent.Text, treeView1.SelectedNode.Text));
            get_Servers();
        }
        private void button6_Click(object sender, EventArgs e)
        {
            if (getServer(textBox5.Text, textBox2.Text) == null)
            {
                try
                {
                    Server i = new Server();
                    i.GroupName = textBox5.Text;
                    i.HostName = textBox4.Text;
                    i.Port = Convert.ToInt32(textBox3.Text);
                    i.Name = textBox2.Text;
                    FireIRCCore.Settings.Servers.Add(i);
                    get_Servers();
                }
                catch (FormatException ex) { MessageService.ShowError(ex); }
            }
            else
            {
                MessageService.ShowError("Server Already Exists.");
            }
        }
    }
}
