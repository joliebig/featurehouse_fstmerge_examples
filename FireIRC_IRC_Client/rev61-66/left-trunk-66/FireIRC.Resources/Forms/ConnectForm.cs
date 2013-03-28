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
    public partial class ConnectForm : Form
    {
        public ConnectForm()
        {
            InitializeComponent();
            foreach (NickInformation i in FireIRCCore.Settings.NickInfo)
            {
                comboBox1.Items.Add(i.Identity);
            }
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
                    NickInformation nickInformation = FireIRCCore.Settings.ReturnNickInformation(comboBox1.Text);
                    FireIRCCore.Clients.Add(s.HostName, new Client(s.HostName, s.Port, nickInformation.Nicks, nickInformation.UserName, nickInformation.RealName, get_Perform(s.GroupName,nickInformation), nickInformation));
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
            comboBox1.Text = "Default Identity";
        }
        Perform get_Perform(string group, NickInformation n)
        {
            Perform ren = null;
            foreach (Perform p in n.Performs)
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
