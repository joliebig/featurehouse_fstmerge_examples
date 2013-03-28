using System.Collections.Generic;
using System.Windows.Forms;
namespace WikiFunctions.ReplaceSpecial
{
    public class RuleTreeHistory
    {
        readonly List<List<TreeNode> > History = new List<List<TreeNode> >();
        int index_ = -1;
        readonly TreeView treeView_;
        public RuleTreeHistory(TreeView tv)
        {
            treeView_ = tv;
        }
        public void Clear()
        {
            History.Clear();
            index_ = -1;
        }
        public void Save()
        {
            if (index_ != -1)
            {
                Clear();
                index_ = -1;
            }
            InternalSave();
        }
        private void InternalSave()
        {
            List<TreeNode> cp = Copy(treeView_.Nodes);
            History.Insert(0, cp);
        }
        public bool CanUndo
        {
            get
            {
                return (History.Count > 0) && (index_ == -1 || index_ + 1 < History.Count);
            }
        }
        public void Undo()
        {
            if (!CanUndo)
                return;
            if (index_ == -1)
            {
                InternalSave();
                index_ = 1;
            }
            else
            {
                ++index_;
            }
            Restore();
        }
        public bool CanRedo { get { return (History.Count > 0) && (index_ > 0); } }
        public void Redo()
        {
            if (!CanRedo)
                return;
            --index_;
            Restore();
        }
        private void Restore()
        {
            treeView_.Nodes.Clear();
            List<TreeNode> hcol = History[index_];
            foreach (TreeNode t in hcol)
            {
                TreeNode copy = (TreeNode)t.Clone();
                treeView_.Nodes.Add(copy);
                UpdateNames(copy);
            }
        }
        private static void UpdateNames(TreeNode t)
        {
            if (t == null)
                return;
            IRule r = (IRule)t.Tag;
            t.Text = r.Name;
            foreach (TreeNode sub in t.Nodes)
                UpdateNames(sub);
        }
        private static List<TreeNode> Copy(TreeNodeCollection col)
        {
            List<TreeNode> newCol = new List<TreeNode>();
            foreach (TreeNode t in col)
            {
                TreeNode copy = (TreeNode)t.Clone();
                newCol.Add(copy);
            }
            return newCol;
        }
    }
}
