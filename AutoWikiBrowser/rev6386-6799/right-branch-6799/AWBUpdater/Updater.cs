using System;
using System.Text;
using System.Windows.Forms;
using System.IO;
using System.Diagnostics;
using System.Reflection;
using ICSharpCode.SharpZipLib.Zip;
using System.Text.RegularExpressions;
using System.Net;
namespace AwbUpdater
{
    internal sealed partial class Updater : Form
    {
        private readonly string AWBdirectory = "", TempDirectory = "";
        private string AWBZipName = "", AWBWebAddress = "", UpdaterZipName = "", UpdaterWebAddress = "";
        private IWebProxy Proxy;
        private bool UpdaterUpdate, AWBUpdate, UpdateSucessful;
        public Updater()
        {
            InitializeComponent();
            Text += " - " + Application.ProductVersion;
            AWBdirectory = Path.GetDirectoryName(Application.ExecutablePath);
            TempDirectory = Environment.GetEnvironmentVariable("TEMP") ?? "C:\\Windows\\Temp";
            TempDirectory = Path.Combine(TempDirectory,"$AWB$Updater$Temp$");
        }
        private static int AssemblyVersion
        {
            get { return StringToVersion(Application.ProductVersion); }
        }
        private void Updater_Load(object sender, EventArgs e)
        {
            tmrTimer.Enabled = true;
            UpdateUI("Initialising...", true);
        }
        private void UpdateAwb()
        {
            try
            {
                Proxy = WebRequest.GetSystemWebProxy();
                if (Proxy.IsBypassed(new Uri("http://en.wikipedia.org")))
                    Proxy = null;
                UpdateUI("Getting current AWB and Updater versions", true);
                AWBVersion();
                if ((!UpdaterUpdate && !AWBUpdate) && string.IsNullOrEmpty(AWBWebAddress))
                    ExitEarly();
                else
                {
                    UpdateUI("Creating a temporary directory", true);
                    CreateTempDir();
                    UpdateUI("Downloading AWB", true);
                    GetAwbFromInternet();
                    UpdateUI("Unzipping AWB to the temporary directory", true);
                    UnzipAwb();
                    UpdateUI("Making sure AWB is closed", true);
                    CloseAwb();
                    UpdateUI("Copying AWB files from temp to AWB directory...", true);
                    CopyFiles();
                    UpdateUI("Update successful", true);
                    UpdateUI("Cleaning up from update", true);
                    KillTempDir();
                    UpdateSucessful = true;
                    ReadyToExit();
                }
            }
            catch (AbortException)
            {
                ReadyToExit();
            }
            catch (Exception ex)
            {
                ErrorHandler.Handle(ex);
            }
        }
        private void UpdateUI(string currentStatus, bool newLine)
        {
            if (newLine)
            {
                lstLog.Items.Add(currentStatus);
            }
            else
            {
                lstLog.Items[lstLog.Items.Count - 1] = currentStatus;
            }
            lstLog.SelectedIndex = lstLog.Items.Count - 1;
            Application.DoEvents();
        }
        private void AppendLine(string line)
        {
            lstLog.Items[lstLog.Items.Count - 1] += line;
        }
        private void ExitEarly()
        {
            UpdateUI("Nothing to update", true);
            StartAwb();
            ReadyToExit();
        }
        private void ReadyToExit()
        {
            btnCancel.Text = "Close";
            lblStatus.Text = "Press close to exit";
            progressUpdate.Visible = false;
            btnCancel.Enabled = true;
        }
        private void CreateTempDir()
        {
            if (Directory.Exists(TempDirectory))
            {
                Directory.Delete(TempDirectory, true);
            }
            Directory.CreateDirectory(TempDirectory);
            progressUpdate.Value = 10;
        }
        private void AWBVersion()
        {
            string text;
            UpdateUI("   Retrieving current version...", true);
            try
            {
                HttpWebRequest rq = (HttpWebRequest)WebRequest.Create("http://en.wikipedia.org/w/index.php?title=Wikipedia:AutoWikiBrowser/CheckPage/Version&action=raw");
                rq.Proxy = Proxy;
                rq.UserAgent = string.Format("AWBUpdater/{0} ({1}; .NET CLR {2})",
                                             Assembly.GetExecutingAssembly().GetName().Version,
                                             Environment.OSVersion.VersionString, Environment.Version);
                HttpWebResponse response = (HttpWebResponse)rq.GetResponse();
                Stream stream = response.GetResponseStream();
                StreamReader sr = new StreamReader(stream, Encoding.UTF8);
                text = sr.ReadToEnd();
                sr.Close();
                stream.Close();
                response.Close();
            }
            catch
            {
                AppendLine("FAILED");
                throw new AbortException();
            }
            int awbCurrentVersion =
                StringToVersion(Regex.Match(text, @"<!-- Current version: (.*?) -->").Groups[1].Value);
            int awbNewestVersion =
                StringToVersion(Regex.Match(text, @"<!-- Newest version: (.*?) -->").Groups[1].Value);
            int updaterVersion = StringToVersion(Regex.Match(text, @"<!-- Updater version: (.*?) -->").Groups[1].Value);
            try
            {
                AWBUpdate = UpdaterUpdate = false;
                FileVersionInfo awbVersionInfo = FileVersionInfo.GetVersionInfo(Path.Combine(AWBdirectory, "AutoWikiBrowser.exe"));
                int awbFileVersion = StringToVersion(awbVersionInfo.FileVersion);
                if (awbFileVersion < awbCurrentVersion)
                    AWBUpdate = true;
                else if ((awbFileVersion >= awbCurrentVersion) &&
                         (awbFileVersion < awbNewestVersion) &&
                         MessageBox.Show("There is an optional update to AutoWikiBrowser. Would you like to upgrade?", "Optional update", MessageBoxButtons.YesNo) == DialogResult.Yes)
                    AWBUpdate = true;
                if (AWBUpdate)
                {
                    AWBZipName = "AutoWikiBrowser" + awbNewestVersion + ".zip";
                    AWBWebAddress = "http://downloads.sourceforge.net/autowikibrowser/" + AWBZipName;
                }
                else if ((updaterVersion > 1400) &&
                         (updaterVersion > AssemblyVersion))
                {
                    UpdaterZipName = "AWBUpdater" + updaterVersion + ".zip";
                    UpdaterWebAddress = "http://downloads.sourceforge.net/autowikibrowser/" + UpdaterZipName;
                    UpdaterUpdate = true;
                }
            }
            catch
            {
                UpdateUI("   Unable to find AutoWikiBrowser.exe to query its version", true);
            }
            progressUpdate.Value = 35;
        }
        private void GetAwbFromInternet()
        {
            WebClient client = new WebClient() {Proxy = Proxy};
            if (!string.IsNullOrEmpty(AWBWebAddress))
                client.DownloadFile(AWBWebAddress, Path.Combine(TempDirectory, AWBZipName));
            else if (!string.IsNullOrEmpty(UpdaterWebAddress))
                client.DownloadFile(UpdaterWebAddress, Path.Combine(TempDirectory, UpdaterZipName));
            client.Dispose();
            progressUpdate.Value = 50;
        }
        private void UnzipAwb()
        {
            string zip = Path.Combine(TempDirectory, AWBZipName);
            if (!string.IsNullOrEmpty(AWBZipName) && File.Exists(zip))
            {
                Extract(zip);
                DeleteAbsoluteIfExists(zip);
            }
            zip = Path.Combine(TempDirectory, UpdaterZipName);
            if (!string.IsNullOrEmpty(UpdaterZipName) && File.Exists(zip))
            {
                Extract(zip);
                DeleteAbsoluteIfExists(zip);
            }
            progressUpdate.Value = 70;
        }
        private void Extract(string file)
        {
            try
            {
                new FastZip().ExtractZip(file, TempDirectory, null);
            }
            catch (Exception ex)
            {
                ErrorHandler.Handle(ex);
                Application.Exit();
            }
        }
        private void CloseAwb()
        {
            bool awbOpen = false;
            do
            {
                foreach (Process p in Process.GetProcesses())
                {
                    awbOpen = (p.ProcessName == "AutoWikiBrowser" || p.ProcessName == "IRCMonitor");
                    if (awbOpen)
                    {
                        MessageBox.Show("Please save your settings (if you wish) and close " + p.ProcessName + " completely before pressing OK.");
                        break;
                    }
                }
            }
            while (awbOpen);
            progressUpdate.Value = 75;
        }
        private static void DeleteAbsoluteIfExists(string path)
        {
            if (File.Exists(path))
                File.Delete(path);
        }
        private void DeleteIfExists(string name)
        {
         string path = Path.Combine(AWBdirectory, name);
         while (true)
         {
          try
          {
                    DeleteAbsoluteIfExists(path);
          }
                catch (UnauthorizedAccessException)
                {
                    MessageBox.Show(this,
                        "Access denied for deleting files. Program Files and such are not the best place to run AWB from.\r\n" +
                        "Please run the updater with Administrator rights.");
                    Fail();
                }
          catch (Exception ex)
          {
           if (MessageBox.Show(
            this,
            "Problem deleting file:\r\n   " + ex.Message + "\r\n\r\n" +
            "Please close all applications that may use it and press 'Retry' to try again " +
            "or 'Cancel' to cancel the upgrade.",
            "Error",
            MessageBoxButtons.RetryCancel, MessageBoxIcon.Error) == DialogResult.Retry)
           {
            continue;
           }
              Fail();
          }
             break;
         }
        }
        private void Fail()
        {
            AppendLine("... FAILED");
            UpdateUI("Update aborted. AutoWikiBrowser may be unfunctional", true);
            KillTempDir();
            ReadyToExit();
            throw new AbortException();
        }
        private void CopyFiles()
        {
            string updater = Path.Combine(TempDirectory, "AWBUpdater.exe");
            if (UpdaterUpdate || File.Exists(updater))
                CopyFile(updater, Path.Combine(AWBdirectory, "AWBUpdater.exe.new"));
            if (AWBUpdate)
            {
                DeleteIfExists("Wikidiff2.dll");
                DeleteIfExists("Diff.dll");
                DeleteIfExists("WikiFunctions2.dll");
                DeleteIfExists("WPAssessmentsCatCreator.dll");
                if (Directory.Exists(Path.Combine(AWBdirectory, "Plugins\\WPAssessmentsCatCreator")))
                    Directory.Delete(Path.Combine(AWBdirectory, "Plugins\\WPAssessmentsCatCreator"), true);
                foreach (string file in Directory.GetFiles(TempDirectory, "*.*", SearchOption.AllDirectories))
                {
                    if (file.Contains("AWBUpdater"))
                        continue;
                    CopyFile(file,
                             Path.Combine(AWBdirectory, file.Replace(TempDirectory + "\\", "")));
                }
                string[] pluginFiles = Directory.GetFiles(Path.Combine(AWBdirectory, "Plugins"), "*.*", SearchOption.AllDirectories);
                foreach (string file in Directory.GetFiles(AWBdirectory, "*.*", SearchOption.TopDirectoryOnly))
                {
                    foreach (string pluginFile in pluginFiles)
                    {
                        if (file.Substring(file.LastIndexOf("\\")) == pluginFile.Substring(pluginFile.LastIndexOf("\\")))
                        {
                            File.Copy(pluginFile, file, true);
                            break;
                        }
                    }
                }
            }
            progressUpdate.Value = 95;
        }
        private void CopyFile(string source, string destination)
        {
            CreatePath(destination);
            UpdateUI("     " + destination, true);
            while (true)
            {
                try
                {
                    File.Copy(source, destination, true);
                }
                catch (UnauthorizedAccessException)
                {
                    MessageBox.Show(this,
                        "Access denied for copying files. Program Files and such are not the best place to run AWB from.\r\n" +
                        "Please run the updater with Administrator rights.");
                    Fail();
                }
                catch (Exception ex)
                {
                    if (MessageBox.Show(
                            this,
                            "Problem replacing file:\r\n   " + ex.Message + "\r\n\r\n" +
                            "Please close all applications that may use it and press 'Retry' to try again " +
                            "or 'Cancel' to cancel the upgrade.",
                            "Error",
                            MessageBoxButtons.RetryCancel, MessageBoxIcon.Error) == DialogResult.Retry)
                    {
                        continue;
                    }
                    Fail();
                }
                break;
            }
        }
        private void CreatePath(string path)
        {
            path = Path.GetDirectoryName(path);
            if (!Directory.Exists(path))
            {
                UpdateUI("   Creating directory " + path + "...", true);
                try
                {
                    Directory.CreateDirectory(path);
                }
                catch (Exception ex)
                {
                    AppendLine(" FAILED");
                    UpdateUI("     (" + ex.Message + ")", true);
                }
            }
        }
        private void StartAwb()
        {
            bool awbOpen = false;
            foreach (Process p in Process.GetProcesses())
            {
                awbOpen = (p.ProcessName == "AutoWikiBrowser");
                if (awbOpen)
                    break;
            }
            if (!awbOpen && File.Exists(AWBdirectory + "AutoWikiBrowser.exe")
                && MessageBox.Show("Would you like to start AWB?", "Start AWB?", MessageBoxButtons.YesNo) == DialogResult.Yes)
            {
                Process.Start(AWBdirectory + "AutoWikiBrowser.exe");
            }
            progressUpdate.Value = 99;
        }
        private void KillTempDir()
        {
            if (Directory.Exists(TempDirectory))
                Directory.Delete(TempDirectory, true);
            progressUpdate.Value = 100;
        }
        private void tmrTimer_Tick(object sender, EventArgs e)
        {
            tmrTimer.Enabled = false;
            UpdateAwb();
        }
        static int StringToVersion(string version)
        {
            int res;
            if (!int.TryParse(version.Replace(".", ""), out res))
                res = 0;
            return res;
        }
        private void btnCancel_Click(object sender, EventArgs e)
        {
            if (UpdateSucessful) StartAwb();
            Close();
        }
    }
    [Serializable]
    public class AbortException : Exception
    {
        public AbortException()
        { }
        public AbortException(string message)
            : base(message)
        { }
        public AbortException(string message, Exception innerException) :
            base(message, innerException)
        { }
        protected AbortException(System.Runtime.Serialization.SerializationInfo info,
           System.Runtime.Serialization.StreamingContext context)
            : base(info, context)
        { }
    }
}
