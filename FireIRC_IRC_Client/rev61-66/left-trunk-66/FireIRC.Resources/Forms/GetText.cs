using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
namespace OVT.FireIRC.Resources.Forms
{
    public partial class GetText : Form
    {
        string returnValue;
        public string ReturnValue
        {
            get { return returnValue; }
            set { returnValue = value; }
        }
        public GetText()
        {
            InitializeComponent();
        }
        private void button1_Click(object sender, EventArgs e)
        {
            returnValue = textBox1.Text;
            this.Close();
        }
        private void button2_Click(object sender, EventArgs e)
        {
            this.Close();
        }
        private void GetText_Load(object sender, EventArgs e)
        {
        }
        public static string ShowWindow(string Title, string Text, string DefaultValue)
        {
            GetText g = new GetText();
            g.textBox1.Text = DefaultValue;
            g.returnValue = DefaultValue;
            g.label1.Text = Text;
            g.Text = Title;
            g.ShowDialog();
            return g.ReturnValue;
        }
    }
}
