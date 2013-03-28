using System;
using System.ComponentModel;
using System.IO;
using log4net;
using Microsoft.Win32;
using NewsComponents.Storage;
using NewsComponents.Utils;
using Logger = RssBandit.Common.Logging;
namespace NewsComponents
{
 public interface INewsComponentsConfiguration : INotifyPropertyChanged
 {
  string ApplicationID { get; }
  Version ApplicationVersion { get; }
  string UserApplicationDataPath { get; }
  string UserLocalApplicationDataPath { get; }
  string DownloadedFilesDataPath { get; }
  IPersistedSettings PersistedSettings { get; }
  CacheManager CacheManager { get; }
  SearchIndexBehavior SearchIndexBehavior { get; }
  int RefreshRate { get; }
  bool DownloadEnclosures { get; }
 }
 public interface IPersistedSettings
 {
  object GetProperty(string name, Type returnType, object defaultValue);
  void SetProperty(string name, object value);
 }
 public enum SearchIndexBehavior
 {
  NoIndexing = 0,
  LocalAppDataDirectoryBased = 1,
  AppDataDirectoryBased = 2,
  TempDirectoryBased = 3,
  Default = LocalAppDataDirectoryBased
 }
 public class NewsComponentsConfiguration: INewsComponentsConfiguration
 {
  public static INewsComponentsConfiguration Default = CreateDefaultConfiguration();
  const string defaultApplicationID = "NewsComponents";
  protected string appID = null;
  protected Version appVersion = new Version(1, 0);
  protected string applicationDataPath = null;
  protected string applicationLocalDataPath = null;
  protected string applicationDownloadPath = null;
  protected SearchIndexBehavior searchBehavior = NewsComponents.SearchIndexBehavior.Default;
  protected IPersistedSettings settings = null;
  protected CacheManager p_cacheManager = null;
  protected int p_refreshRate = -1;
  private bool downloadEnclosures;
  public virtual string ApplicationID {
   get { return appID; }
   set {
    appID = value;
    this.OnPropertyChanged("ApplicationID");
   }
  }
  public virtual Version ApplicationVersion
  {
   get { return appVersion; }
   set {
    appVersion = value;
    this.OnPropertyChanged("ApplicationVersion");
   }
  }
  public virtual string UserApplicationDataPath {
   get {
    if (applicationDataPath == null)
     applicationDataPath = Path.Combine(Environment.GetFolderPath(Environment.SpecialFolder.ApplicationData), this.ApplicationID);
    return applicationDataPath;
   }
   set {
    applicationDataPath = value;
    this.OnPropertyChanged("UserApplicationDataPath");
   }
  }
  public virtual string UserLocalApplicationDataPath {
   get {
    if (applicationLocalDataPath == null)
     applicationLocalDataPath= Path.Combine(Environment.GetFolderPath(Environment.SpecialFolder.LocalApplicationData), this.ApplicationID);
    return applicationLocalDataPath;
   }
   set {
    applicationLocalDataPath = value;
    this.OnPropertyChanged("UserLocalApplicationDataPath");
   }
  }
  public virtual string DownloadedFilesDataPath {
   get { return applicationDownloadPath; }
   set {
    applicationDownloadPath = value;
    this.OnPropertyChanged("DownloadedFilesDataPath");
   }
  }
  public virtual IPersistedSettings PersistedSettings {
   get { return settings; }
   set {
    settings = value;
    this.OnPropertyChanged("PersistedSettings");
   }
  }
  public virtual CacheManager CacheManager {
   get { return p_cacheManager; }
   set {
    p_cacheManager = value;
    this.OnPropertyChanged("CacheManager");
   }
  }
  public virtual SearchIndexBehavior SearchIndexBehavior {
   get { return searchBehavior; }
   set {
    searchBehavior = value;
    this.OnPropertyChanged("SearchIndexBehavior");
   }
  }
  public virtual int RefreshRate {
   get {
    if (p_refreshRate >= 0)
     return p_refreshRate;
    return FeedSource.DefaultRefreshRate;
   }
   set {
    p_refreshRate = value;
    this.OnPropertyChanged("RefreshRate");
   }
  }
  public virtual bool DownloadEnclosures {
   get { return downloadEnclosures; }
   set {
    downloadEnclosures = value;
    this.OnPropertyChanged("DownloadEnclosures");
   }
  }
  private static INewsComponentsConfiguration CreateDefaultConfiguration() {
   NewsComponentsConfiguration cfg = new NewsComponentsConfiguration();
   cfg.ApplicationID = defaultApplicationID;
   cfg.SearchIndexBehavior = SearchIndexBehavior.Default;
   cfg.UserApplicationDataPath = Path.Combine(Environment.GetFolderPath(Environment.SpecialFolder.ApplicationData), cfg.ApplicationID);
   cfg.UserLocalApplicationDataPath = Path.Combine(Environment.GetFolderPath(Environment.SpecialFolder.LocalApplicationData), cfg.ApplicationID);
   string mydocs = Path.Combine(Environment.GetFolderPath(Environment.SpecialFolder.Personal), cfg.ApplicationID);
   cfg.DownloadedFilesDataPath = Path.Combine(mydocs, "My Downloaded Files");
   string path = Path.Combine(cfg.UserApplicationDataPath, "Cache");
   if (!Directory.Exists(path))
    Directory.CreateDirectory(path);
   cfg.CacheManager = new FileCacheManager(path);
   cfg.PersistedSettings = new SettingStore(cfg.ApplicationID);
   return cfg;
  }
  class SettingStore: IPersistedSettings
  {
   private readonly string settingsRoot;
   private static readonly ILog _log = Logger.Log.GetLogger(typeof(SettingStore));
   public SettingStore(string appID) {
    this.settingsRoot = String.Format(@"Software\{0}\Settings", appID);
   }
   public object GetProperty(string name, Type returnType, object defaultValue) {
    RegistryKey key = null;
    try {
     key = Registry.CurrentUser.OpenSubKey(settingsRoot, false);
     if (key == null)
      return defaultValue;
     object val = key.GetValue(name, defaultValue);
     if (val != null) {
      try {
       return Convert.ChangeType(val, returnType);
      } catch {}
     }
     return defaultValue;
    } catch (Exception ex) {
     _log.Error("Failed to read value of '"+name+"' from registry hive '" + settingsRoot + "'.", ex);
     return defaultValue;
    } finally {
     if (key != null) key.Close();
    }
   }
   public void SetProperty(string name, object value) {
    try {
     RegistryKey keySettings = Registry.CurrentUser.OpenSubKey(settingsRoot, true);
     if (keySettings == null) {
      keySettings = Registry.CurrentUser.CreateSubKey(settingsRoot);
     }
     keySettings.SetValue(name, value);
     keySettings.Close();
    } catch (Exception) {}
   }
  }
  public event PropertyChangedEventHandler PropertyChanged;
  protected virtual void OnPropertyChanged(string propertyName)
  {
   OnPropertyChanged(DataBindingHelper.GetPropertyChangedEventArgs(propertyName));
  }
  protected virtual void OnPropertyChanged(PropertyChangedEventArgs e)
  {
   if (null != PropertyChanged)
   {
    PropertyChanged(this, e);
   }
  }
 }
}
