using System;
using System.Threading;
using System.IO;
using System.Net;
using System.Xml;
using System.Xml.Xsl;
using System.Xml.Serialization;
using System.Diagnostics;
using System.Collections;
using System.Collections.Generic;
using System.Collections.Specialized;
using ClrMappedWebReference = RssBandit.DasBlog;
using NgosLocation = RssBandit.com.newsgator.services;
using NgosFolder = RssBandit.com.newsgator.services1;
using NgosFeed = RssBandit.com.newsgator.services2;
using NgosSubscription = RssBandit.com.newsgator.services3;
using NgosPostItem = RssBandit.com.newsgator.services4;
using ICSharpCode.SharpZipLib.Zip;
using Logger = RssBandit.Common.Logging;
using NewsComponents;
using NewsComponents.Feed;
using NewsComponents.Utils;
using NewsComponents.Threading;
using RssBandit.SpecialFeeds;
using RssBandit.WinGui.Utility;
using NGAPIToken=RssBandit.CLR20.com.newsgator.services3.NGAPIToken;
namespace RssBandit.WinGui
{
    public enum SynchronizationFormat
    {
        Zip,
        Siam
    }
    internal class NgosDownloadFeedState
    {
        public INewsFeed feed;
        public StringCollection readItems2Sync;
        public StringCollection deletedItems2Sync;
        public FeedWebService fws;
    }
    internal class RemoteFeedlistThreadHandler : EntertainmentThreadHandlerBase
    {
        public enum
            Operation
        {
            None,
            Upload,
            Download,
        }
        public RemoteFeedlistThreadHandler(Operation operation, RssBanditApplication rssBanditApp,
                                           RemoteStorageProtocolType protocol, string remoteLocation,
                                           string credentialUser, string credentialPwd, Settings settings)
        {
            operationToRun = operation;
            this.rssBanditApp = rssBanditApp;
            remoteProtocol = protocol;
            this.remoteLocation = remoteLocation;
            this.credentialUser = credentialUser;
            credentialPassword = credentialPwd;
            this.settings = settings;
        }
        private static readonly ILog _log = Log.GetLogger(typeof (RemoteFeedlistThreadHandler));
        private static readonly string NgosProductKey = "7AF62582A5334A9CADF967818E734558";
        private static readonly string NgosLocationName = "RssBandit-" + Environment.MachineName;
        private static readonly string InvalidFeedUrl =
            "http://www.example.com/no-url-for-rss-feed-provided-in-imported-opml";
        private readonly Operation operationToRun = Operation.None;
        private readonly RssBanditApplication rssBanditApp = null;
        private RemoteStorageProtocolType remoteProtocol = RemoteStorageProtocolType.UNC;
        private string remoteLocation = null;
        private readonly string credentialUser = null;
        private readonly string credentialPassword = null;
        private string remoteFileName = "rssbandit-state.zip";
        private readonly Settings settings = null;
        private ManualResetEvent eventX;
        private int ngosFeedsToDownload = 0;
        private int ngosDownloadedFeeds = 0;
        private static readonly string NgosOpmlNamespace = "http://newsgator.com/schema/opml";
        protected override void Run()
        {
            if (operationToRun == Operation.Download)
                RunDownload();
            if (operationToRun == Operation.Upload)
                RunUpload();
        }
        public RemoteStorageProtocolType RemoteProtocol
        {
            get
            {
                return remoteProtocol;
            }
            set
            {
                remoteProtocol = value;
            }
        }
        public string RemoteLocation
        {
            get
            {
                return remoteLocation;
            }
            set
            {
                remoteLocation = value;
            }
        }
        public string RemoteFileName
        {
            get
            {
                return remoteFileName;
            }
            set
            {
                remoteFileName = value;
            }
        }
        public void RunUpload()
        {
            RunUpload(SynchronizationFormat.Zip);
        }
        private static void ZipFiles(IEnumerable<string> files, ZipOutputStream zos)
        {
            zos.SetLevel(5);
            foreach (string file in files)
            {
                if (File.Exists(file))
                {
                    FileStream fs2 = File.OpenRead(file);
                    byte[] buffer = new byte[fs2.Length];
                    fs2.Read(buffer, 0, buffer.Length);
                    ZipEntry entry = new ZipEntry(Path.GetFileName(file));
                    zos.PutNextEntry(entry);
                    zos.Write(buffer, 0, buffer.Length);
                    fs2.Close();
                }
            }
            zos.Finish();
        }
        public void RunUpload(SynchronizationFormat syncFormat)
        {
            string feedlistXml = Path.Combine(Path.GetTempPath(), "feedlist.xml");
            string[] files = {
                                 RssBanditApplication.GetFeedListFileName(),
                                 RssBanditApplication.GetFlagItemsFileName(),
                                 RssBanditApplication.GetSearchFolderFileName(),
                                 RssBanditApplication.GetSentItemsFileName(),
                                 feedlistXml
                             };
            ZipOutputStream zos;
            try
            {
                rssBanditApp.SaveApplicationState();
                using (Stream xsltStream = Resource.GetStream("Resources.feedlist2subscriptions.xslt"))
                {
                    XslCompiledTransform xslt = new XslCompiledTransform();
                    xslt.Load(new XmlTextReader(xsltStream));
                    xslt.Transform(RssBanditApplication.GetFeedListFileName(), feedlistXml);
                }
                using (MemoryStream tempStream = new MemoryStream())
                {
                    switch (remoteProtocol)
                    {
                        case RemoteStorageProtocolType.UNC:
                            FileStream fs =
                                FileHelper.OpenForWrite(
                                    Path.Combine(Environment.ExpandEnvironmentVariables(remoteLocation), remoteFileName));
                            zos = new ZipOutputStream(fs);
                            ZipFiles(files, zos);
                            zos.Close();
                            break;
                        case RemoteStorageProtocolType.NewsgatorOnline:
                            StringCollection readItems2Sync = new StringCollection(),
                                             deletedItems2Sync = new StringCollection();
                            bool feedListChanged = false;
                            eventX = new ManualResetEvent(false);
                            FolderWebService fows = new FolderWebService();
                            SubscriptionWebService sws = new SubscriptionWebService();
                            FeedWebService fws = new FeedWebService();
                            PostItem pws = new PostItem();
                            LocationWebService lws = new LocationWebService();
                            fows.Credentials =
                                lws.Credentials =
                                pws.Credentials =
                                fws.Credentials =
                                sws.Credentials = new NetworkCredential(credentialUser, credentialPassword);
                            fows.Proxy = lws.Proxy = pws.Proxy = fws.Proxy = sws.Proxy = rssBanditApp.Proxy;
                            sws.NGAPITokenValue = new NGAPIToken();
                            fws.NGAPITokenValue = new CLR20.com.newsgator.services2.NGAPIToken();
                            pws.NGAPITokenValue = new CLR20.com.newsgator.services4.NGAPIToken();
                            lws.NGAPITokenValue = new CLR20.com.newsgator.services.NGAPIToken();
                            fows.NGAPITokenValue = new CLR20.com.newsgator.services1.NGAPIToken();
                            fows.NGAPITokenValue.Token =
                                lws.NGAPITokenValue.Token =
                                pws.NGAPITokenValue.Token =
                                fws.NGAPITokenValue.Token = sws.NGAPITokenValue.Token = NgosProductKey;
                            try
                            {
                                lws.CreateLocation(NgosLocationName, true);
                            }
                            catch (Exception e)
                            {
                                _log.Error(e);
                            }
                            XmlElement opmlFeedList = sws.GetSubscriptionList(NgosLocationName, null);
                            XmlDocument opmlDoc = new XmlDocument();
                            opmlDoc.AppendChild(opmlDoc.ImportNode(opmlFeedList, true));
                            XmlNodeReader reader = new XmlNodeReader(rssBanditApp.FeedHandler.ConvertFeedList(opmlDoc));
                            XmlSerializer serializer = XmlHelper.SerializerCache.GetSerializer(typeof (feeds));
                            feeds myFeeds = (feeds) serializer.Deserialize(reader);
                            reader.Close();
                            ArrayList deletedFeeds = new ArrayList();
                            for (int i = myFeeds.feed.Count; i-- > 0; Interlocked.Increment(ref ngosFeedsToDownload))
                            {
                                INewsFeed feed = myFeeds.feed[i];
                                if (!feed.link.Equals(InvalidFeedUrl) &&
                                    rssBanditApp.FeedHandler.IsSubscribed(feed.link))
                                {
                                    NgosDownloadFeedState state = new NgosDownloadFeedState();
                                    state.feed = feed;
                                    state.deletedItems2Sync = deletedItems2Sync;
                                    state.readItems2Sync = readItems2Sync;
                                    state.fws = fws;
                                    PriorityThreadPool.QueueUserWorkItem(
                                        NgosDownloadFeedToSetReadItems, state,
                                        (int) ThreadPriority.Normal);
                                }
                                else
                                {
                                    feedListChanged = true;
                                    try
                                    {
                                        deletedFeeds.Add(
                                            Int32.Parse(
                                                NewsFeed.GetElementWildCardValue(feed, "http://newsgator.com/schema/opml", "id")));
                                    }
                                    catch (Exception e)
                                    {
                                        _log.Error("Error while trying to parse Newsgator feed ID for" + feed.link, e);
                                    }
                                    Interlocked.Decrement(ref ngosFeedsToDownload);
                                }
                            }
                            if (ngosFeedsToDownload != 0)
                            {
                                eventX.WaitOne(System.Threading.Timeout.Infinite, true);
                            }
                            string[] readItems = new string[readItems2Sync.Count];
                            readItems2Sync.CopyTo(readItems, 0);
                            string[] deletedItems = new string[deletedItems2Sync.Count];
                            deletedItems2Sync.CopyTo(deletedItems, 0);
                            pws.SetState(NgosLocationName, null, readItems, null );
                            ArrayList addedFeeds = new ArrayList();
                            if ((myFeeds.feed.Count != rssBanditApp.FeedHandler.GetFeeds().Count) || feedListChanged)
                            {
                                feedListChanged = true;
                                NewsFeed[] currentFeeds =
                                    new NewsFeed[rssBanditApp.FeedHandler.GetFeeds().Values.Count];
                                rssBanditApp.FeedHandler.GetFeeds().Values.CopyTo(currentFeeds, 0);
                                foreach (NewsFeed f in currentFeeds)
                                {
                                    if (!myFeeds.feed.Contains(f))
                                    {
                                        addedFeeds.Add(f);
                                    }
                                }
                            }
                            if (feedListChanged)
                            {
                                if (deletedFeeds.Count > 0)
                                {
                                    int[] deleted = new int[deletedFeeds.Count];
                                    deletedFeeds.CopyTo(deleted, 0);
                                    sws.DeleteSubscriptions(deleted);
                                }
                                if (addedFeeds.Count > 0)
                                {
                                    foreach (NewsFeed f in addedFeeds)
                                    {
                                        try
                                        {
                                            sws.AddSubscription(f.link, GetFolderId(fows, f, opmlFeedList), null);
                                        }
                                        catch (Exception e)
                                        {
                                            _log.Error("Error adding subscription " + f.link + " to NewsGator Online", e);
                                        }
                                    }
                                }
                            }
                            ngosDownloadedFeeds = ngosFeedsToDownload = 0;
                            break;
                        case RemoteStorageProtocolType.dasBlog:
                            rssBanditApp.FeedHandler.SaveFeedList(tempStream, FeedListFormat.OPML);
                            tempStream.Position = 0;
                            XmlDocument doc = new XmlDocument();
                            doc.Load(tempStream);
                            ConfigEditingService remoteStore = new ConfigEditingService();
                            remoteStore.Url = remoteLocation;
                            remoteStore.authenticationHeaderValue = new authenticationHeader();
                            remoteStore.authenticationHeaderValue.userName = credentialUser;
                            remoteStore.authenticationHeaderValue.password = credentialPassword;
                            remoteStore.Proxy = rssBanditApp.Proxy;
                            remoteStore.PostBlogroll("blogroll", doc.DocumentElement);
                            break;
                        case RemoteStorageProtocolType.FTP:
                            zos = new ZipOutputStream(tempStream);
                            ZipFiles(files, zos);
                            tempStream.Position = 0;
                            Uri remoteUri = new Uri(remoteLocation);
                            UriBuilder builder = new UriBuilder(remoteUri);
                            builder.Path = remoteFileName;
                            FtpWebRequest ftpRequest = (FtpWebRequest)WebRequest.Create(builder.Uri);
                            ftpRequest.Method = WebRequestMethods.Ftp.UploadFile;
                            ftpRequest.KeepAlive = false;
                            ftpRequest.UseBinary = true;
                            ftpRequest.UsePassive =
                                settings.GetBoolean("RemoteFeedlist/Ftp.ConnectionMode.Passive", true);
                            ftpRequest.Credentials = new NetworkCredential(credentialUser, credentialPassword);
                            ftpRequest.ContentLength = tempStream.Length;
                            try
                            {
                                int buffLength = 2048;
                                byte[] buff = new byte[buffLength];
                                int contentLen;
                                Stream strm = ftpRequest.GetRequestStream();
                                contentLen = tempStream.Read(buff, 0, buffLength);
                                while (contentLen != 0)
                                {
                                    strm.Write(buff, 0, contentLen);
                                    contentLen = tempStream.Read(buff, 0, buffLength);
                                }
                                strm.Close();
                            }
                            catch (Exception ex)
                            {
                                _log.Error("FTP Upload Error", ex);
                            }
                            zos.Close();
                            break;
                        case RemoteStorageProtocolType.WebDAV:
                            zos = new ZipOutputStream(tempStream);
                            ZipFiles(files, zos);
                            remoteUri = new Uri(remoteLocation.EndsWith("/")
                                                    ?
                                                        remoteLocation + remoteFileName
                                                    :
                                                        remoteLocation + "/" + remoteFileName);
                            tempStream.Position = 0;
                            HttpWebRequest request = (HttpWebRequest) HttpWebRequest.Create(remoteUri);
                            request.Method = "PUT";
                            request.ContentType = "application/zip";
                            request.AllowAutoRedirect = true;
                            request.UserAgent = RssBanditApplication.UserAgent;
                            request.Proxy = rssBanditApp.Proxy;
                            if (!string.IsNullOrEmpty(credentialUser))
                            {
                                NetworkCredential nc =
                                    FeedSource.CreateCredentialsFrom(credentialUser, credentialPassword);
                                CredentialCache cc = new CredentialCache();
                                cc.Add(remoteUri, "Basic", nc);
                                cc.Add(remoteUri, "Digest", nc);
                                cc.Add(remoteUri, "NTLM", nc);
                                request.Credentials = cc;
                            }
                            byte[] bytes = new byte[tempStream.Length];
                            tempStream.Read(bytes, 0, bytes.Length);
                            zos.Close();
                            request.ContentLength = bytes.Length;
                            Stream requestStream = request.GetRequestStream();
                            requestStream.Write(bytes, 0, bytes.Length);
                            requestStream.Close();
                            request.GetResponse().Close();
                            break;
                        default:
                            Debug.Assert(false,
                                         "unknown remote protocol: '" + remoteProtocol +
                                         "' in RemoteFeedlistThreadHandler");
                            break;
                    }
                }
            }
            catch (ThreadAbortException)
            {
            }
            catch (Exception ex)
            {
                p_operationException = ex;
                _log.Error("RunUpload(" + syncFormat + ") Exception", ex);
            }
            finally
            {
                WorkDone.Set();
            }
        }
        public void RunDownload()
        {
            RunDownload(SynchronizationFormat.Zip);
        }
        public void RunDownload(SynchronizationFormat syncFormat)
        {
            try
            {
                Stream importStream = null;
                string tempFileName = null;
                feeds syncedFeeds = null;
                switch (remoteProtocol)
                {
                    case RemoteStorageProtocolType.UNC:
                        importStream =
                            File.Open(
                                Path.Combine(Environment.ExpandEnvironmentVariables(remoteLocation), remoteFileName),
                                FileMode.Open);
                        break;
                    case RemoteStorageProtocolType.dasBlog:
                        ConfigEditingService remoteStore = new ConfigEditingService();
                        remoteStore.Url = remoteLocation;
                        remoteStore.authenticationHeaderValue = new authenticationHeader();
                        remoteStore.authenticationHeaderValue.userName = credentialUser;
                        remoteStore.authenticationHeaderValue.password = credentialPassword;
                        remoteStore.Proxy = rssBanditApp.Proxy;
                        importStream = new MemoryStream();
                        XmlElement xml = remoteStore.GetBlogroll("blogroll");
                        XmlDocument doc = new XmlDocument();
                        doc.LoadXml(xml.OuterXml);
                        doc.Save(importStream);
                        importStream.Position = 0;
                        break;
                    case RemoteStorageProtocolType.NewsgatorOnline:
                        syncedFeeds = new feeds();
                        string syncToken = rssBanditApp.Preferences.NgosSyncToken;
                        eventX = new ManualResetEvent(false);
                        SubscriptionWebService sws = new SubscriptionWebService();
                        FeedWebService fws = new FeedWebService();
                        LocationWebService lws = new LocationWebService();
                        lws.Credentials =
                            fws.Credentials =
                            sws.Credentials = new NetworkCredential(credentialUser, credentialPassword);
                        lws.Proxy = fws.Proxy = sws.Proxy = rssBanditApp.Proxy;
                        sws.NGAPITokenValue = new NGAPIToken();
                        fws.NGAPITokenValue = new CLR20.com.newsgator.services2.NGAPIToken();
                        lws.NGAPITokenValue = new CLR20.com.newsgator.services.NGAPIToken();
                        lws.NGAPITokenValue.Token =
                            fws.NGAPITokenValue.Token = sws.NGAPITokenValue.Token = NgosProductKey;
                        try
                        {
                            lws.CreateLocation(NgosLocationName, true);
                        }
                        catch (Exception)
                        {
                            ;
                        }
                        XmlElement opmlFeedList = sws.GetSubscriptionList(NgosLocationName, syncToken);
                        XmlNode tokenNode =
                            opmlFeedList.Attributes.GetNamedItem("token", "http://newsgator.com/schema/opml");
                        if (tokenNode != null)
                        {
                            syncToken = tokenNode.Value;
                        }
                        XmlDocument opmlDoc = new XmlDocument();
                        opmlDoc.AppendChild(opmlDoc.ImportNode(opmlFeedList, true));
                        XmlNodeReader reader = new XmlNodeReader(rssBanditApp.FeedHandler.ConvertFeedList(opmlDoc));
                        XmlSerializer serializer = XmlHelper.SerializerCache.GetSerializer(typeof (feeds));
                        feeds myFeeds = (feeds) serializer.Deserialize(reader);
                        reader.Close();
                        for (int i = myFeeds.feed.Count; i-- > 0; Interlocked.Increment(ref ngosFeedsToDownload))
                        {
                            INewsFeed feed = myFeeds.feed[i];
                            string unseen = NewsFeed.GetElementWildCardValue(feed, "http://newsgator.com/schema/opml", "unseen");
                            if (!feed.link.Equals(InvalidFeedUrl) &&
                                ((unseen != null) && unseen.ToLower().Equals("true")))
                            {
                                NgosDownloadFeedState state = new NgosDownloadFeedState();
                                state.feed = feed;
                                state.fws = fws;
                                PriorityThreadPool.QueueUserWorkItem(NgosDownloadFeedToGetReadItems,
                                                                     state, (int) ThreadPriority.Normal);
                            }
                            else
                            {
                                Interlocked.Decrement(ref ngosFeedsToDownload);
                            }
                            if (!feed.link.Equals(InvalidFeedUrl))
                            {
                                syncedFeeds.feed.Add(feed as NewsFeed);
                            }
                        }
                        syncedFeeds.feed.AddRange(rssBanditApp.FeedHandler.GetNonInternetFeeds() as IEnumerable<NewsFeed>);
                        if (ngosFeedsToDownload != 0)
                        {
                            eventX.WaitOne(System.Threading.Timeout.Infinite, true);
                        }
                        rssBanditApp.Preferences.NgosSyncToken = syncToken;
                        ngosDownloadedFeeds = ngosFeedsToDownload = 0;
                        break;
                    case RemoteStorageProtocolType.FTP:
                        Uri remoteUri = new Uri(remoteLocation);
                        UriBuilder builder = new UriBuilder(remoteUri);
                        builder.Path = remoteFileName;
                        FtpWebRequest ftpRequest = (FtpWebRequest)WebRequest.Create(builder.Uri);
                        ftpRequest.Method = WebRequestMethods.Ftp.DownloadFile;
                        ftpRequest.KeepAlive = false;
                        ftpRequest.UseBinary = true;
                        ftpRequest.UsePassive = settings.GetBoolean("RemoteFeedlist/Ftp.ConnectionMode.Passive", true);
                        ftpRequest.Credentials = new NetworkCredential(credentialUser, credentialPassword);
                        tempFileName = Path.GetTempFileName();
                        Stream fileStream = File.Create(tempFileName);
                        try
                        {
                            int buffLength = 2048;
                            byte[] buff = new byte[buffLength];
                            int contentLen;
                            Stream strm = ftpRequest.GetResponse().GetResponseStream();
                            contentLen = strm.Read(buff, 0, buffLength);
                            while (contentLen != 0)
                            {
                                fileStream.Write(buff, 0, contentLen);
                                contentLen = strm.Read(buff, 0, buffLength);
                            }
                            strm.Close();
                        }
                        catch (Exception ex)
                        {
                            _log.Error("FTP Upload Error", ex);
                        }
                        fileStream.Close();
                        importStream = File.OpenRead(tempFileName);
                        break;
                    case RemoteStorageProtocolType.WebDAV:
                        remoteUri = new Uri(remoteLocation.EndsWith("/")
                                                ?
                                                    remoteLocation + remoteFileName
                                                :
                                                    remoteLocation + "/" + remoteFileName);
                        HttpWebRequest request = (HttpWebRequest) HttpWebRequest.Create(remoteUri);
                        request.Method = "GET";
                        request.AllowAutoRedirect = true;
                        request.UserAgent = RssBanditApplication.UserAgent;
                        request.Proxy = rssBanditApp.Proxy;
                        if (!string.IsNullOrEmpty(credentialUser))
                        {
                            NetworkCredential nc =
                                FeedSource.CreateCredentialsFrom(credentialUser, credentialPassword);
                            CredentialCache cc = new CredentialCache();
                            cc.Add(remoteUri, "Basic", nc);
                            cc.Add(remoteUri, "Digest", nc);
                            cc.Add(remoteUri, "NTLM", nc);
                            request.Credentials = cc;
                        }
                        importStream = request.GetResponse().GetResponseStream();
                        break;
                    default:
                        Debug.Assert(false,
                                     "unknown remote protocol: '" + remoteProtocol + "' in RemoteFeedlistThreadHandler");
                        break;
                }
                try
                {
                    if ((remoteProtocol == RemoteStorageProtocolType.dasBlog) ||
                        (remoteProtocol == RemoteStorageProtocolType.dasBlog_1_3))
                    {
                        rssBanditApp.FeedHandler.ImportFeedlist(importStream);
                    }
                    else if (remoteProtocol == RemoteStorageProtocolType.NewsgatorOnline)
                    {
                        rssBanditApp.FeedHandler.ImportFeedlist(syncedFeeds, null, true , true
                            );
                    }
                    else
                    {
                        Synchronize(importStream, syncFormat);
                    }
                }
                catch (Exception ex)
                {
                    p_operationException = ex;
                }
                finally
                {
                    if (importStream != null)
                        importStream.Close();
                }
                if (tempFileName != null)
                    File.Delete(tempFileName);
            }
            catch (ThreadAbortException)
            {
            }
            catch (Exception ex)
            {
                p_operationException = ex;
                _log.Error("RunDownload(" + syncFormat + ") Exception", ex);
            }
            finally
            {
                WorkDone.Set();
            }
        }
        public void Synchronize(Stream stream, SynchronizationFormat syncFormat)
        {
            string feedlist = Path.GetFileName(RssBanditApplication.GetFeedListFileName());
            string oldschoolfeedlist = Path.GetFileName(RssBanditApplication.GetOldFeedListFileName());
            string flaggeditems = Path.GetFileName(RssBanditApplication.GetFlagItemsFileName());
            string searchfolders = Path.GetFileName(RssBanditApplication.GetSearchFolderFileName());
            string sentitems = Path.GetFileName(RssBanditApplication.GetSentItemsFileName());
            bool subscriptionsXmlSeen = false;
            if (syncFormat == SynchronizationFormat.Zip)
            {
                ZipInputStream zis = new ZipInputStream(stream);
                ZipEntry theEntry;
                while ((theEntry = zis.GetNextEntry()) != null)
                {
                    if (theEntry.Name == feedlist)
                    {
                        subscriptionsXmlSeen = true;
                        rssBanditApp.FeedHandler.ReplaceFeedlist(zis);
                    }
                    else if (!subscriptionsXmlSeen && (theEntry.Name == oldschoolfeedlist))
                    {
                        rssBanditApp.FeedHandler.ReplaceFeedlist(zis);
                    }
                    else if (theEntry.Name == flaggeditems)
                    {
                        LocalFeedsFeed flaggedItemsFeed = rssBanditApp.FlaggedItemsFeed;
                        LocalFeedsFeed lff = new LocalFeedsFeed(flaggedItemsFeed.link, flaggedItemsFeed.title,
                                                                flaggedItemsFeed.Description, new XmlTextReader(zis));
                        rssBanditApp.ClearFlaggedItems();
                        foreach (NewsItem item in lff.Items)
                        {
                            flaggedItemsFeed.Add(item);
                            rssBanditApp.ReFlagNewsItem(item);
                        }
                    }
                    else if (theEntry.Name == sentitems)
                    {
                        LocalFeedsFeed sentItemsFeed = rssBanditApp.SentItemsFeed;
                        LocalFeedsFeed lff2 = new LocalFeedsFeed(sentItemsFeed.link, sentItemsFeed.title,
                                                                 sentItemsFeed.Description, new XmlTextReader(zis));
                        sentItemsFeed.Add(lff2);
                    }
                    else if (theEntry.Name == searchfolders)
                    {
                        XmlSerializer ser = XmlHelper.SerializerCache.GetSerializer(typeof (FinderSearchNodes));
                        rssBanditApp.FindersSearchRoot = (FinderSearchNodes) ser.Deserialize(zis);
                    }
                }
                zis.Close();
            }
        }
        private void NgosDownloadFeedToGetReadItems(object stateInfo)
        {
            try
            {
                NgosDownloadFeedState state = (NgosDownloadFeedState)stateInfo;
                string feedId = NewsFeed.GetElementWildCardValue(state.feed, "http://newsgator.com/schema/opml", "id");
                XmlElement feed2Sync =
                    state.fws.GetNews(Int32.Parse(feedId), NgosLocationName, null
                                      , false);
                XmlNamespaceManager nsMgr = new XmlNamespaceManager(new NameTable());
                nsMgr.AddNamespace("ng", "http://newsgator.com/schema/extensions");
                foreach (XmlNode item in feed2Sync.SelectNodes("//item[ng:read='True']", nsMgr))
                {
                    XmlNode link = item.SelectSingleNode("./link");
                    XmlNode guid = item.SelectSingleNode("./guid");
                    if (guid != null)
                    {
                        state.feed.AddViewedStory(guid.InnerText);
                    }
                    else if (link != null)
                    {
                        state.feed.AddViewedStory(link.InnerText);
                    }
                }
            }
            finally
            {
                Interlocked.Increment(ref ngosDownloadedFeeds);
                if (ngosDownloadedFeeds == ngosFeedsToDownload)
                {
                    eventX.Set();
                }
            }
        }
        private void NgosDownloadFeedToSetReadItems(object stateInfo)
        {
            try
            {
                NgosDownloadFeedState state = (NgosDownloadFeedState)stateInfo;
                INewsFeed feedInBandit = rssBanditApp.FeedHandler.GetFeeds()[state.feed.link];
                List<string> readItems = new List<string>();
                readItems.AddRange(feedInBandit.storiesrecentlyviewed);
                string feedId = NewsFeed.GetElementWildCardValue(state.feed, "http://newsgator.com/schema/opml", "id");
                XmlElement feed2Sync =
                    state.fws.GetNews(Int32.Parse(feedId), NgosLocationName, null
                                      , false);
                XmlNamespaceManager nsMgr = new XmlNamespaceManager(new NameTable());
                nsMgr.AddNamespace("ng", "http://newsgator.com/schema/extensions");
                foreach (XmlNode item in feed2Sync.SelectNodes("//item"))
                {
                    XmlNode link = item.SelectSingleNode("./link");
                    XmlNode guid = item.SelectSingleNode("./guid");
                    XmlNode read = item.SelectSingleNode("./ng:read", nsMgr);
                    bool itemRead = false;
                    if (read != null && read.InnerText.ToLower().Equals("true"))
                    {
                        itemRead = true;
                    }
                    string ngosId = item.SelectSingleNode("./ng:postId", nsMgr).InnerText;
                    string id;
                    if (guid != null)
                    {
                        id = guid.InnerText;
                    }
                    else if (link != null)
                    {
                        id = link.InnerText;
                    }
                    else
                    {
                        continue;
                    }
                    if (!itemRead && readItems.Contains(id))
                    {
                        state.readItems2Sync.Add(ngosId);
                    }
                }
            }
            finally
            {
                Interlocked.Increment(ref ngosDownloadedFeeds);
                if (ngosDownloadedFeeds == ngosFeedsToDownload)
                {
                    eventX.Set();
                }
            }
        }
        private static int GetFolderId(FolderWebService fows, NewsFeed feed, XmlElement newsgatorOpml)
        {
            int folderId;
            folderId = CreateNewsgatorCategoryHive(fows, feed, newsgatorOpml);
            return folderId;
        }
        private static int CreateNewsgatorCategoryHive(FolderWebService fows, NewsFeed feed, XmlElement newsgatorOpml)
        {
            string category = feed.category;
            if (newsgatorOpml == null)
                return 0;
            if (category == null || category.Length == 0)
                return 0;
            XmlElement startNode = (XmlElement) newsgatorOpml.ChildNodes[1];
            int folderId = 0;
            try
            {
                string[] catHives = category.Split(FeedSource.CategorySeparator.ToCharArray());
                XmlElement n;
                bool wasNew = false;
                foreach (string catHive in catHives)
                {
                    if (!wasNew)
                    {
                        string xpath = "child::outline[@title=" + FeedSource.buildXPathString(catHive) +
                                       " and (count(@*)= 1)]";
                        n = (XmlElement) startNode.SelectSingleNode(xpath);
                    }
                    else
                    {
                        n = null;
                    }
                    if (n == null)
                    {
                        n = startNode.OwnerDocument.CreateElement("outline");
                        n.SetAttribute("title", catHive);
                        startNode.AppendChild(n);
                        wasNew = true;
                        int parentId = 0;
                        XmlNode parentFolderIdNode = startNode.Attributes.GetNamedItem("folderId", NgosOpmlNamespace);
                        if (parentFolderIdNode != null)
                        {
                            parentId = Convert.ToInt32(parentFolderIdNode.Value);
                        }
                        folderId = fows.GetOrCreateFolder(catHive, parentId, "MYF");
                        XmlAttribute folderIdNode = n.SetAttributeNode("folderId", NgosOpmlNamespace);
                        folderIdNode.Value = folderId.ToString();
                    }
                    startNode = n;
                }
                return folderId;
            }
            catch (Exception e)
            {
                _log.Error("Error in CreateNewsgatorCategoryHive() when attempting to create " + category, e);
            }
            return 0;
        }
    }
}
