using System;
using System.Collections.Generic;
using System.Windows.Forms;
using System.Text.RegularExpressions;
namespace WikiFunctions
{
    public partial class SubstTemplates : Form
    {
        public SubstTemplates()
        {
            InitializeComponent();
        }
        private string[] LocTemplateList = new string[0];
        private readonly Dictionary<Regex, string> Regexes = new Dictionary<Regex, string>();
        private readonly Parse.HideText RemoveUnformatted = new Parse.HideText(true, false, true);
        public string[] TemplateList
        {
            get { return LocTemplateList; }
            set
            {
                textBoxTemplates.Lines = LocTemplateList = value;
                textBoxTemplates.Select(0, 0);
                RefreshRegexes();
            }
        }
        public bool ExpandRecursively
        {
            get { return chkUseExpandTemplates.Checked; }
            set { chkUseExpandTemplates.Checked = value; }
        }
        public bool IgnoreUnformatted
        {
            get { return chkIgnoreUnformatted.Checked; }
            set { chkIgnoreUnformatted.Checked = value; }
        }
        public bool IncludeComments
        {
            get { return chkIncludeComment.Checked; }
            set { chkIncludeComment.Checked = value; }
        }
        public void Clear()
        {
            LocTemplateList = new string[0];
            Regexes.Clear();
        }
        private void RefreshRegexes()
        {
            Regexes.Clear();
            string templ = Variables.NamespacesCaseInsensitive[Namespace.Template];
            if (templ[0] == '(')
                templ = "(" + templ.Insert(templ.IndexOf(')'), "|[Mm]sg") + ")?";
            else
                templ = @"(?:" + templ + "|[Mm]sg:|)";
            foreach (string s in TemplateList)
            {
                if (string.IsNullOrEmpty(s.Trim())) continue;
                Regexes.Add(new Regex(@"\{\{\s*" + templ + Tools.CaseInsensitive(s) + @"\s*(\|[^\}]*|)\}\}",
                    RegexOptions.Singleline | RegexOptions.Compiled), @"{{subst:" + s + "$1}}");
            }
        }
        private void btnClear_Click(object sender, EventArgs e)
        {
            textBoxTemplates.Text = "";
        }
        private void btnOk_Click(object sender, EventArgs e)
        {
            TemplateList = textBoxTemplates.Lines;
            Close();
        }
        private void btnReset_Click(object sender, EventArgs e)
        {
            textBoxTemplates.Lines = TemplateList;
        }
        public int NoOfRegexes { get { return Regexes.Count; } }
        public bool HasSubstitutions { get { return NoOfRegexes != 0; } }
        public string SubstituteTemplates(string articleText, string articleTitle)
        {
            if (!HasSubstitutions)
                return articleText;
            if (chkIgnoreUnformatted.Checked)
                articleText = RemoveUnformatted.HideUnformatted(articleText);
            if (!chkUseExpandTemplates.Checked)
            {
                foreach (KeyValuePair<Regex, string> p in Regexes)
                {
                    articleText = p.Key.Replace(articleText, p.Value);
                }
            }
            else
                articleText = Tools.ExpandTemplate(articleText, articleTitle, Regexes, chkIncludeComment.Checked);
            if (chkIgnoreUnformatted.Checked)
                articleText = RemoveUnformatted.AddBackUnformatted(articleText);
            return articleText;
        }
        private void chkUseExpandTemplates_CheckedChanged(object sender, EventArgs e)
        {
            chkIncludeComment.Enabled = chkUseExpandTemplates.Checked;
        }
    }
}
