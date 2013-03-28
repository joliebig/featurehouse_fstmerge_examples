using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
namespace OVT.FireIRC.Resources
{
    public partial class Favorates : Form
    {
        string serv;
        public Favorates(string server)
        {
            InitializeComponent();
            if (server == "null")
            {
                button4.Enabled = false;
            }
            serv = server;
            GetFavorates();
        }
        private void button2_Click(object sender, EventArgs e)
        {
            try
            {
                FireIRCCore.Settings.Favorates.Remove(GetFavorate(treeView1.SelectedNode.Text));
                GetFavorates();
            }
            catch (NullReferenceException) { }
        }
        private void button3_Click(object sender, EventArgs e)
        {
            this.Close();
        }
        private void Favorates_Load(object sender, EventArgs e)
        {
        }
        void GetFavorates()
        {
            treeView1.Nodes.Clear();
            foreach (Favorate f in FireIRCCore.Settings.Favorates)
            {
                treeView1.Nodes.Add(f.Channel);
            }
        }
        Favorate GetFavorate(string channel)
        {
            Favorate fd = null;
            foreach (Favorate f in FireIRCCore.Settings.Favorates)
            {
                if (f.Channel == channel)
                {
                    fd = f;
                    break;
                }
            }
            return fd;
        }
        private void button5_Click(object sender, EventArgs e)
        {
            try
            {
                EditChannel ee = new EditChannel(GetFavorate(treeView1.SelectedNode.Text));
                ee.ShowDialog();
                GetFavorates();
            }
            catch (NullReferenceException) { }
        }
        private void button1_Click(object sender, EventArgs e)
        {
            EditChannel ee = new EditChannel();
            ee.ShowDialog();
            GetFavorates();
        }
        private void button4_Click(object sender, EventArgs e)
        {
            FireIRCCore.Clients[serv].IrcClient.RfcJoin(treeView1.SelectedNode.Text);
        }
    }
}
