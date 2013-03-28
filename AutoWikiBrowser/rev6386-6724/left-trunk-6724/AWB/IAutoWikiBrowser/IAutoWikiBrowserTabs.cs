using System.Collections.Generic;
using System.Windows.Forms;
using WikiFunctions.Plugin;
namespace AutoWikiBrowser
{
    partial class MainForm
    {
        private static List<TabPage> HiddenTabPages = new List<TabPage>();
        TabPage IAutoWikiBrowserTabs.MoreOptionsTab { get { return tpMoreOptions; } }
        TabPage IAutoWikiBrowserTabs.OptionsTab { get { return tpOptions; } }
        TabPage IAutoWikiBrowserTabs.SkipTab { get { return tpSkip; } }
        TabPage IAutoWikiBrowserTabs.StartTab { get { return tpStart; } }
        TabPage IAutoWikiBrowserTabs.DabTab { get { return tpDab; } }
        TabPage IAutoWikiBrowserTabs.BotTab { get { return tpBots; } }
        TabPage IAutoWikiBrowserTabs.LoggingTab { get { return tpLoggingOptions; } }
        bool IAutoWikiBrowserTabs.ContainsTabPage(TabPage tabp) { return MainTab.TabPages.Contains(tabp); }
        private void AddTabPage(TabPage tabp)
        {
            if (!MainTab.TabPages.Contains(tabp))
                MainTab.TabPages.Add(tabp);
        }
        void IAutoWikiBrowserTabs.AddTabPage(TabPage tabp)
        { AddTabPage(tabp); }
        void IAutoWikiBrowserTabs.RemoveTabPage(TabPage tabp)
        {
            if (MainTab.TabPages.Contains(tabp))
                MainTab.TabPages.Remove(tabp);
            if (HiddenTabPages.Contains(tabp))
                HiddenTabPages.Remove(tabp);
        }
        void IAutoWikiBrowserTabs.HideAllTabPages()
        {
            HiddenTabPages = new List<TabPage>();
            foreach (TabPage tabp in MainTab.TabPages)
            {
                HiddenTabPages.Add(tabp);
            }
            MainTab.TabPages.Clear();
        }
        void IAutoWikiBrowserTabs.ShowAllTabPages()
        {
            foreach (TabPage tabp in MainTab.TabPages)
            {
                if (!HiddenTabPages.Contains(tabp))
                    HiddenTabPages.Add(tabp);
            }
            MainTab.TabPages.Clear();
            AddTabPage(tpOptions);
            AddTabPage(tpMoreOptions);
            AddTabPage(tpSkip);
            AddTabPage(tpDab);
            AddTabPage(tpBots);
            AddTabPage(tpStart);
            HiddenTabPages.Remove(tpOptions);
            HiddenTabPages.Remove(tpMoreOptions);
            HiddenTabPages.Remove(tpSkip);
            HiddenTabPages.Remove(tpDab);
            HiddenTabPages.Remove(tpBots);
            HiddenTabPages.Remove(tpStart);
            foreach (TabPage tabp in HiddenTabPages)
            { MainTab.TabPages.Add(tabp); }
            HiddenTabPages.Clear();
        }
    }
}
