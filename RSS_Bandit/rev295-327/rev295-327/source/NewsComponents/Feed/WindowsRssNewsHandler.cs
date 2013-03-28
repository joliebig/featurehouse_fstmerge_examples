 
 
 
 
 
 
 
using System.ComponentModel; 
using System.Xml; 
using System.Xml.Serialization; 
using RssBandit.Common; 
using NewsComponents.Collections; 
using NewsComponents.Utils; namespace  NewsComponents.Feed {
	class  WindowsRssNewsHandler   {
		
           
          <<<<<<< /work/joliebig/semistructured_merge/fse2011_artifact/binary/fstmerge_tmp1307442548156/fstmerge_var1_1269556225261346118
void LoadFeedlist()
        {
            this.BootstrapAndLoadFeedlist(new feeds());
        }
=======
>>>>>>> /work/joliebig/semistructured_merge/fse2011_artifact/binary/fstmerge_tmp1307442548156/fstmerge_var2_5573839811030035941
 
        private  IFeedsManager feedManager = new FeedsManagerClass(); 
        public  IFeedFolder AddFolder(string path)
        {
            IFeedFolder folder = feedManager.RootFolder as IFeedFolder;
            if (!StringHelper.EmptyTrimOrNull(path))
            {
                string[] categoryPath = path.Split(new char[] { '\\' });
                foreach (string c in categoryPath)
                {
                    if (folder.ExistsSubfolder(c))
                    {
                        folder = folder.GetSubfolder(c) as IFeedFolder;
                    }
                    else
                    {
                        folder = folder.CreateSubfolder(c) as IFeedFolder;
                        if (!folder.Path.Equals(path) && !categories.ContainsKey(folder.Path)) {
                            this.categories.Add(folder.Path, new WindowsRssNewsFeedCategory(folder));
                        }
                    }
                }
            }
            return folder;
        } 
        public override  INewsFeedCategory AddCategory(INewsFeedCategory cat)
        {
            if (cat is WindowsRssNewsFeedCategory)
            {
                if (!categories.ContainsKey(cat.Value))
                {
                    categories.Add(cat.Value, cat);
                }
            }
            else
            {
                if (!categories.ContainsKey(cat.Value))
                {
                    IFeedFolder folder = this.AddFolder(cat.Value);
                    cat = new WindowsRssNewsFeedCategory(folder, cat);
                    this.categories.Add(cat.Value, cat);
                }
                else
                {
                    cat = categories[cat.Value];
                }
            }
            return cat;
        } 
        public override  INewsFeedCategory AddCategory(string cat)
        {
            INewsFeedCategory toReturn;
            if (!this.categories.ContainsKey(cat))
            {
                    IFeedFolder folder = this.AddFolder(cat);
                    toReturn = new WindowsRssNewsFeedCategory(folder);
                    this.categories.Add(cat, toReturn);
            }else{
                toReturn = categories[cat];
            }
            return toReturn;
        } 
        public override  void ChangeCategory(INewsFeed feed, INewsFeedCategory cat)
        {
            if (feed == null)
                throw new ArgumentNullException("feed");
            if (cat == null)
                throw new ArgumentNullException("cat");
            WindowsRssNewsFeed f = feed as WindowsRssNewsFeed;
            if (f != null && feedsTable.ContainsKey(f.link))
            {
                IFeedFolder folder = String.IsNullOrEmpty(f.category) ? feedManager.RootFolder as IFeedFolder
                                                                      : feedManager.GetFolder(f.category) as IFeedFolder;
                IFeed ifeed = folder.GetFeed(f.title) as IFeed;
                ifeed.Move(cat.Value);
            }
        } 
        public override  void RenameCategory(string oldName, string newName)
        {
            if (StringHelper.EmptyTrimOrNull(oldName))
                throw new ArgumentNullException("oldName");
            if (StringHelper.EmptyTrimOrNull(newName))
                throw new ArgumentNullException("newName");
            if (this.categories.ContainsKey(oldName))
            {
                WindowsRssNewsFeedCategory cat = this.categories[oldName] as WindowsRssNewsFeedCategory;
                IFeedFolder folder = feedManager.GetFolder(oldName) as IFeedFolder;
                if (folder != null)
                {
                    folder.Rename(newName);
                    this.categories.Remove(oldName);
                    categories.Add(newName, new WindowsRssNewsFeedCategory(folder, cat));
                }
            }
        } 
        public override  INewsFeed AddFeed(INewsFeed f, FeedInfo fi)
        {
            if (f is WindowsRssNewsFeed)
            {
                if (!feedsTable.ContainsKey(f.link))
                {
                    feedsTable.Add(f.link, f);
                }
            }
            else
            {
                if (!feedManager.ExistsFolder(f.category))
                {
                    this.AddCategory(f.category);
                }
                IFeedFolder folder = feedManager.GetFolder(f.category) as IFeedFolder;
                IFeed newFeed = folder.CreateFeed(f.title, f.link) as IFeed;
                f = new WindowsRssNewsFeed(newFeed, f);
                feedsTable.Add(f.link, f);
            }
            return f;
        } 
        public override  void DeleteFeed(string feedUrl)
        {
            if (this.FeedsTable.ContainsKey(feedUrl))
            {
                WindowsRssNewsFeed f = this.FeedsTable[feedUrl] as WindowsRssNewsFeed;
                this.feedsTable.Remove(f.link);
                IFeed feed = feedManager.GetFeedByUrl(feedUrl) as IFeed;
                if (feed != null)
                {
                    feed.Delete();
                }
            }
        } 
        public override  void DeleteCategory(string cat)
        {
            base.DeleteCategory(cat);
            IFeedFolder folder = feedManager.GetFolder(cat) as IFeedFolder;
            folder.Delete();
        } 
        private  void LoadFolder(IFeedFolder folder2load, Dictionary<string, NewsFeed> bootstrapFeeds, Dictionary<string, INewsFeedCategory> bootstrapCategories)
        {
            if (folder2load != null)
            {
                IFeedsEnum Feeds = folder2load.Feeds as IFeedsEnum;
                IFeedsEnum Subfolders = folder2load.Subfolders as IFeedsEnum;
                if (Feeds.Count > 0)
                {
                    foreach (IFeed feed in Feeds)
                    {
                        Uri uri = null;
                        try
                        {
                            uri = new Uri(feed.DownloadUrl);
                        }
                        catch (Exception)
                        {
                            continue;
                        }
                        string feedUrl = uri.CanonicalizedUri();
                        INewsFeed bootstrapFeed = (bootstrapFeeds.ContainsKey(feedUrl) ? bootstrapFeeds[feedUrl] : null);
                        this.feedsTable.Add(feedUrl, new WindowsRssNewsFeed(feed, bootstrapFeed));
                    }
                }
                if (Subfolders.Count > 0)
                {
                    foreach (IFeedFolder folder in Subfolders)
                    {
                        string categoryName = folder.Path;
                        INewsFeedCategory bootstrapCategory = (bootstrapCategories.ContainsKey(categoryName) ? bootstrapCategories[categoryName] : null);
                        this.categories.Add(folder.Path, new WindowsRssNewsFeedCategory(folder, bootstrapCategory));
                        LoadFolder(folder, bootstrapFeeds, bootstrapCategories);
                    }
                }
            }
        } 
        public override  void BootstrapAndLoadFeedlist(feeds feedlist)
        {
            Dictionary<string, NewsFeed> bootstrapFeeds = new Dictionary<string, NewsFeed>();
            Dictionary<string, INewsFeedCategory> bootstrapCategories = new Dictionary<string, INewsFeedCategory>();
            foreach (NewsFeed f in feedlist.feed)
            {
                bootstrapFeeds.Add(f.link, f);
            }
            foreach (category c in feedlist.categories)
            {
                bootstrapCategories.Add(c.Value, c);
            }
            IFeedFolder root = feedManager.RootFolder as IFeedFolder;
            LoadFolder(root, bootstrapFeeds, bootstrapCategories);
        }
	}
	class  WindowsRssNewsFeed  : INewsFeed, IDisposable {
		
        private  WindowsRssNewsFeed() { ;} 
        public  WindowsRssNewsFeed(IFeed feed) {
            if (feed == null) throw new ArgumentNullException("feed");
            this.myfeed = feed;
        } 
        public  WindowsRssNewsFeed(IFeed feed, INewsFeed banditfeed): this(feed)
        {
            if (banditfeed != null)
            {
                this.refreshrate = banditfeed.refreshrate;
                this.refreshrateSpecified = banditfeed.refreshrateSpecified;
                this.maxitemage = banditfeed.maxitemage;
                this.markitemsreadonexit = banditfeed.markitemsreadonexit;
                this.markitemsreadonexitSpecified = banditfeed.markitemsreadonexitSpecified;
                this.listviewlayout = banditfeed.listviewlayout;
                this.favicon = banditfeed.favicon;
                this.stylesheet = banditfeed.stylesheet;
                this.enclosurealert = banditfeed.enclosurealert;
                this.enclosurealertSpecified = banditfeed.enclosurealertSpecified;
                this.alertEnabled = banditfeed.alertEnabled;
                this.alertEnabledSpecified = banditfeed.alertEnabledSpecified;
                this.Any = banditfeed.Any;
                this.AnyAttr = banditfeed.AnyAttr;
            }
        } 
        ~WindowsRssNewsFeed() {
            Dispose(false);
        } 
        public  void Dispose()
        {
            if (!disposed)
            {
                Dispose(true);
            }
        } 
        public  void Dispose(bool disposing)
        {
            lock (this)
            {
                if (myfeed != null)
                {
                    Marshal.ReleaseComObject(myfeed);
                }
                System.GC.SuppressFinalize(this);
                disposed = true;
            }
        } 
        private  bool disposed = false; 
        private  IFeed myfeed = null; 
        internal  void SetIFeed(IFeed feed) {
            if (feed != null)
            {
                lock (this)
                {
                    if (myfeed != null)
                    {
                        Marshal.ReleaseComObject(myfeed);
                    }
                    myfeed = feed;
                }
            }
        } 
        public  string title
        {
            get { return myfeed.Title; }
            set
            {
              if(!StringHelper.EmptyTrimOrNull(value))
              {
                  myfeed.Rename(value);
                  OnPropertyChanged("title");
              }
            }
        } 
        [XmlElement(DataType = "anyURI")] 
        public  string link
        {
            get { return myfeed.DownloadUrl; }
            set
            {
            }
        } 
        [XmlAttribute] 
        public  string id
        {
            get { return myfeed.LocalId; }
            set
            {
            }
        } 
        [XmlElement("last-retrieved")] 
        public  DateTime lastretrieved
        {
            get { return myfeed.LastDownloadTime; }
            set
            {
            }
        } 
        [XmlIgnore] 
        public  bool lastretrievedSpecified
        {
            get
            {
                return true;
            }
            set
            {
            }
        } 
        [XmlElement("refresh-rate")] 
        public  int refreshrate { get; set; } 
        [XmlIgnore] 
        public  bool refreshrateSpecified { get; set; } 
        public  string etag { get { return null; } set { } } 
        [XmlElement(DataType = "anyURI")] 
        public  string cacheurl { get { return null; } set { } } 
        [XmlElement("max-item-age", DataType = "duration")] 
        public  string maxitemage { get; set; } 
        private  List<string> _storiesrecentlyviewed = new List<string>(); 
        [XmlArray(ElementName = "stories-recently-viewed", IsNullable = false)]
        [XmlArrayItem("story", Type = typeof(String), IsNullable = false)] 
        public  List<string> storiesrecentlyviewed
        {
            get
            {
                _storiesrecentlyviewed.Clear();
                IFeedsEnum items = myfeed.Items as IFeedsEnum;
                foreach (IFeedItem item in items)
                {
                    if (item.IsRead)
                    {
                        _storiesrecentlyviewed.Add(item.LocalId.ToString());
                    }
                }
                return _storiesrecentlyviewed;
            }
            set
            {
                _storiesrecentlyviewed = new List<string>(value);
            }
        } 
        private  List<string> _deletedstories = new List<string>(); 
        [XmlArray(ElementName = "deleted-stories", IsNullable = false)]
        [XmlArrayItem("story", Type = typeof(String), IsNullable = false)] 
        public  List<string> deletedstories
        {
            get
            {
                return _deletedstories;
            }
            set
            {
                _deletedstories = new List<string>(value);
            }
        } 
        [XmlElement("if-modified-since")] 
        public  DateTime lastmodified {
            get
            {
                return myfeed.LastWriteTime;
            }
            set
            {
            }
        } 
        [XmlIgnore] 
        public  bool lastmodifiedSpecified { get { return true; } set { } } 
        [XmlElement("auth-user")] 
        public  string authUser { get { return null; } set { } } 
        [XmlElement("auth-password", DataType = "base64Binary")] 
        public  Byte[] authPassword { get { return null; } set { } } 
        [XmlElement("listview-layout")] 
        public  string listviewlayout { get; set; } 
        private  string _favicon; 
        public  string favicon
        {
            get
            {
                return _favicon;
            }
            set
            {
                if (String.IsNullOrEmpty(_favicon) || !_favicon.Equals(value))
                {
                    _favicon = value;
                    this.OnPropertyChanged("favicon");
                }
            }
        } 
        private  bool _downloadenclosures; 
        [XmlElement("download-enclosures")] 
        public  bool downloadenclosures
        {
            get
            {
                return myfeed.DownloadEnclosuresAutomatically;
            }
            set
            {
                myfeed.DownloadEnclosuresAutomatically = value;
            }
        } 
        [XmlIgnore] 
        public  bool downloadenclosuresSpecified { get { return true; } set { } } 
        [XmlElement("enclosure-folder")] 
        public  string enclosurefolder
        {
            get
            {
                return myfeed.LocalEnclosurePath;
            }
            set
            {
            }
        } 
        [XmlAttribute("replace-items-on-refresh")] 
        public  bool replaceitemsonrefresh
        {
            get
            {
                return myfeed.IsList;
            }
            set
            {
            }
        } 
        [XmlIgnore] 
        public  bool replaceitemsonrefreshSpecified { get { return true; } set { } } 
        public  string stylesheet { get; set; } 
        [XmlElement("news-account")] 
        public  string newsaccount { get; set; } 
        [XmlElement("mark-items-read-on-exit")] 
        public  bool markitemsreadonexit { get; set; } 
        [XmlIgnore] 
        public  bool markitemsreadonexitSpecified { get; set; } 
        [XmlAnyElement] 
        public  XmlElement[] Any { get; set; } 
        [XmlAttribute("alert"), DefaultValue(false)] 
        public  bool alertEnabled { get; set; } 
        [XmlIgnore] 
        public  bool alertEnabledSpecified { get; set; } 
        [XmlAttribute("enclosure-alert"), DefaultValue(false)] 
        public  bool enclosurealert { get; set; } 
        [XmlIgnore] 
        public  bool enclosurealertSpecified { get; set; } 
        [XmlAttribute] 
        public  string category {
            get
            {
                IFeedFolder myfolder = myfeed.Parent as IFeedFolder;
                if (myfolder != null)
                {
                    return myfolder.Path;
                }
                else
                {
                    return null;
                }
            }
            set
            {
                if (!StringHelper.EmptyTrimOrNull(value) && !value.Equals(this.category))
                {
                    owner.ChangeCategory(this, owner.AddCategory(value));
                }
            }
        } 
        [XmlAnyAttribute] 
        public  XmlAttribute[] AnyAttr { get; set; } 
        [XmlIgnore] 
        public  bool causedException
        {
            get
            {
                return causedExceptionCount != 0;
            }
            set
            {
                if (value)
                {
                    causedExceptionCount++;
                    lastretrievedSpecified = true;
                    lastretrieved = new DateTime(DateTime.Now.Ticks);
                }
                else
                    causedExceptionCount = 0;
            }
        } 
        [XmlIgnore] 
        public  int causedExceptionCount { get; set; } 
        [XmlIgnore] 
        public  object Tag { get; set; } 
        private  bool _containsNewMessages; 
        [XmlIgnore] 
        public  bool containsNewMessages
        {
            get
            {
                return _containsNewMessages;
            }
            set
            {
                if (!_containsNewMessages.Equals(value))
                {
                    _containsNewMessages = value;
                    this.OnPropertyChanged("containsNewMessages");
                }
            }
        } 
        private  bool _containsNewComments; 
        [XmlIgnore] 
        public  bool containsNewComments
        {
            get
            {
                return _containsNewComments;
            }
            set
            {
                if (!_containsNewComments.Equals(value))
                {
                    _containsNewComments = value;
                    this.OnPropertyChanged("containsNewComments");
                }
            }
        } 
        [XmlIgnore] 
        public  NewsHandler owner { get; set; } 
        public  string GetElementWildCardValue(string namespaceUri, string localName)
        {
            foreach (XmlElement element in Any)
            {
                if (element.LocalName == localName && element.NamespaceURI == namespaceUri)
                    return element.InnerText;
            }
            return null;
        } 
        public  void AddViewedStory(string storyid)
        {
            if (!_storiesrecentlyviewed.Contains(storyid))
            {
                _storiesrecentlyviewed.Add(storyid);
                if (null != PropertyChanged)
                {
                    this.OnPropertyChanged(new CollectionChangedEventArgs("storiesrecentlyviewed", CollectionChangeAction.Add, storyid));
                }
            }
        } 
        public  void RemoveViewedStory(string storyid)
        {
            if (_storiesrecentlyviewed.Contains(storyid))
            {
                _storiesrecentlyviewed.Remove(storyid);
                if (null != PropertyChanged)
                {
                    this.OnPropertyChanged(new CollectionChangedEventArgs("storiesrecentlyviewed", CollectionChangeAction.Remove, storyid));
                }
            }
        } 
        public  void AddDeletedStory(string storyid)
        {
            if (!_deletedstories.Contains(storyid))
            {
                _deletedstories.Add(storyid);
                if (null != PropertyChanged)
                {
                    this.OnPropertyChanged(new CollectionChangedEventArgs("deletedstories", CollectionChangeAction.Add, storyid));
                }
            }
        } 
        public  void RemoveDeletedStory(string storyid)
        {
            if (_deletedstories.Contains(storyid))
            {
                _deletedstories.Remove(storyid);
                if (null != PropertyChanged)
                {
                    this.OnPropertyChanged(new CollectionChangedEventArgs("deletedstories", CollectionChangeAction.Remove, storyid));
                }
            }
        } 
        public  event PropertyChangedEventHandler PropertyChanged; 
        protected virtual  void OnPropertyChanged(string propertyName)
        {
            OnPropertyChanged(DataBindingHelper.GetPropertyChangedEventArgs(propertyName));
        } 
        protected virtual  void OnPropertyChanged(PropertyChangedEventArgs e)
        {
            if (null != PropertyChanged)
            {
                PropertyChanged(this, e);
            }
        }
	}
	
    public class  WindowsRssNewsFeedCategory  : INewsFeedCategory, IDisposable {
		
        private  WindowsRssNewsFeedCategory() { ;} 
        public  WindowsRssNewsFeedCategory(IFeedFolder folder)
        {
            if (folder == null) throw new ArgumentNullException("folder");
            this.myfolder = folder;
        } 
        public  WindowsRssNewsFeedCategory(IFeedFolder folder, INewsFeedCategory category) : this(folder)
        {
            this.AnyAttr = category.AnyAttr;
            this.downloadenclosures = category.downloadenclosures;
            this.downloadenclosuresSpecified = category.downloadenclosuresSpecified;
            this.enclosurealert = category.enclosurealert;
            this.enclosurealertSpecified = category.enclosurealertSpecified;
            this.listviewlayout = category.listviewlayout;
            this.markitemsreadonexit = category.markitemsreadonexit;
            this.markitemsreadonexitSpecified = category.markitemsreadonexitSpecified;
            this.maxitemage = category.maxitemage;
            this.refreshrate = category.refreshrate;
            this.refreshrateSpecified = category.refreshrateSpecified;
            this.stylesheet = category.stylesheet;
        } 
        private  bool disposed = false; 
        private  IFeedFolder myfolder = null; 
        ~WindowsRssNewsFeedCategory()
        {
            Dispose(false);
        } 
        public  void Dispose()
        {
            if (!disposed)
            {
                Dispose(true);
            }
        } 
        public  void Dispose(bool disposing)
        {
            lock (this)
            {
                if (myfolder != null)
                {
                    Marshal.ReleaseComObject(myfolder);
                }
                System.GC.SuppressFinalize(this);
                disposed = true;
            }
        } 
        [XmlAttribute("mark-items-read-on-exit")] 
        public  bool markitemsreadonexit { get; set; } 
        [XmlIgnore] 
        public  bool markitemsreadonexitSpecified { get; set; } 
        [XmlAttribute("download-enclosures")] 
        public  bool downloadenclosures { get; set; } 
        [XmlIgnore] 
        public  bool downloadenclosuresSpecified { get; set; } 
        [XmlAttribute("enclosure-folder")] 
        public  string enclosurefolder { get { return null; } set { } } 
        [XmlAttribute("listview-layout")] 
        public  string listviewlayout { get; set; } 
        [XmlAttribute] 
        public  string stylesheet { get; set; } 
        [XmlAttribute("refresh-rate")] 
        public  int refreshrate { get; set; } 
        [XmlIgnore] 
        public  bool refreshrateSpecified { get; set; } 
        [XmlAttribute("max-item-age", DataType = "duration")] 
        public  string maxitemage { get; set; } 
        [XmlText] 
        public  string Value
        {
            get
            {
                return myfolder.Path;
            }
            set
            {
                if (!StringHelper.EmptyTrimOrNull(value))
                {
                    myfolder.Rename(value);
                }
            }
        } 
        [XmlIgnore] 
        public  INewsFeedCategory parent { get; set; } 
        [XmlAttribute("enclosure-alert"), DefaultValue(false)] 
        public  bool enclosurealert { get; set; } 
        [XmlIgnore] 
        public  bool enclosurealertSpecified { get; set; } 
        [XmlAnyAttribute] 
        public  XmlAttribute[] AnyAttr { get; set; } 
        public override  bool Equals(Object obj)
        {
            if (ReferenceEquals(this, obj))
            {
                return true;
            }
            WindowsRssNewsFeedCategory c = obj as WindowsRssNewsFeedCategory;
            if (c == null)
            {
                return false;
            }
            return this.myfolder.Path.Equals(c.myfolder.Path);
        } 
        public override  int GetHashCode()
        {
            return this.myfolder.Path.GetHashCode();
        }
	}

}
