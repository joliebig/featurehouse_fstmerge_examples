using System;
using System.Windows.Forms;
namespace WikiFunctions.ReplaceSpecial
{
    public partial class TemplateParamRuleControl : UserControl
    {
        readonly IRuleControlOwner owner_;
        public TemplateParamRuleControl(IRuleControlOwner owner)
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
        public void SaveToRule(TemplateParamRule r)
        {
            if (r == null)
                return;
            r.enabled_ = RuleEnabledCheckBox.Checked;
            r.Name = NameTextbox.Text.Trim();
            r.ParamName = ParamNameTextBox.Text.Trim();
            r.NewParamName = ChangeNameToTextBox.Text.Trim();
        }
        public void RestoreFromRule(TemplateParamRule r)
        {
            if (r == null)
                return;
            RuleEnabledCheckBox.Checked = r.enabled_;
            NameTextbox.Text = r.Name;
            ParamNameTextBox.Text = r.ParamName;
            ChangeNameToTextBox.Text = r.NewParamName;
        }
        private void NameTextbox_TextChanged(object sender, EventArgs e)
        {
            owner_.NameChanged(this, NameTextbox.Text.Trim());
        }
    }
}
