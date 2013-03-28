using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Windows.Forms;
using System.Text.RegularExpressions;
using System.Globalization;
using WikiFunctions.Controls.Lists;
using WikiFunctions.Lists.Providers;
namespace WikiFunctions.Lists
{
    public partial class ListFilterForm : Form
    {
        private readonly ListBoxArticle _destListBox;
        string _project = Variables.URL;
        public ListFilterForm(ListBoxArticle lb)
        {
            InitializeComponent();
            if (lb == null)
                throw new ArgumentNullException("lb");
            _destListBox = lb;
            if (_prefs != null)
                Settings = _prefs;
        }
        private List<Article> _list = new List<Article>();
        private static AWBSettings.SpecialFilterPrefs _prefs;
        private void btnApply_Click(object sender, EventArgs e)
        {
            try
            {
                if (chkRemoveDups.Checked)
                    RemoveDuplicates();
                if (chkSortAZ.Checked)
                    _destListBox.Sort();
                _list.Clear();
                foreach (Article a in _destListBox)
                    _list.Add(a);
                bool does = (chkContains.Checked && !string.IsNullOrEmpty(txtContains.Text));
                bool doesnot = (chkNotContains.Checked && !string.IsNullOrEmpty(txtDoesNotContain.Text));
                if (lbRemove.Items.Count > 0)
                    FilterList();
                if (does || doesnot)
                    FilterMatches(does, doesnot);
                if (_destListBox.Items.Count > 0 && _destListBox.Items[0] is Article)
                    FilterNamespace();
             _destListBox.BeginUpdate();
                _destListBox.Items.Clear();
                foreach (Article a in _list)
                    _destListBox.Items.Add(a);
             _destListBox.EndUpdate();
                if (_destListBox.Parent is ListMaker)
                    (_destListBox.Parent as ListMaker).UpdateNumberOfArticles();
            }
            catch (Exception ex)
            {
                ErrorHandler.Handle(ex);
            }
            DialogResult = DialogResult.OK;
        }
        public void RemoveDuplicates()
        {
            _list.Clear();
            foreach (Article a in _destListBox)
            {
                if (!_list.Contains(a))
                    _list.Add(a);
            }
            _destListBox.BeginUpdate();
            _destListBox.Items.Clear();
            foreach (Article a in _list)
                _destListBox.Items.Add(a);
            _destListBox.EndUpdate();
        }
        private void FilterNamespace()
        {
            List<int> selectedNS = pageNamespaces.GetSelectedNamespaces();
            if (selectedNS.Count == 0)
                return;
            int i = 0;
            while (i < _list.Count)
            {
                if (!selectedNS.Contains(_list[i].NameSpaceKey))
                    _list.RemoveAt(i);
                else
                    i++;
            }
        }
        private void FilterMatches(bool does, bool doesnot)
        {
            if (!does && !doesnot)
                return;
            try
            {
                Regex match = null, notMatch = null;
                if (does)
                    match = new Regex(!chkIsRegex.Checked ? Regex.Escape(txtContains.Text) : txtContains.Text,
                                      RegexOptions.Compiled);
                if (doesnot)
                    notMatch = new Regex(
                        !chkIsRegex.Checked ? Regex.Escape(txtDoesNotContain.Text) : txtDoesNotContain.Text,
                        RegexOptions.Compiled);
                int i = 0;
                while (i < _list.Count)
                {
                    if (does && match.IsMatch(_list[i].Name))
                        _list.RemoveAt(i);
                    else if (doesnot && !notMatch.IsMatch(_list[i].Name))
                        _list.RemoveAt(i);
                    else
                        i++;
                }
            }
            catch (Exception ex)
            {
                ErrorHandler.Handle(ex);
            }
        }
        private void FilterList()
        {
            List<Article> remove = new List<Article>();
            remove.AddRange(lbRemove);
            remove.Sort();
            List<Article> list2 = new List<Article>();
            if (cbOpType.SelectedIndex == 0)
            {
                foreach (Article a in _list)
                    if (BinarySearch(remove, a, 0, remove.Count - 1) == -1)
                        list2.Add(a);
            }
            else
            {
                foreach (Article a in _list)
                    if (BinarySearch(remove, a, 0, remove.Count - 1) != -1)
                        list2.Add(a);
            }
            _list = list2;
        }
        private static int BinarySearch(IList<Article> articleList, Article article, int left, int right)
        {
            if (right < left)
                return -1;
            int mid = (left + right) / 2;
            int compare = String.Compare(articleList[mid].ToString(), article.ToString(), false, CultureInfo.InvariantCulture);
            if (compare > 0)
                return BinarySearch(articleList, article, left, mid - 1);
            if (compare < 0)
                return BinarySearch(articleList, article, mid + 1, right);
            return mid;
        }
        private void btnGetList_Click(object sender, EventArgs e)
        {
            foreach (Article a in new TextFileListProvider().MakeList())
                lbRemove.Items.Add(a);
        }
        private void btnClear_Click(object sender, EventArgs e)
        {
            lbRemove.Items.Clear();
        }
        private void btnCancel_Click(object sender, EventArgs e)
        {
            DialogResult = DialogResult.Cancel;
        }
        private void chkContains_CheckedChanged(object sender, EventArgs e)
        {
            txtContains.Enabled = chkContains.Checked;
            chkIsRegex.Enabled = (chkContains.Checked || chkNotContains.Checked);
        }
        private void chkNotContains_CheckedChanged(object sender, EventArgs e)
        {
            txtDoesNotContain.Enabled = chkNotContains.Checked;
            chkIsRegex.Enabled = (chkContains.Checked || chkNotContains.Checked);
        }
        private void specialFilter_Load(object sender, EventArgs e)
        {
            cbOpType.SelectedIndex = 0;
        }
        internal void Clear()
        {
            _list.Clear();
        }
        private void SpecialFilter_VisibleChanged(object sender, EventArgs e)
        {
            if (Visible && _project != Variables.URL)
            {
                _project = Variables.URL;
                pageNamespaces.Populate();
            }
        }
        [Browsable(false)]
        [Localizable(false)]
        public AWBSettings.SpecialFilterPrefs Settings
        {
            get
            {
                _prefs = new AWBSettings.SpecialFilterPrefs()
                            {
                                namespaceValues = pageNamespaces.GetSelectedNamespaces(),
                                filterTitlesThatContain = chkContains.Checked,
                                filterTitlesThatContainText = txtContains.Text,
                                filterTitlesThatDontContain = chkNotContains.Checked,
                                filterTitlesThatDontContainText = txtDoesNotContain.Text,
                                areRegex = chkIsRegex.Checked,
                                remDupes = chkRemoveDups.Checked,
                                sortAZ = chkSortAZ.Checked,
                                opType = cbOpType.SelectedIndex
                            };
                foreach (Article a in lbRemove.Items)
                {
                    _prefs.remove.Add(a.Name);
                }
                return _prefs;
            }
            set
            {
                if (value == null || DesignMode)
                    return;
                _prefs = value;
                if (_prefs.namespaceValues == null)
                    _prefs.namespaceValues = new List<int>(new [] { 0, 1, 2, 3, 4, 5, 6, 7, 10, 11, 14, 15 });
                if (_prefs.namespaceValues.Count > 0)
                    pageNamespaces.SetSelectedNamespaces(_prefs.namespaceValues);
                chkContains.Checked = _prefs.filterTitlesThatContain;
                txtContains.Text = _prefs.filterTitlesThatContainText;
                chkNotContains.Checked = _prefs.filterTitlesThatDontContain;
                txtDoesNotContain.Text = _prefs.filterTitlesThatDontContainText;
                chkIsRegex.Checked = _prefs.areRegex;
                chkRemoveDups.Checked = _prefs.remDupes;
                chkSortAZ.Checked = _prefs.sortAZ;
                cbOpType.SelectedIndex = _prefs.opType;
                foreach (string s in _prefs.remove)
                    lbRemove.Items.Add(new Article(s));
            }
        }
    }
}
