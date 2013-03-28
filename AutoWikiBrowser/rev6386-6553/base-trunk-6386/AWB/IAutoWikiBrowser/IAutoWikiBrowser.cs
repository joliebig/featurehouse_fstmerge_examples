using System.Collections.Generic;
using WikiFunctions.Plugin;
using WikiFunctions.Logging;
namespace AutoWikiBrowser
{
    partial class MainForm
    {
        TraceManager IAutoWikiBrowser.TraceManager { get { return Program.MyTrace; } }
        WikiFunctions.Logging.Uploader.UploadableLogSettings2 IAutoWikiBrowser.LoggingSettings { get { return Program.MyTrace.LS.Settings; } }
        bool IAutoWikiBrowser.SkipNoChanges { get { return chkSkipNoChanges.Checked; } set { chkSkipNoChanges.Checked = value; } }
        WikiFunctions.Parse.FindandReplace IAutoWikiBrowser.FindandReplace { get { return FindAndReplace; } }
        WikiFunctions.SubstTemplates IAutoWikiBrowser.SubstTemplates { get { return SubstTemplates; } }
        string IAutoWikiBrowser.CustomModule { get { return (CModule.ModuleUsable) ? CModule.Code : null; } }
        public event GetLogUploadLocationsEvent GetLogUploadLocations;
        internal void RaiseGetLogUploadLocationsEvent(object sender, List<WikiFunctions.Logging.Uploader.LogEntry> locations)
        {
            if (GetLogUploadLocations != null)
                GetLogUploadLocations(this, locations);
        }
    }
}
