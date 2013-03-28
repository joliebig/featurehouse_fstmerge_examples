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
    public partial class ConnectForm : Form
    {
        public ConnectForm()
        {
            InitializeComponent();
            textBox1.Text = FireIRCCore.Settings.NickInfo.Nick1;
            textBox2.Text = FireIRCCore.Settings.NickInfo.Nick2;
            textBox3.Text = FireIRCCore.Settings.NickInfo.Nick3;
            textBox4.Text = FireIRCCore.Settings.NickInfo.UserName;
            textBox5.Text = FireIRCCore.Settings.NickInfo.RealName;
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
            ResourceManagement.ConvertToManagedResource(this);
        }
        private void button1_Click(object sender, EventArgs e)
        {
            try
            {
                Server s = getServer(treeView1.SelectedNode.Parent.Text, treeView1.SelectedNode.Text);
                try
                {
                    FireIRCCore.Clients.Add(s.HostName, new Client(s.HostName, s.Port, new string[] { textBox1.Text, textBox2.Text, textBox3.Text }, textBox4.Text, textBox5.Text, get_Perform(s.GroupName)));
                    FireIRCCore.Settings.NickInfo.Nick1 = textBox1.Text;
                    FireIRCCore.Settings.NickInfo.Nick2 = textBox2.Text;
                    FireIRCCore.Settings.NickInfo.Nick3 = textBox3.Text;
                    FireIRCCore.Settings.NickInfo.UserName = textBox4.Text;
                    FireIRCCore.Settings.NickInfo.RealName = textBox5.Text;
                    this.Close();
                }
                catch(Exception ex)
                {
                    MessageService.ShowError(ex,"A Connection Error Occured");
                }
            }
            catch (NullReferenceException) { MessageBox.Show("Please Select a Server Name", "FireIRC", MessageBoxButtons.OK, MessageBoxIcon.Error); }
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
        private void button2_Click(object sender, EventArgs e)
        {
            this.Close();
        }
        private void ConnectForm_Load(object sender, EventArgs e)
        {
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
            if (ren == null)
            {
                ren = new Perform();
                ren.Perf = new string[] { };
            }
            return ren;
        }
    }
}
