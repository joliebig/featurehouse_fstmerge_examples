using System; 
using System.Configuration; 
using System.Globalization; 
using System.IO; 
using System.Reflection; 
using System.Runtime.InteropServices; 
using System.Security; 
using System.Text; 
using System.Threading; 
using System.Windows.Forms; 
using System.Xml.Schema; 
using Microsoft.ApplicationBlocks.ExceptionManagement; 
using NewsComponents; 
using NewsComponents.Feed; 
using NewsComponents.Net; 
using RssBandit.Resources; 
using RssBandit.WinGui.Forms; namespace  RssBandit {
	
    internal partial class  RssBanditApplication {
		
        public static  CultureInfo SharedUICulture
        {
            get
            {
                return sharedUICulture;
            }
            set
            {
                lock (typeof (RssBanditApplication))
                {
                    sharedUICulture = value;
                }
            }
        }
 
        public static  CultureInfo SharedCulture
        {
            get
            {
                return sharedCulture;
            }
            set
            {
                lock (typeof (RssBanditApplication))
                {
                    sharedCulture = value;
                }
            }
        }
 
        public static  Version Version
        {
            get
            {
                if (appVersion == null)
                {
                    try
                    {
                        appVersion = Assembly.GetEntryAssembly().GetName().Version;
                    }
                    catch
                    {
                        appVersion = new Version(Application.ProductVersion);
                    }
                }
                return appVersion;
            }
        }
 
        public static  string VersionLong
        {
            get
            {
                Version verInfo = Version;
                string versionStr = String.Format("{0}.{1}.{2}.{3}",
                                                  verInfo.Major, verInfo.Minor,
                                                  verInfo.Build, verInfo.Revision);
                if (!string.IsNullOrEmpty(versionPostfix))
                    return String.Format("{0} {1}", versionStr, versionPostfix);
                return versionStr;
            }
        }
 
        public static  string VersionShort
        {
            get
            {
                Version verInfo = Version;
                string versionStr = String.Format("{0}.{1}",
                                                  verInfo.Major, verInfo.Minor);
                if (!string.IsNullOrEmpty(versionPostfix))
                    return String.Format("{0} {1}", versionStr, versionPostfix);
                return versionStr;
            }
        }
 
        public static  string ApplicationInfos
        {
            get
            {
                StringBuilder sb = new StringBuilder();
                sb.AppendFormat("{0};UI:{1};", Name, Thread.CurrentThread.CurrentUICulture.Name);
                try
                {
                    sb.AppendFormat("OS:{0},", Environment.OSVersion);
                }
                catch
                {
                    sb.Append("OS:n/a,");
                }
                sb.AppendFormat("{0};", CultureInfo.InstalledUICulture.Name);
                try
                {
                    sb.AppendFormat(".NET CLR:{0};", RuntimeEnvironment.GetSystemVersion());
                }
                catch
                {
                    sb.Append(".NET CLR:n/a;");
                }
                return sb.ToString();
            }
        }
 
        public static  string UpdateServiceUrl
        {
            get
            {
                int idx = DateTime.Now.Second%applicationUpdateServiceUrls.Length;
                return applicationUpdateServiceUrls[idx];
            }
        }
 
        public static  string FeedValidationUrlBase
        {
            get
            {
                return validationUrlBase;
            }
        }
 
        public static  string AppGuid
        {
            get
            {
                return applicationGuid;
            }
        }
 
        public static  string Name
        {
            get
            {
                return applicationId;
            }
        }
 
        public static  string Caption
        {
            get
            {
                return String.Format("{0} {1}", applicationName, VersionLong);
            }
        }
 
        public static  string CaptionOnly
        {
            get
            {
                return applicationName;
            }
        }
 
        public static  string DefaultCategory
        {
            get
            {
                return defaultCategory;
            }
        }
 
        public static  string UserAgent
        {
            get
            {
                return String.Format("{0}/{1}", applicationId, Version);
            }
        }
 
        public static  RssBanditPreferences DefaultPreferences
        {
            get
            {
                return defaultPrefs;
            }
        }
 
        public static  bool UnconditionalCommentRss
        {
            get
            {
                return unconditionalCommentRss;
            }
        }
 
        public static  bool AutomaticColorSchemes
        {
            get
            {
                return automaticColorSchemes;
            }
        }
 
        public static  bool PortableApplicationMode
        {
            get
            {
                return portableApplicationMode;
            }
        }
 
        private static  string ApplicationDataFolderFromEnv
        {
            get
            {
                if (string.IsNullOrEmpty(appDataFolderPath))
                {
                    appDataFolderPath = ConfigurationManager.AppSettings["AppDataFolder"];
                    if (!string.IsNullOrEmpty(appDataFolderPath))
                    {
                        appDataFolderPath = Environment.ExpandEnvironmentVariables(appDataFolderPath);
                    }
                    else
                    {
                        try
                        {
                            appDataFolderPath =
                                Path.Combine(Environment.GetFolderPath(Environment.SpecialFolder.ApplicationData), Name);
                            if (!Directory.Exists(appDataFolderPath))
                                Directory.CreateDirectory(appDataFolderPath);
                        }
                        catch (SecurityException secEx)
                        {
                            MessageBox.Show(
                                "Cannot query for Environment.SpecialFolder.ApplicationData:\n" + secEx.Message,
                                "Security violation");
                            Application.Exit();
                        }
                    }
                    if (!Path.IsPathRooted(appDataFolderPath))
                        appDataFolderPath =
                            Path.Combine(Path.GetDirectoryName(Application.ExecutablePath), appDataFolderPath);
                    if (-1 == Path.GetPathRoot(appDataFolderPath).IndexOf(":"))
                        appDataFolderPath =
                            Path.Combine(Path.GetPathRoot(Application.ExecutablePath), appDataFolderPath.Substring(1));
                    if (!Directory.Exists(appDataFolderPath))
                        Directory.CreateDirectory(appDataFolderPath);
                }
                return appDataFolderPath;
            }
        }
 
        private static  string ApplicationLocalDataFolderFromEnv
        {
            get
            {
                string s = ConfigurationManager.AppSettings["AppCacheFolder"];
                if (!string.IsNullOrEmpty(s))
                {
                    s = Environment.ExpandEnvironmentVariables(s);
                }
                else
                {
                    try
                    {
                        s =
                            Path.Combine(Environment.GetFolderPath(Environment.SpecialFolder.LocalApplicationData), Name);
                    }
                    catch (SecurityException secEx)
                    {
                        MessageBox.Show(
                            "Cannot query for Environment.SpecialFolder.LocalApplicationData:\n" + secEx.Message,
                            "Security violation");
                        Application.Exit();
                    }
                }
                if (!Path.IsPathRooted(s))
                    s = Path.Combine(Path.GetDirectoryName(Application.ExecutablePath), s);
                if (-1 == Path.GetPathRoot(s).IndexOf(":"))
                    appDataFolderPath = Path.Combine(Path.GetPathRoot(Application.ExecutablePath), s.Substring(1));
                if (!Directory.Exists(s))
                    Directory.CreateDirectory(s);
                return s;
            }
        }
 
        public static  bool ShouldAskForDefaultAggregator
        {
            get
            {
                return (bool) guiSettings.GetProperty("AskForMakeDefaultAggregator", true);
            }
            set
            {
                guiSettings.SetProperty("AskForMakeDefaultAggregator", value);
            }
        }
 
        public static  string GetUserPath()
        {
            return ApplicationDataFolderFromEnv;
        }
 
        public static  string GetSearchesPath()
        {
            string s = Path.Combine(ApplicationDataFolderFromEnv, "searches");
            if (!Directory.Exists(s)) Directory.CreateDirectory(s);
            return s;
        }
 
        public static  string GetTemplatesPath()
        {
            string s = Path.Combine(Application.StartupPath, "templates");
            if (!Directory.Exists(s)) return null;
            return s;
        }
 
        public static  string GetEnclosuresPath()
        {
            string mydocs = Environment.GetFolderPath(Environment.SpecialFolder.Personal);
            string s = Path.Combine(mydocs, "RSS Bandit\\My Downloaded Files");
            return s;
        }
 
        public static  string GetPlugInPath()
        {
            string s = Path.Combine(Application.StartupPath, "plugins");
            if (!Directory.Exists(s)) return null;
            return s;
        }
 
        public static  string GetSearchIndexPath()
        {
            return Path.Combine(ApplicationLocalDataFolderFromEnv, "index");
        }
 
        public static  string GetFeedFileCachePath()
        {
            if (appCacheFolderPath == null)
            {
                string s = Path.Combine(ApplicationLocalDataFolderFromEnv, "Cache");
                if (!Directory.Exists(s))
                {
                    string old_cache = Path.Combine(GetUserPath(), "Cache");
                    if (Directory.Exists(old_cache))
                    {
                        if (s.StartsWith(old_cache))
                        {
                            _log.Error("GetFeedFileCachePath(): " + SR.CacheFolderInvalid_CannotBeMoved(s));
                            Splash.Close();
                            MessageBox.Show(
                                SR.CacheFolderInvalid_CannotBeMoved(s),
                                Caption,
                                MessageBoxButtons.OK,
                                MessageBoxIcon.Error);
                            s = old_cache;
                        }
                        else
                        {
                            try
                            {
                                string s_root_old = Directory.GetDirectoryRoot(old_cache);
                                string s_root = Directory.GetDirectoryRoot(s);
                                if (s_root_old == s_root)
                                {
                                    Directory.Move(old_cache, s);
                                }
                                else
                                {
                                    if (!Directory.Exists(s))
                                        Directory.CreateDirectory(s);
                                    foreach (string f in Directory.GetFiles(old_cache))
                                    {
                                        File.Copy(f, Path.Combine(s, Path.GetFileName(f)), true);
                                    }
                                    Directory.Delete(old_cache, true);
                                }
                            }
                            catch (Exception ex)
                            {
                                _log.Error("GetFeedFileCachePath()error while moving cache folder.", ex);
                                Splash.Close();
                                MessageBox.Show(
                                    SR.CacheFolderInvalid_CannotBeMovedException(s, ex.Message),
                                    Caption,
                                    MessageBoxButtons.OK,
                                    MessageBoxIcon.Error);
                                s = old_cache;
                            }
                        }
                    }
                    else
                    {
                        Directory.CreateDirectory(s);
                    }
                }
                appCacheFolderPath = s;
            }
            return appCacheFolderPath;
        }
 
        public static  string GetErrorLogPath()
        {
            string s = Path.Combine(ApplicationDataFolderFromEnv, "errorlog");
            if (!Directory.Exists(s)) Directory.CreateDirectory(s);
            return s;
        }
 
        public static  string GetFeedErrorFileName()
        {
            return Path.Combine(GetErrorLogPath(), "feederrors.xml");
        }
 
        public static  string GetFlagItemsFileName()
        {
            return Path.Combine(GetUserPath(), "flagitems.xml");
        }
 
        public static  string GetWatchedItemsFileName()
        {
            return Path.Combine(GetUserPath(), "watcheditems.xml");
        }
 
        public static  string GetSentItemsFileName()
        {
            return Path.Combine(GetUserPath(), "replyitems.xml");
        }
 
        public static  string GetDeletedItemsFileName()
        {
            return Path.Combine(GetUserPath(), "deleteditems.xml");
        }
 
        public static  string GetSearchFolderFileName()
        {
            return Path.Combine(GetUserPath(), "searchfolders.xml");
        }
 
        public static  string GetShortcutSettingsFileName()
        {
            return Path.Combine(GetUserPath(), "shortcutsettings.xml");
        }
 
        public static  string GetSettingsFileName()
        {
            string clr = String.Empty;
            if (NewsComponents.Utils.Common.ClrVersion.Major > 1)
                clr = NewsComponents.Utils.Common.ClrVersion.Major.ToString();
            return Path.Combine(GetUserPath(), ".settings" + clr + ".xml");
        }
 
        public static  string GetFeedListFileName()
        {
            return Path.Combine(GetUserPath(), "subscriptions.xml");
        }
 
        public static  string GetTopStoriesFileName()
        {
            return Path.Combine(GetUserPath(), "top-stories.html");
        }
 
        public static  string GetCommentsFeedListFileName()
        {
            return Path.Combine(GetUserPath(), "comment-subscriptions.xml");
        }
 
        public static  string GetOldFeedListFileName()
        {
            return Path.Combine(GetUserPath(), "feedlist.xml");
        }
 
        public static  string GetTrustedCertIssuesFileName()
        {
            return Path.Combine(GetUserPath(), "certificates.config.xml");
        }
 
        public static  string GetLogFileName()
        {
            return Path.Combine(GetUserPath(), "error.log");
        }
 
        public static  string GetBrowserTabStateFileName()
        {
            return Path.Combine(GetUserPath(), ".openbrowsertabs.xml");
        }
 
        public static  string GetSubscriptionTreeStateFileName()
        {
            return Path.Combine(GetUserPath(), ".treestate.xml");
        }
 
        public static  string GetPreferencesFileNameOldBinary()
        {
            return Path.Combine(GetUserPath(), ".preferences");
        }
 
        public static  string GetPreferencesFileName()
        {
            return Path.Combine(GetUserPath(), ".preferences.xml");
        }
 
        public static  void CommentFeedListValidationCallback(object sender,
                                                             ValidationEventArgs args)
        {
            if (args.Severity == XmlSeverityType.Warning)
            {
                _log.Info(GetCommentsFeedListFileName() + " validation warning: " + args.Message);
            }
            else if (args.Severity == XmlSeverityType.Error)
            {
                _log.Error(GetCommentsFeedListFileName() + " validation error: " + args.Message);
            }
        }
 
        public static  void FeedListValidationCallback(object sender,
                                                      ValidationEventArgs args)
        {
            if (args.Severity == XmlSeverityType.Warning)
            {
                _log.Info(GetFeedListFileName() + " validation warning: " + args.Message);
            }
            else if (args.Severity == XmlSeverityType.Error)
            {
                validationErrorOccured = true;
                _log.Error(GetFeedListFileName() + " validation error: " + args.Message);
                ExceptionManager.Publish(args.Exception);
            }
        }
 
        public static  void MakeDefaultAggregator()
        {
            string appPath = Application.ExecutablePath;
            try
            {
                Win32.Registry.CurrentFeedProtocolHandler = appPath;
                ShouldAskForDefaultAggregator = true;
            }
            catch (Exception ex)
            {
                _log.Debug("Unable to set CurrentFeedProtocolHandler", ex);
                throw;
            }
            CheckAndRegisterIEMenuExtensions();
        }
 
        public static  void CheckAndRegisterIEMenuExtensions()
        {
            try
            {
                if (Win32.Registry.IsInternetExplorerExtensionRegistered(Win32.IEMenuExtension.DefaultFeedAggregator))
                    Win32.Registry.UnRegisterInternetExplorerExtension(Win32.IEMenuExtension.DefaultFeedAggregator);
                if (!Win32.Registry.IsInternetExplorerExtensionRegistered(Win32.IEMenuExtension.Bandit))
                    Win32.Registry.RegisterInternetExplorerExtension(Win32.IEMenuExtension.Bandit);
            }
            catch (Exception ex)
            {
                _log.Debug("CheckAndRegisterIEMenuExtensions(): Unable to modify InternetExplorerExtension", ex);
            }
        }
 
        public static  bool IsDefaultAggregator()
        {
            return IsDefaultAggregator(Application.ExecutablePath);
        }
 
        public static  bool IsDefaultAggregator(string appPath)
        {
            bool isDefault = false;
            try
            {
                string currentHandler = Win32.Registry.CurrentFeedProtocolHandler;
                if (string.IsNullOrEmpty(currentHandler))
                {
                    MakeDefaultAggregator();
                    isDefault = true;
                }
                isDefault = (String.Concat(appPath, " ", "\"", "%1", "\"").CompareTo(currentHandler) == 0);
            }
            catch (SecurityException secex)
            {
                _log.Warn("Security exception error on make default aggregator.", secex);
            }
            catch (Exception e)
            {
                _log.Error("Unexpected Error while check for default aggregator", e);
            }
            return isDefault;
        }
 
        public static  DialogResult PublishException(Exception ex)
        {
            return PublishException(ex, false);
        }
 
        public static  DialogResult PublishException(Exception ex, bool resumable)
        {
            return ApplicationExceptionHandler.ShowExceptionDialog(ex, resumable);
        }
 
        public static  FeedRequestException CreateLocalFeedRequestException(Exception e, NewsFeed f, IFeedDetails fi)
        {
            return new FeedRequestException(e.Message, e, NewsHandler.GetFailureContext(f, fi));
        }

	}

}
