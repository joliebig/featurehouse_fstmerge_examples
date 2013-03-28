using System;
using System.Windows.Forms;
using System.Reflection;
using WikiFunctions;
namespace AutoWikiBrowser
{
    internal sealed partial class AboutBox : Form
    {
        public AboutBox(string ieVersion)
        {
            InitializeComponent();
            lblAWBVersion.Text = "Version " + Program.VersionString;
            lblRevision.Text = "SVN " + Variables.Revision;
            lblIEVersion.Text = "Internet Explorer version: " + ieVersion;
            txtWarning.Text = WikiFunctions.Controls.AboutBox.GetDetailedMessage(Assembly.GetExecutingAssembly());
            lblOSVersion.Text = "Windows version: " + Environment.OSVersion.Version.Major + "." + Environment.OSVersion.Version.Minor;
            lblNETVersion.Text = ".NET version: " + Environment.Version;
        }
        private void okButton_Click(object sender, EventArgs e)
        {
            Close();
        }
        private void linkAWBPage_LinkClicked(object sender, LinkLabelLinkClickedEventArgs e)
        {
            linkAWBPage.LinkVisited = true;
            Tools.OpenENArticleInBrowser("Wikipedia:AutoWikiBrowser", false);
        }
        private void linkBugs_LinkClicked(object sender, LinkLabelLinkClickedEventArgs e)
        {
            linkBugs.LinkVisited = true;
            Tools.OpenENArticleInBrowser("Wikipedia_talk:AutoWikiBrowser/Bugs", false);
        }
        private void linkFeatureRequests_LinkClicked(object sender, LinkLabelLinkClickedEventArgs e)
        {
            linkFeatureRequests.LinkVisited = true;
            Tools.OpenENArticleInBrowser("Wikipedia_talk:AutoWikiBrowser/Feature_requests", false);
        }
        private void UsageStatsLabel_LinkClicked(object sender, LinkLabelLinkClickedEventArgs e)
        {
            UsageStats.OpenUsageStatsURL();
        }
    }
}
