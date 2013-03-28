using System;
using System.Collections.Generic;
using System.Windows.Forms;
using WikiFunctions.API;
using WikiFunctions.Plugin;
namespace WikiFunctions.Logging.Uploader
{
    public struct EditPageRetvals
    {
        public string Article;
        public string ResponseText;
        public string DiffLink;
    }
 public class LogUploader
 {
  protected readonly string BotTag;
  protected readonly string TableHeaderUserName;
  protected readonly string TableHeaderNoUserName;
  protected const string NewCell = "\r\n|";
     private readonly AsyncApiEdit editor;
        protected string UserAgent
        {
            get
            {
                return Tools.DefaultUserAgentString;
            }
        }
  public LogUploader(AsyncApiEdit e)
  {
   BotTag = "|}<!--/bottag-->";
   TableHeaderUserName = "! Job !! Category !! Page # !! Performed By !! Date";
   TableHeaderNoUserName = "! Job !! Category !! Page # !! Date";
      editor = e.Clone();
  }
  public void LogIn(string username, string password)
  {
   editor.Login(username, password);
  }
  public virtual void LogIn(UsernamePassword loginDetails)
  {
   if (loginDetails.IsSet)
   {
    LogIn(loginDetails.Username, loginDetails.Password);
   }
   else
   {
    throw new System.Configuration.SettingsPropertyNotFoundException("Login details not found");
   }
  }
        public virtual List<EditPageRetvals> LogIt(string log, string logTitle, string logDetails, string uploadTo,
            List<LogEntry> linksToLog, int pageNumber, DateTime startDate, bool openInBrowser,
            bool addToWatchlist, string username, string logHeader, bool addLogTemplate,
            string editSummary, string logSummaryEditSummary, string sender, bool addLogArticlesToAnAWBList,
            IAutoWikiBrowser awb)
  {
            List<EditPageRetvals> retval = new List<EditPageRetvals>();
            string uploadToNoSpaces = uploadTo.Replace(" ", "_");
            string strLogText = "";
            AWBLogListener awbLogListener = null;
            if (DoAWBLogListener(addLogArticlesToAnAWBList, awb))
                awbLogListener = new AWBLogListener(uploadTo);
            if (addLogTemplate)
            {
                strLogText = "{{log|name=" + uploadToNoSpaces + "|page=" + pageNumber + "}}" + Environment.NewLine;
            }
            strLogText += logHeader + log;
            Application.DoEvents();
            try
            {
                editor.Open(uploadToNoSpaces);
                editor.Wait();
                SaveInfo save = editor.SynchronousEditor.Save(strLogText, editSummary, false, WatchOptions.NoChange);
                retval.Add(new EditPageRetvals()
                               {
                                   Article = uploadToNoSpaces,
                                   DiffLink = editor.URL + "index.php?oldid=" + save.NewId + "&diff=prev",
                                   ResponseText = save.ResponseXml.OuterXml
                               });
            }
            catch (Exception ex)
            {
                if (awbLogListener != null)
                    AWBLogListenerUploadFailed(ex, sender, awbLogListener, awb);
                throw;
            }
            Application.DoEvents();
            foreach (LogEntry logEntry in linksToLog)
            {
                retval.Add(DoLogEntry(logEntry, logTitle, logDetails, pageNumber, startDate, uploadTo, logSummaryEditSummary,
                    username, addLogArticlesToAnAWBList, awb, sender));
                Application.DoEvents();
            }
            if (openInBrowser)
                OpenLogInBrowser(uploadTo);
            return retval;
        }
        protected virtual EditPageRetvals DoLogEntry(LogEntry logEntry, string logTitle, string logDetails, int pageNumber,
            DateTime startDate, string uploadTo, string editSummary, string username,
            bool addLogArticlesToAnAWBList, IAutoWikiBrowser awb, string sender)
        {
            AWBLogListener awbLogListener = null;
            try
            {
                editor.Open(logEntry.Location);
                editor.Wait();
                string strExistingText = editor.Page.Text;
                if (DoAWBLogListener(addLogArticlesToAnAWBList, awb))
                {
                    if (string.IsNullOrEmpty(sender))
                        sender = "WikiFunctions DLL";
                    awbLogListener = new AWBLogListener(logEntry.Location);
                }
                Application.DoEvents();
                string tableAddition = "|-" + NewCell + "[[" + uploadTo + "|" + logTitle + "]]" + NewCell +
                    logDetails + NewCell + "[[" + uploadTo + "|" + pageNumber + "]]" +
                    (logEntry.LogUserName ? NewCell + "[[User:" + username + "|" + username + "]]" : "") +
                    NewCell + string.Format("[[{0:d MMMM}]] [[{0:yyyy}]]", startDate) +
                    Environment.NewLine + BotTag;
                SaveInfo save;
                if (strExistingText.Contains(BotTag))
                {
                    save = editor.SynchronousEditor.Save(strExistingText.Replace(BotTag, tableAddition), editSummary, false, WatchOptions.NoChange);
                }
                else
                {
                    save = editor.SynchronousEditor.Save(strExistingText + Environment.NewLine + "<!--bottag-->" +
                                Environment.NewLine + "{| class=\"wikitable\" width=\"100%\"" +
                                Environment.NewLine +
                                (logEntry.LogUserName ? TableHeaderUserName : TableHeaderNoUserName) +
                                Environment.NewLine + tableAddition, editSummary, false, WatchOptions.NoChange);
                }
                EditPageRetvals retval = new EditPageRetvals()
                {
                    Article = logEntry.Location,
                    DiffLink = editor.URL + "index.php?oldid=" + save.NewId + "&diff=prev",
                    ResponseText = save.ResponseXml.OuterXml
                };
                try
                {
                    if (awbLogListener != null)
                    {
                        awbLogListener.WriteLine("Log entry uploaded", sender);
                    }
                }
                catch { }
                logEntry.Success=true;
                return retval;
            }
            catch (Exception ex)
            {
                if (awbLogListener != null)
                    AWBLogListenerUploadFailed(ex, sender, awbLogListener, awb);
                throw;
            }
        }
  protected virtual void OpenLogInBrowser(string uploadTo)
  {
            Tools.OpenArticleInBrowser(uploadTo);
  }
        private static bool DoAWBLogListener(bool doIt, IAutoWikiBrowser awb)
        { return (doIt && awb != null); }
        private void AWBLogListenerUploadFailed(Exception ex, string sender, AWBLogListener logListener,
            IAutoWikiBrowser AWB)
        {
            logListener.WriteLine("Error: " + ex.Message, sender);
            ((IMyTraceListener)logListener).SkippedArticle(sender, "Error");
        }
 }
}
