using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using OVT.Melissa.PluginSupport;
namespace Chris.Plugin
{
    public partial class ReasonsOptions : Form
    {
        public ReasonsOptions()
        {
            InitializeComponent();
            OVT.FireIRC.Resources.Resources.ResourceManagement.ConvertToManagedResource(this);
            textBox1.Lines = PropertyService.Get<string[]>("FireIRC.Notices", new string[] { });
        }
        private void button1_Click(object sender, EventArgs e)
        {
            PropertyService.Set<string[]>("FireIRC.Notices", textBox1.Lines);
            this.Close();
        }
        private void button2_Click(object sender, EventArgs e)
        {
            this.Close();
        }
    }
}
