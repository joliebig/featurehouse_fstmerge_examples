using System;
using System.IO;
using log4net;
using Microsoft.Win32;
using NewsComponents.Storage;
using Logger = RssBandit.Common.Logging;
namespace NewsComponents
{
 public interface INewsComponentsConfiguration
 {
  string ApplicationID { get; }
  string UserApplicationDataPath { get; }
  string UserLocalApplicationDataPath { get; }
  string DownloadedFilesDataPath { get; }
  IPersistedSettings PersistedSettings { get; }
  CacheManager CacheManager { get; }
  SearchIndexBehavior SearchIndexBehavior { get; }
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
  protected string applicationDataPath = null;
  protected string applicationLocalDataPath = null;
  protected string applicationDownloadPath = null;
  protected SearchIndexBehavior searchBehavior = NewsComponents.SearchIndexBehavior.Default;
  protected IPersistedSettings settings = null;
  protected CacheManager p_cacheManager = null;
  public virtual string ApplicationID {
   get { return appID; }
   set { appID = value;}
  }
  public virtual string UserApplicationDataPath {
   get {
    if (applicationDataPath == null)
     applicationDataPath = Path.Combine(Environment.GetFolderPath(Environment.SpecialFolder.ApplicationData), this.ApplicationID);
    return applicationDataPath;
   }
   set {
    applicationDataPath = value;
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
   }
  }
  public virtual string DownloadedFilesDataPath {
   get { return applicationDownloadPath; }
   set { applicationDownloadPath = value; }
  }
  public virtual IPersistedSettings PersistedSettings {
   get { return settings; }
   set { settings = value;}
  }
  public virtual CacheManager CacheManager {
   get { return p_cacheManager; }
   set { p_cacheManager = value; }
  }
  public virtual SearchIndexBehavior SearchIndexBehavior {
   get { return searchBehavior; }
   set { searchBehavior = value; }
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
     _log.Error("Failed to read value of '"+name+"' from_ registry hive '" + settingsRoot + "'.", ex);
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
 }
}
