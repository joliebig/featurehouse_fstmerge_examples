using WikiFunctions.Logging.Uploader;
namespace WikiFunctions.Logging
{
    public class WikiTraceListener : TraceListenerUploadableBase
    {
        protected static readonly System.Globalization.CultureInfo DateFormat =
            new System.Globalization.CultureInfo("en-US", false);
        public WikiTraceListener(UploadableLogSettings2 uploadSettings, TraceStatus traceStatus)
            : base(uploadSettings, traceStatus)
        {
            WriteBulletedLine("Logging: WikiFunctions.dll v" + Tools.VersionString, false, false);
        }
        protected virtual string DateStamp()
        {
            return Variables.IsWikipediaEN ? WikiDateStamp() : NonWikiDateStamp();
        }
        protected virtual string NonWikiDateStamp()
        {
            return System.DateTime.Now.ToString("d MMMM yyyy HH:mm ");
        }
        protected virtual string WikiDateStamp()
        {
            return System.DateTime.Now.ToString("[[d MMMM]] [[yyyy]] HH:mm ", DateFormat);
        }
        public override void WriteBulletedLine(string line, bool bold, bool verboseOnly, bool dateStamp)
        {
            if (verboseOnly && !Verbose)
                return;
            if (dateStamp)
                line = DateStamp() + line;
            if (bold)
                base.WriteLine("*'''" + line + "'''", true);
            else
                base.WriteLine("*" + line, true);
        }
        public override void ProcessingArticle(string fullArticleTitle, int ns)
        {
            CheckCounterForUpload();
            base.WriteLine(GetArticleTemplate(fullArticleTitle, ns), false);
        }
        public override void SkippedArticle(string skippedBy, string reason)
        {
            if (!string.IsNullOrEmpty(reason))
                reason = ": " + reason;
            base.WriteLine("#*''" + skippedBy + ": Skipped" + reason + "''", false);
        }
        public override void SkippedArticleBadTag(string skippedBy, string fullArticleTitle, int ns)
        {
            SkippedArticle(skippedBy, "Bad tag");
        }
        public override void WriteArticleActionLine(string line, string pluginName)
        {
            base.WriteLine("#*" + pluginName + ": " + line.Replace("[[Category:", "[[:Category:"), false);
        }
        public override void WriteTemplateAdded(string template, string pluginName)
        {
            base.WriteLine(string.Format("#*{1}: [[Template:{0}|{0}]] added", template, pluginName), false);
        }
        public override void WriteLine(string line)
        {
            WriteLine(line, true);
        }
        public override void WriteComment(string line)
        {
            base.Write("<!-- " + line + " -->");
        }
        public override void WriteCommentAndNewLine(string line)
        {
            base.WriteLine("<!-- " + line + " -->", false);
        }
    }
    public class XHTMLTraceListener : TraceListenerBase
    {
        protected static int mArticleCount = 1;
        protected static bool mVerbose;
        public XHTMLTraceListener(string filename, bool logVerbose)
            : base(filename)
        {
            mVerbose = logVerbose;
            base.WriteLine("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" " + "\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">");
            base.WriteLine("<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\" " + "lang=\"en\" dir=\"ltr\">");
            base.WriteLine("<head>");
            base.WriteLine("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />");
            base.WriteLine("<meta name=\"generator\" content=\"WikiFunctions" + Tools.VersionString + "\" />");
            base.WriteLine("<title>AWB log</title>");
            base.WriteLine("</head><body>");
        }
        public override void Close()
        {
            base.WriteLine("</body>");
            base.WriteLine("</html>");
            base.Close();
        }
        public override bool Verbose
        {
            get { return mVerbose; }
        }
        public override void WriteBulletedLine(string line, bool bold, bool verboseOnly, bool dateStamp)
        {
            if (verboseOnly && !mVerbose)
                return;
            if (dateStamp)
                line = string.Format("{0:g}: {1}", System.DateTime.Now, line);
            if (bold)
                base.WriteLine("<br/><li><b>" + line + "</b></li>");
            else
                base.WriteLine("<li>" + line + "</li>");
        }
        public override void ProcessingArticle(string fullArticleTitle, int ns)
        {
            base.WriteLine("<br/>" + mArticleCount + ". <a href=\"" + Variables.NonPrettifiedURL(fullArticleTitle) + "\">[[" + fullArticleTitle + "]]</a>");
            mArticleCount += 1;
        }
        public override void SkippedArticle(string skippedBy, string reason)
        {
            if (!string.IsNullOrEmpty(reason))
                reason = ": " + reason;
            base.WriteLine("<li><i>" + skippedBy + ": Skipped" + reason + "</i></li>");
        }
        public override void SkippedArticleBadTag(string skippedBy, string fullArticleTitle, int ns)
        {
            SkippedArticle(skippedBy, "Bad tag");
        }
        public override void WriteArticleActionLine(string line, string pluginName)
        {
            base.WriteLine("<li><i>" + pluginName + ": " + line + "</i></li>");
        }
        public override void WriteTemplateAdded(string template, string pluginName)
        {
            base.WriteLine("<br/><li><i>" + pluginName + ": " + template + "</i></li>");
        }
        public override void WriteComment(string line)
        {
            base.Write("<!-- " + line + " -->");
        }
        public override void WriteCommentAndNewLine(string line)
        {
            base.WriteLine("<!-- " + line + " -->");
        }
        public override bool Uploadable
        {
            get { return false; }
        }
    }
}
