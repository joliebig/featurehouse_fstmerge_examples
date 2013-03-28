using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using OVT.Melissa.PluginSupport;
namespace FireIRC.Extenciblility
{
    public partial class Management : Form
    {
        public Management()
        {
            InitializeComponent();
        }
        private void button1_Click(object sender, EventArgs e)
        {
            this.Close();
        }
        private void Management_Load(object sender, EventArgs e)
        {
            foreach (AddIn i in AddInTree.AddIns)
            {
                listView1.Items.Add(new ListViewItem(new string[] { i.Name, i.Enabled.ToString() }));
            }
        }
        private void button2_Click(object sender, EventArgs e)
        {
            PlugInProperties p = new PlugInProperties(AddInTree.AddIns[listView1.SelectedItems[0].Index]);
            p.ShowDialog();
        }
    }
}
