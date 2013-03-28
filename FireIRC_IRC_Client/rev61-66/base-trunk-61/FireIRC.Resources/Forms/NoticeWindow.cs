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
    public partial class NoticeWindow : Form
    {
        public string Reason
        {
            get { return comboBox1.Text; }
        }
        public NoticeWindow()
        {
            InitializeComponent();
            OVT.FireIRC.Resources.Resources.ResourceManagement.ConvertToManagedResource(this);
            comboBox1.Items.Clear();
            foreach (string i in PropertyService.Get<string[]>("FireIRC.Notices", new string[] { }))
            {
                comboBox1.Items.Add(i);
            }
        }
        private void NoticeWindow_Load(object sender, EventArgs e)
        {
        }
        private void button1_Click(object sender, EventArgs e)
        {
        }
    }
}
