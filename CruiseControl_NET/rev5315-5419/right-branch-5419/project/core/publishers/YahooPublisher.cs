using System;
using System.Collections;
using System.IO;
using System.Xml;
using Exortech.NetReflector;
using System.Runtime.InteropServices;
namespace ThoughtWorks.CruiseControl.Core.Publishers
{
 public unsafe class YahooWrap
 {
  [ DllImport( "YpluginDLL.DLL", EntryPoint="SendYahooMessage")]
  public static extern int SendYahooMessage(String to, String message);
 }
 [ReflectorType("yplugin")]
 public class YahooPublisher : PublisherBase
 {
  private string _projectUrl;
  private Hashtable _users = new Hashtable();
  private Hashtable _groups = new Hashtable();
  public YahooPublisher()
  {
  }
  public override void Publish(object source, IntegrationResult result)
  {
   String Message = CreateMessage(result);
   SendMessageToNotifyGroupMembers(result, Message);
   SendMessageToModifiersWhoCheckedInTheFiles(result.Modifications, Message);
  }
  public void SendMessageToNotifyGroupMembers(IntegrationResult result, String Message)
  {
   foreach (YahooUser yuser in YahooUserIDs.Values)
   {
    YahooGroup group = GetYahooGroup(yuser.Group);
    if ( group!= null && group.Notification.Equals(YahooGroup.NotificationType.Always))
    {
     YahooWrap.SendYahooMessage(yuser.ID, Message);
    }
   }
  }
  public void SendMessageToModifiersWhoCheckedInTheFiles(Modification[] modifications, String Message)
  {
   foreach (Modification modification in modifications)
   {
    YahooUser yuser = GetYahooUser(modification.UserName);
    if (yuser!=null)
    {
     YahooWrap.SendYahooMessage(yuser.ID, Message);
    }
   }
  }
  public void SendYahooMessage(String name, String message)
  {
   YahooWrap.SendYahooMessage(name, message);
  }
  public YahooUser GetYahooUser(string username)
  {
   return (YahooUser)_users[username];
  }
  public YahooGroup GetYahooGroup(string groupname)
  {
   return (YahooGroup)_groups[groupname];
  }
  internal string CreateMessage(IntegrationResult result)
  {
   return String.Format(@"CC.NET Build Results for {0}: {1}",
    result.ProjectName, LogFile.CreateUrl(ProjectUrl, result));
  }
  [ReflectorProperty("projectUrl")]
  public string ProjectUrl
  {
   get { return _projectUrl; }
   set { _projectUrl = value; }
  }
  [ReflectorHash("yahoousers", "id")]
  public Hashtable YahooUserIDs
  {
   get { return _users; }
   set { _users = value; }
  }
  [ReflectorHash("yahoogroups", "name")]
  public Hashtable YahooGroups
  {
   get { return _groups; }
   set { _groups = value; }
  }
 }
}
