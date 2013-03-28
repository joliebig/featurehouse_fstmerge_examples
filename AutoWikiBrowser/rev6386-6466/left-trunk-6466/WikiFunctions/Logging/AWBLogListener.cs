using System;
using System.Windows.Forms;
namespace WikiFunctions.Logging
{
    [Serializable]
    public class AWBLogListener : ListViewItem, IAWBTraceListener
    {
        public const string UploadingLogEntryDefaultEditSummary = "Adding log entry",
                     UploadingLogDefaultEditSummary = "Uploading log",
                     LoggingStartButtonClicked = "Initialising log.",
                     StringUser = "User",
                     StringUserSkipped = "Clicked skip",
                     StringPlugin = "Plugin",
                     StringPluginSkipped = "Plugin sent skip event";
        public static string AWBLoggingEditSummary
        { get { return "(" + Variables.WPAWB + " Logging) "; } }
        private bool Datestamped, HaveSkipInfo;
        public bool Skipped { get; internal set; }
        public AWBLogListener(string articleTitle)
        {
            Text = articleTitle;
            ArticleTitle = articleTitle;
        }
        public void UserSkipped()
        {
            Skip(StringUser, StringUserSkipped);
        }
        public void AWBSkipped(string reason)
        {
            Skip("AWB", reason);
        }
        public void PluginSkipped()
        {
            Skip(StringPlugin, StringPluginSkipped);
        }
        public void OpenInBrowser()
        {
            Tools.OpenArticleInBrowser(ArticleTitle);
        }
        public void OpenHistoryInBrowser()
        {
            Tools.OpenArticleHistoryInBrowser(ArticleTitle);
        }
        public void AddAndDateStamp(ListView listView)
        {
            ListViewSubItem dateStamp = new ListViewSubItem() {Text = DateTime.Now.ToString()};
            base.SubItems.Insert(1, dateStamp);
            try
            {
                listView.Items.Insert(0, this);
            }
            catch { }
            Datestamped = true;
        }
        public string Output(LogFileType logFileType)
        {
            switch (logFileType)
            {
                case LogFileType.AnnotatedWikiText:
                    string output = "*" + TimeStamp + ": [[" + ArticleTitle + "]]\r\n";
                    if (Skipped)
                        output += "'''Skipped''' by: " + SkippedBy + "\r\n" + "Skip reason: " +
                            SkipReason + "\r\n";
                    return output + ToolTipText + "\r\n";
                case LogFileType.PlainText:
                    return ArticleTitle;
                case LogFileType.WikiText:
                    return "#[[:" + ArticleTitle + "]]";
                default:
                    throw new ArgumentOutOfRangeException("LogFileType");
            }
        }
        public string ArticleTitle { get; private set; }
        public string SkipReason
        {
            get { return GetSubItemText(SubItem.SkippedReason); }
            protected set { SetSubItemText(SubItem.SkippedReason, value); }
        }
        public string TimeStamp
        {
            get { return GetSubItemText(SubItem.TimeStamp); }
        }
        public string SkippedBy
        {
            get { return GetSubItemText(SubItem.SkippedBy); }
            protected set { SetSubItemText(SubItem.SkippedBy, value); }
        }
        void IMyTraceListener.Close() { }
        void IMyTraceListener.Flush() { }
        void IMyTraceListener.ProcessingArticle(string fullArticleTitle, int ns) { }
        void IMyTraceListener.WriteComment(string line) { }
        void IMyTraceListener.WriteCommentAndNewLine(string line) { }
        void IMyTraceListener.SkippedArticle(string skippedBy, string reason)
        {
            Skip(skippedBy, reason);
        }
        void IMyTraceListener.SkippedArticleBadTag(string skippedBy, string fullArticleTitle, int ns)
        {
            Skip(skippedBy, "Bad tag");
        }
        void IMyTraceListener.SkippedArticleRedlink(string skippedBy, string fullArticleTitle, int ns)
        {
            Skip(skippedBy, "Red link (article deleted)");
        }
        bool IMyTraceListener.Uploadable
        {
            get { return false; }
        }
        void IMyTraceListener.WriteArticleActionLine(string line, string pluginName, bool verboseOnly)
        {
            if (!verboseOnly) WriteLine(line, pluginName);
        }
        void IMyTraceListener.WriteArticleActionLine(string line, string pluginName)
        {
            WriteLine(line, pluginName);
        }
        void IMyTraceListener.WriteBulletedLine(string line, bool bold, bool verboseOnly)
        {
            if (!verboseOnly) Write(line);
        }
        void IMyTraceListener.WriteBulletedLine(string line, bool bold, bool verboseOnly, bool dateStamp)
        {
            if (!verboseOnly) Write(line);
        }
        void IMyTraceListener.WriteLine(string line)
        {
            Write(line);
        }
        void IMyTraceListener.WriteTemplateAdded(string template, string pluginName)
        {
            WriteLine("{{" + template + "}} added", pluginName);
        }
        public void Write(string text)
        {
            if (string.IsNullOrEmpty(ToolTipText.Trim()))
            { ToolTipText = text; }
            else
            { ToolTipText = text + Environment.NewLine + ToolTipText; }
        }
        public void WriteLine(string text, string sender)
        {
            if (!string.IsNullOrEmpty(text.Trim())) Write(sender + ": " + text);
        }
        private enum SubItem
        {
            SkippedBy,
            SkippedReason,
            TimeStamp
        };
        private int GetSubItemNumber(SubItem subItem)
        {
            switch (subItem)
            {
                case SubItem.SkippedBy:
                    return Datestamped ? 2 : 1;
                case SubItem.SkippedReason:
                    return Datestamped ? 3 : 2;
                case SubItem.TimeStamp:
                    return (Datestamped) ? 1 : -1;
                default:
                    throw new ArgumentOutOfRangeException("SubItem");
            }
        }
        private string GetSubItemText(SubItem subItem)
        {
            switch (subItem)
            {
                case SubItem.SkippedBy:
                case SubItem.SkippedReason:
                    return HaveSkipInfo ? base.SubItems[GetSubItemNumber(subItem)].Text : "";
                case SubItem.TimeStamp:
                    return Datestamped ? base.SubItems[1].Text : "";
                default:
                    return base.SubItems[GetSubItemNumber(subItem)].Text;
            }
        }
        private void SetSubItemText(SubItem subItem, string value)
        {
            if ((subItem == SubItem.SkippedBy || subItem == SubItem.SkippedReason) &! (HaveSkipInfo))
            {
                base.SubItems.Add("SkippedBy");
                base.SubItems.Add("SkipReason");
                HaveSkipInfo = true;
            }
            base.SubItems[GetSubItemNumber(subItem)].Text = value;
        }
        protected void Skip(string mSkippedBy, string mSkipReason)
        {
            SetSubItemText(SubItem.SkippedBy, mSkippedBy);
            SetSubItemText(SubItem.SkippedReason, mSkipReason);
            WriteLine(SkipReason, SkippedBy);
            Skipped = true;
        }
        public static new ListViewSubItemCollection SubItems
        {
            get { throw new NotImplementedException("The SubItems property should not be accessed directly"); }
        }
    }
}
