using System;
using System.Collections.Generic;
using System.Collections;
using System.ComponentModel;
using System.Drawing;
using System.Windows.Forms;
using System.Threading;
using System.Text.RegularExpressions;
using WikiFunctions.API;
using WikiFunctions.Lists;
using WikiFunctions.Lists.Providers;
namespace WikiFunctions.Controls.Lists
{
    public delegate void ListMakerEventHandler(object sender, EventArgs e);
    public delegate void ListMakerProviderAdded(IListProvider provider);
    public partial class ListMaker : UserControl, IList<Article>
    {
        private readonly static BindingList<IListProvider> DefaultProviders = new BindingList<IListProvider>();
        private readonly ListFilterForm _specialFilter;
        private readonly BindingList<IListProvider> _listProviders = new BindingList<IListProvider>();
        private static readonly IListProvider RedirectLProvider = new RedirectsListProvider(),
                                              WhatLinksHereLProvider = new WhatLinksHereListProvider(),
                                              WhatTranscludesLProvider = new WhatTranscludesPageListProvider(),
                                              CategoriesOnPageLProvider = new CategoriesOnPageListProvider(),
                                              NewPagesLProvider = new NewPagesListProvider(),
                                              RandomPagesLProvider = new RandomPagesSpecialPageProvider(),
                                              HtmlScraperLProvider = new HTMLPageScraperListProvider(),
                                              AdvHtmlScraperLProvider = new AdvancedRegexHtmlScraper(),
                                              CheckWikiLProvider = new CheckWikiListProvider(),
                                              UserContribLProvider = new UserContribsListProvider();
        public event ListMakerEventHandler StatusTextChanged,
            BusyStateChanged, NoOfArticlesChanged;
        public bool FilterNonMainAuto, AutoAlpha, FilterDuplicates;
        public event ListMakerEventHandler ListFinished;
        public static ListMakerProviderAdded ListProviderAdded;
        static ListMaker()
        {
            if (DefaultProviders.Count == 0)
            {
                DefaultProviders.Add(new CategoryListProvider());
                DefaultProviders.Add(new CategoryRecursiveListProvider());
                DefaultProviders.Add(new CategoryRecursiveOneLevelListProvider());
                DefaultProviders.Add(new CategoryRecursiveUserDefinedLevelListProvider());
                DefaultProviders.Add(CategoriesOnPageLProvider);
                DefaultProviders.Add(new CategoriesOnPageOnlyHiddenListProvider());
                DefaultProviders.Add(new CategoriesOnPageNoHiddenListProvider());
                DefaultProviders.Add(WhatLinksHereLProvider);
                DefaultProviders.Add(new WhatLinksHereAllNSListProvider());
                DefaultProviders.Add(new WhatLinksHereAndToRedirectsListProvider());
                DefaultProviders.Add(new WhatLinksHereAndToRedirectsAllNSListProvider());
                DefaultProviders.Add(new WhatLinksHereExcludingPageRedirectsListProvider());
                DefaultProviders.Add(new WhatLinksHereAndPageRedirectsExcludingTheRedirectsListProvider());
                DefaultProviders.Add(WhatTranscludesLProvider);
                DefaultProviders.Add(new WhatTranscludesPageAllNSListProvider());
                DefaultProviders.Add(new LinksOnPageListProvider());
                DefaultProviders.Add(new LinksOnPageOnlyBlueListProvider());
                DefaultProviders.Add(new LinksOnPageOnlyRedListProvider());
                DefaultProviders.Add(new ImagesOnPageListProvider());
                DefaultProviders.Add(new TransclusionsOnPageListProvider());
                DefaultProviders.Add(new TextFileListProvider());
                DefaultProviders.Add(new GoogleSearchListProvider());
                DefaultProviders.Add(UserContribLProvider);
                DefaultProviders.Add(new UserContribUserDefinedNumberListProvider());
                DefaultProviders.Add(new SpecialPageListProvider(WhatLinksHereLProvider, NewPagesLProvider,
                                                          CategoriesOnPageLProvider, RandomPagesLProvider,
                                                          WhatTranscludesLProvider, RedirectLProvider,
                                                          UserContribLProvider));
                DefaultProviders.Add(new ImageFileLinksListProvider());
                DefaultProviders.Add(new MyWatchlistListProvider());
                DefaultProviders.Add(new WikiSearchListProvider());
                DefaultProviders.Add(new WikiTitleSearchListProvider());
                DefaultProviders.Add(RandomPagesLProvider);
                DefaultProviders.Add(RedirectLProvider);
                DefaultProviders.Add(NewPagesLProvider);
            }
        }
        public ListMaker()
        {
            InitializeComponent();
            ListProviderAdded += ProviderAdded;
            _specialFilter = new ListFilterForm(lbArticles);
            foreach (IListProvider prov in DefaultProviders)
            {
                if (!prov.UserInputTextBoxEnabled) continue;
                ToolStripMenuItem addToFromSelectedListFrom = new ToolStripMenuItem(prov.DisplayText) { Tag = prov };
                addToFromSelectedListFrom.Click += AddToFromSelectedListFrom;
                addSelectedToListToolStripMenuItem.DropDownItems.Add(addToFromSelectedListFrom);
            }
            _listProviders = new BindingList<IListProvider>()
                                 {
                                     new DatabaseScannerListProvider(this),
                                     HtmlScraperLProvider,
                                     CheckWikiLProvider,
                                     AdvHtmlScraperLProvider
                                 };
            foreach (IListProvider lvi in DefaultProviders)
            {
                _listProviders.Add(lvi);
            }
            cmboSourceSelect.DataSource = _listProviders;
            cmboSourceSelect.DisplayMember = "DisplayText";
            cmboSourceSelect.ValueMember = "DisplayText";
            SelectedProvider = "CategoryListProvider";
        }
        private void AddToFromSelectedListFrom(object sender, EventArgs e)
        {
            AddFromSelectedList((IListProvider)((ToolStripMenuItem)sender).Tag);
        }
        private void ProviderAdded(IListProvider provider)
        {
            if (!_listProviders.Contains(provider))
                _listProviders.Add(provider);
        }
        new public static void Refresh() { }
        public IEnumerator<Article> GetEnumerator()
        {
            int i = 0;
            while (i < lbArticles.Items.Count)
            {
                yield return (Article)lbArticles.Items[i];
                i++;
            }
        }
        IEnumerator IEnumerable.GetEnumerator()
        {
            int i = 0;
            while (i < lbArticles.Items.Count)
            {
                yield return lbArticles.Items[i];
                i++;
            }
        }
        public void Add(Article item)
        {
            lbArticles.Items.Add(item);
            UpdateNumberOfArticles();
        }
        public void Clear()
        {
            lbArticles.Items.Clear();
            UpdateNumberOfArticles();
        }
        public bool Contains(Article item)
        {
            return lbArticles.Items.Contains(item);
        }
        public bool Contains(string title)
        {
            return Contains(new Article(title));
        }
        public void CopyTo(Article[] array, int arrayIndex)
        {
            lbArticles.Items.CopyTo(array, arrayIndex);
        }
        public int Count
        { get { return lbArticles.Items.Count; } }
        public bool IsReadOnly
        { get { return false; } }
        public bool Remove(string title)
        {
            return Remove(new Article(title));
        }
        public bool Remove(Article item)
        {
            if (lbArticles.Items.Contains(item))
            {
                txtPage.Text = item.Name;
                int intPosition = 0;
                if(lbArticles.SelectedItems.Count == 1 && lbArticles.SelectedItems.Contains(item))
                    intPosition = lbArticles.SelectedIndex;
                else
                    intPosition = lbArticles.Items.IndexOf(item);
                while (lbArticles.SelectedItems.Count > 0)
                    lbArticles.SetSelected(lbArticles.SelectedIndex, false);
                lbArticles.Items.Remove(item);
                if (lbArticles.Items.Count == intPosition)
                    intPosition--;
                if (lbArticles.Items.Count > 0)
                    lbArticles.SelectedIndex = intPosition;
                UpdateNumberOfArticles();
                return true;
            }
            return false;
        }
        public int IndexOf(Article item)
        {
            return lbArticles.Items.IndexOf(item);
        }
        public int IndexOf(string item)
        {
            return lbArticles.Items.IndexOf(item);
        }
        public void Insert(int index, Article item)
        {
            lbArticles.Items.Insert(index, item);
            UpdateNumberOfArticles();
        }
        public void Insert(int index, string item)
        {
            lbArticles.Items.Insert(index, new Article(item));
            UpdateNumberOfArticles();
        }
        public void RemoveAt(int index)
        {
            lbArticles.Items.RemoveAt(index);
            UpdateNumberOfArticles();
        }
        public Article this[int index]
        {
            get { return (Article)lbArticles.Items[index]; }
            set { lbArticles.Items[index] = value; }
        }
        private void cmboSourceSelect_SelectedIndexChanged(object sender, EventArgs e)
        {
            if (DesignMode) return;
            IListProvider searchItem = (IListProvider)cmboSourceSelect.SelectedItem;
            searchItem.Selected();
            lblUserInput.Text = searchItem.UserInputTextBoxText;
            UserInputTextBox.Enabled = searchItem.UserInputTextBoxEnabled;
            tooltip.SetToolTip(cmboSourceSelect, searchItem.DisplayText);
        }
        private void btnAdd_Click(object sender, EventArgs e)
        {
            if (txtPage.Text.Trim().Length == 0)
                return;
            Add(NormalizeTitle(txtPage.Text));
            txtPage.Text = "";
        }
        private void btnRemoveArticle_Click(object sender, EventArgs e)
        {
            RemoveSelectedArticle();
        }
        private void btnFilter_Click(object sender, EventArgs e)
        {
            Filter();
        }
        private void btnMakeList_Click(object sender, EventArgs e)
        {
            UserInputTextBox.Text = UserInputTextBox.Text.Trim();
            if (UserInputTextBox.Enabled && string.IsNullOrEmpty(UserInputTextBox.Text))
            {
                MessageBox.Show("Please enter some text", "No text", MessageBoxButtons.OK, MessageBoxIcon.Warning);
                return;
            }
            if (!UserInputTextBox.AutoCompleteCustomSource.Contains(UserInputTextBox.Text))
                UserInputTextBox.AutoCompleteCustomSource.Add(UserInputTextBox.Text);
            MakeList();
        }
        private void lbArticles_MouseMove(object sender, MouseEventArgs e)
        {
            string strTip = "";
            int nIdx = lbArticles.IndexFromPoint(e.Location);
            if ((nIdx >= 0) && (nIdx < lbArticles.Items.Count))
                strTip = lbArticles.Items[nIdx].ToString();
            if (strTip != tooltip.GetToolTip(lbArticles))
                tooltip.SetToolTip(lbArticles, strTip);
        }
        private void txtNewArticle_MouseMove(object sender, MouseEventArgs e)
        {
            if (txtPage.Text != tooltip.GetToolTip(txtPage))
                tooltip.SetToolTip(txtPage, txtPage.Text);
        }
        private void lbArticles_KeyDown(object sender, KeyEventArgs e)
        {
            if (e.KeyCode == Keys.Delete)
                btnRemove.PerformClick();
        }
        private void txtNewArticle_KeyDown(object sender, KeyEventArgs e)
        {
            if (e.KeyCode == Keys.Return || e.KeyCode == Keys.Enter)
            {
                btnAdd.PerformClick();
                e.SuppressKeyPress = true;
                e.Handled = true;
            }
        }
        private void txtSelectSource_KeyDown(object sender, KeyEventArgs e)
        {
            if (e.KeyCode == Keys.Return || e.KeyCode == Keys.Enter)
                btnGenerate.PerformClick();
        }
        private void mnuListBox_Opening(object sender, CancelEventArgs e)
        {
            openInBrowserToolStripMenuItem.Enabled =
                openHistoryInBrowserToolStripMenuItem.Enabled =
                cutToolStripMenuItem.Enabled =
                copyToolStripMenuItem.Enabled =
                selectedToolStripMenuItem.Enabled =
                addSelectedToListToolStripMenuItem.Enabled =
                moveToTopToolStripMenuItem.Enabled =
                moveToBottomToolStripMenuItem.Enabled =
                (lbArticles.SelectedItem != null);
            specialFilterToolStripMenuItem.Enabled =
                sortAlphaMenuItem.Enabled =
                (lbArticles.Items.Count > 1);
            selectMnu.Enabled =
                removeToolStripMenuItem.Enabled =
                convertToTalkPagesToolStripMenuItem.Enabled =
                convertFromTalkPagesToolStripMenuItem.Enabled =
                saveListToFileToolStripMenuItem.Enabled =
                (lbArticles.Items.Count > 0);
        }
        private void txtNewArticle_DoubleClick(object sender, EventArgs e)
        {
            txtPage.SelectAll();
        }
        private void txtSelectSource_DoubleClick(object sender, EventArgs e)
        {
            UserInputTextBox.SelectAll();
        }
        public ListBox Items
        { get { return lbArticles; } }
        public bool ButtonsEnabled
        {
            set
            {
                btnFilter.Enabled = btnRemove.Enabled = saveListToFileToolStripMenuItem.Enabled = value;
            }
        }
        public string SelectedProvider
        {
            get { return cmboSourceSelect.SelectedItem.GetType().Name; }
            set
            {
                int index;
                if (int.TryParse(value, out index))
                {
                    if (index < (cmboSourceSelect.Items.Count - 1))
                    {
                        cmboSourceSelect.SelectedIndex = index;
                    }
                }
                else
                {
                    foreach (var provider in _listProviders)
                    {
                        if (provider.GetType().Name == value)
                        {
                            cmboSourceSelect.SelectedItem = provider;
                            break;
                        }
                    }
                }
                cmboSourceSelect_SelectedIndexChanged(null, null);
            }
        }
        public string SourceText
        {
            get { return UserInputTextBox.Text; }
            set { UserInputTextBox.Text = value; }
        }
        public bool MakeListEnabled
        {
            set { btnGenerate.Enabled = value; }
        }
        public int NumberOfArticles
        {
            get { return lbArticles.Items.Count; }
        }
        string _status = "";
        public string Status
        {
            get { return _status; }
            private set
            {
                _status = value;
                if (StatusTextChanged != null)
                    StatusTextChanged(null, null);
            }
        }
        bool _busyStatus;
        public bool BusyStatus
        {
            get { return _busyStatus; }
            private set
            {
                _busyStatus = value;
                if (BusyStateChanged != null)
                    BusyStateChanged(null, null);
            }
        }
        public Article SelectedArticle()
        {
            if (lbArticles.SelectedItem == null)
                lbArticles.SelectedIndex = 0;
            return (Article)lbArticles.SelectedItem;
        }
        public bool NextArticle()
        {
            ((Article)lbArticles.SelectedItem).PreProcessed = true;
            if (lbArticles.Items.Count == lbArticles.SelectedIndex + 1 ||
                (lbArticles.Items.Count == 1 && lbArticles.SelectedIndex == 0))
                return false;
            lbArticles.SelectedIndex++;
            lbArticles.SetSelected(lbArticles.SelectedIndex, false);
            return true;
        }
        public string NormalizeTitle(string s)
        {
            string url = Variables.URL + "/wiki/";
            return Regex.Match(s, url).Success ? s.Replace(url, "") : s;
        }
        private delegate void AddToListDel(string s);
        public void Add(string s)
        {
            if (InvokeRequired)
            {
                Invoke(new AddToListDel(Add), s);
                return;
            }
            lbArticles.Items.Add(new Article(Tools.TurnFirstToUpper(Tools.RemoveSyntax(s))));
            UpdateNumberOfArticles();
            if (FilterNonMainAuto)
                FilterNonMainArticles();
            if (FilterDuplicates)
                RemoveListDuplicates();
        }
        private delegate void AddDel(List<Article> l);
        public void Add(List<Article> l)
        {
            if (l == null || l.Count == 0)
                return;
            if (InvokeRequired)
            {
                Invoke(new AddDel(Add), l);
                return;
            }
            lbArticles.BeginUpdate();
            lbArticles.Items.AddRange(l.ToArray());
            lbArticles.EndUpdate();
            UpdateNumberOfArticles();
        }
        public List<Article> GetArticleList()
        {
            List<Article> list = new List<Article>();
            list.AddRange(lbArticles);
            return list;
        }
        public List<Article> GetSelectedArticleList()
        {
            List<Article> list = new List<Article>();
            foreach (Article a in lbArticles.SelectedItems)
            {
                list.Add(a);
            }
            return list;
        }
        private delegate void StartProgBarDelegate();
        private void StartProgressBar()
        {
            if (InvokeRequired)
            {
                BeginInvoke(new StartProgBarDelegate(StartProgressBar));
                return;
            }
            BusyStatus = true;
            Cursor = Cursors.WaitCursor;
            Status = "Getting list";
            btnGenerate.Enabled = false;
        }
        private delegate void StopProgBarDelegate(int count);
        private void StopProgressBar(int newArticles)
        {
            if (InvokeRequired)
            {
                BeginInvoke(new StopProgBarDelegate(StopProgressBar), newArticles);
                return;
            }
            BusyStatus = false;
            Cursor = Cursors.Default;
            if (newArticles == -1)
                Status = "List creation aborted.";
            else if (newArticles > 0)
                Status = "List complete!";
            else
                Status = "No results.";
            btnGenerate.Enabled = true;
            btnStop.Visible = false;
            UpdateNumberOfArticles();
            if (ListFinished != null)
                ListFinished(null, null);
        }
        private Thread _listerThread;
        public void MakeList()
        {
            MakeList((IListProvider)cmboSourceSelect.SelectedItem,
                     UserInputTextBox.Text.Split(new[] { '|' }, StringSplitOptions.RemoveEmptyEntries));
        }
        public void MakeList(IListProvider provider, string[] sourceValues)
        {
            btnStop.Visible = true;
            _providerToRun = provider;
            _source = sourceValues;
            if (_providerToRun.RunOnSeparateThread)
            {
                _listerThread = new Thread(MakeTheListThreaded)
                                    {
                                        IsBackground = true
                                    };
                _listerThread.SetApartmentState(ApartmentState.STA);
                _listerThread.Start();
            }
            else
                MakeTheList();
        }
        private string[] _source;
        private IListProvider _providerToRun;
        private void MakeTheListThreaded()
        {
            Thread.CurrentThread.Name = "ListMaker (" + _providerToRun.GetType().Name + ": "
                                        + UserInputTextBox.Text + ")";
            MakeTheList();
        }
        private void MakeTheList()
        {
            StartProgressBar();
            List<Article> articles = null;
            try
            {
                articles = _providerToRun.MakeList(_providerToRun.UserInputTextBoxEnabled ? _source : new string[0]);
                Add(articles);
            }
            catch (ThreadAbortException)
            {
            }
            catch (FeatureDisabledException fde)
            {
                DisabledListProvider(fde);
            }
            catch (LoggedOffException)
            {
                UserLoggedOff();
            }
            catch (ApiErrorException aee)
            {
                if (aee.ErrorCode == "eiinvalidtitle")
                {
                    MessageBox.Show("An invalid title of \"" + aee.GetErrorVariable() + "\" was passed to the API.",
                                    "Invalid Title");
                }
            }
            catch (ArgumentException ae)
            {
                MessageBox.Show(ae.Message, "Invalid Parameter passed to List Maker");
            }
            catch (Exception ex)
            {
                ErrorHandler.ListMakerText = UserInputTextBox.Text;
                ErrorHandler.Handle(ex);
                ErrorHandler.ListMakerText = "";
            }
            finally
            {
                if (FilterNonMainAuto)
                    FilterNonMainArticles();
                if (FilterDuplicates)
                    RemoveListDuplicates();
                StopProgressBar((articles != null) ? articles.Count : 0);
            }
        }
        private void DisabledListProvider(FeatureDisabledException fde)
        {
            MessageBox.Show(
                "Unable to generate lists using " + _providerToRun.DisplayText +
                ". Removing from the list of providers during this session", fde.ApiErrorMessage);
            _listProviders.Remove(_providerToRun);
        }
        private void UserLoggedOff()
        {
            MessageBox.Show(
                "User must be logged in to use \"" + _providerToRun.DisplayText + "\". Please login and try again.",
                "User logged out");
        }
        private void RemoveSelectedArticle()
        {
            lbArticles.RemoveSelected();
            UpdateNumberOfArticles();
        }
        public void Filter()
        {
            _specialFilter.ShowDialog(this);
        }
        public void RemoveListDuplicates()
        {
            if (InvokeRequired)
            {
                Invoke(new GenericDelegate(RemoveListDuplicates));
                return;
            }
            _specialFilter.Clear();
            _specialFilter.RemoveDuplicates();
            UpdateNumberOfArticles();
        }
        public void SaveList()
        {
            lbArticles.SaveList();
        }
        private delegate void GenericDelegate();
        public void FilterNonMainArticles()
        {
            if (InvokeRequired)
            {
                Invoke(new GenericDelegate(FilterNonMainArticles));
                return;
            }
            int i = 0;
            lbArticles.BeginUpdate();
            while (i < lbArticles.Items.Count)
            {
                string s = lbArticles.Items[i].ToString();
                if (!Namespace.IsMainSpace(s))
                    lbArticles.Items.Remove(lbArticles.Items[i]);
                else
                    i++;
            }
            lbArticles.EndUpdate();
            UpdateNumberOfArticles();
        }
        public void AlphaSortList()
        {
            lbArticles.Sort();
        }
        public void ReplaceArticle(Article oldArticle, Article newArticle)
        {
            int intPos = 0;
            if(lbArticles.SelectedItems.Count == 1 && lbArticles.SelectedItems.Contains(oldArticle))
                intPos = lbArticles.SelectedIndex;
            else
                lbArticles.Items.IndexOf(oldArticle);
            lbArticles.Items.Remove(oldArticle);
            lbArticles.ClearSelected();
            lbArticles.Items.Insert(intPos, newArticle);
            lbArticles.SetSelected(intPos, true);
        }
        public void Stop()
        {
            if (_listerThread != null)
                _listerThread.Abort();
            StopProgressBar(-1);
        }
        public void UpdateNumberOfArticles()
        {
            lblNumOfPages.Text = lbArticles.Items.Count + " page";
            if (lbArticles.Items.Count != 1)
                lblNumOfPages.Text += "s";
            if (NoOfArticlesChanged != null)
                NoOfArticlesChanged(null, null);
            if (AutoAlpha)
                AlphaSortList();
        }
        public void ConvertToTalkPages()
        {
            List<Article> list = GetArticleList();
            lbArticles.Items.Clear();
            Add(Tools.ConvertToTalk(list));
        }
        public void ConvertFromTalkPages()
        {
            List<Article> list = GetArticleList();
            lbArticles.Items.Clear();
            Add(Tools.ConvertFromTalk(list));
        }
        private void filterOutNonMainSpaceArticlesToolStripMenuItem_Click(object sender, EventArgs e)
        {
            FilterNonMainArticles();
        }
        private void specialFilterToolStripMenuItem_Click(object sender, EventArgs e)
        {
            Filter();
        }
        private void sortAlphebeticallyMenuItem_Click(object sender, EventArgs e)
        {
            AlphaSortList();
        }
        private void saveListToTextFileToolStripMenuItem1_Click(object sender, EventArgs e)
        {
            SaveList();
        }
        private void selectedToolStripMenuItem_Click(object sender, EventArgs e)
        {
            RemoveSelectedArticle();
        }
        private void duplicatesToolStripMenuItem_Click(object sender, EventArgs e)
        {
            RemoveListDuplicates();
        }
        private void convertToTalkPagesToolStripMenuItem_Click(object sender, EventArgs e)
        {
            ConvertToTalkPages();
        }
        private void convertFromTalkPagesToolStripMenuItem_Click(object sender, EventArgs e)
        {
            ConvertFromTalkPages();
        }
        private void cutToolStripMenuItem_Click(object sender, EventArgs e)
        {
            Tools.Copy(lbArticles);
            RemoveSelectedArticle();
        }
        private void copyToolStripMenuItem_Click(object sender, EventArgs e)
        {
            Tools.Copy(lbArticles);
        }
        private void pasteToolStripMenuItem_Click(object sender, EventArgs e)
        {
            lbArticles.BeginUpdate();
            try
            {
                object obj = Clipboard.GetDataObject();
                if (obj == null)
                    return;
                string textTba = ((IDataObject)obj).GetData(DataFormats.UnicodeText).ToString();
                BeginUpdate();
                foreach (string entry in textTba.Split(new[] { "\r\n", "\n", "|" }, StringSplitOptions.RemoveEmptyEntries))
                {
                    if (!string.IsNullOrEmpty(entry.Trim()))
                        Add(NormalizeTitle(entry));
                }
                EndUpdate();
            }
            catch
            { }
            lbArticles.EndUpdate();
        }
        private void selectAllToolStripMenuItem_Click(object sender, EventArgs e)
        {
            SetSelected(true);
        }
        private void invertSelectionToolStripMenuItem_Click(object sender, EventArgs e)
        {
            lbArticles.BeginUpdate();
            for (int i = 0; i < lbArticles.Items.Count; i++)
                lbArticles.SetSelected(i, !lbArticles.GetSelected(i));
            lbArticles.EndUpdate();
        }
        private void selectNoneToolStripMenuItem_Click(object sender, EventArgs e)
        {
            SetSelected(false);
        }
        private void SetSelected(bool selected)
        {
            lbArticles.BeginUpdate();
            for (int i = 0; i < lbArticles.Items.Count; i++)
                lbArticles.SetSelected(i, selected);
            lbArticles.EndUpdate();
        }
        private void AddFromSelectedList(IListProvider provider)
        {
            if (lbArticles.SelectedItems.Count == 0)
                return;
            List<string> articles = new List<string>();
            foreach (Article a in lbArticles.SelectedItems)
                articles.Add(a.Name);
            MakeList(provider, articles.ToArray());
        }
        private void clearToolStripMenuItem1_Click(object sender, EventArgs e)
        {
            if (lbArticles.Items.Count <= 100 || (MessageBox.Show(
            "Are you sure you want to clear the large list?", "Clear?", MessageBoxButtons.YesNo)
            == DialogResult.Yes))
                Clear();
        }
        private void openInBrowserToolStripMenuItem_Click(object sender, EventArgs e)
        {
            if ((lbArticles.SelectedItems.Count < 10) || (MessageBox.Show("Opening " + lbArticles.SelectedItems.Count + " articles in your browser at once could cause your system to run slowly, and even stop responding.\r\nAre you sure you want to continue?", "Continue?", MessageBoxButtons.YesNo) == DialogResult.Yes))
                LoadArticlesInBrowser();
        }
        private void LoadArticlesInBrowser()
        {
            Article[] articles = new Article[lbArticles.SelectedItems.Count];
            lbArticles.SelectedItems.CopyTo(articles, 0);
            foreach (Article item in articles)
            {
                Variables.MainForm.TheSession.Site.OpenPageInBrowser(item.Name);
            }
        }
        private void btnStop_Click(object sender, EventArgs e)
        {
            btnStop.Visible = false;
            Stop();
        }
        private void openHistoryInBrowserToolStripMenuItem_Click(object sender, EventArgs e)
        {
            if ((lbArticles.SelectedItems.Count < 10) || (MessageBox.Show("Opening " + lbArticles.SelectedItems.Count + " articles in your browser at once could cause your system to run slowly, and even stop responding.\r\nAre you sure you want to continue?", "Continue?", MessageBoxButtons.YesNo) == DialogResult.Yes))
                LoadArticleHistoryInBrowser();
        }
        private void LoadArticleHistoryInBrowser()
        {
            foreach (Article item in lbArticles.SelectedItems)
                Tools.OpenArticleHistoryInBrowser(item.Name);
        }
        private void lbArticles_DoubleClick(object sender, EventArgs e)
        {
            LoadArticlesInBrowser();
        }
        private void moveToTopToolStripMenuItem_Click(object sender, EventArgs e)
        {
            MoveSelectedItems(0);
        }
        private void moveToBottomToolStripMenuItem_Click(object sender, EventArgs e)
        {
            MoveSelectedItems(lbArticles.Items.Count - 1);
        }
        private void MoveSelectedItems(int toIndex)
        {
            bool toTop = (toIndex == 0);
            lbArticles.BeginUpdate();
            List<Article> articlesToMove = GetSelectedArticleList();
            articlesToMove.Reverse();
            lbArticles.RemoveSelected();
            if(toIndex > lbArticles.Items.Count)
                toIndex = lbArticles.Items.Count;
            foreach (Article a in articlesToMove)
            {
                lbArticles.Items.Insert(toIndex, a);
                if (toTop)
                    toIndex++;
            }
            lbArticles.EndUpdate();
        }
        [Browsable(false)]
        [Localizable(false)]
        public AWBSettings.SpecialFilterPrefs SpecialFilterSettings
        {
            get { return _specialFilter.Settings; }
            set
            {
                if (DesignMode)
                    return;
                _specialFilter.Settings = value;
            }
        }
        public static void AddProvider(IListProvider provider)
        {
            DefaultProviders.Add(provider);
            if (ListProviderAdded != null)
                ListProviderAdded(provider);
        }
        public DBScanner.DatabaseScanner DBScanner()
        {
            return new DBScanner.DatabaseScanner(this);
        }
        private void lbArticles_DrawItem(object sender, DrawItemEventArgs e)
        {
            if (e.Index < 0)
                return;
            Article a = (Article)lbArticles.Items[e.Index];
            bool selected = ((e.State & DrawItemState.Selected) == DrawItemState.Selected);
            if (!selected)
                e = new DrawItemEventArgs(e.Graphics, e.Font, e.Bounds, e.Index,
                                          e.State,
                                          e.ForeColor, (a.PreProcessed) ? Color.GreenYellow : e.BackColor);
            e.DrawBackground();
            e.Graphics.DrawString(a.Name, e.Font, (selected) ? Brushes.White : Brushes.Black, e.Bounds,
                                  StringFormat.GenericDefault);
            e.DrawFocusRectangle();
        }
        public void BeginUpdate()
        {
            lbArticles.BeginUpdate();
        }
        public void EndUpdate()
        {
            lbArticles.EndUpdate();
        }
    }
}
