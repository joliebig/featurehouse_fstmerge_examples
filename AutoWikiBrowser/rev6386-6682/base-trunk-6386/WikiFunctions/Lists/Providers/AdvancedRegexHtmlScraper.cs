using System;
using System.Collections.Generic;
using System.Text.RegularExpressions;
using System.Windows.Forms;
using WikiFunctions.Controls;
namespace WikiFunctions.Lists.Providers
{
    public partial class AdvancedRegexHtmlScraper : Form, IListProvider
    {
        private Regex _regexToUse;
        private int _groupNumber;
        public AdvancedRegexHtmlScraper()
        {
            InitializeComponent();
        }
        public List<Article> MakeList(params string[] searchCriteria)
        {
            if (Visible)
                return null;
            List<Article> list = new List<Article>();
            if (ShowDialog() == DialogResult.OK)
            {
                foreach (string url in searchCriteria)
                {
                    string urlBuilt = url.Contains("http") ? url : "http://" + url;
                    foreach (Match m in _regexToUse.Matches(Tools.GetHTML(urlBuilt)))
                    {
                        if (m.Groups[_groupNumber].Length > 0)
                        {
                            list.Add(new Article(ModifyArticleName(m.Groups[_groupNumber].Value)));
                        }
                    }
                }
            }
            return list;
        }
        private static string ModifyArticleName(string title)
        {
            title = Regex.Replace(title, @"&#0?39;|&#146;|&amp;#0?39;|&amp;#146;|[`â]", "'");
            title = title.Replace(@"&amp;", "&");
            title = title.Replace(@"&quot;", @"""");
            return title.Replace("<br />", "");
        }
        public string DisplayText
        {
            get { return "HTML Scraper (Advanced Regex)"; }
        }
        public string UserInputTextBoxText
        {
            get { return "Url:"; }
        }
        public bool UserInputTextBoxEnabled
        {
            get { return true; }
        }
        public void Selected()
        {
        }
        public bool RunOnSeparateThread
        {
            get { return true; }
        }
        private void cutToolStripMenuItem_Click(object sender, EventArgs e)
        {
            RegexTextBox.Cut();
        }
        private void copyToolStripMenuItem_Click(object sender, EventArgs e)
        {
            RegexTextBox.Copy();
        }
        private void pasteToolStripMenuItem_Click(object sender, EventArgs e)
        {
            RegexTextBox.Paste();
        }
        private void copyToRegexTesterToolStripMenuItem_Click(object sender, EventArgs e)
        {
            using (RegexTester t = new RegexTester(true))
            {
                t.Find = RegexTextBox.Text;
                t.IgnoreCase = !CaseSensitiveCheckBox.Checked;
                t.Multiline = MultiLineCheckBox.Checked;
                t.Singleline = SingleLineCheckBox.Checked;
                if (t.ShowDialog(this) != DialogResult.OK) return;
                RegexTextBox.Text = t.Find;
                CaseSensitiveCheckBox.Checked = t.IgnoreCase;
                MultiLineCheckBox.Checked = t.Multiline;
                SingleLineCheckBox.Checked = t.Singleline;
            }
        }
        private void AdvancedRegexHtmlScraper_FormClosing(object sender, FormClosingEventArgs e)
        {
            RegexOptions opts = RegexOptions.Compiled;
            if (CaseSensitiveCheckBox.Checked)
                opts |= RegexOptions.IgnoreCase;
            if (SingleLineCheckBox.Checked)
                opts |= RegexOptions.Singleline;
            if (MultiLineCheckBox.Checked)
                opts |= RegexOptions.Multiline;
            if (_regexToUse == null || _regexToUse.ToString() != RegexTextBox.Text || _regexToUse.Options != opts)
            {
                try
                {
                    _regexToUse = new Regex(RegexTextBox.Text, opts);
                }
                catch (ArgumentException ae)
                {
                    _regexToUse = null;
                    e.Cancel = true;
                    MessageBox.Show(ae.Message, "Bad Regex");
                }
            }
            _groupNumber = (int)GroupNumber.Value;
        }
        private void OkButton_Click(object sender, EventArgs e)
        {
            Close();
        }
        private void CancelButton_Click(object sender, EventArgs e)
        {
            Close();
        }
    }
}
