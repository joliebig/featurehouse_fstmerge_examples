using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using OVT.Melissa.PluginSupport;
namespace OVT.FireIRC.Resources.PlugIn
{
    public partial class PlugInProperties : Form
    {
        AddIn b;
        public PlugInProperties(AddIn a)
        {
            InitializeComponent();
            b = a;
            label3.Text = b.Name;
            this.Text = b.Name + " Properties";
            ExtensionPath e = a.GetExtensionPath("/FireIRC/Aliases");
            foreach (Codon c in e.Codons)
            {
                listBox1.Items.Add(c.Id);
            }
            ExtensionPath f = a.GetExtensionPath("/FireIRC/Themes");
            foreach (Codon d in f.Codons)
            {
                listBox2.Items.Add(d.Id);
            }
            if (a.Enabled == true) { button1.Enabled = false; button2.Enabled = true; }
            else if (a.Enabled == false) { button1.Enabled = true; button2.Enabled = false; }
        }
        private void PlugInProperties_Load(object sender, EventArgs e)
        {
        }
        private void button1_Click(object sender, EventArgs e)
        {
            b.Action = AddInAction.Disable;
            button1.Enabled = false; button2.Enabled = true;
        }
        private void button2_Click(object sender, EventArgs e)
        {
            b.Action = AddInAction.Enable;
            button1.Enabled = true; button2.Enabled = false;
        }
        private void button3_Click(object sender, EventArgs e)
        {
            this.Close();
        }
    }
}
