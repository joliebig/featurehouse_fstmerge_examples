using WikiFunctions.Plugin;
using System.Windows.Forms;
namespace AutoWikiBrowser
{
    partial class MainForm
    {
        CheckBox IAutoWikiBrowserForm.BotModeCheckbox { get { return chkAutoMode; } }
        CheckBox IAutoWikiBrowserForm.SkipNoChangesCheckBox { get { return chkSkipNoChanges; } }
        Button IAutoWikiBrowserForm.PreviewButton { get { return btnPreview; } }
        Button IAutoWikiBrowserForm.SaveButton { get { return btnSave; } }
        Button IAutoWikiBrowserForm.SkipButton { get { return btnIgnore; } }
        Button IAutoWikiBrowserForm.StopButton { get { return btnStop; } }
        Button IAutoWikiBrowserForm.DiffButton { get { return btnDiff; } }
        Button IAutoWikiBrowserForm.StartButton { get { return btnStart; } }
        ComboBox IAutoWikiBrowserForm.EditSummaryComboBox { get { return cmboEditSummary; } }
        StatusStrip IAutoWikiBrowserForm.StatusStrip { get { return StatusMain; } }
        NotifyIcon IAutoWikiBrowserForm.NotifyIcon { get { return ntfyTray; } }
        RadioButton IAutoWikiBrowserForm.SkipNonExistentPages { get { return radSkipNonExistent; } }
        CheckBox IAutoWikiBrowserForm.ApplyGeneralFixesCheckBox { get { return chkGeneralFixes; } }
        CheckBox IAutoWikiBrowserForm.AutoTagCheckBox { get { return chkAutoTagger; } }
        CheckBox IAutoWikiBrowserForm.RegexTypoFix { get { return chkRegExTypo; } }
        bool IAutoWikiBrowserForm.PreParseMode { get { return preParseModeToolStripMenuItem.Checked; }}
        TextBoxBase IAutoWikiBrowserForm.EditBox { get { return txtEdit; } }
        TextBox IAutoWikiBrowserForm.CategoryTextBox { get { return loggingSettings1.LoggingCategoryTextBox; } }
        Form IAutoWikiBrowserForm.Form { get { return this; } }
        ToolStripMenuItem IAutoWikiBrowserForm.HelpToolStripMenuItem { get { return helpToolStripMenuItem; } }
        ToolStripMenuItem IAutoWikiBrowserForm.PluginsToolStripMenuItem { get { return pluginsToolStripMenuItem; } }
        ToolStripMenuItem IAutoWikiBrowserForm.InsertTagToolStripMenuItem { get { return insertTagToolStripMenuItem; } }
        ToolStripMenuItem IAutoWikiBrowserForm.ToolStripMenuGeneral { get { return ToolStripMenuGeneral; } }
        WikiFunctions.Controls.Lists.ListMaker IAutoWikiBrowserForm.ListMaker { get { return listMaker; } }
        ContextMenuStrip IAutoWikiBrowserForm.EditBoxContextMenu { get { return mnuTextBox; } }
        WikiFunctions.Logging.LogControl IAutoWikiBrowserForm.LogControl { get { return logControl; } }
        string IAutoWikiBrowserForm.StatusLabelText { get { return StatusLabelText; } set { StatusLabelText = value; } }
    }
}
