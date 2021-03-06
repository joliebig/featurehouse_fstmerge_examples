using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using OVT.Melissa.PluginSupport;
using OVT.FireIRC.Resources.Resources;
namespace OVT.FireIRC.Resources
{
    public partial class EditChannel : Form
    {
        bool @new;
        Favorate b;
        public EditChannel()
        {
            InitializeComponent();
            @new = true;
            b = new Favorate();
            ResourceManagement.ConvertToManagedResource(this);
        }
        public EditChannel(Favorate f)
        {
            InitializeComponent();
            b = f;
            textBox1.Text = f.Channel;
            textBox2.Text = f.Password;
            ResourceManagement.ConvertToManagedResource(this);
        }
        private void button1_Click(object sender, EventArgs e)
        {
            if (@new == true)
            {
                b.Channel = textBox1.Text;
                b.Password = textBox2.Text;
                FireIRCCore.Settings.Favorates.Add(b);
                this.Close();
            }
            else
            {
                FireIRCCore.Settings.Favorates.Remove(GetFavorate(b.Channel));
                b.Channel = textBox1.Text;
                b.Password = textBox2.Text;
                FireIRCCore.Settings.Favorates.Add(b);
                this.Close();
            }
        }
        private void EditChannel_Load(object sender, EventArgs e)
        {
        }
        private void button2_Click(object sender, EventArgs e)
        {
            this.Close();
        }
        Favorate GetFavorate(string channel)
        {
            Favorate fd = null;
            foreach (Favorate f in FireIRCCore.Settings.Favorates)
            {
                if (f.Channel == channel)
                {
                    fd = f;
                    break;
                }
            }
            return fd;
        }
    }
}
