using System;
using System.Collections;
using System.Text.RegularExpressions;
namespace ChannelServices.AdsBlocker.AddIn
{
 public enum ListUpdateState {
  None,
  Updated,
  Failed
 }
 public interface IBlacklist
 {
  void Initialize(string blacklist);
  ListUpdateState UpdateBlacklist();
  Match IsBlacklisted(Uri uri);
 }
 public interface IWhitelist {
  void Initialize(string whitelist);
  ListUpdateState UpdateWhitelist();
  Match IsWhitelisted(Uri uri);
 }
 public class BlackWhiteListFactory {
  private static readonly Hashtable blacklists = new Hashtable();
  private static readonly Hashtable whitelists = new Hashtable();
  public static void AddBlacklist(IBlacklist blackList, string content) {
   if (blacklists.ContainsKey(blackList.GetType().Name) == false) {
       blackList.Initialize(content);
       blackList.UpdateBlacklist();
       blacklists.Add(blackList.GetType().Name, blackList);
   }
   else {
    IBlacklist refr = blacklists[blackList.GetType().Name] as IBlacklist;
       refr.Initialize(content);
       ListUpdateState updateState = refr.UpdateBlacklist();
       if (updateState == ListUpdateState.Failed) {
           throw new InvalidOperationException(blackList + " could not be updated.");
       }
   }
  }
  public static void AddWhitelist(IWhitelist whitelist, string content) {
   if (blacklists.ContainsKey(whitelist.GetType().Name) == false) {
       whitelist.Initialize(content);
       whitelist.UpdateWhitelist();
       whitelists.Add(whitelist.GetType().Name, whitelist);
   }
   else {
    IWhitelist refr = whitelists[whitelist.GetType().Name] as IWhitelist;
       refr.Initialize(content);
       ListUpdateState updateState = refr.UpdateWhitelist();
       if (updateState == ListUpdateState.Failed) {
           throw new InvalidOperationException(whitelist + " could not be updated.");
       }
   }
  }
  public static void RemoveBlacklist(Type type) {
   if (blacklists.ContainsKey(type.Name))
   {
       blacklists.Remove(type.Name);
   }
  }
  public static void RemoveWhitelist(Type type) {
   if (whitelists.ContainsKey(type.Name))
   {
       whitelists.Remove(type.Name);
   }
  }
  public static bool HasBlacklists {
   get { return blacklists.Count > 0; }
  }
  public static IBlacklist[] Blacklists {
   get {
    ArrayList list = new ArrayList();
    foreach (IBlacklist blacklist in blacklists.Values) {
     list.Add(blacklist);
    }
    return list.ToArray(typeof(IBlacklist)) as IBlacklist[];
   }
  }
  public static bool HasWhitelists {
   get { return whitelists.Count > 0; }
  }
  public static IWhitelist[] Whitelists {
   get {
    ArrayList list = new ArrayList();
    foreach (IWhitelist whitelist in whitelists.Values) {
     list.Add(whitelist);
    }
    return list.ToArray(typeof(IWhitelist)) as IWhitelist[];
   }
  }
 }
}
