using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using System.Reflection;
using System.IO;
namespace FireIRC
{
    public partial class Splash : Form
    {
        bool updatorEnabled = false;
        public Splash()
        {
            InitializeComponent();
            toolStripStatusLabel2.Text = "Version: " + VersionInformation.ToString();
        }
        private void timer1_Tick(object sender, EventArgs e)
        {
            toolStripStatusLabel1.Text = "Starting up FireIRC...";
            timer2.Enabled = true;
        }
        private void timer2_Tick(object sender, EventArgs e)
        {
            this.Close();
        }
        private void Splash_Load(object sender, EventArgs e)
        {
        }
    }
}
