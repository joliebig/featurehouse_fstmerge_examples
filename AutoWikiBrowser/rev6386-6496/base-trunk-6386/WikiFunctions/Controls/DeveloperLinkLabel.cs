using System.ComponentModel;
using System.Windows.Forms;
namespace WikiFunctions.Controls
{
    public enum Developers { Bluemoose, Kingboyk, Ligulem, Reedy, Rjwilmsi, MaxSem }
    public class DeveloperLinkLabel : LinkLabel
    {
        public DeveloperLinkLabel()
        {
            LinkClicked += DeveloperLinkLabel_LinkClicked;
            WhichDeveloper = dev;
        }
        private void DeveloperLinkLabel_LinkClicked(object sender, LinkLabelLinkClickedEventArgs e)
        {
            LinkVisited = true;
            Tools.OpenENArticleInBrowser(Text, true);
        }
        Developers dev = Developers.Bluemoose;
        [DefaultValue(Developers.Bluemoose), Category("Appearance")]
        [Browsable(true)]
        public Developers WhichDeveloper
        {
            get { return dev; }
            set
            {
                dev = value;
                Text = dev.ToString();
            }
        }
        [Browsable(false)]
        public override string Text
        {
            get { return base.Text; }
            set { base.Text = value; }
        }
    }
}
