using WikiFunctions.Logging;
using WikiFunctions.API;
namespace AutoWikiBrowser
{
    internal sealed class ArticleEX : WikiFunctions.Article
    {
        public override IAWBTraceListener Trace
        { get { return Program.MyTrace; } }
        public override AWBLogListener InitialiseLogListener()
        {
            InitialiseLogListener("AWB", Program.MyTrace);
            Program.MyTrace.AddListener("AWB", mAWBLogListener);
            return mAWBLogListener;
        }
        public ArticleEX()
        {
        }
        public ArticleEX(string title, string text)
            : base(title, text)
        {
            InitialiseLogListener();
        }
        public ArticleEX(PageInfo page)
            : base(page)
        {
            InitialiseLogListener();
        }
        internal static void Close()
        { Program.MyTrace.RemoveListener("AWB"); }
        internal static ArticleEX SwitchToNewArticleObject(ArticleEX old, ArticleEX @new)
        {
            if(old != null && old.LogListener != null)
                Close();
            @new.InitialiseLogListener();
            return @new;
        }
    }
}
