namespace WikiFunctions.Logging
{
    public enum LogFileType
    {
        PlainText = 1, WikiText, AnnotatedWikiText
    }
    public interface IMyTraceListener
    {
        void Close();
        void Flush();
        void ProcessingArticle(string fullArticleTitle, int ns);
        void SkippedArticle(string skippedBy, string reason);
        void SkippedArticleBadTag(string skippedBy, string fullArticleTitle, int ns);
        void SkippedArticleRedlink(string skippedBy, string fullArticleTitle, int ns);
        void Write(string text);
        void WriteArticleActionLine(string line, string pluginName);
        void WriteArticleActionLine(string line, string pluginName, bool verboseOnly);
        void WriteBulletedLine(string line, bool bold, bool verboseOnly);
        void WriteBulletedLine(string line, bool bold, bool verboseOnly, bool dateStamp);
        void WriteComment(string line);
        void WriteCommentAndNewLine(string line);
        void WriteLine(string line);
        void WriteTemplateAdded(string template, string pluginName);
        bool Uploadable { get; }
    }
    public interface IAWBTraceListener : IMyTraceListener
    {
        void AWBSkipped(string reason);
        void UserSkipped();
        void PluginSkipped();
    }
}
