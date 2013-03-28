using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Text;
using System.Windows.Forms;
using System.Reflection;
using System.IO;
using AutoWikiBrowser.Plugins;
using WikiFunctions.Plugin;
using WikiFunctions;
namespace AutoWikiBrowser
{
    internal sealed partial class PluginManager : Form
    {
        private readonly IAutoWikiBrowser _awb;
        private static string _lastPluginLoadedLocation;
        public PluginManager(IAutoWikiBrowser awb)
        {
            InitializeComponent();
            _awb = awb;
        }
        public static void LoadNewPlugin(IAutoWikiBrowser awb)
        {
            OpenFileDialog pluginOpen = new OpenFileDialog();
            if (string.IsNullOrEmpty(_lastPluginLoadedLocation))
                LoadLastPluginLoadedLocation();
            pluginOpen.InitialDirectory = string.IsNullOrEmpty(_lastPluginLoadedLocation) ? Application.StartupPath : _lastPluginLoadedLocation;
            pluginOpen.DefaultExt = "dll";
            pluginOpen.Filter = "DLL files|*.dll";
            pluginOpen.CheckFileExists = pluginOpen.Multiselect = true;
            pluginOpen.ShowDialog();
            if (!string.IsNullOrEmpty(pluginOpen.FileName))
            {
                string newPath = Path.GetDirectoryName(pluginOpen.FileName);
                if (_lastPluginLoadedLocation != newPath)
                {
                    _lastPluginLoadedLocation = newPath;
                    SaveLastPluginLoadedLocation();
                }
            }
            Plugin.LoadPlugins(awb, pluginOpen.FileNames, true);
        }
        static void LoadLastPluginLoadedLocation()
        {
            try
            {
                Microsoft.Win32.RegistryKey reg = Microsoft.Win32.Registry.CurrentUser.
                    OpenSubKey("Software\\AutoWikiBrowser");
                if (reg != null)
                    _lastPluginLoadedLocation = reg.GetValue("RecentPluginLoadedLocation", "").ToString();
            }
            catch
            {
            }
        }
        static void SaveLastPluginLoadedLocation()
        {
            try
            {
                Microsoft.Win32.RegistryKey reg = Microsoft.Win32.Registry.CurrentUser.
                    CreateSubKey("Software\\AutoWikiBrowser");
                if (reg != null)
                    reg.SetValue("RecentPluginLoadedLocation", _lastPluginLoadedLocation);
            }
            catch
            {
            }
        }
        private void PluginManager_Load(object sender, EventArgs e)
        {
            LoadLoadedPluginList();
        }
        private void loadNewPluginsToolStripMenuItem_Click(object sender, EventArgs e)
        {
            LoadNewPlugin(_awb);
            lvPlugin.Items.Clear();
            LoadLoadedPluginList();
        }
        private void LoadLoadedPluginList()
        {
            foreach (string pluginName in Plugin.GetAWBPluginList())
            {
                lvPlugin.Items.Add(new ListViewItem(pluginName) { Group = lvPlugin.Groups["groupAWBLoaded"] });
            }
            foreach (string pluginName in Plugin.GetBasePluginList())
            {
                lvPlugin.Items.Add(new ListViewItem(pluginName) { Group = lvPlugin.Groups["groupBaseLoaded"] });
            }
            foreach (string pluginName in Plugin.GetListMakerPluginList())
            {
                lvPlugin.Items.Add(new ListViewItem(pluginName) { Group = lvPlugin.Groups["groupLMLoaded"] });
            }
            UpdatePluginCount();
        }
        private void contextMenuStrip1_Opening(object sender, CancelEventArgs e)
        {
            loadPluginToolStripMenuItem.Enabled = false;
        }
        private void loadPluginToolStripMenuItem_Click(object sender, EventArgs e)
        {
            string[] plugins = new string[lvPlugin.SelectedItems.Count];
            for (int i = 0; i < lvPlugin.SelectedItems.Count; i++)
            {
                plugins[i] = lvPlugin.Items[lvPlugin.SelectedIndices[i]].Text;
            }
            Plugin.LoadPlugins(_awb, plugins, true);
        }
        private void UpdatePluginCount()
        {
            lblPluginCount.Text = lvPlugin.Items.Count.ToString();
        }
    }
    namespace Plugins
    {
        internal static class Plugin
        {
            static Plugin()
            {
                ErrorHandler.AppendToEventHandler += ErrorHandler_AppendToEventHandler;
            }
            static string ErrorHandler_AppendToEventHandler()
            {
                if (AWBPlugins.Count == 0 && AWBBasePlugins.Count == 0 && ListMakerPlugins.Count == 0)
                    return "";
                StringBuilder builder = new StringBuilder();
                builder.AppendLine("<table>");
                builder.AppendLine("<tr>");
                builder.AppendLine("<th>AWBBasePlugins</th>");
                builder.AppendLine("<th>AWBBasePlugins</th>");
                builder.AppendLine("<th>ListMakerPlugins</th>");
                builder.AppendLine("</tr>");
                builder.AppendLine("<tr>");
                builder.AppendLine("<td>");
                foreach (var p in AWBPlugins)
                {
                    builder.AppendLine("*" + p.Key);
                }
                builder.AppendLine("</td>");
                builder.AppendLine("<td>");
                foreach (var p in AWBBasePlugins)
                {
                    builder.AppendLine("*" + p.Key);
                }
                builder.AppendLine("</td>");
                builder.AppendLine("<td>");
                foreach (var p in ListMakerPlugins)
                {
                    builder.AppendLine("*" + p.Key);
                }
                builder.AppendLine("</td>");
                builder.AppendLine("</tr>");
                builder.AppendLine("</table>");
                return builder.ToString();
            }
            internal static readonly Dictionary<string, IAWBPlugin> AWBPlugins = new Dictionary<string, IAWBPlugin>();
            internal static readonly Dictionary<string, IAWBBasePlugin> AWBBasePlugins = new Dictionary<string, IAWBBasePlugin>();
            internal static readonly Dictionary<string, IListMakerPlugin> ListMakerPlugins = new Dictionary<string, IListMakerPlugin>();
            public static readonly List<IAWBPlugin> FailedPlugins = new List<IAWBPlugin>();
            internal static string GetPluginsWikiTextBlock()
            {
                StringBuilder retval = new StringBuilder();
                foreach (KeyValuePair<string, IAWBPlugin> plugin in AWBPlugins)
                {
                    retval.AppendLine("* " + plugin.Value.WikiName);
                }
                return retval.ToString();
            }
            internal static int Count()
            {
                return AWBPlugins.Count + AWBBasePlugins.Count + ListMakerPlugins.Count;
            }
            internal static List<string> GetAWBPluginList()
            {
                List<string> plugins = new List<string>();
                foreach (KeyValuePair<string, IAWBPlugin> a in AWBPlugins)
                {
                    plugins.Add(a.Key);
                }
                return plugins;
            }
            internal static List<string> GetBasePluginList()
            {
                List<string> plugins = new List<string>();
                foreach (KeyValuePair<string, IAWBBasePlugin> a in AWBBasePlugins)
                {
                    plugins.Add(a.Key);
                }
                return plugins;
            }
            internal static List<string> GetListMakerPluginList()
            {
                List<string> plugins = new List<string>();
                foreach (KeyValuePair<string, IListMakerPlugin> a in ListMakerPlugins)
                {
                    plugins.Add(a.Key);
                }
                return plugins;
            }
            internal static void LoadPluginsStartup(IAutoWikiBrowser awb, Splash splash)
            {
                splash.SetProgress(25);
                string path = Application.StartupPath;
                string[] pluginFiles = Directory.GetFiles(path, "*.DLL");
                LoadPlugins(awb, pluginFiles, false);
                splash.SetProgress(50);
            }
            public static void PluginObsolete(IAWBPlugin plugin)
            {
                if (!FailedPlugins.Contains(plugin))
                    FailedPlugins.Add(plugin);
                PluginObsolete(plugin.GetType().Assembly.Location, plugin.GetType().Assembly.GetName().Version.ToString());
            }
            static void PluginObsolete(string name, string version)
            {
                MessageBox.Show(
                    "The plugin '" + name + "' " + (!string.IsNullOrEmpty(name) ? "(" + version + ")" : "") +
                    "is out-of date and needs to be updated.",
                    "Plugin error", MessageBoxButtons.OK, MessageBoxIcon.Error);
            }
            public static void PurgeFailedPlugins()
            {
                if (FailedPlugins.Count == 0) return;
                foreach (IAWBPlugin p in FailedPlugins)
                {
                    foreach (string s in AWBPlugins.Keys)
                    {
                        if (AWBPlugins[s] == p)
                        {
                            AWBPlugins.Remove(s);
                            break;
                        }
                    }
                }
                FailedPlugins.Clear();
            }
            internal static void LoadPlugins(IAutoWikiBrowser awb, string[] plugins, bool afterStartup)
            {
                try
                {
                    foreach (string plugin in plugins)
                    {
                        if (plugin.EndsWith("DotNetWikiBot.dll") || plugin.EndsWith("Diff.dll")
                            || plugin.EndsWith("WikiFunctions.dll"))
                            continue;
                        Assembly asm = null;
                        try
                        {
                            asm = Assembly.LoadFile(plugin);
                        }
                        catch
                        {
                        }
                        if (asm == null)
                            continue;
                        try
                        {
                            foreach (Type t in asm.GetTypes())
                            {
                                if (t.GetInterface("IAWBPlugin") != null)
                                {
                                    IAWBPlugin awbPlugin =
                                        (IAWBPlugin)Activator.CreateInstance(t);
                                    if (AWBPlugins.ContainsKey(awbPlugin.Name))
                                    {
                                        MessageBox.Show(
                                            "A plugin with the name \"" + awbPlugin.Name +
                                            "\", has already been added.\r\nPlease remove old duplicates from your AutoWikiBrowser Directory, and restart AWB.\r\nThis was loaded from the plugin file \"" +
                                            plugin + "\".", "Duplicate AWB Plugin");
                                        break;
                                    }
                                    InitialisePlugin(awbPlugin, awb);
                                    AWBPlugins.Add(awbPlugin.Name, awbPlugin);
                                    if (afterStartup) UsageStats.AddedPlugin(awbPlugin);
                                }
                                else if (t.GetInterface("IAWBBasePlugin") != null)
                                {
                                    IAWBBasePlugin awbBasePlugin = (IAWBBasePlugin)Activator.CreateInstance(t);
                                    if (AWBBasePlugins.ContainsKey(awbBasePlugin.Name))
                                    {
                                        MessageBox.Show(
                                            "A plugin with the name \"" + awbBasePlugin.Name +
                                            "\", has already been added.\r\nPlease remove old duplicates from your AutoWikiBrowser Directory, and restart AWB.\r\nThis was loaded from the plugin file \"" +
                                            plugin + "\".", "Duplicate AWB Base Plugin");
                                        break;
                                    }
                                    InitialisePlugin(awbBasePlugin, awb);
                                    AWBBasePlugins.Add(awbBasePlugin.Name, awbBasePlugin);
                                    if (afterStartup) UsageStats.AddedPlugin(awbBasePlugin);
                                }
                                else if (t.GetInterface("IListMakerPlugin") != null)
                                {
                                    IListMakerPlugin listMakerPlugin =
                                        (IListMakerPlugin)Activator.CreateInstance(t);
                                    if (ListMakerPlugins.ContainsKey(listMakerPlugin.Name))
                                    {
                                        MessageBox.Show(
                                            "A plugin with the name \"" + listMakerPlugin.Name +
                                            "\", has already been added.\r\nPlease remove old duplicates from your AutoWikiBrowser Directory, and restart AWB.\r\nThis was loaded from the plugin file \"" +
                                            plugin + "\".", "Duplicate AWB ListMaker Plugin");
                                        break;
                                    }
                                    WikiFunctions.Controls.Lists.ListMaker.AddProvider(listMakerPlugin);
                                    ListMakerPlugins.Add(listMakerPlugin.Name, listMakerPlugin);
                                    if (afterStartup) UsageStats.AddedPlugin(listMakerPlugin);
                                }
                            }
                        }
                        catch (ReflectionTypeLoadException)
                        {
                            PluginObsolete(plugin, asm.GetName().Version.ToString());
                        }
                        catch (MissingMemberException)
                        {
                            PluginObsolete(plugin, asm.GetName().Version.ToString());
                        }
                        catch (Exception ex)
                        {
                            ErrorHandler.Handle(ex);
                        }
                    }
                }
                catch (Exception ex)
                {
                    MessageBox.Show(ex.Message, "Problem loading plugins");
                }
            }
            private static void InitialisePlugin(IAWBBasePlugin plugin, IAutoWikiBrowser awb)
            {
                plugin.Initialise(awb);
            }
            internal static string GetPluginVersionString(IAWBBasePlugin plugin)
            { return Assembly.GetAssembly(plugin.GetType()).GetName().Version.ToString(); }
            internal static string GetPluginVersionString(IListMakerPlugin plugin)
            { return Assembly.GetAssembly(plugin.GetType()).GetName().Version.ToString(); }
        }
    }
}
