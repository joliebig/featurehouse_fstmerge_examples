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
            Height = 159;
            label3.Text = e.Message;
            textBox1.Text = e.ToString();
            tabControl1.Visible = false;
        }
        private void button2_Click(object sender, EventArgs e)
        {
            if (Height == 159)
            {
                Height = 439;
                button2.Text = "Details <<";
                tabControl1.Visible = true;
            }
            else
            {
                Height = 159;
                button2.Text = "Details >>";
                tabControl1.Visible = false;
            }
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
