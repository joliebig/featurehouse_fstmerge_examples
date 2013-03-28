using System;
using System.Collections.Generic;
using System.IO;
using System.Windows.Forms;
using System.Diagnostics;
using System.Net;
using NewsComponents;
using NewsComponents.Feed;
using NewsComponents.News;
using NewsComponents.Utils;
using RssBandit.Resources;
using RssBandit.WinGui;
using RssBandit.WinGui.Forms;
namespace RssBandit
{
 public class IdentityNewsServerManager: IServiceProvider
 {
  public event EventHandler NewsServerDefinitionsModified;
  public event EventHandler IdentityDefinitionsModified;
  private static readonly log4net.ILog _log = Common.Logging.Log.GetLogger(typeof(IdentityNewsServerManager));
  private static UserIdentity anonymous;
  private readonly RssBanditApplication app;
  private readonly string cachePath;
     internal IdentityNewsServerManager(RssBanditApplication app) {
   this.app = app;
   this.cachePath = RssBanditApplication.GetFeedFileCachePath();
  }
  public static UserIdentity AnonymousIdentity {
   get {
    if (anonymous != null)
     return anonymous;
    anonymous = new UserIdentity();
    anonymous.Name = anonymous.RealName = "anonymous";
    anonymous.MailAddress = anonymous.ResponseAddress = String.Empty;
    anonymous.Organization = anonymous.ReferrerUrl = String.Empty;
    anonymous.Signature = String.Empty;
    return anonymous;
   }
  }
  public IDictionary<string, UserIdentity> CurrentIdentities {
   get { return this.app.FeedHandler.UserIdentity; }
  }
        public IDictionary<string, INntpServerDefinition> CurrentNntpServers
        {
   get { return this.app.FeedHandler.NntpServers; }
  }
        public IList<NewsFeed> CurrentSubscriptions(INntpServerDefinition sd)
        {
   return new List<NewsFeed>();
  }
  public IList<string> LoadNntpNewsGroups(IWin32Window owner, INntpServerDefinition sd, bool forceLoadFromServer) {
   IList<string> list;
   if (forceLoadFromServer) {
    list = FetchNewsGroupsFromServer(owner, sd);
    if (list != null && list.Count > 0)
     SaveNewsGroupsToCache(sd, list);
    else
     RemoveCachedGroups(sd);
   } else {
    list = LoadNewsGroupsFromCache(sd);
   }
   return list;
  }
  public static Uri BuildNntpRequestUri(INntpServerDefinition sd) {
   return BuildNntpRequestUri(sd, null);
  }
  public static Uri BuildNntpRequestUri(INntpServerDefinition sd, string nntpGroup) {
   string schema = NntpWebRequest.NntpUriScheme;
   if (sd.UseSSL)
    schema = NntpWebRequest.NntpsUriScheme;
   int port = NntpWebRequest.NntpDefaultServerPort;
   if (sd.Port > 0 && sd.Port != NntpWebRequest.NntpDefaultServerPort)
    port = sd.Port;
   UriBuilder uriBuilder;
   if (string.IsNullOrEmpty(nntpGroup))
    uriBuilder = new UriBuilder(schema, sd.Server, port);
   else
    uriBuilder = new UriBuilder(schema, sd.Server, port, nntpGroup);
   return uriBuilder.Uri;
  }
  private string BuildCacheFileName(INntpServerDefinition sd) {
   return Path.Combine(cachePath, String.Format("{0}_{1}.xml", sd.Server , sd.Port != 0 ? sd.Port : NntpWebRequest.NntpDefaultServerPort) );
  }
  private void RemoveCachedGroups(INntpServerDefinition sd) {
   RemoveCachedGroups(BuildCacheFileName(sd));
  }
  private static void RemoveCachedGroups(string cachedFileName) {
   if (File.Exists(cachedFileName))
    FileHelper.Delete(cachedFileName);
  }
  private void SaveNewsGroupsToCache(INntpServerDefinition sd, ICollection<string> list) {
   string fn = BuildCacheFileName(sd);
   RemoveCachedGroups(fn);
   if (list != null && list.Count > 0) {
    try {
     using (StreamWriter w = new StreamWriter(FileHelper.OpenForWrite(fn))) {
      foreach (string line in list)
       w.WriteLine(line);
     }
    } catch (Exception ex) {
     _log.Error("SaveNewsGroupsToCache() failed to save '" + fn + "'", ex);
    }
   }
  }
  private IList<string> LoadNewsGroupsFromCache(INntpServerDefinition sd) {
            List<string> result = new List<string>();
   string fn = BuildCacheFileName(sd);
   if (File.Exists(fn)) {
    try {
     using (StreamReader r = new StreamReader(FileHelper.OpenForRead(fn))) {
      string line = r.ReadLine();
      while (line != null) {
       result.Add(line);
       line = r.ReadLine();
      }
     }
    } catch (Exception ex) {
     _log.Error("LoadNewsGroupsFromCache() failed to load '" + fn + "'", ex);
    }
   }
   return result;
  }
  static IList<string> FetchNewsGroupsFromServer(IWin32Window owner, INntpServerDefinition sd) {
   if (sd == null)
    throw new ArgumentNullException("sd");
   FetchNewsgroupsThreadHandler threadHandler = new FetchNewsgroupsThreadHandler(sd);
   DialogResult result = threadHandler.Start(owner, SR.NntpLoadingGroupsWaitMessage, true);
   if (DialogResult.OK != result)
                return new List<string>(0);
   if (!threadHandler.OperationSucceeds) {
    MessageBox.Show(
     SR.ExceptionNntpLoadingGroupsFailed(sd.Server, threadHandler.OperationException.Message),
     SR.GUINntpLoadingGroupsFailedCaption, MessageBoxButtons.OK,MessageBoxIcon.Error);
                return new List<string>(0);
   }
   return threadHandler.Newsgroups;
  }
  public void ShowIdentityDialog(IWin32Window owner) {
   this.ShowDialog(owner, NewsgroupSettingsView.Identity);
  }
  public void ShowNewsServerSubscriptionsDialog(IWin32Window owner) {
   this.ShowDialog(owner, NewsgroupSettingsView.NewsServerSubscriptions);
  }
  public void ShowDialog(IWin32Window owner, NewsgroupSettingsView view) {
   NewsgroupsConfiguration cfg = new NewsgroupsConfiguration(this, view);
   cfg.DefinitionsModified += OnCfgDefinitionsModified;
   try {
    if (DialogResult.OK == cfg.ShowDialog(owner)) {
     RaiseNewsServerDefinitionsModified();
     RaiseIdentityDefinitionsModified();
     app.SubscriptionModified(NewsFeedProperty.General);
    }
   } catch (Exception ex) {
    Trace.WriteLine("Exception in NewsGroupsConfiguration dialog: "+ex.Message);
   }
  }
  void RaiseNewsServerDefinitionsModified() {
   if (NewsServerDefinitionsModified != null)
    NewsServerDefinitionsModified(this, EventArgs.Empty);
  }
  void RaiseIdentityDefinitionsModified() {
   if (IdentityDefinitionsModified != null)
    IdentityDefinitionsModified(this, EventArgs.Empty);
  }
  private void OnCfgDefinitionsModified(object sender, EventArgs e) {
   NewsgroupsConfiguration cfg = (NewsgroupsConfiguration)sender;
   lock (app.FeedHandler.UserIdentity) {
    app.FeedHandler.UserIdentity.Clear();
    if (cfg.ConfiguredIdentities != null) {
     foreach (UserIdentity ui in cfg.ConfiguredIdentities.Values) {
      app.FeedHandler.UserIdentity.Add(ui.Name, (UserIdentity)ui.Clone());
     }
    }
   }
   lock (app.FeedHandler.NntpServers) {
    app.FeedHandler.NntpServers.Clear();
    if (cfg.ConfiguredNntpServers != null) {
     foreach (NntpServerDefinition sd in cfg.ConfiguredNntpServers.Values) {
      app.FeedHandler.NntpServers.Add(sd.Name, (NntpServerDefinition)sd.Clone());
     }
    }
   }
   app.SubscriptionModified(NewsFeedProperty.General);
   RaiseNewsServerDefinitionsModified();
   RaiseIdentityDefinitionsModified();
  }
  public object GetService(Type serviceType)
  {
   IServiceProvider p = this.app;
   if (p != null)
    return p.GetService(serviceType);
   return null;
  }
 }
 internal class FetchNewsgroupsThreadHandler: EntertainmentThreadHandlerBase {
  private readonly INntpServerDefinition serverDef;
  public List<string> Newsgroups;
  public FetchNewsgroupsThreadHandler(INntpServerDefinition sd) {
   this.serverDef = sd;
            this.Newsgroups = new List<string>(0);
  }
  protected override void Run() {
            List<string> result = new List<string>(500);
   try {
    NntpWebRequest request = (NntpWebRequest) WebRequest.Create(IdentityNewsServerManager.BuildNntpRequestUri(serverDef));
    request.Method = "LIST";
    if (!string.IsNullOrEmpty(serverDef.AuthUser)) {
     string u = null, p = null;
     NewsHandler.GetNntpServerCredentials(serverDef, ref u, ref p);
     request.Credentials = NewsHandler.CreateCredentialsFrom(u, p);
    }
    request.Timeout = 1000 * 60;
                if (serverDef.Timeout > 0) {
                    request.Timeout = serverDef.Timeout * 1000 * 60;
                }
    WebResponse response = request.GetResponse();
    foreach(string s in NntpParser.GetNewsgroupList(response.GetResponseStream())){
     result.Add(s);
    }
    this.Newsgroups = result;
   } catch (System.Threading.ThreadAbortException) {
   } catch (Exception ex) {
    p_operationException = ex;
   } finally {
    WorkDone.Set();
   }
  }
 }
}
