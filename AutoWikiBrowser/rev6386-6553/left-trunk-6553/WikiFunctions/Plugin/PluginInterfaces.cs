using System.Collections.Generic;
using WikiFunctions.Lists.Providers;
namespace WikiFunctions.Plugin
{
    public interface IAWBBasePlugin
    {
        void Initialise(IAutoWikiBrowser sender);
        string Name { get; }
        string WikiName { get; }
        void LoadSettings(object[] prefs);
        object[] SaveSettings();
    }
    public interface IAWBPlugin : IAWBBasePlugin
    {
        string ProcessArticle(IAutoWikiBrowser sender, IProcessArticleEventArgs eventargs);
        void Reset();
        void Nudge(out bool cancel);
        void Nudged(int nudges);
    }
    public delegate void GetLogUploadLocationsEvent(IAutoWikiBrowser sender, List<Logging.Uploader.LogEntry> locations);
    public interface IProcessArticleEventArgs
    {
        string ArticleText { get; }
        string ArticleTitle { get; }
        string EditSummary { get; set; }
        int NameSpaceKey { get; }
        bool Skip { get; set; }
        Exists Exists { get; }
    }
    public interface IListMakerPlugin : IListProvider
    {
        string Name { get; }
    }
}
