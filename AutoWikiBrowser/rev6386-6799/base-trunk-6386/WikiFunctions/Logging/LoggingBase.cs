using System.Text.RegularExpressions;
using WikiFunctions.Logging.Uploader;
namespace WikiFunctions.Logging
{
    public abstract class TraceListenerBase : System.IO.StreamWriter, IMyTraceListener
    {
        private static readonly Regex GetArticleTemplateRegex = new Regex("( talk)?:", RegexOptions.Compiled);
        protected TraceListenerBase(string filename)
            : base(filename, false, System.Text.Encoding.UTF8)
        {
        }
  public abstract void ProcessingArticle(string fullArticleTitle, int ns);
  public abstract void WriteBulletedLine(string line, bool bold, bool verboseOnly, bool dateStamp);
  public void WriteBulletedLine(string line, bool bold, bool verboseOnly)
  {
   WriteBulletedLine(line, bold, verboseOnly, false);
  }
  public abstract void SkippedArticle(string skippedBy, string reason);
  public abstract void SkippedArticleBadTag(string skippedBy, string fullArticleTitle, int ns);
  public abstract void WriteArticleActionLine(string line, string pluginName);
  public abstract void WriteTemplateAdded(string template, string pluginName);
  public abstract void WriteComment(string line);
  public abstract void WriteCommentAndNewLine(string line);
  public virtual void SkippedArticleRedlink(string skippedBy, string fullArticleTitle, int ns)
  {
   SkippedArticle(skippedBy, "Attached article doesn't exist - maybe deleted?");
  }
  public void WriteArticleActionLine(string line, string pluginName, bool verboseOnly)
  {
   WriteArticleActionLineVerbose(line, pluginName, verboseOnly);
  }
  public void WriteArticleActionLineVerbose(string line, string pluginName, bool verboseOnly)
  {
   if (verboseOnly && ! Verbose)
   {
    return;
   }
   WriteArticleActionLine(line, pluginName);
  }
  public abstract bool Uploadable {get;}
  public static string GetArticleTemplate(string articleFullTitle, int ns)
  {
            switch (ns)
            {
                case Namespace.Article:
     return "#{{subst:la|" + articleFullTitle + "}}";
                case Namespace.Talk:
     return "#{{subst:lat|" + Tools.RemoveNamespaceString(articleFullTitle).Trim() + "}}";
                default:
                    string strnamespace = GetArticleTemplateRegex.Replace(Variables.Namespaces[ns], "");
                    string templ = ns % 2 == 1 ? "lnt" : "ln";
     return "#{{subst:" + templ + "|" + strnamespace + "|" +
                        Tools.RemoveNamespaceString(articleFullTitle).Trim() + "}}";
   }
  }
        public abstract bool Verbose { get; }
 }
    public abstract class TraceListenerUploadableBase : TraceListenerBase, ITraceStatusProvider
 {
        protected TraceStatus mTraceStatus;
        protected UploadableLogSettings2 mUploadSettings;
  public delegate void UploadEventHandler(TraceListenerUploadableBase sender, ref bool success);
  public event UploadEventHandler Upload;
        protected TraceListenerUploadableBase(UploadableLogSettings2 uploadSettings, TraceStatus traceStatus)
            : base(traceStatus.FileName)
  {
   mTraceStatus = traceStatus;
   mUploadSettings = uploadSettings;
  }
  public override bool Uploadable
  {
            get { return true; }
  }
  public override bool Verbose
  {
            get { return mUploadSettings.LogVerbose; }
  }
  public virtual new void WriteLine(string line)
  {
   WriteLine(line, true);
  }
  public virtual void WriteLine(string line, bool checkCounter)
  {
   base.WriteLine(line);
   mTraceStatus.LogUpload += line + System.Environment.NewLine;
   mTraceStatus.LinesWritten += 1;
   if (checkCounter)
   {
    CheckCounterForUpload();
   }
  }
  protected virtual bool IsReadyToUpload
  {
   get
   {
    return mTraceStatus.LinesWrittenSinceLastUpload >= mUploadSettings.UploadMaxLines;
   }
  }
  public virtual void CheckCounterForUpload()
  {
   if (IsReadyToUpload)
   {
    UploadLog();
   }
  }
  public void Close(bool upload)
  {
   if (upload)
   {
    UploadLog();
   }
   mTraceStatus.Close();
   base.Close();
  }
  public virtual bool UploadLog()
  {
   return UploadLog(false);
  }
  public virtual bool UploadLog(bool newJob)
  {
            bool retval = false;
   if (Upload != null) Upload(this, ref retval);
   if (newJob)
   {
    mTraceStatus.PageNumber = 1;
    mTraceStatus.StartDate = System.DateTime.Now;
   }
   else
   {
    mTraceStatus.PageNumber += 1;
    mTraceStatus.LinesWrittenSinceLastUpload = 0;
    mTraceStatus.LogUpload = "";
   }
   return retval;
  }
  public TraceStatus TraceStatus
  {
            get { return mTraceStatus; }
  }
  public UploadableLogSettings2 UploadSettings
  {
            get { return mUploadSettings; }
  }
  public virtual string PageName
  {
            get { return string.Format("{0:ddMMyy} {1}", mTraceStatus.StartDate, mUploadSettings.UploadJobName); }
  }
 }
}
