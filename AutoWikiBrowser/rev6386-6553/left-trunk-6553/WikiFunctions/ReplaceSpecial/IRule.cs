using System;
using System.Collections.Generic;
using System.Windows.Forms;
namespace WikiFunctions.ReplaceSpecial
{
    [System.Xml.Serialization.XmlInclude(typeof(Rule))]
    [System.Xml.Serialization.XmlInclude(typeof(TemplateParamRule))]
    [System.Xml.Serialization.XmlInclude(typeof(InTemplateRule))]
    public abstract class IRule : ICloneable
    {
        public bool enabled_ = true;
        public List<IRule> Children;
        public string Name { get; set; }
        public abstract Control GetControl();
        public abstract void ForgetControl();
        public abstract void SelectName();
        public abstract void Save();
        public abstract void Restore();
        public abstract Control CreateControl(IRuleControlOwner owner, Control.ControlCollection collection, System.Drawing.Point pos);
        public void DisposeControl()
        {
            Control old = GetControl();
            if (old == null)
                return;
            ForgetControl();
            old.Hide();
            if (old.Parent != null)
                old.Parent.Controls.Remove(old);
            old.Dispose();
        }
        public abstract string Apply(TreeNode tn, string text, string title);
        public abstract Object Clone();
        public static TreeNode CloneTreeNode(TreeNode tn)
        {
            if (tn == null)
                return null;
            TreeNode res = (TreeNode)tn.Clone();
            CloneTags(res);
            return res;
        }
        private static void CloneTags(TreeNode tn)
        {
            IRule r = (IRule)tn.Tag;
            tn.Tag = r.Clone();
            foreach (TreeNode t in tn.Nodes)
            {
                CloneTags(t);
            }
        }
    }
}
