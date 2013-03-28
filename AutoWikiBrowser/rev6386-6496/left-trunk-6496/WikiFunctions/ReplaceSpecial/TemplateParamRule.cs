using System;
using System.Windows.Forms;
using System.Text.RegularExpressions;
namespace WikiFunctions.ReplaceSpecial
{
    public class TemplateParamRule : IRule
    {
        public string ParamName = "", NewParamName = "";
        TemplateParamRuleControl ruleControl_;
        public override Object Clone()
        {
            TemplateParamRule res = (TemplateParamRule)MemberwiseClone();
            res.ruleControl_ = null;
            return res;
        }
        public TemplateParamRule()
        {
            Name = "Template Parameter Rule";
        }
        public override Control GetControl()
        {
            return ruleControl_;
        }
        public override void ForgetControl()
        {
            ruleControl_ = null;
        }
        public override Control CreateControl(IRuleControlOwner owner, Control.ControlCollection collection, System.Drawing.Point pos)
        {
            TemplateParamRuleControl rc = new TemplateParamRuleControl(owner) {Location = pos};
            rc.RestoreFromRule(this);
            DisposeControl();
            ruleControl_ = rc;
            collection.Add(rc);
            return rc;
        }
        public override void Save()
        {
            if (ruleControl_ == null)
                return;
            ruleControl_.SaveToRule(this);
        }
        public override void Restore()
        {
            if (ruleControl_ == null)
                return;
            ruleControl_.RestoreFromRule(this);
        }
        public override void SelectName()
        {
            if (ruleControl_ == null)
                return;
            ruleControl_.SelectName();
        }
        public override string Apply(TreeNode tn, string text, string title)
        {
            if (string.IsNullOrEmpty(text))
                return text;
            if (!enabled_)
                return text;
            string pattern = "(\\|[\\s]*)" + ParamName + "([\\s]*=)";
            text = Regex.Replace(text, pattern, "$1" + NewParamName + "$2");
            foreach (TreeNode t in tn.Nodes)
            {
                IRule sr = (IRule)t.Tag;
                text = sr.Apply(t, text, title);
            }
            return text;
        }
    }
}
