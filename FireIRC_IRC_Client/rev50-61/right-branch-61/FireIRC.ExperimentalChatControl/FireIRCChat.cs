using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Drawing;
using System.Data;
using System.Text;
using System.Windows.Forms;
namespace OVT.FireIRC.ExperimentalChatControl
{
    public partial class FireIRCChat : UserControl
    {
        bool cleared;
        public bool Cleared
        {
            get { return cleared; }
            set
            {
                cleared = value;
                panel1.Refresh();
            }
        }
        public FireIRCChat()
        {
            InitializeComponent();
        }
        private void UserControl1_Load(object sender, EventArgs e)
        {
        }
        public void Write(string s)
        {
            panel1.Refresh();
        }
    }
}
