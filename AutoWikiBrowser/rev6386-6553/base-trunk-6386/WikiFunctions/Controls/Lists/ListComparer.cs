using System;
using System.Collections.Generic;
using System.Windows.Forms;
namespace WikiFunctions.Controls.Lists
{
    public partial class ListComparer : Form
    {
        private readonly ListMaker _mainFormListMaker;
        public ListComparer(ListMaker lmMain)
        {
            InitializeComponent();
            if (lmMain != null)
            {
                btnMoveOnly1.Enabled = btnMoveOnly2.Enabled = btnMoveCommon.Enabled = true;
                _mainFormListMaker = lmMain;
            }
            listMaker1.MakeListEnabled = true;
            listMaker2.MakeListEnabled = true;
        }
        public ListComparer(ListMaker lmMain, List<Article> list)
            : this(lmMain)
        {
            listMaker1.Add(list);
        }
        private void CompareLists()
        {
            List<Article> list1 = listMaker1.GetArticleList();
            list1.Sort();
            List<Article> list2 = listMaker2.GetArticleList();
            list2.Sort();
            if (listMaker1.Count < listMaker2.Count)
                CompareLists(list1, list2, lbNo1, lbNo2, lbBoth);
            else
                CompareLists(list2, list1, lbNo2, lbNo1, lbBoth);
            UpdateCounts();
        }
        private static void CompareLists(IList<Article> list1, List<Article> list2, ListBox lb1, ListBox lb2, ListBox lb3)
        {
            lb1.BeginUpdate();
            lb2.BeginUpdate();
            lb3.BeginUpdate();
            while (list1.Count > 0)
            {
                Article a = list1[0];
                if (list2.Contains(a))
                {
                    lb3.Items.Add(a.Name);
                    if (list2.IndexOf(a) > 0)
                    {
                     foreach (Article a2 in list2.GetRange(0, list2.IndexOf(a) - 1))
                     {
                       lb2.Items.Add(a2.Name);
                          list2.Remove(a2);
                     }
                    }
                    list2.Remove(a);
                }
                else
                    lb1.Items.Add(a.Name);
                list1.Remove(a);
            }
            foreach (Article article in list2)
            {
                lb2.Items.Add(article.Name);
            }
            lb1.EndUpdate();
            lb2.EndUpdate();
            lb3.EndUpdate();
        }
        private void btnGo_Click(object sender, EventArgs e)
        {
            Clear();
            CompareLists();
        }
        private void btnClear_Click(object sender, EventArgs e)
        {
            Clear();
            UpdateCounts();
        }
        private void Clear()
        {
            lbBoth.Items.Clear();
            lbNo1.Items.Clear();
            lbNo2.Items.Clear();
        }
        private void UpdateCounts()
        {
            lblNo1.Text = lbNo1.Items.Count + " pages";
            lblNo2.Text = lbNo2.Items.Count + " pages";
            lblNoBoth.Text = lbBoth.Items.Count + " pages";
        }
        private void btnSaveOnly1_Click(object sender, EventArgs e)
        {
            SaveList(lbNo1);
        }
        private void btnSaveOnly2_Click(object sender, EventArgs e)
        {
            SaveList(lbNo2);
        }
        private void btnSave_Click(object sender, EventArgs e)
        {
            SaveList(lbBoth);
        }
        private static void SaveList(ListBox2<string> lb)
        {
            if (lb.Items.Count == 0)
            {
                MessageBox.Show("Nothing to save", "No items in List Boxes");
                return;
            }
            lb.SaveList();
        }
        private void transferDuplicatesToList1ToolStripMenuItem_Click(object sender, EventArgs e)
        {
            listMaker1.BeginUpdate();
            foreach (string str in MenuItemOwner(sender).Items)
                listMaker1.Add(str);
            listMaker1.EndUpdate();
        }
        private void transferToListMaker2ToolStripMenuItem_Click(object sender, EventArgs e)
        {
            listMaker2.BeginUpdate();
            foreach (string str in MenuItemOwner(sender).Items)
                listMaker2.Add(str);
            listMaker2.EndUpdate();
        }
        private void openInBrowserToolStripMenuItem_Click(object sender, EventArgs e)
        {
            foreach (string article in MenuItemOwner(sender).SelectedItems)
                Tools.OpenArticleInBrowser(article);
        }
        private void copyToolStripMenuItem_Click(object sender, EventArgs e)
        {
            Tools.Copy(MenuItemOwner(sender));
        }
        private static ListBoxString MenuItemOwner(object sender)
        {
            try { return ((ListBoxString)((ContextMenuStrip)sender).SourceControl); }
            catch
            {
                return (ListBoxString)(((ContextMenuStrip)((ToolStripMenuItem)sender).Owner).SourceControl);
            }
        }
        private void btnMoveOnly1_Click(object sender, EventArgs e)
        {
            _mainFormListMaker.BeginUpdate();
            foreach (string a in lbNo1.Items)
                _mainFormListMaker.Add(a);
            _mainFormListMaker.EndUpdate();
        }
        private void btnMoveCommon_Click(object sender, EventArgs e)
        {
            _mainFormListMaker.BeginUpdate();
            foreach (string a in lbBoth.Items)
                _mainFormListMaker.Add(a);
            _mainFormListMaker.EndUpdate();
        }
        private void btnMoveOnly2_Click(object sender, EventArgs e)
        {
            _mainFormListMaker.BeginUpdate();
            foreach (string a in lbNo2.Items)
                _mainFormListMaker.Add(a);
            _mainFormListMaker.EndUpdate();
        }
        private void removeSelectedToolStripMenuItem_Click(object sender, EventArgs e)
        {
            MenuItemOwner(sender).RemoveSelected();
            UpdateCounts();
        }
    }
}
