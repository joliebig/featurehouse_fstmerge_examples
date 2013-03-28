using System;
using System.Windows.Forms;
using WikiFunctions.Logging;
namespace WikiFunctions.Plugin
{
    public interface IAutoWikiBrowserForm : IAutoWikiBrowserTabs
    {
        Form Form { get; }
        TextBoxBase EditBox { get; }
        TextBox CategoryTextBox { get; }
        CheckBox BotModeCheckbox { get; }
        CheckBox SkipNoChangesCheckBox { get; }
        Button DiffButton { get; }
        Button PreviewButton { get; }
        Button SaveButton { get; }
        Button SkipButton { get; }
        Button StartButton { get; }
        Button StopButton { get; }
        ComboBox EditSummaryComboBox { get; }
        StatusStrip StatusStrip { get; }
        NotifyIcon NotifyIcon { get; }
        ToolStripMenuItem HelpToolStripMenuItem { get; }
        RadioButton SkipNonExistentPages { get; }
        CheckBox ApplyGeneralFixesCheckBox { get; }
        CheckBox AutoTagCheckBox { get; }
        CheckBox RegexTypoFix { get; }
        bool PreParseMode { get; }
        ToolStripMenuItem PluginsToolStripMenuItem { get; }
        ToolStripMenuItem InsertTagToolStripMenuItem { get; }
        ToolStripMenuItem ToolStripMenuGeneral { get; }
        Controls.Lists.ListMaker ListMaker { get; }
        ContextMenuStrip EditBoxContextMenu { get; }
        LogControl LogControl { get; }
        Session TheSession { get; }
        void NotifyBalloon(string message, ToolTipIcon icon);
        string StatusLabelText { get; set; }
    }
    public interface IAutoWikiBrowserTabs
    {
        TabPage MoreOptionsTab { get; }
        TabPage OptionsTab { get; }
        TabPage StartTab { get; }
        TabPage SkipTab { get; }
        TabPage DabTab { get; }
        TabPage BotTab { get; }
        TabPage LoggingTab { get; }
        void AddTabPage(TabPage tabp);
        void RemoveTabPage(TabPage tabp);
        void HideAllTabPages();
        void ShowAllTabPages();
        bool ContainsTabPage(TabPage tabp);
    }
    public interface IAutoWikiBrowserInfo
    {
        Version AWBVersion { get; }
        Version WikiFunctionsVersion { get; }
        string AWBVersionString { get; }
        string WikiFunctionsVersionString { get; }
        string WikiDiffVersionString { get; }
        int NumberOfEdits { get; }
        int NumberOfNewPages { get; }
        int NumberOfIgnoredEdits { get; }
        int NumberOfEditsPerMinute { get; }
        int NumberOfPagesPerMinute { get; }
        int Nudges { get; }
        ProjectEnum Project { get; }
        string LangCode { get; }
        bool CheckStatus(bool login);
        bool Privacy { get; }
        bool Shutdown { get; }
    }
    public interface IAutoWikiBrowserCommands
    {
        void ShowHelp(string url);
        void ShowHelpEnWiki(string article);
        void Start(IAWBPlugin sender);
        void Start(string sender);
        void Stop(IAWBPlugin sender);
        void Stop(string sender);
        void Save(IAWBPlugin sender);
        void Save(string sender);
        void AddLogItem(bool skipped, AWBLogListener logListener);
        void TurnOffLogging();
        void SkipPage(IAWBPlugin sender, string reason);
        void SkipPage(string sender, string reason);
        void GetDiff(IAWBPlugin sender);
        void GetDiff(string sender);
        void GetPreview(IAWBPlugin sender);
        void GetPreview(string sender);
        void AddMainFormClosingEventHandler(FormClosingEventHandler handler);
        void StartProgressBar();
        void StopProgressBar();
        void AddArticleRedirectedEventHandler(ArticleRedirected handler);
    }
    public interface IAutoWikiBrowser : IAutoWikiBrowserForm, IAutoWikiBrowserCommands, IAutoWikiBrowserInfo
    {
        TraceManager TraceManager { get; }
        Logging.Uploader.UploadableLogSettings2 LoggingSettings { get; }
        bool SkipNoChanges { get; set; }
        Parse.FindandReplace FindandReplace { get; }
        SubstTemplates SubstTemplates { get; }
        string CustomModule { get; }
        event GetLogUploadLocationsEvent GetLogUploadLocations;
    }
}
