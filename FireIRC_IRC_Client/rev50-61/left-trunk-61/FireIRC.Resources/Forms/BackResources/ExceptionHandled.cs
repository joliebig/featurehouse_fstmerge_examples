using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
namespace OVT.FireIRC.Resources
{
    public partial class ExceptionHandled : Form
    {
        public ExceptionHandled(Exception e)
        {
            InitializeComponent();
            label3.Text = e.Message;
            textBox1.Text = e.ToString();
            OVT.FireIRC.Resources.Resources.ResourceManagement.ConvertToManagedResource(this);
        }
        private void ExceptionHandled_Load(object sender, EventArgs e)
        {
        }
        private void button1_Click(object sender, EventArgs e)
        {
            this.Close();
        }
    }
}
