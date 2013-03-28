using System;
using System.Collections.Generic;
using System.Collections.Specialized;
using System.Windows.Forms;
using WikiFunctions.Background;
using WikiFunctions.Plugin;
namespace WikiFunctions.Plugins.ListMaker.TypoScan
{
    class TypoScanBasePlugin : IAWBBasePlugin
    {
        private static IAutoWikiBrowser AWB;
        internal static readonly Dictionary<string, int> PageList = new Dictionary<string, int>();
        private static readonly List<string> SavedPages = new List<string>();
        private static readonly List<string> SkippedPages = new List<string>();
        private static readonly List<string> SkippedReasons = new List<string>();
        internal static readonly List<string> SavedPagesThisSession = new List<string>();
        internal static readonly List<string> SkippedPagesThisSession = new List<string>();
        internal static int UploadedThisSession;
        internal static DateTime CheckoutTime;
        private readonly ToolStripMenuItem PluginMenuItem = new ToolStripMenuItem("TypoScan plugin");
        private readonly ToolStripMenuItem PluginUploadMenuItem = new ToolStripMenuItem("Upload finished articles to server now");
        private readonly ToolStripMenuItem PluginReAddArticlesMenuItem = new ToolStripMenuItem("Re-add Unprocessed TypoScan articles to ListMaker");
        private readonly ToolStripMenuItem AboutMenuItem = new ToolStripMenuItem("About the TypoScan plugin");
        public void Initialise(IAutoWikiBrowser sender)
        {
            AWB = sender;
            AWB.LogControl.LogAdded += LogControl_LogAdded;
            AWB.AddMainFormClosingEventHandler(UploadFinishedArticlesToServer);
            AWB.AddArticleRedirectedEventHandler(ArticleRedirected);
            PluginMenuItem.DropDownItems.Add(PluginUploadMenuItem);
            PluginMenuItem.DropDownItems.Add(PluginReAddArticlesMenuItem);
            PluginUploadMenuItem.Click += PluginUploadMenuItemClick;
            PluginReAddArticlesMenuItem.Click += pluginReAddArticlesMenuItem_Click;
            sender.PluginsToolStripMenuItem.DropDownItems.Add(PluginMenuItem);
            AboutMenuItem.Click += AboutMenuItemClick;
            sender.HelpToolStripMenuItem.DropDownItems.Add(AboutMenuItem);
        }
        private static void ArticleRedirected(string oldTitle, string newTitle)
        {
            int id;
            if (PageList.TryGetValue(oldTitle, out id))
            {
                PageList.Remove(oldTitle);
                PageList.Add(newTitle, id);
            }
        }
        private static void pluginReAddArticlesMenuItem_Click(object sender, EventArgs e)
        {
            foreach (string a in PageList.Keys)
            {
                if (SkippedPagesThisSession.Contains(a) || SavedPagesThisSession.Contains(a))
                    continue;
                AWB.ListMaker.Add(new Article(a));
            }
        }
        private static void AboutMenuItemClick(object sender, EventArgs e)
        {
            new About().Show();
        }
        private static void PluginUploadMenuItemClick(object sender, EventArgs e)
        {
            UploadFinishedArticlesToServer();
        }
        private static void LogControl_LogAdded(bool skipped, Logging.AWBLogListener logListener)
        {
            int articleID;
            if ((PageList.Count > 0) && (PageList.TryGetValue(logListener.Text, out articleID)))
            {
                if (skipped)
                {
                    SkippedPages.Add(articleID.ToString());
                    SkippedReasons.Add(logListener.SkipReason);
                    SkippedPagesThisSession.Add(articleID.ToString());
                }
                else
                {
                    SavedPages.Add(articleID.ToString());
                    SavedPagesThisSession.Add(articleID.ToString());
                }
                if (EditAndIgnoredPages >= 25)
                    UploadFinishedArticlesToServer();
            }
        }
        public string Name
        {
            get { return "TypoScan Plugin"; }
        }
        public string WikiName
        {
            get
            {
                return "[[Wikipedia:TypoScan|TypoScan AWB Plugin]], Plugin version " +
                    System.Reflection.Assembly.GetExecutingAssembly().GetName().Version;
            }
        }
        public void LoadSettings(object[] prefs)
        {
        }
        public object[] SaveSettings()
        {
            return new object[0];
        }
        private static void UploadFinishedArticlesToServer(object sender, FormClosingEventArgs e)
        {
            UploadFinishedArticlesToServer();
        }
        private static int EditsAndIgnored;
        private static bool IsUploading;
        private static readonly BackgroundRequest Thread = new BackgroundRequest(UploadFinishedArticlesToServerFinished,
                                                            UploadFinishedArticlesToServerErrored);
        private static readonly List<string> CurrentlyUploadingSkipped = new List<string>(),
            CurrentlyUploadingSaved = new List<string>(),
            CurrentlyUploadingReasons = new List<string>();
        private static void UploadFinishedArticlesToServer()
        {
            if (IsUploading || EditAndIgnoredPages == 0)
                return;
            IsUploading = true;
            EditsAndIgnored = EditAndIgnoredPages;
            AWB.StartProgressBar();
            AWB.StatusLabelText = "Uploading " + EditsAndIgnored + " TypoScan articles to server...";
            CurrentlyUploadingSaved.AddRange(SavedPages);
            CurrentlyUploadingSkipped.AddRange(SkippedPages);
            CurrentlyUploadingReasons.AddRange(SkippedReasons);
            NameValueCollection postVars = new NameValueCollection()
                                               {
                                                   new String[] {"articles", string.Join(",", CurrentlyUploadingSaved.ToArray())},
                                                   new String[] {"skipped", string.Join(",", CurrentlyUploadingSkipped.ToArray())},
                                                   new String[] {"skipreason", string.Join(",", CurrentlyUploadingReasons.ToArray())},
                                                   new String[] {"user", AWB.Privacy ? "[withheld]" : AWB.TheSession.User.Name}
                                               };
            if (!AWB.Shutdown)
                Thread.PostData(Common.GetUrlFor("finished"), postVars);
            else
                UploadResult(Tools.PostData(postVars, Common.GetUrlFor("finished")));
        }
        private static void UploadFinishedArticlesToServerFinished(BackgroundRequest req)
        {
            UploadResult(req.Result.ToString());
        }
        private static void UploadResult(string result)
        {
            if (string.IsNullOrEmpty(Common.CheckOperation(result)))
            {
                UploadedThisSession += EditsAndIgnored;
                if (CurrentlyUploadingSaved.Count > 0)
                {
                    SavedPages.RemoveRange(0, CurrentlyUploadingSaved.Count - 1);
                    CurrentlyUploadingSaved.Clear();
                }
                if (CurrentlyUploadingSkipped.Count > 0)
                {
                    SkippedPages.RemoveRange(0, CurrentlyUploadingSkipped.Count - 1);
                    CurrentlyUploadingSkipped.Clear();
                }
                if (CurrentlyUploadingReasons.Count > 0)
                {
                    SkippedReasons.RemoveRange(0, CurrentlyUploadingReasons.Count - 1);
                    CurrentlyUploadingReasons.Clear();
                }
                if ((UploadedThisSession % 100) == 0)
                    CheckoutTime = DateTime.Now;
            }
            AWB.StopProgressBar();
            AWB.StatusLabelText = "";
            IsUploading = false;
        }
        private static void UploadFinishedArticlesToServerErrored(BackgroundRequest req)
        {
            AWB.StopProgressBar();
            AWB.StatusLabelText = "TypoScan reporting failed";
            IsUploading = false;
            if (req.ErrorException is System.IO.IOException || req.ErrorException is System.Net.WebException)
            {
                Tools.WriteDebug("TypoScanAWBPlugin", req.ErrorException.Message);
            }
        }
        internal static int EditAndIgnoredPages
        {
            get { return (SavedPages.Count + SkippedPages.Count); }
        }
    }
}
