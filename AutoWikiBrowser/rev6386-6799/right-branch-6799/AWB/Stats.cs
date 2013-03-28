using System;
using System.Collections.Specialized;
using System.Collections.Generic;
using WikiFunctions;
using System.Net;
using System.IO;
using WikiFunctions.Plugin;
using System.Xml;
namespace AutoWikiBrowser
{
    partial class MainForm
    {
        private int NoEdits;
        public int NumberOfEdits
        {
            get { return NoEdits; }
            private set
            {
                NoEdits = value;
                lblEditCount.Text = "Edits: " + value;
                if (value == 100 || (value > 0 && value % 1000 == 0))
                    UsageStats.Do(false);
            }
        }
        private int NoNewPages;
        public int NumberOfNewPages
        {
            get { return NoNewPages; }
            private set
            {
                NoNewPages = value;
                lblNewArticles.Text = "New: " + value;
            }
        }
        private int NoIgnoredEdits;
        public int NumberOfIgnoredEdits
        {
            get { return NoIgnoredEdits; }
            private set
            {
                NoIgnoredEdits = value;
                lblIgnoredArticles.Text = "Skipped: " + value;
            }
        }
        private int NoEditsPerMin;
        public int NumberOfEditsPerMinute
        {
            get { return NoEditsPerMin; }
            private set
            {
                NoEditsPerMin = value;
                lblEditsPerMin.Text = "Edits/min: " + value;
            }
        }
        private int NoPagesPerMin;
        public int NumberOfPagesPerMinute
        {
            get { return NoPagesPerMin; }
            private set
            {
                NoPagesPerMin = value;
                lblPagesPerMin.Text = "Pages/min: " + value;
            }
        }
    }
    internal static class UsageStats
    {
        private const string StatsURL = "http://toolserver.org/~awb/stats/";
        private static int RecordId,
            SecretNumber,
            LastEditCount;
        private static bool SentUserName;
        private static readonly List<IAWBPlugin> NewAWBPlugins = new List<IAWBPlugin>();
        private static readonly List<IAWBBasePlugin> NewAWBBasePlugins = new List<IAWBBasePlugin>();
        private static readonly List<IListMakerPlugin> NewListMakerPlugins = new List<IListMakerPlugin>();
        private static string UserName
        {
            get { return Variables.MainForm.TheSession.User.Name; }
        }
        internal static void Do(bool appexit)
        {
            try
            {
                if (EstablishedContact)
                {
                    if (Program.AWB.NumberOfEdits > LastEditCount || NewPluginsAdded
                        || HaveUserNameToSend) SubsequentContact();
                }
                else
                    FirstContact();
                LastEditCount = Program.AWB.NumberOfEdits;
            }
            catch (Exception ex)
            {
                if (appexit) ErrorHandler.Handle(ex);
            }
        }
        static bool NewPluginsAdded
        {
            get
            {
                return NewAWBPlugins.Count > 0 || NewAWBBasePlugins.Count > 0
                       || NewListMakerPlugins.Count > 0;
            }
        }
        internal static void AddedPlugin(IAWBPlugin plugin)
        {
            if (EstablishedContact) NewAWBPlugins.Add(plugin);
        }
        internal static void AddedPlugin(IAWBBasePlugin plugin)
        {
            if (EstablishedContact) NewAWBBasePlugins.Add(plugin);
        }
        internal static void AddedPlugin(IListMakerPlugin plugin)
        {
            if (EstablishedContact) NewListMakerPlugins.Add(plugin);
        }
        private static void FirstContact()
        {
            if (Program.AWB.NumberOfEdits == 0) return;
            NameValueCollection postvars = new NameValueCollection()
                                               {
                                                   new String[] {"Action", "Hello"},
                                                   new String[] {"Version", Program.VersionString}
                                               };
            if (Variables.IsCustomProject || Variables.IsWikia)
                postvars.Add("Wiki", Variables.Host);
            else
                postvars.Add("Wiki", Variables.Project.ToString());
            if (Variables.IsWikia)
            {
                postvars.Add("Language", "WIK");
            }
            else if (Variables.IsCustomProject || Variables.IsWikimediaMonolingualProject)
            {
                postvars.Add("Language", "CUS");
            }
            else
            {
                postvars.Add("Language", Variables.LangCode);
            }
            postvars.Add("Culture", System.Threading.Thread.CurrentThread.CurrentCulture.ToString());
            ProcessUsername(postvars);
            postvars.Add("Saves", Program.AWB.NumberOfEdits.ToString());
            postvars.Add("OS", Environment.OSVersion.VersionString);
            postvars.Add("Debug", "N");
            EnumeratePlugins(postvars,
                             Plugins.Plugin.AWBPlugins.Values,
                             Plugins.Plugin.AWBBasePlugins.Values,
                             Plugins.Plugin.ListMakerPlugins.Values);
            ReadXML(PostData(postvars));
        }
        private static void SubsequentContact()
        {
            NameValueCollection postvars = new NameValueCollection()
                                               {
                                                  new String[] {"Action", "Update"},
                                                  new String[] {"RecordID", RecordId.ToString()},
                                                  new String[] {"Verify", SecretNumber.ToString()}
                                               };
            EnumeratePlugins(postvars, NewAWBPlugins, NewAWBBasePlugins, NewListMakerPlugins);
            ProcessUsername(postvars);
            if (Program.AWB.NumberOfEdits > LastEditCount)
                postvars.Add("Saves", Program.AWB.NumberOfEdits.ToString());
            PostData(postvars);
            NewAWBPlugins.Clear();
            NewAWBBasePlugins.Clear();
            NewListMakerPlugins.Clear();
        }
        private static bool EstablishedContact
        { get { return (RecordId > 0); } }
        private static string PostData(NameValueCollection postvars)
        {
            try
            {
                Program.AWB.StartProgressBar();
                StatusLabelText = "Contacting stats server...";
                Program.AWB.Form.Cursor = System.Windows.Forms.Cursors.WaitCursor;
                return Tools.PostData(postvars, StatsURL);
            }
            catch (WebException ex)
            {
                Tools.WriteDebug("UsageStats", ex.Message);
            }
            catch (IOException ex)
            {
                Tools.WriteDebug("UsageStats", ex.Message);
            }
            finally
            {
                Program.AWB.StopProgressBar();
                StatusLabelText = "";
                Program.AWB.Form.Cursor = System.Windows.Forms.Cursors.Default;
            }
            return null;
        }
        private static string StatusLabelText { set { Program.AWB.StatusLabelText = value; } }
        private static void EnumeratePlugins(NameValueCollection postvars, ICollection<IAWBPlugin> awbPlugins, ICollection<IAWBBasePlugin> awbBasePlugins, ICollection<IListMakerPlugin> listMakerPlugins)
        {
            int i = 0;
            postvars.Add("PluginCount", (awbPlugins.Count + awbBasePlugins.Count + listMakerPlugins.Count).ToString());
            foreach (IAWBPlugin plugin in awbPlugins)
            {
                i++;
                string p = "P" + i;
                postvars.Add(p + "N", plugin.Name);
                postvars.Add(p + "V", Plugins.Plugin.GetPluginVersionString(plugin));
                postvars.Add(p + "T", "0");
            }
            foreach (IListMakerPlugin plugin in listMakerPlugins)
            {
                i++;
                string p = "P" + i;
                postvars.Add(p + "N", plugin.Name);
                postvars.Add(p + "V", Plugins.Plugin.GetPluginVersionString(plugin));
                postvars.Add(p + "T", "1");
            }
            foreach (IAWBBasePlugin plugin in awbBasePlugins)
            {
                i++;
                string p = "P" + i;
                postvars.Add(p + "N", plugin.Name);
                postvars.Add(p + "V", Plugins.Plugin.GetPluginVersionString(plugin));
                postvars.Add(p + "T", "2");
            }
        }
        private static void ReadXML(string xml)
        {
            try
            {
                if (string.IsNullOrEmpty(xml)) return;
                XmlDocument doc = new XmlDocument();
                doc.LoadXml(xml);
                XmlNodeList nodes = doc.GetElementsByTagName("DB");
                if (nodes.Count == 1 && nodes[0].Attributes.Count == 2)
                {
                    RecordId = int.Parse(nodes[0].Attributes["Record"].Value);
                    SecretNumber = int.Parse(nodes[0].Attributes["Verify"].Value);
                }
                else
                {
                    throw new XmlException("Error parsing XML returned from UsageStats server");
                }
            }
            catch (Exception ex)
            {
                if (ex is XmlException)
                    throw;
                throw new XmlException("Error parsing XML returned from UsageStats server", ex);
            }
        }
        private static void ProcessUsername(NameValueCollection postvars)
        {
            if (!SentUserName)
            {
                if (Properties.Settings.Default.Privacy)
                {
                    postvars.Add("User", "<Withheld>");
                    SentUserName = true;
                }
                else if (!string.IsNullOrEmpty(UserName))
                {
                    postvars.Add("User", UserName);
                    SentUserName = true;
                }
            }
        }
        private static bool HaveUserNameToSend
        {
            get
            {
                return (!SentUserName &&
                    (Properties.Settings.Default.Privacy || !string.IsNullOrEmpty(UserName)));
            }
        }
        internal static void OpenUsageStatsURL()
        { Tools.OpenURLInBrowser(StatsURL); }
    }
}
