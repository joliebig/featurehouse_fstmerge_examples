using System;
using System.Text.RegularExpressions;
namespace ChannelServices.AdsBlocker.AddIn
{
 public class UserAdsWhitelist: IWhitelist
 {
  private static bool loaded = false;
  private static Regex whiteListRegex = null;
  private static string whitelist = null;
  private static readonly object whiteListLock = new object();
  public void Initialize(string newWhiteList) {
   lock(whiteListLock) {
    if (whitelist == null || whitelist != newWhiteList) {
     if (newWhiteList != null && newWhiteList.Length > 0) {
      whitelist = newWhiteList;
      whitelist = whitelist.Replace(";","|");
      whiteListRegex = new Regex(whitelist,RegexOptions.IgnoreCase|RegexOptions.Singleline);
     } else {
                        throw new ArgumentNullException("newWhiteList");
     }
    }
   }
   loaded = true;
  }
  public ListUpdateState UpdateWhitelist() {
   return ListUpdateState.None;
  }
  public Match IsWhitelisted(Uri uri) {
   if (!loaded || uri == null)
    return null;
   try {
    Match match = null;
    string strippedUri = uri.Scheme + "://" + uri.Authority + uri.AbsolutePath;
    lock (whiteListLock) {
     match = whiteListRegex.Match(strippedUri);
    }
    return match;
   }
   catch (Exception ex) {
    throw new Exception(String.Format("An error occured trying to determine if {0} is whitelisted", uri), ex.InnerException);
   }
  }
 }
}
