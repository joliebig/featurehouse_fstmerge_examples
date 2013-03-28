using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
namespace OVT.FireIRC.Resources
{
    public partial class Timer : Form
    {
        int timerInterval;
        public Timer()
        {
            InitializeComponent();
        }
        private void timer1_Tick(object sender, EventArgs e)
        {
            timerInterval += 1;
            label1.Text = timerInterval.ToString();
        }
        private void Timer_FormClosing(object sender, FormClosingEventArgs e)
        {
            e.Cancel = true;
            this.Hide();
        }
        private void button1_Click(object sender, EventArgs e)
        {
            this.Close();
        }
        private void Timer_Load(object sender, EventArgs e)
        {
        }
    }
}
