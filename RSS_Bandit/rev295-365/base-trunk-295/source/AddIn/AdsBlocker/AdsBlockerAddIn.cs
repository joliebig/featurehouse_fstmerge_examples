using System;
using System.IO;
using System.Reflection;
using System.Text;
using RssBandit.AppServices;
using RssBandit.UIServices;
namespace ChannelServices.AdsBlocker.AddIn
{
 public class AdsBlockerAddIn: AddInBase, IAddInPackage
 {
  private readonly NewsItemAdsBlocker _adsBlocker;
  private ICoreApplication _app;
  public AdsBlockerAddIn()
  {
   _adsBlocker = new NewsItemAdsBlocker();
  }
  public void Load(IServiceProvider serviceProvider) {
   string path = GetConfigLocation();
   string blackListFile = Path.Combine(path, "ads.blacklist.txt");
   string whiteListFile = Path.Combine(path, "ads.whitelist.txt");
   if (File.Exists(blackListFile)) {
    using (Stream s = File.OpenRead(blackListFile)) {
     using (StreamReader r = new StreamReader(s, Encoding.UTF8)) {
      BlackWhiteListFactory.AddBlacklist(new UserAdsBlacklist(), ParseContent(r));
     }
    }
   }
   if (File.Exists(whiteListFile)) {
    using (Stream s = File.OpenRead(whiteListFile)) {
     using (StreamReader r = new StreamReader(s, Encoding.UTF8)) {
      BlackWhiteListFactory.AddWhitelist(new UserAdsWhitelist(), ParseContent(r));
     }
    }
   }
   if (BlackWhiteListFactory.HasBlacklists) {
    _app = (ICoreApplication) serviceProvider.GetService(typeof(ICoreApplication));
    _app.RegisterDisplayingNewsChannelProcessor(_adsBlocker);
   }
  }
  public void Unload() {
   if (BlackWhiteListFactory.HasBlacklists && _app != null) {
    _app.UnregisterDisplayingNewsChannelProcessor(_adsBlocker);
   }
  }
  public new void Dispose() {
   _app = null;
  }
  private static string GetConfigLocation() {
   return Path.GetDirectoryName(Assembly.GetExecutingAssembly().Location);
  }
  private static string ParseContent(StreamReader reader) {
   StringBuilder sb = new StringBuilder();
   string line = null;
   while ((line = reader.ReadLine()) != null) {
    if (!line.StartsWith("#")) {
     if (line.IndexOf('#') > -1) {
      line = line.Substring(0, line.IndexOf('#'));
     }
     sb.Append(line.TrimEnd(' ', '\t'));
     sb.Append(";");
    }
   }
   return sb.ToString().TrimEnd(';');
  }
 }
}
