using System;
using WikiFunctions;
using WikiFunctions.Plugin;
namespace AutoWikiBrowser
{
    partial class MainForm
    {
        Version IAutoWikiBrowserInfo.AWBVersion { get { return Program.Version; } }
        Version IAutoWikiBrowserInfo.WikiFunctionsVersion { get { return Tools.Version; } }
        string IAutoWikiBrowserInfo.AWBVersionString { get { return Program.VersionString; } }
        string IAutoWikiBrowserInfo.WikiFunctionsVersionString { get { return Tools.VersionString; } }
        string IAutoWikiBrowserInfo.WikiDiffVersionString { get { return "(internal)"; } }
        string IAutoWikiBrowserInfo.LangCode { get { return Variables.LangCode; } }
        ProjectEnum IAutoWikiBrowserInfo.Project { get { return Variables.Project; } }
        bool IAutoWikiBrowserInfo.Privacy { get { return Properties.Settings.Default.Privacy; } }
        bool IAutoWikiBrowserInfo.Shutdown { get { return ShuttingDown; } }
    }
}
