using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
namespace OVT.FireIRC.Resources
{
    public partial class AboutFireIRC : Form
    {
        public AboutFireIRC()
        {
            InitializeComponent();
            label2.Text = "Version: " + VersionInformation.ToString();
        }
        private void button1_Click(object sender, EventArgs e)
        {
            this.Close();
        }
        private void AboutFireIRC_Load(object sender, EventArgs e)
        {
        }
    }
}
