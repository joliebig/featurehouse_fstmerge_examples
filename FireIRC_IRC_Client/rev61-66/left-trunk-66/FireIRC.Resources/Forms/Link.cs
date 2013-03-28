using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using OVT.FireIRC.Resources.Resources;
namespace OVT.FireIRC.Resources
{
    public partial class Link : Form
    {
        public Link(string link)
        {
            InitializeComponent();
            textBox1.Text = link;
            ResourceManagement.ConvertToManagedResource(this);
        }
        private void button1_Click(object sender, EventArgs e)
        {
            System.Diagnostics.Process.Start(textBox1.Text);
            this.Close();
        }
        private void button2_Click(object sender, EventArgs e)
        {
            this.Close();
        }
        private void Link_Load(object sender, EventArgs e)
        {
        }
    }
}
