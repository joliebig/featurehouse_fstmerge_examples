using System;
using System.Windows.Forms;
using System.Text.RegularExpressions;
using WikiFunctions.Controls;
namespace WikiFunctions.ReplaceSpecial
{
    public partial class RuleControl : UserControl
    {
        readonly IRuleControlOwner owner_;
        public RuleControl(IRuleControlOwner owner)
        {
            InitializeComponent();
            owner_ = owner;
            Anchor =
              AnchorStyles.Bottom | AnchorStyles.Left | AnchorStyles.Right | AnchorStyles.Top;
        }
        public void SetName(string name)
        {
            NameTextbox.Text = name;
        }
        public void SelectName()
        {
            NameTextbox.Select();
            NameTextbox.SelectAll();
        }
        public void SaveToRule(Rule r)
        {
            if (r == null)
                return;
            r.Name = NameTextbox.Text.Trim();
            r.replace_ = ReplaceTextbox.Text.Replace("\r\n", "\n");
            r.with_ = WithTextbox.Text.Replace("\r\n", "\n");
            r.ruletype_ = (Rule.T)RuleTypeCombobox.SelectedIndex;
            r.enabled_ = RuleEnabledCheckBox.Checked;
            r.regex_ = ReplaceIsRegexCheckbox.Checked;
            r.regexOptions_ = RegexOptions.None;
            if (!ReplaceIsCaseSensitiveCheckBox.Checked)
                r.regexOptions_ |= RegexOptions.IgnoreCase;
            if (ReplaceIsSinglelineCheckbox.Checked)
                r.regexOptions_ |= RegexOptions.Singleline;
            if (ReplaceIsMultilineCheckBox.Checked)
                r.regexOptions_ |= RegexOptions.Multiline;
            r.numoftimes_ = (int)NumberOfTimesUpDown.Value;
            r.ifContains_ = IfContainsTextBox.Text;
            r.ifNotContains_ = IfNotContainsTextBox.Text;
            r.ifIsRegex_ = IfIsRegexCheckBox.Checked;
            r.ifRegexOptions_ = RegexOptions.None;
            if (!IfIsCaseSensitiveCheckBox.Checked)
                r.ifRegexOptions_ |= RegexOptions.IgnoreCase;
            if (IfIsSinglelineCheckBox.Checked)
                r.ifRegexOptions_ |= RegexOptions.Singleline;
            if (IfIsMultilineCheckbox.Checked)
                r.ifRegexOptions_ |= RegexOptions.Multiline;
        }
        public void RestoreFromRule(Rule r)
        {
            NameTextbox.Text = r.Name;
            ReplaceTextbox.Text = r.replace_.Replace("\n", "\r\n");
            WithTextbox.Text = r.with_.Replace("\n", "\r\n");
            RuleTypeCombobox.SelectedIndex = (int)r.ruletype_;
            RuleEnabledCheckBox.Checked = r.enabled_;
            ReplaceIsRegexCheckbox.Checked = r.regex_;
            ReplaceIsCaseSensitiveCheckBox.Checked = (r.regexOptions_ & RegexOptions.IgnoreCase) == 0;
            ReplaceIsMultilineCheckBox.Checked = (r.regexOptions_ & RegexOptions.Multiline) > 0;
            ReplaceIsSinglelineCheckbox.Checked = (r.regexOptions_ & RegexOptions.Singleline) > 0;
            NumberOfTimesUpDown.Value = r.numoftimes_;
            IfContainsTextBox.Text = r.ifContains_;
            IfNotContainsTextBox.Text = r.ifNotContains_;
            IfIsRegexCheckBox.Checked = r.ifIsRegex_;
            IfIsCaseSensitiveCheckBox.Checked = (r.ifRegexOptions_ & RegexOptions.IgnoreCase) == 0;
            IfIsMultilineCheckbox.Checked = (r.ifRegexOptions_ & RegexOptions.Multiline) > 0;
            IfIsSinglelineCheckBox.Checked = (r.ifRegexOptions_ & RegexOptions.Singleline) > 0;
            UpdateRegexOptionCheckboxes();
        }
        private void UpdateRegexOptionCheckboxes()
        {
            bool enable = ReplaceIsRegexCheckbox.Checked;
            ReplaceIsCaseSensitiveCheckBox.Enabled = enable;
            ReplaceIsMultilineCheckBox.Enabled = enable;
            ReplaceIsSinglelineCheckbox.Enabled = enable;
            TestFind.Enabled = enable;
            enable = IfIsRegexCheckBox.Checked;
            IfIsMultilineCheckbox.Enabled = enable;
            IfIsSinglelineCheckBox.Enabled = enable;
            TestIf.Enabled = enable;
        }
        private void ReplaceIsRegexCheckbox_CheckedChanged(object sender, EventArgs e)
        {
            UpdateRegexOptionCheckboxes();
        }
        private void IfIsRegexCheckBox_CheckedChanged(object sender, EventArgs e)
        {
            UpdateRegexOptionCheckboxes();
        }
        private void NameTextbox_TextChanged(object sender, EventArgs e)
        {
            owner_.NameChanged(this, NameTextbox.Text.Trim());
        }
        private void NameTextbox_DoubleClick(object sender, EventArgs e)
        {
            NameTextbox.SelectAll();
        }
        private void TestIf_Click(object sender, EventArgs e)
        {
            contextMenu.Show(TestIf, 0, TestIf.Height);
        }
        private void TestFind_Click(object sender, EventArgs e)
        {
            RegexTester.Test(ParentForm, ReplaceTextbox, WithTextbox, ReplaceIsMultilineCheckBox,
                ReplaceIsSinglelineCheckbox, ReplaceIsCaseSensitiveCheckBox);
        }
        private void testIfContainsToolStripMenuItem_Click(object sender, EventArgs e)
        {
            RegexTester.Test(ParentForm, IfContainsTextBox, null, IfIsMultilineCheckbox,
                IfIsSinglelineCheckBox, IfIsCaseSensitiveCheckBox);
        }
        private void testIfNotContainsToolStripMenuItem_Click(object sender, EventArgs e)
        {
            RegexTester.Test(ParentForm, IfNotContainsTextBox, null, IfIsMultilineCheckbox,
                IfIsSinglelineCheckBox, IfIsCaseSensitiveCheckBox);
        }
    }
}
