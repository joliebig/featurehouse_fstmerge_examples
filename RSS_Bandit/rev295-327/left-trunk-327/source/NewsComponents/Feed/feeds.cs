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
    public interface INewsFeedCategory {
        bool markitemsreadonexit { get; set; }
        bool markitemsreadonexitSpecified { get; set; }
        bool downloadenclosures { get; set; }
        bool downloadenclosuresSpecified { get; set; }
        string enclosurefolder { get; set; }
        string listviewlayout { get; set; }
        string stylesheet { get; set; }
        int refreshrate { get; set; }
        bool refreshrateSpecified { get; set; }
        string maxitemage { get; set; }
        string Value { get; set; }
        INewsFeedCategory parent { get; set; }
        bool enclosurealert { get; set; }
        bool enclosurealertSpecified { get; set; }
        XmlAttribute[] AnyAttr { get; set; }
    }
    [XmlType(Namespace=NamespaceCore.Feeds_vCurrent)]
    public class category : INewsFeedCategory
    {
        private category(){;}
        public category(string name) {
            if (StringHelper.EmptyTrimOrNull(name))
                throw new ArgumentNullException("name");
            this.Value = name;
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
            string[] s = key.Split(NewsHandler.CategorySeparator.ToCharArray());
            if (s.Length != 1)
            {
                for (int i = 0; i < (s.Length - 1); i++)
                {
                    current += (i == 0 ? s[i] : NewsHandler.CategorySeparator + s[i]);
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
    public interface INewsFeed : INotifyPropertyChanged
    {
        string title { get; set; }
        string link { get; set; }
        string id { get; set; }
        bool lastretrievedSpecified { get; set; }
        DateTime lastretrieved { get; set; }
        int refreshrate { get; set; }
        bool refreshrateSpecified { get; set; }
        string etag { get; set; }
        string cacheurl { get; set; }
        string maxitemage { get; set; }
        List<string> storiesrecentlyviewed { get; set; }
        List<string> deletedstories { get; set; }
        DateTime lastmodified { get; set; }
        bool lastmodifiedSpecified { get; set; }
        string authUser { get; set; }
        Byte[] authPassword { get; set; }
        string listviewlayout { get; set; }
        string favicon { get; set; }
        bool downloadenclosures { get; set; }
        bool downloadenclosuresSpecified { get; set; }
        string enclosurefolder { get; set; }
        string stylesheet { get; set; }
        int causedExceptionCount { get; set; }
        bool causedException { get; set; }
        bool replaceitemsonrefresh { get; set; }
        bool replaceitemsonrefreshSpecified {get; set;}
        string newsaccount { get; set; }
        bool markitemsreadonexit { get; set; }
        bool markitemsreadonexitSpecified { get; set; }
        XmlElement[] Any { get; set; }
        XmlAttribute[] AnyAttr { get; set; }
        bool alertEnabled { get; set; }
        bool alertEnabledSpecified { get; set; }
        bool enclosurealert { get; set; }
        bool enclosurealertSpecified { get; set; }
        object Tag { get; set; }
        NewsHandler owner { get; set; }
        bool containsNewComments { get; set; }
        bool containsNewMessages { get; set; }
        string category { get; set; }
        void AddViewedStory(string storyid);
        void RemoveViewedStory(string storyid);
        void AddDeletedStory(string storyid);
        void RemoveDeletedStory(string storyid);
    }
    [XmlType(Namespace=NamespaceCore.Feeds_vCurrent)]
    public class NewsFeed : INewsFeed
    {
        private string _title = null;
        public string title {
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
        private string _link = null;
        [XmlElement(DataType = "anyURI")]
        public string link
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
        private string _id;
        [XmlAttribute]
        public string id
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
        private int _refreshrate;
        [XmlElement("refresh-rate")]
        public int refreshrate
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
        public bool refreshrateSpecified { get; set; }
        [XmlElement("last-retrieved")]
        public DateTime lastretrieved { get; set; }
        [XmlIgnore]
        public bool lastretrievedSpecified { get; set; }
        public string etag { get; set; }
        [XmlElement(DataType = "anyURI")]
        public string cacheurl { get; set; }
        private string _maxitemage;
        [XmlElement("max-item-age", DataType = "duration")]
        public string maxitemage
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
        private List<string> _storiesrecentlyviewed = new List<string>();
        [XmlArray(ElementName = "stories-recently-viewed", IsNullable = false)]
        [XmlArrayItem("story", Type = typeof(String), IsNullable = false)]
        public List<string> storiesrecentlyviewed
        {
            get{
                return _storiesrecentlyviewed;
            }
            set
            {
                _storiesrecentlyviewed = new List<string>(value);
            }
        }
        private List<string> _deletedstories = new List<string>();
        [XmlArray(ElementName = "deleted-stories", IsNullable = false)]
        [XmlArrayItem("story", Type = typeof (String), IsNullable = false)]
        public List<string> deletedstories
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
        public DateTime lastmodified { get; set; }
        [XmlIgnore]
        public bool lastmodifiedSpecified { get; set; }
        [XmlElement("auth-user")]
        public string authUser { get; set; }
        [XmlElement("auth-password", DataType = "base64Binary")]
        public Byte[] authPassword { get; set; }
        [XmlElement("listview-layout")]
        public string listviewlayout { get; set; }
        private string _favicon;
        public string favicon
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
        private bool _downloadenclosures;
        [XmlElement("download-enclosures")]
        public bool downloadenclosures
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
        public bool downloadenclosuresSpecified { get; set; }
        private string _enclosurefolder;
        [XmlElement("enclosure-folder")]
        public string enclosurefolder
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
        public bool replaceitemsonrefresh { get; set; }
        [XmlIgnore]
        public bool replaceitemsonrefreshSpecified {get; set;}
        private string _stylesheet;
        public string stylesheet
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
        public string newsaccount { get; set; }
        [XmlElement("mark-items-read-on-exit")]
        public bool markitemsreadonexit { get; set; }
        [XmlIgnore]
        public bool markitemsreadonexitSpecified { get; set; }
        [XmlAnyElement]
        public XmlElement[] Any { get; set; }
        [XmlAttribute("alert"), DefaultValue(false)]
        public bool alertEnabled { get; set; }
        [XmlIgnore]
        public bool alertEnabledSpecified { get; set; }
        [XmlAttribute("enclosure-alert"), DefaultValue(false)]
        public bool enclosurealert { get; set; }
        [XmlIgnore]
        public bool enclosurealertSpecified { get; set; }
        [XmlAttribute]
        public string category { get; set; }
        [XmlAnyAttribute]
        public XmlAttribute[] AnyAttr { get; set; }
        [XmlIgnore]
        public bool causedException
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
        public int causedExceptionCount { get; set; }
        [XmlIgnore]
        public object Tag { get; set; }
        private bool _containsNewMessages;
        [XmlIgnore]
        public bool containsNewMessages
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
        private bool _containsNewComments;
        [XmlIgnore]
        public bool containsNewComments
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
        public NewsHandler owner { get; set; }
        public static string GetElementWildCardValue(INewsFeed f, string namespaceUri, string localName)
        {
            foreach (XmlElement element in f.Any)
            {
                if (element.LocalName == localName && element.NamespaceURI == namespaceUri)
                    return element.InnerText;
            }
            return null;
        }
        public void AddViewedStory(string storyid) {
            if (!_storiesrecentlyviewed.Contains(storyid))
            {
                _storiesrecentlyviewed.Add(storyid);
                if (null != PropertyChanged)
                {
                    this.OnPropertyChanged(new CollectionChangedEventArgs("storiesrecentlyviewed", CollectionChangeAction.Add, storyid));
                }
            }
        }
        public void RemoveViewedStory(string storyid)
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
        public void AddDeletedStory(string storyid)
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
        public void RemoveDeletedStory(string storyid) {
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
