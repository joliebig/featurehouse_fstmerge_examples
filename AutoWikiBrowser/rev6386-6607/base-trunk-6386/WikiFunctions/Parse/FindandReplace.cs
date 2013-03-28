using System;
using System.Windows.Forms;
using System.Text.RegularExpressions;
using System.Collections.Generic;
using WikiFunctions.Controls;
namespace WikiFunctions.Parse
{
    public partial class FindandReplace : Form
    {
        public FindandReplace()
        {
            InitializeComponent();
        }
        private readonly HideText _remove = new HideText(true, false, true);
        private readonly List<Replacement> _replacementList = new List<Replacement>();
        private bool _applyDefault;
        private bool ApplyDefaultFormatting
        {
            get { return _applyDefault; }
            set
            {
                _applyDefault = value;
                dataGridView1.AllowUserToAddRows = value;
            }
        }
        public static string Arrow
        {
            get
            {
                return " â ";
            }
        }
        private static Replacement RowToReplacement(DataGridViewRow dataGridRow)
        {
            Replacement rep = new Replacement()
                                  {
                                      Enabled = ((bool)dataGridRow.Cells["enabled"].FormattedValue),
                                      Minor = ((bool)dataGridRow.Cells["minor"].FormattedValue),
                                      IsRegex = ((bool)dataGridRow.Cells["regex"].FormattedValue)
                                  };
            if (dataGridRow.Cells["replace"].Value == null)
                dataGridRow.Cells["replace"].Value = "";
            string f = Encode(dataGridRow.Cells["find"].Value.ToString());
            string r = Encode(dataGridRow.Cells["replace"].Value.ToString());
            if (!rep.IsRegex)
                f = Regex.Escape(f);
            rep.Find = f;
            rep.Replace = r;
            if (!(bool)dataGridRow.Cells["casesensitive"].FormattedValue)
                rep.RegularExpressionOptions |= RegexOptions.IgnoreCase;
            if ((bool)dataGridRow.Cells["multi"].FormattedValue)
                rep.RegularExpressionOptions |= RegexOptions.Multiline;
            if ((bool)dataGridRow.Cells["single"].FormattedValue)
                rep.RegularExpressionOptions |= RegexOptions.Singleline;
            rep.Comment = (string)dataGridRow.Cells["comment"].FormattedValue ?? "";
            return rep;
        }
        public void MakeList()
        {
            _replacementList.Clear();
            foreach (DataGridViewRow dataGridRow in dataGridView1.Rows)
            {
                if (dataGridRow.IsNewRow)
                    continue;
                if (dataGridRow.Cells["find"].Value == null)
                    continue;
                _replacementList.Add(RowToReplacement(dataGridRow));
            }
        }
        private static readonly Regex NewlineRegex = new Regex(@"(?<!\\)\\n", RegexOptions.Compiled),
            TabulationRegex = new Regex(@"(?<!\\)\\t", RegexOptions.Compiled);
        private static string PrepareReplacePart(string replace)
        {
            replace = NewlineRegex.Replace(replace, "\n");
            return TabulationRegex.Replace(replace, "\t");
        }
        public int NoOfReplacements { get { return _replacementList.Count; } }
        public bool HasReplacements { get { return NoOfReplacements != 0; } }
        public string MultipleFindAndReplace(string articleText, string strTitle, ref string editSummary, out bool majorChangesMade)
        {
            majorChangesMade = false;
            if (!HasReplacements)
                return articleText;
            _replacedSummary = "";
            _removedSummary = "";
            if (chkIgnoreMore.Checked)
                articleText = _remove.HideMore(articleText);
            else if (chkIgnoreLinks.Checked)
                articleText = _remove.Hide(articleText);
            foreach (Replacement rep in _replacementList)
            {
                if (!rep.Enabled)
                    continue;
                bool changeMade;
                articleText = PerformFindAndReplace(rep, articleText, strTitle, out changeMade);
                if (changeMade && !rep.Minor)
                    majorChangesMade = true;
            }
            if (chkIgnoreMore.Checked)
                articleText = _remove.AddBackMore(articleText);
            else if (chkIgnoreLinks.Checked)
                articleText = _remove.AddBack(articleText);
            if (chkAddToSummary.Checked)
            {
                if (!string.IsNullOrEmpty(_replacedSummary))
                      editSummary = "replaced: " + _replacedSummary.Trim();
                if (!string.IsNullOrEmpty(_removedSummary))
                {
                    if (!string.IsNullOrEmpty(editSummary))
                        editSummary += ", ";
                    editSummary += "removed: " + _removedSummary.Trim();
                }
            }
            return articleText;
        }
        private string _removedSummary, _replacedSummary;
        private string PerformFindAndReplace(Replacement rep, string articleText, string articleTitle, out bool changeMade)
        {
            if (rep == null) throw new ArgumentNullException("rep");
            string findThis = Tools.ApplyKeyWords(articleTitle, rep.Find);
            string replaceWith = Tools.ApplyKeyWords(articleTitle, PrepareReplacePart(rep.Replace));
            Regex findRegex = new Regex(findThis, rep.RegularExpressionOptions);
            MatchCollection matches = findRegex.Matches(articleText);
            changeMade = false;
            if (matches.Count > 0)
            {
                articleText = findRegex.Replace(articleText, replaceWith);
                if (matches[0].Value != matches[0].Result(replaceWith))
                {
                    changeMade = true;
                    if (!string.IsNullOrEmpty(matches[0].Result(replaceWith)))
                    {
                        if (!string.IsNullOrEmpty(_replacedSummary))
                            _replacedSummary += ", ";
                        string summary = matches[0].Value + Arrow + matches[0].Result(replaceWith);
                        if (matches.Count > 1)
                            summary += " (" + matches.Count + ")";
                        _replacedSummary += summary;
                    }
                    else
                    {
                        if (!string.IsNullOrEmpty(_removedSummary))
                            _removedSummary += ", ";
                        _removedSummary += matches[0].Value;
                        if (matches.Count > 1)
                            _removedSummary += " (" + matches.Count + ")";
                    }
                }
            }
            return articleText;
        }
        private void btnDone_Click(object sender, EventArgs e)
        {
            DialogResult = DialogResult.OK;
        }
        private void linkLabel2_LinkClicked(object sender, LinkLabelLinkClickedEventArgs e)
        {
            lnkWpRE.LinkVisited = true;
            Tools.OpenENArticleInBrowser("Regular_expression", false);
        }
        private void lblMsdn_LinkClicked(object sender, LinkLabelLinkClickedEventArgs e)
        {
            lblMsdn.LinkVisited = true;
            Tools.OpenURLInBrowser("http://msdn.microsoft.com/en-us/library/hs600312.aspx");
        }
        private void btnClear_Click(object sender, EventArgs e)
        {
            if (MessageBox.Show("Do you really want to clear the whole table?", "Really clear?", MessageBoxButtons.YesNo,
                                MessageBoxIcon.Question) == DialogResult.Yes)
                Clear();
        }
        public void Clear()
        {
            _replacementList.Clear();
            dataGridView1.Rows.Clear();
        }
        private static string Encode(string text)
        {
            return text.Replace("\\r\\n", "\r\n");
        }
        private static string Decode(string text)
        {
            return text.Replace("\n", "\\r\\n");
        }
        public void AddNew(string findText, string replaceWith, bool caseSensitive, bool isRegex, bool multiline, bool singleline, bool lineEnabled, string lineComment)
        {
            dataGridView1.Rows.Add(findText, replaceWith, caseSensitive, isRegex, multiline, singleline, lineEnabled, lineComment);
            MakeList();
        }
        public void AddNew(Replacement r)
        {
            bool caseSens = (r.RegularExpressionOptions & RegexOptions.IgnoreCase) != RegexOptions.IgnoreCase;
            bool multiline = (r.RegularExpressionOptions & RegexOptions.Multiline) == RegexOptions.Multiline;
            bool singleLine = (r.RegularExpressionOptions & RegexOptions.Singleline) == RegexOptions.Singleline;
            dataGridView1.Rows.Add(r.IsRegex ? Decode(r.Find) : Regex.Unescape(Decode(r.Find)), Decode(r.Replace),
                                   caseSens, r.IsRegex, multiline, singleLine, r.Minor, r.Enabled, r.Comment);
            _replacementList.Add(r);
        }
        public void AddNew(List<Replacement> rList)
        {
            foreach (Replacement r in rList)
            {
                AddNew(r);
            }
        }
        public List<Replacement> GetList()
        {
            return _replacementList;
        }
        public bool IgnoreLinks
        {
            get { return chkIgnoreLinks.Checked; }
            set { chkIgnoreLinks.Checked = value; }
        }
        public bool IgnoreMore
        {
            get { return chkIgnoreMore.Checked; }
            set { chkIgnoreMore.Checked = value; }
        }
        public bool AppendToSummary
        {
            get { return chkAddToSummary.Checked; }
            set { chkAddToSummary.Checked = value; }
        }
        public bool AfterOtherFixes
        {
            get { return chkAfterOtherFixes.Checked; }
            set { chkAfterOtherFixes.Checked = value; }
        }
        private void ChangeChecked(string col, bool value)
        {
            dataGridView1.EndEdit();
            foreach (DataGridViewRow r in dataGridView1.Rows)
            {
                if (r.IsNewRow)
                    break;
                r.Cells[col].Value = value;
            }
        }
        private void dataGridView1_RowsAdded(object sender, DataGridViewRowsAddedEventArgs e)
        {
            if (!ApplyDefaultFormatting)
                return;
            dataGridView1.Rows[e.RowIndex].Cells["enabled"].Value = true;
        }
        private void FindandReplace_Shown(object sender, EventArgs e)
        {
            ApplyDefaultFormatting = true;
        }
        private void FindandReplace_FormClosing(object sender, FormClosingEventArgs e)
        {
            ApplyDefaultFormatting = false;
            MakeList();
            Hide();
        }
        private void allCaseSensitiveToolStripMenuItem_Click(object sender, EventArgs e)
        {
            ChangeChecked("casesensitive", true);
        }
        private void uncheckAllCaseSensitiveToolStripMenuItem_Click(object sender, EventArgs e)
        {
            ChangeChecked("casesensitive", false);
        }
        private void checkAllRegularExpressionsToolStripMenuItem_Click(object sender, EventArgs e)
        {
            ChangeChecked("regex", true);
        }
        private void uncheckAllRegularExpressionsToolStripMenuItem_Click(object sender, EventArgs e)
        {
            ChangeChecked("regex", false);
        }
        private void checkAllMultlineToolStripMenuItem_Click(object sender, EventArgs e)
        {
            ChangeChecked("multi", true);
        }
        private void uncheckAllMultilineToolStripMenuItem_Click(object sender, EventArgs e)
        {
            ChangeChecked("multi", false);
        }
        private void enableAllToolStripMenuItem_Click(object sender, EventArgs e)
        {
            ChangeChecked("enabled", true);
        }
        private void disableAllToolStripMenuItem_Click(object sender, EventArgs e)
        {
            ChangeChecked("enabled", false);
        }
        private void checkAllSinglelineToolStripMenuItem_Click(object sender, EventArgs e)
        {
            ChangeChecked("single", true);
        }
        private void uncheckAllSinglelineToolStripMenuItem_Click(object sender, EventArgs e)
        {
            ChangeChecked("single", false);
        }
        private void deleteRowToolStripMenuItem_Click(object sender, EventArgs e)
        {
            while (dataGridView1.SelectedRows.Count > 0)
            {
                if (dataGridView1.Rows[dataGridView1.SelectedRows[0].Index].IsNewRow)
                {
                    dataGridView1.SelectedRows[0].Selected = false;
                    continue;
                }
                dataGridView1.Rows.RemoveAt(dataGridView1.SelectedRows[0].Index);
            }
        }
        private void FindAndReplaceContextMenu_Opening(object sender, System.ComponentModel.CancelEventArgs e)
        {
            deleteRowToolStripMenuItem.Enabled = (dataGridView1.SelectedRows.Count > 0);
            testRegexToolStripMenuItem.Enabled = createRetfRuleToolStripMenuItem.Enabled =
                ((dataGridView1.CurrentRow != null)
                && ((bool)dataGridView1.CurrentRow.Cells["regex"].FormattedValue));
        }
        private void testRegexToolStripMenuItem_Click(object sender, EventArgs e)
        {
            DataGridViewRow row = dataGridView1.CurrentRow;
            if (row == null) return;
            using (RegexTester t = new RegexTester(true))
            {
                t.Find = (string)row.Cells["find"].Value;
                t.Replace = (string)row.Cells["replace"].Value;
                t.Multiline = (bool)row.Cells["multi"].FormattedValue;
                t.Singleline = (bool)row.Cells["single"].FormattedValue;
                t.IgnoreCase = !(bool)row.Cells["casesensitive"].FormattedValue;
                if (Variables.MainForm != null && Variables.MainForm.EditBox.Enabled)
                    t.ArticleText = Variables.MainForm.EditBox.Text;
                if (t.ShowDialog(this) != DialogResult.OK) return;
                row.Cells["find"].Value = t.Find;
                row.Cells["replace"].Value = t.Replace;
                row.Cells["multi"].Value = t.Multiline;
                row.Cells["single"].Value = t.Singleline;
                row.Cells["casesensitive"].Value = !t.IgnoreCase;
            }
        }
        private void chkIgnoreMore_CheckedChanged(object sender, EventArgs e)
        {
            if (chkIgnoreMore.Checked)
                chkIgnoreLinks.Checked = false;
        }
        private void chkIgnoreLinks_CheckedChanged(object sender, EventArgs e)
        {
            if (chkIgnoreLinks.Checked)
                chkIgnoreMore.Checked = false;
        }
        private void txtSearch_TextChanged(object sender, EventArgs e)
        {
            btnSearch.Enabled = txtSearch.Text.Length > 0;
        }
        private void btnSearch_Click(object sender, EventArgs e)
        {
            for (int i = 0; i < 2; i++)
                for (int j = 0; j < dataGridView1.Rows.Count; j++)
                {
                    DataGridViewCell c = dataGridView1.Rows[j].Cells[i];
                    if (c.Value != null && c.Value.ToString().Contains(txtSearch.Text))
                    {
                        dataGridView1.ClearSelection();
                        dataGridView1.EndEdit();
                        dataGridView1.CurrentCell = c;
                        if (!c.Displayed) dataGridView1.FirstDisplayedScrollingRowIndex = c.RowIndex;
                        dataGridView1.Focus();
                        dataGridView1.BeginEdit(false);
                    }
                }
        }
        private void txtSearch_KeyPress(object sender, KeyPressEventArgs e)
        {
            if (e.KeyChar == 13)
            {
                e.Handled = true;
                btnSearch_Click(null, null);
            }
        }
        private void addRowToolStripMenuItem_Click(object sender, EventArgs e)
        {
            if (dataGridView1.SelectedRows.Count > 0)
                dataGridView1.Rows.Insert(dataGridView1.SelectedRows[0].Index, true);
            else
                dataGridView1.Rows.Add();
        }
        private void moveUpToolStripMenuItem_Click(object sender, EventArgs e)
        {
            foreach (DataGridViewRow row in dataGridView1.SelectedRows)
            {
                if (row.Index > 0 && row.Index < (dataGridView1.Rows.Count - 1))
                {
                    int index = row.Index;
                    DataGridViewRow tmp = row;
                    dataGridView1.Rows.Remove(row);
                    dataGridView1.Rows.Insert(index - 1, tmp);
                }
            }
        }
        private void moveDownToolStripMenuItem_Click(object sender, EventArgs e)
        {
            foreach (DataGridViewRow row in dataGridView1.SelectedRows)
            {
                if (row.Index < (dataGridView1.Rows.Count - 2))
                {
                    int index = row.Index;
                    DataGridViewRow tmp = row;
                    dataGridView1.Rows.Remove(row);
                    dataGridView1.Rows.Insert(index + 1, tmp);
                }
            }
        }
        private void moveToTopToolStripMenuItem_Click(object sender, EventArgs e)
        {
            foreach (DataGridViewRow row in dataGridView1.SelectedRows)
            {
                if (row.Index > 0 && row.Index < (dataGridView1.Rows.Count - 1))
                {
                    DataGridViewRow tmp = row;
                    dataGridView1.Rows.Remove(row);
                    dataGridView1.Rows.Insert(0, tmp);
                }
            }
        }
        private void moveToBottomToolStripMenuItem_Click(object sender, EventArgs e)
        {
            foreach (DataGridViewRow row in dataGridView1.SelectedRows)
            {
                if (row.Index < (dataGridView1.Rows.Count - 2))
                {
                    DataGridViewRow tmp = row;
                    dataGridView1.Rows.Remove(row);
                    dataGridView1.Rows.Add(tmp);
                }
            }
        }
        private void createRetfRuleToolStripMenuItem_Click(object sender, EventArgs e)
        {
            DataGridViewRow row = dataGridView1.CurrentRow;
            if (row == null)
                return;
            string typoName = (string)row.Cells["Comment"].Value;
            if (string.IsNullOrEmpty(typoName))
                typoName = "<enter a name>";
            Tools.CopyToClipboard(RegExTypoFix.CreateRule((string)row.Cells["find"].Value,
                                                          (string)row.Cells["replace"].Value, typoName));
        }
        private void checkAllMinorToolStripMenuItem_Click(object sender, EventArgs e)
        {
            ChangeChecked("minor", true);
        }
        private void uncheckAllMinorToolStripMenuItem_Click(object sender, EventArgs e)
        {
            ChangeChecked("minor", false);
        }
    }
    public class Replacement
    {
        public Replacement()
        {
            RegularExpressionOptions = RegexOptions.None;
        }
        public Replacement(string find, string replace, bool isRegex, bool enabled, bool minor, RegexOptions regularExpressionOptions, string comment)
        {
            Find = find;
            Replace = replace;
            IsRegex = isRegex;
            Enabled = enabled;
            Minor = minor;
            RegularExpressionOptions = regularExpressionOptions;
            Comment = comment;
        }
        public string Find,
            Replace, Comment;
        public bool IsRegex,
            Enabled, Minor;
        public RegexOptions RegularExpressionOptions;
    }
}
