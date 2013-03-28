using System;
using System.Windows.Forms;
using System.Reflection;
namespace WikiFunctions.Controls
{
    public partial class AboutBox : Form
    {
        public AboutBox()
        {
            InitializeComponent();
        }
        protected virtual void Initialise()
        {
            lblVersion.Text = "Version " + Tools.VersionString;
            textBoxDescription.Text = GPLNotice;
        }
        protected virtual void okButton_Click(object sender, EventArgs e)
        {
            Close();
        }
        protected virtual void linkLabel1_LinkClicked(object sender, LinkLabelLinkClickedEventArgs e)
        {
            linkLabel1.LinkVisited = true;
            Tools.OpenENArticleInBrowser("WP:AWB", false);
        }
        public static string GPLNotice
        { get { return Properties.Resources.GPL; } }
        public static string AssemblyDescription(Assembly Ass)
        {
            object[] attributes = Ass.GetCustomAttributes(typeof(AssemblyDescriptionAttribute), false);
            return (attributes.Length == 0) ? "" : ((AssemblyDescriptionAttribute)attributes[0]).Description;
        }
        public static string AssemblyCopyright(Assembly Ass)
        {
            object[] attributes = Ass.GetCustomAttributes(typeof(AssemblyCopyrightAttribute), false);
            return (attributes.Length == 0) ? "" : ((AssemblyCopyrightAttribute)attributes[0]).Copyright;
        }
        public static string GetDetailedMessage(Assembly Ass)
        { return AssemblyDescription(Ass) + Environment.NewLine + Environment.NewLine + GPLNotice; }
        private void AboutBox_Load(object sender, EventArgs e)
        {
            Initialise();
        }
    }
}
