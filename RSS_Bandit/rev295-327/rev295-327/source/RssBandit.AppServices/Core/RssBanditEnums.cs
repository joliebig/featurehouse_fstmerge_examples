using System; namespace  RssBandit {
	
  [Flags] 
 public enum  INetState  {
  Invalid = 0,
  DisConnected = 1,
  Connected = 2,
  Offline = 4,
  Online = 8
 } 
 public enum  OptionDialogSection 
 {
  Default = 0,
  General = Default,
  NewsItems = 1,
  RemoteStorage = 2,
  Display = 3,
  InternetConnection = 4,
  Fonts = 5,
  WebBrowser = 6,
  WebSearch = 7,
  Attachments = 8,
 } 
 public enum  HideToTray :int  {
  None = 0,
  OnMinimize,
  OnClose
 } 
 public enum  AutoUpdateMode :int  {
  Manually = 0,
  OnApplicationStart,
  OnceIn14Days,
  OnceAMonth
 } 
 public enum  RemoteStorageProtocolType :int  {
  Unknown = -1,
  UNC = 0,
  FTP,
  dasBlog,
  dasBlog_1_3,
  WebDAV,
  NewsgatorOnline,
 } 
 public enum  BrowserBehaviorOnNewWindow :int  {
  OpenNewTab = 0,
  OpenDefaultBrowser,
  OpenWithCustomExecutable
 } 
 public enum  DisplayFeedAlertWindow :int  {
  None = 0,
  AsConfiguredPerFeed,
  AsConfiguredPerCategory,
  All
 }
}
