using System;
using System.Collections.Generic;
using System.Collections.Specialized;
using System.ComponentModel;
using System.Diagnostics;
using System.IO;
using System.Runtime.Serialization;
using System.Text;
using System.Xml;
using System.Xml.Serialization;
using NewsComponents.Collections;
using NewsComponents.Utils;
using RssBandit.AppServices.Core;
namespace NewsComponents.Feed
{
    [XmlType(Namespace=NamespaceCore.Feeds_vCurrent)]
    [XmlRoot("feeds", Namespace=NamespaceCore.Feeds_vCurrent, IsNullable=false)]
    public class feeds
    {
        [XmlElement("feed", Type = typeof (NewsFeed), IsNullable = false)]
        public List<NewsFeed> feed = new List<NewsFeed>();
        [XmlArrayItem("category", Type = typeof (category), IsNullable = false)]
        public List<category> categories = new List<category>();
        [XmlArray("listview-layouts")]
        [XmlArrayItem("listview-layout", Type = typeof (listviewLayout), IsNullable = false)]
        public List<listviewLayout> listviewLayouts = new List<listviewLayout>();
        [XmlArrayItem("server", Type = typeof (NntpServerDefinition), IsNullable = false)]
        [XmlArray(ElementName = "nntp-servers", IsNullable = false)]
        public List<NntpServerDefinition> nntpservers = new List<NntpServerDefinition>();
        [XmlArrayItem("identity", Type = typeof (UserIdentity), IsNullable = false)]
        [XmlArray(ElementName = "user-identities", IsNullable = false)]
        public List<UserIdentity> identities = new List<UserIdentity>();
        [XmlAttribute("refresh-rate")]
        public int refreshrate;
        [XmlIgnore]
        public bool refreshrateSpecified;
        [XmlAttribute("create-subfolders-for-enclosures"), DefaultValue(false)]
        public bool createsubfoldersforenclosures;
        [XmlIgnore]
        public bool createsubfoldersforenclosuresSpecified;
        [XmlAttribute("download-enclosures")]
        public bool downloadenclosures;
        [XmlIgnore]
        public bool downloadenclosuresSpecified;
        [XmlAttribute("enclosure-cache-size-in-MB")]
        public int enclosurecachesize;
        [XmlIgnore]
        public bool enclosurecachesizeSpecified;
        [XmlAttribute("num-enclosures-to-download-on-new-feed")]
        public int numtodownloadonnewfeed;
        [XmlIgnore]
        public bool numtodownloadonnewfeedSpecified;
        [XmlAttribute("enclosure-alert")]
        public bool enclosurealert;
        [XmlIgnore]
        public bool enclosurealertSpecified;
        [XmlAttribute("mark-items-read-on-exit")]
        public bool markitemsreadonexit;
        [XmlIgnore]
        public bool markitemsreadonexitSpecified;
        [XmlAttribute("enclosure-folder")]
        public string enclosurefolder;
        [XmlAttribute("podcast-folder")]
        public string podcastfolder;
        [XmlAttribute("podcast-file-exts")]
        public string podcastfileexts;
        [XmlAttribute("listview-layout")]
        public string listviewlayout;
        [XmlAttribute("max-item-age", DataType="duration")]
        public string maxitemage;
        [XmlAttribute]
        public string stylesheet;
        [XmlAnyAttribute]
        public XmlAttribute[] AnyAttr;
    }
    [XmlType(Namespace=NamespaceCore.Feeds_vCurrent)]
    public class listviewLayout
    {
        public listviewLayout()
        {
        }
        public listviewLayout(string id, FeedColumnLayout layout)
        {
            ID = id;
            FeedColumnLayout = layout;
        }
        [XmlAttribute]
        public string ID;
        [XmlAnyAttribute]
        public XmlAttribute[] AnyAttr;
        [XmlElement]
            public FeedColumnLayout FeedColumnLayout;
    }
    public interface IFeedColumnLayout
    {
        string SortByColumn { get; }
        SortOrder SortOrder { get; }
        string ArrangeByColumn { get; }
        IList<string> Columns { get; }
        IList<int> ColumnWidths { get; }
    }
    [Serializable]
    [XmlType(Namespace=NamespaceCore.Feeds_vCurrent)]
    public class FeedColumnLayout : IFeedColumnLayout, ICloneable, ISerializable
    {
        private string _sortByColumn;
        private SortOrder _sortOrder;
        private LayoutType _layoutType;
        private string _arrangeByColumn;
        internal List<string> _columns;
        internal List<int> _columnWidths;
        public FeedColumnLayout() :
            this(null, null, null, SortOrder.None, LayoutType.IndividualLayout, null)
        {
        }
        public FeedColumnLayout(IEnumerable<string> columns, IEnumerable<int> columnWidths, string sortByColumn,
                                SortOrder sortOrder, LayoutType layoutType) :
                                    this(columns, columnWidths, sortByColumn, sortOrder, layoutType, null)
        {
        }
        public FeedColumnLayout(IEnumerable<string> columns, IEnumerable<int> columnWidths, string sortByColumn,
                                SortOrder sortOrder, LayoutType layoutType, string arrangeByColumn)
        {
            if (columns != null)
                _columns = new List<string>(columns);
            else
                _columns = new List<string>();
            if (columnWidths != null)
                _columnWidths = new List<int>(columnWidths);
            else
                _columnWidths = new List<int>();
            _sortOrder = SortOrder.None;
            if (sortByColumn != null && _columns.IndexOf(sortByColumn) >= 0)
            {
                _sortByColumn = sortByColumn;
                _sortOrder = sortOrder;
            }
            if (arrangeByColumn != null && _columns.IndexOf(arrangeByColumn) >= 0)
            {
                _arrangeByColumn = arrangeByColumn;
            }
            _layoutType = layoutType;
        }
        public static FeedColumnLayout CreateFromXML(string xmlString)
        {
            if (xmlString != null && xmlString.Length > 0)
            {
                XmlSerializer formatter = XmlHelper.SerializerCache.GetSerializer(typeof (FeedColumnLayout));
                StringReader reader = new StringReader(xmlString);
                return (FeedColumnLayout) formatter.Deserialize(reader);
            }
            return null;
        }
        public static string SaveAsXML(FeedColumnLayout layout)
        {
            if (layout == null)
                return null;
            try
            {
                XmlSerializer formatter = XmlHelper.SerializerCache.GetSerializer(typeof (FeedColumnLayout));
                StringWriter writer = new StringWriter();
                formatter.Serialize(writer, layout);
                return writer.ToString();
            }
            catch (Exception ex)
            {
                Trace.WriteLine("SaveAsXML() failed.", ex.Message);
            }
            return null;
        }
        public LayoutType LayoutType
        {
            get
            {
                return _layoutType;
            }
            set
            {
                _layoutType = value;
            }
        }
        public string SortByColumn
        {
            get
            {
                return _sortByColumn;
            }
            set
            {
                _sortByColumn = value;
            }
        }
        public SortOrder SortOrder
        {
            get
            {
                return _sortOrder;
            }
            set
            {
                _sortOrder = value;
            }
        }
        public string ArrangeByColumn
        {
            get
            {
                return _arrangeByColumn;
            }
            set
            {
                _arrangeByColumn = value;
            }
        }
        [XmlIgnore]
        public IList<string> Columns
        {
            get
            {
                return _columns;
            }
            set
            {
                if (value != null)
                    _columns = new List<string>(value);
                else
                    _columns = new List<string>();
            }
        }
        [XmlIgnore]
        public IList<int> ColumnWidths
        {
            get
            {
                return _columnWidths;
            }
            set
            {
                if (value != null)
                    _columnWidths = new List<int>(value);
                else
                    _columnWidths = new List<int>();
            }
        }
        [XmlArrayItem(typeof (string))]
        public List<string> ColumnList
        {
            get
            {
                return _columns;
            }
            set
            {
                if (value != null)
                    _columns = value;
                else
                    _columns = new List<string>();
            }
        }
        [XmlArrayItem(typeof (int))]
        public List<int> ColumnWidthList
        {
            get
            {
                return _columnWidths;
            }
            set
            {
                if (value != null)
                    _columnWidths = value;
                else
                    _columnWidths = new List<int>();
            }
        }
        public override bool Equals(Object obj)
        {
            return Equals(obj, false);
        }
        public bool Equals(object obj, bool ignoreColumnWidths)
        {
            if (obj == null)
                return false;
            FeedColumnLayout o = obj as FeedColumnLayout;
            if (o == null)
                return false;
            if (SortOrder != o.SortOrder)
                return false;
            if (SortByColumn != o.SortByColumn)
                return false;
            if (_columns == null && o._columns == null)
                return true;
            if (_columns == null || o._columns == null)
                return false;
            if (_columns.Count != o._columns.Count)
                return false;
            if (ignoreColumnWidths)
            {
                for (int i = 0; i < _columns.Count; i++)
                {
                    if (String.Compare(_columns[i], o._columns[i]) != 0)
                        return false;
                }
            }
            else
            {
                for (int i = 0; i < _columns.Count; i++)
                {
                    if (String.Compare(_columns[i], o._columns[i]) != 0 ||
                        _columnWidths[i] != o._columnWidths[i])
                        return false;
                }
            }
            return true;
        }
        public bool IsSimilarFeedLayout(FeedColumnLayout layout)
        {
            if (layout == null)
                return false;
            if ((_layoutType == LayoutType.IndividualLayout || _layoutType == LayoutType.GlobalFeedLayout) &&
                (layout._layoutType == LayoutType.IndividualLayout || layout._layoutType == LayoutType.GlobalFeedLayout))
                return Equals(layout, true);
            return false;
        }
        public bool IsSimilarCategoryLayout(FeedColumnLayout layout)
        {
            if (layout == null)
                return false;
            if ((_layoutType == LayoutType.IndividualLayout || _layoutType == LayoutType.GlobalCategoryLayout) &&
                (layout._layoutType == LayoutType.IndividualLayout || layout._layoutType == LayoutType.GlobalFeedLayout))
                return Equals(layout, true);
            return false;
        }
        public override int GetHashCode()
        {
            StringBuilder sb = new StringBuilder();
            if (_columns != null && _columns.Count > 0)
            {
                for (int i = 0; i < _columns.Count; i++)
                {
                    sb.AppendFormat("{0};", _columns[i]);
                }
            }
            if (_columnWidths != null && _columnWidths.Count > 0)
            {
                for (int i = 0; i < _columnWidths.Count; i++)
                {
                    sb.AppendFormat("{0};", _columnWidths[i]);
                }
            }
            sb.AppendFormat("{0};", _sortByColumn);
            sb.AppendFormat("{0};", _sortOrder);
            sb.AppendFormat("{0};", _arrangeByColumn);
            sb.AppendFormat("{0};", _layoutType);
            return sb.ToString().GetHashCode();
        }
        public object Clone()
        {
            return
                new FeedColumnLayout(_columns, _columnWidths, _sortByColumn, _sortOrder, _layoutType, _arrangeByColumn);
        }
        protected FeedColumnLayout(SerializationInfo info, StreamingContext context)
        {
            _columns = (List<string>) info.GetValue("ColumnList", typeof (List<string>));
            _columnWidths = (List<int>) info.GetValue("ColumnWidthList", typeof (List<int>));
            _sortByColumn = info.GetString("SortByColumn");
            _sortOrder = (SortOrder) info.GetValue("SortOrder", typeof (SortOrder));
            _arrangeByColumn = info.GetString("ArrangeByColumn");
        }
        public void GetObjectData(SerializationInfo info, StreamingContext context)
        {
            info.AddValue("version", 1);
            info.AddValue("ColumnList", _columns);
            info.AddValue("ColumnWidthList", _columnWidths);
            info.AddValue("SortByColumn", _sortByColumn);
            info.AddValue("SortOrder", _sortOrder);
            info.AddValue("ArrangeByColumn", _arrangeByColumn);
        }
    }
    public interface INewsFeedCategory : ISharedProperty
 {
        string Value { get; set; }
        INewsFeedCategory parent { get; set; }
        XmlAttribute[] AnyAttr { get; set; }
    }
    [XmlType(Namespace=NamespaceCore.Feeds_vCurrent)]
    public class category : INewsFeedCategory
    {
        protected category(){;}
        public category(string name) {
            if (StringHelper.EmptyTrimOrNull(name))
                throw new ArgumentNullException("name");
            this.Value = name;
        }
        public category(INewsFeedCategory categorytoclone)
        {
            if (categorytoclone != null)
            {
                this.AnyAttr = categorytoclone.AnyAttr;
                this.downloadenclosures = categorytoclone.downloadenclosures;
                this.downloadenclosuresSpecified = categorytoclone.downloadenclosuresSpecified;
                this.enclosurealert = categorytoclone.enclosurealert;
                this.enclosurealertSpecified = categorytoclone.enclosurealertSpecified;
                this.enclosurefolder = categorytoclone.enclosurefolder;
                this.listviewlayout = categorytoclone.listviewlayout;
                this.markitemsreadonexit = categorytoclone.markitemsreadonexit;
                this.markitemsreadonexitSpecified = categorytoclone.markitemsreadonexitSpecified;
                this.maxitemage = categorytoclone.maxitemage;
                this.refreshrate = categorytoclone.refreshrate;
                this.refreshrateSpecified = categorytoclone.refreshrateSpecified;
                this.stylesheet = categorytoclone.stylesheet;
                this.Value = categorytoclone.Value;
            }
        }
        [XmlAttribute("mark-items-read-on-exit")]
        public bool markitemsreadonexit { get; set; }
        [XmlIgnore]
        public bool markitemsreadonexitSpecified { get; set; }
        [XmlAttribute("download-enclosures")]
        public bool downloadenclosures { get; set; }
        [XmlIgnore]
        public bool downloadenclosuresSpecified { get; set; }
        [XmlAttribute("enclosure-folder")]
        public string enclosurefolder { get; set; }
        [XmlAttribute("listview-layout")]
        public string listviewlayout { get; set; }
        [XmlAttribute]
        public string stylesheet { get; set; }
        [XmlAttribute("refresh-rate")]
        public int refreshrate { get; set; }
        [XmlIgnore]
        public bool refreshrateSpecified { get; set; }
        [XmlAttribute("max-item-age", DataType="duration")]
        public string maxitemage { get; set; }
        [XmlText]
        public string Value { get; set; }
        [XmlIgnore]
        public INewsFeedCategory parent { get; set; }
        [XmlAttribute("enclosure-alert"), DefaultValue(false)]
        public bool enclosurealert { get; set; }
        [XmlIgnore]
        public bool enclosurealertSpecified { get; set; }
        [XmlAnyAttribute]
        public XmlAttribute[] AnyAttr { get; set; }
        public static List<string> GetAncestors(string key)
        {
            List<string> list = new List<string>();
            string current = String.Empty;
            string[] s = key.Split(FeedSource.CategorySeparator.ToCharArray());
            if (s.Length != 1)
            {
                for (int i = 0; i < (s.Length - 1); i++)
                {
                    current += (i == 0 ? s[i] : FeedSource.CategorySeparator + s[i]);
                    list.Add(current);
                }
            }
            return list;
        }
        public override bool Equals(Object obj)
        {
            if (ReferenceEquals(this, obj))
            {
                return true;
            }
            category c = obj as category;
            if (c == null)
            {
                return false;
            }
            if (Value.Equals(c.Value))
            {
                return true;
            }
            return false;
        }
        public override int GetHashCode()
        {
            return Value.GetHashCode();
        }
    }
    [XmlType(Namespace=NamespaceCore.Feeds_vCurrent)]
    [XmlInclude(typeof(GoogleReaderNewsFeed))]
    public class NewsFeed : INewsFeed
    {
        public NewsFeed() { ; }
         public NewsFeed(INewsFeed feedtoclone)
        {
            if (feedtoclone != null)
            {
                this.link = feedtoclone.link;
                this.title = feedtoclone.title;
                this.category = feedtoclone.category;
                this.cacheurl = cacheurl;
                this.storiesrecentlyviewed = new List<string>(feedtoclone.storiesrecentlyviewed);
                this.deletedstories = new List<string>(feedtoclone.deletedstories);
                this.id = feedtoclone.id;
                this.lastretrieved = feedtoclone.lastretrieved;
                this.lastretrievedSpecified = feedtoclone.lastretrievedSpecified;
                this.lastmodified = feedtoclone.lastmodified;
                this.lastmodifiedSpecified = feedtoclone.lastmodifiedSpecified;
                this.authUser = feedtoclone.authUser;
                this.authPassword = feedtoclone.authPassword;
                this.downloadenclosures = feedtoclone.downloadenclosures;
                this.downloadenclosuresSpecified = feedtoclone.downloadenclosuresSpecified;
                this.enclosurefolder = feedtoclone.enclosurefolder;
                this.replaceitemsonrefresh = feedtoclone.replaceitemsonrefresh;
                this.replaceitemsonrefreshSpecified = feedtoclone.replaceitemsonrefreshSpecified;
                this.refreshrate = feedtoclone.refreshrate;
                this.refreshrateSpecified = feedtoclone.refreshrateSpecified;
                this.maxitemage = feedtoclone.maxitemage;
                this.etag = feedtoclone.etag;
                this.markitemsreadonexit = feedtoclone.markitemsreadonexit;
                this.markitemsreadonexitSpecified = feedtoclone.markitemsreadonexitSpecified;
                this.listviewlayout = feedtoclone.listviewlayout;
                this.favicon = feedtoclone.favicon;
                this.stylesheet = feedtoclone.stylesheet;
                this.enclosurealert = feedtoclone.enclosurealert;
                this.enclosurealertSpecified = feedtoclone.enclosurealertSpecified;
                this.alertEnabled = feedtoclone.alertEnabled;
                this.alertEnabledSpecified = feedtoclone.alertEnabledSpecified;
                this.Any = feedtoclone.Any;
                this.AnyAttr = feedtoclone.AnyAttr;
            }
        }
        protected string _title = null;
        public virtual string title {
            get
            {
                return _title;
            }
            set
            {
                if (String.IsNullOrEmpty(_title) || !_title.Equals(value))
                {
                    _title = value;
                    this.OnPropertyChanged("title");
                }
            }
        }
        protected string _link = null;
        [XmlElement(DataType = "anyURI")]
        public virtual string link
        {
            get
            {
                return _link;
            }
            set
            {
                if (String.IsNullOrEmpty(_link) || !_link.Equals(value))
                {
                    _link = value;
                    this.OnPropertyChanged("link");
                }
            }
        }
        protected string _id;
        [XmlAttribute]
        public virtual string id
        {
            get
            {
                if (_id == null || _id.Length == 0)
                    _id = Guid.NewGuid().ToString("N");
                return _id;
            }
            set
            {
                _id = value;
            }
        }
        protected int _refreshrate;
        [XmlElement("refresh-rate")]
        public virtual int refreshrate
        {
            get
            {
                return _refreshrate;
            }
            set
            {
                if (!_refreshrate.Equals(value))
                {
                    _refreshrate = value;
                    this.OnPropertyChanged("refreshrate");
                }
            }
        }
        [XmlIgnore]
        public virtual bool refreshrateSpecified { get; set; }
        [XmlElement("last-retrieved")]
        public virtual DateTime lastretrieved { get; set; }
        [XmlIgnore]
        public virtual bool lastretrievedSpecified { get; set; }
        public virtual string etag { get; set; }
        [XmlElement(DataType = "anyURI")]
        public virtual string cacheurl { get; set; }
        protected string _maxitemage;
        [XmlElement("max-item-age", DataType = "duration")]
        public virtual string maxitemage
        {
            get
            {
                return _maxitemage;
            }
            set
            {
                if (String.IsNullOrEmpty(_maxitemage) || !_maxitemage.Equals(value))
                {
                    _maxitemage = value;
                    this.OnPropertyChanged("maxitemage");
                }
            }
        }
        protected List<string> _storiesrecentlyviewed = new List<string>();
        [XmlArray(ElementName = "stories-recently-viewed", IsNullable = false)]
        [XmlArrayItem("story", Type = typeof(String), IsNullable = false)]
        public virtual List<string> storiesrecentlyviewed
        {
            get{
                return _storiesrecentlyviewed;
            }
            set
            {
                _storiesrecentlyviewed = new List<string>(value);
            }
        }
        protected List<string> _deletedstories = new List<string>();
        [XmlArray(ElementName = "deleted-stories", IsNullable = false)]
        [XmlArrayItem("story", Type = typeof (String), IsNullable = false)]
        public virtual List<string> deletedstories
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
        public virtual DateTime lastmodified { get; set; }
        [XmlIgnore]
        public virtual bool lastmodifiedSpecified { get; set; }
        [XmlElement("auth-user")]
        public virtual string authUser { get; set; }
        [XmlElement("auth-password", DataType = "base64Binary")]
        public virtual Byte[] authPassword { get; set; }
        [XmlElement("listview-layout")]
        public virtual string listviewlayout { get; set; }
        protected string _favicon;
        public virtual string favicon
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
        protected bool _downloadenclosures;
        [XmlElement("download-enclosures")]
        public virtual bool downloadenclosures
        {
            get
            {
                return _downloadenclosures;
            }
            set
            {
                if (!_downloadenclosures.Equals(value))
                {
                    _downloadenclosures = value;
                    this.OnPropertyChanged("downloadenclosures");
                }
            }
        }
        [XmlIgnore]
        public virtual bool downloadenclosuresSpecified { get; set; }
        protected string _enclosurefolder;
        [XmlElement("enclosure-folder")]
        public virtual string enclosurefolder
        {
            get
            {
                return _enclosurefolder;
            }
            set
            {
                if (String.IsNullOrEmpty(_enclosurefolder) || !_enclosurefolder.Equals(value))
                {
                    _enclosurefolder = value;
                    this.OnPropertyChanged("enclosurefolder");
                }
            }
        }
        [XmlAttribute("replace-items-on-refresh")]
        public virtual bool replaceitemsonrefresh { get; set; }
        [XmlIgnore]
        public virtual bool replaceitemsonrefreshSpecified {get; set;}
        protected string _stylesheet;
        public virtual string stylesheet
        {
            get
            {
                return _stylesheet;
            }
            set
            {
                if (String.IsNullOrEmpty(_stylesheet) || !_stylesheet.Equals(value))
                {
                    _stylesheet = value;
                    this.OnPropertyChanged("stylesheet");
                }
            }
        }
        [XmlElement("news-account")]
        public virtual string newsaccount { get; set; }
        [XmlElement("mark-items-read-on-exit")]
        public virtual bool markitemsreadonexit { get; set; }
        [XmlIgnore]
        public virtual bool markitemsreadonexitSpecified { get; set; }
        [XmlAnyElement]
        public virtual XmlElement[] Any { get; set; }
        [XmlAttribute("alert"), DefaultValue(false)]
        public virtual bool alertEnabled { get; set; }
        [XmlIgnore]
        public virtual bool alertEnabledSpecified { get; set; }
        [XmlAttribute("enclosure-alert"), DefaultValue(false)]
        public virtual bool enclosurealert { get; set; }
        [XmlIgnore]
        public virtual bool enclosurealertSpecified { get; set; }
        [XmlAttribute]
        public virtual string category {
            get
            {
                if (this.categories != null && this.categories.Count > 0)
                {
                    return categories[0];
                }
                else
                {
                    return null;
                }
            }
            set
            {
                this.categories.Clear();
                if (!StringHelper.EmptyTrimOrNull(value))
                {
                    this.categories.Add(value);
                }
            }
        }
        protected List<string> _categories = new List<string>();
        [XmlArray(ElementName = "categories", IsNullable = false)]
        [XmlArrayItem("category", Type = typeof(String), IsNullable = false)]
        public virtual List<string> categories
        {
            get { return _categories; }
            set
            {
                if (value != null) { categories = value; }
            }
        }
        [XmlAnyAttribute]
        public virtual XmlAttribute[] AnyAttr { get; set; }
        [XmlIgnore]
        public virtual bool causedException
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
        public virtual int causedExceptionCount { get; set; }
        [XmlIgnore]
        public virtual object Tag { get; set; }
        protected bool _containsNewMessages;
        [XmlIgnore]
        public virtual bool containsNewMessages
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
        protected bool _containsNewComments;
        [XmlIgnore]
        public virtual bool containsNewComments
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
        public virtual object owner { get; set; }
        public static string GetElementWildCardValue(INewsFeed f, string namespaceUri, string localName)
        {
            foreach (XmlElement element in f.Any)
            {
                if (element.LocalName == localName && element.NamespaceURI == namespaceUri)
                    return element.InnerText;
            }
            return null;
        }
        public virtual void AddViewedStory(string storyid) {
            if (!_storiesrecentlyviewed.Contains(storyid))
            {
                _storiesrecentlyviewed.Add(storyid);
                if (null != PropertyChanged)
                {
                    this.OnPropertyChanged(new CollectionChangedEventArgs("storiesrecentlyviewed", CollectionChangeAction.Add, storyid));
                }
            }
        }
        public virtual void RemoveViewedStory(string storyid)
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
        public virtual void AddCategory(string name)
        {
            if (!_categories.Contains(name))
            {
                _categories.Add(name);
                if (null != PropertyChanged)
                {
                    this.OnPropertyChanged(new CollectionChangedEventArgs("categories", CollectionChangeAction.Add, name));
                }
            }
        }
        public virtual void RemoveCategory(string name)
        {
            if (_categories.Contains(name))
            {
                _categories.Remove(name);
                if (null != PropertyChanged)
                {
                    this.OnPropertyChanged(new CollectionChangedEventArgs("categories", CollectionChangeAction.Remove, name));
                }
            }
        }
        public virtual void AddDeletedStory(string storyid)
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
        public virtual void RemoveDeletedStory(string storyid) {
            if (_deletedstories.Contains(storyid))
            {
                _deletedstories.Remove(storyid);
                if (null != PropertyChanged)
                {
                    this.OnPropertyChanged(new CollectionChangedEventArgs("deletedstories", CollectionChangeAction.Remove, storyid));
                }
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
        public override bool Equals(Object obj)
        {
            if (ReferenceEquals(this, obj))
            {
                return true;
            }
            NewsFeed feed = obj as NewsFeed;
            if (feed == null)
            {
                return false;
            }
            if (link.Equals(feed.link))
            {
                return true;
            }
            return false;
        }
        public override int GetHashCode()
        {
            return link.GetHashCode();
        }
    }
    [XmlType(Namespace=NamespaceCore.Feeds_vCurrent)]
    public class UserIdentity : IUserIdentity, ICloneable
    {
        private string name;
        [XmlAttribute("name")]
        public string Name
        {
            get
            {
                return name;
            }
            set
            {
                name = value;
            }
        }
        private string realName;
        [XmlElement("real-name")]
        public string RealName
        {
            get
            {
                return realName;
            }
            set
            {
                realName = value;
            }
        }
        private string organization;
        [XmlElement("organization")]
        public string Organization
        {
            get
            {
                return organization;
            }
            set
            {
                organization = value;
            }
        }
        private string mailAddress;
        [XmlElement("mail-address")]
        public string MailAddress
        {
            get
            {
                return mailAddress;
            }
            set
            {
                mailAddress = value;
            }
        }
        private string responseAddress;
        [XmlElement("response-address")]
        public string ResponseAddress
        {
            get
            {
                return responseAddress;
            }
            set
            {
                responseAddress = value;
            }
        }
        private string referrerUrl;
        [XmlElement("referrer-url")]
        public string ReferrerUrl
        {
            get
            {
                return referrerUrl;
            }
            set
            {
                referrerUrl = value;
            }
        }
        private string signature;
        [XmlElement("signature")]
        public string Signature
        {
            get
            {
                return signature;
            }
            set
            {
                signature = value;
            }
        }
        [XmlAnyAttribute]
        public XmlAttribute[] AnyAttr;
        [XmlAnyElement]
        public XmlElement[] Any;
        public object Clone()
        {
            return MemberwiseClone();
        }
    }
    [XmlType(Namespace=NamespaceCore.Feeds_vCurrent)]
    public class NntpServerDefinition : INntpServerDefinition, ICloneable
    {
        private string name;
        [XmlAttribute("name")]
        public string Name
        {
            get
            {
                return name;
            }
            set
            {
                name = value;
            }
        }
        private string defaultIdentity;
        [XmlElement("default-identity")]
        public string DefaultIdentity
        {
            get
            {
                return defaultIdentity;
            }
            set
            {
                defaultIdentity = value;
            }
        }
        private bool preventDownloadOnRefresh;
        [XmlElement("prevent-download")]
        public bool PreventDownloadOnRefresh
        {
            get
            {
                return preventDownloadOnRefresh;
            }
            set
            {
                preventDownloadOnRefresh = value;
            }
        }
        [XmlIgnore]
        public bool PreventDownloadOnRefreshSpecified;
        private string server;
        [XmlElement("server-address")]
        public string Server
        {
            get
            {
                return server;
            }
            set
            {
                server = value;
            }
        }
        private string authUser;
        [XmlElement("auth-user")]
        public string AuthUser
        {
            get
            {
                return authUser;
            }
            set
            {
                authUser = value;
            }
        }
        private Byte[] authPassword;
        [XmlElement("auth-password", DataType="base64Binary")]
        public Byte[] AuthPassword
        {
            get
            {
                return authPassword;
            }
            set
            {
                authPassword = value;
            }
        }
        private bool useSecurePasswordAuthentication;
        [XmlElement("auth-use-spa")]
        public bool UseSecurePasswordAuthentication
        {
            get
            {
                return useSecurePasswordAuthentication;
            }
            set
            {
                useSecurePasswordAuthentication = value;
            }
        }
        [XmlIgnore]
        public bool UseSecurePasswordAuthenticationSpecified;
        private int port;
        [XmlElement("port-number")]
        public int Port
        {
            get
            {
                return port;
            }
            set
            {
                port = value;
            }
        }
        [XmlIgnore]
        public bool PortSpecified;
        private bool useSSL;
        [XmlElement("use-ssl")]
        public bool UseSSL
        {
            get
            {
                return useSSL;
            }
            set
            {
                useSSL = value;
            }
        }
        [XmlIgnore]
        public bool UseSSLSpecified;
        private int timeout;
        [XmlElement("timeout")]
        public int Timeout
        {
            get
            {
                return timeout;
            }
            set
            {
                timeout = value;
            }
        }
        [XmlIgnore]
        public bool TimeoutSpecified;
        [XmlAnyAttribute]
        public XmlAttribute[] AnyAttr;
        [XmlAnyElement]
        public XmlElement[] Any;
        public object Clone()
        {
            return MemberwiseClone();
        }
    }
}
