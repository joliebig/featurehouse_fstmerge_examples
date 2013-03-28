using System;
using System.Text.RegularExpressions;
using System.IO;
using System.Diagnostics;
using System.Windows.Forms;
using WikiFunctions.Background;
namespace WikiFunctions
{
    public static class Updater
    {
        private static readonly string AWBDirectory;
        static Updater()
        {
            AWBDirectory = Path.GetDirectoryName(Application.ExecutablePath) + "\\";
            Result = AWBEnabledStatus.None;
        }
        [Flags]
        public enum AWBEnabledStatus
        {
            None = 0,
            Error = 1,
            Disabled = 2,
            Enabled = 4,
            UpdaterUpdate = 8,
            OptionalUpdate = 12
        }
        public static AWBEnabledStatus Result { get; private set; }
        public static string GlobalVersionPage { get; private set; }
        private static readonly Regex EnabledVersions = new Regex(@"\*(.*?) enabled", RegexOptions.IgnoreCase | RegexOptions.Compiled);
        private static void UpdateFunc()
        {
            try
            {
                string text =
                    Tools.GetHTML(
                        "http://en.wikipedia.org/w/index.php?title=Wikipedia:AutoWikiBrowser/CheckPage/Version&action=raw");
                GlobalVersionPage = text;
                int awbCurrentVersion =
                    StringToVersion(Regex.Match(text, @"<!-- Current version: (.*?) -->").Groups[1].Value);
                int awbNewestVersion =
                    StringToVersion(Regex.Match(text, @"<!-- Newest version: (.*?) -->").Groups[1].Value);
                if ((awbCurrentVersion > 4000) || (awbNewestVersion > 4000))
                {
                    int updaterVersion =
                        StringToVersion(Regex.Match(text, @"<!-- Updater version: (.*?) -->").Groups[1].Value);
                    FileVersionInfo awbVersionInfo =
                        FileVersionInfo.GetVersionInfo(AWBDirectory + "AutoWikiBrowser.exe");
                    int awbFileVersion = StringToVersion(awbVersionInfo.FileVersion);
                    Result = AWBEnabledStatus.Disabled;
                    if (awbFileVersion < awbCurrentVersion)
                        return;
                    foreach (Match m in EnabledVersions.Matches(text))
                    {
                        if (StringToVersion(m.Groups[1].Value) == awbFileVersion)
                        {
                            Result = AWBEnabledStatus.Enabled;
                            break;
                        }
                    }
                    if (Result == AWBEnabledStatus.Disabled)
                        return;
                    if ((updaterVersion > 1400) &&
                        (updaterVersion >
                         StringToVersion(FileVersionInfo.GetVersionInfo(AWBDirectory + "AWBUpdater.exe").FileVersion)))
                    {
                        Result |= AWBEnabledStatus.UpdaterUpdate;
                    }
                    if ((awbFileVersion >= awbCurrentVersion) && (awbFileVersion < awbNewestVersion))
                    {
                        Result |= AWBEnabledStatus.OptionalUpdate;
                    }
                }
            }
            catch
            {
                Result = AWBEnabledStatus.Error;
            }
        }
        private static int StringToVersion(string version)
        {
            int res;
            if (!int.TryParse(version.Replace(".", ""), out res))
                res = 0;
            return res;
        }
        private static BackgroundRequest _request;
        public static void UpdateUpdaterFile(Tools.SetProgress setProgress)
        {
            setProgress(67);
            if (File.Exists(AWBDirectory + "AWBUpdater.exe.new"))
            {
                File.Copy(AWBDirectory + "AWBUpdater.exe.new", AWBDirectory + "AWBUpdater.exe", true);
                File.Delete(AWBDirectory + "AWBUpdater.exe.new");
            }
            setProgress(70);
        }
        public static void CheckForUpdates()
        {
            if (_request != null) return;
            _request = new BackgroundRequest();
            _request.Execute(UpdateFunc);
        }
        public static void WaitForCompletion()
        {
            if (_request == null) return;
            _request.Wait();
            _request = null;
        }
        public static void RunUpdater()
        {
            Process.Start(AWBDirectory + "AWBUpdater.exe");
        }
    }
}
