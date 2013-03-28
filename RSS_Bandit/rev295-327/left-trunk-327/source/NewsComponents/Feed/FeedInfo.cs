using System;
using System.Collections;
using System.Collections.Generic;
using System.Collections.Specialized;
using System.IO;
using System.Text;
using System.Xml;
using NewsComponents;
using NewsComponents.Utils;
namespace NewsComponents.Feed
{
 public class FeedInfo : FeedDetailsInternal, ISizeInfo
 {
  public static readonly FeedInfo Empty = new FeedInfo(String.Empty, String.Empty, new List<NewsItem>(), String.Empty,String.Empty,String.Empty, new Dictionary<XmlQualifiedName, string>(0), String.Empty);
  internal string id;
  public string Id{
   get { return id; }
   set { id = value; }
  }
  internal string feedLocation;
  public string FeedLocation {
   get { return feedLocation; }
   set { feedLocation = value; }
  }
  internal List<NewsItem> itemsList;
  public List<NewsItem> ItemsList {
   get { return itemsList; }
   set { itemsList = value; }
  }
        internal Dictionary<XmlQualifiedName, string> optionalElements;
  public FeedInfo(string id, string feedLocation, IList<NewsItem> itemsList){
   this.id = id;
   this.feedLocation = feedLocation;
            if(itemsList != null){
       this.itemsList = new List<NewsItem>(itemsList);
            }
  }
        public FeedInfo(string id, string feedLocation, IList<NewsItem> itemsList, string title, string link, string description)
   :this(id, feedLocation, itemsList, title, link, description, new Dictionary<XmlQualifiedName, string>(), String.Empty){
  }
        public FeedInfo(string id, string feedLocation, IList<NewsItem> itemsList, string title, string link, string description, IDictionary<XmlQualifiedName, string> optionalElements, string language)
        {
   this.id = id;
   this.feedLocation = feedLocation;
   this.itemsList = new List<NewsItem>(itemsList);
   this.title = title;
   this.link = link;
   this.description = description;
   this.optionalElements = new Dictionary<XmlQualifiedName,string>(optionalElements);
   this.language = language;
   if(RssHelper.IsNntpUrl(link)){
    this.type = FeedType.Nntp;
   }else{
    this.type = FeedType.Rss;
   }
  }
  internal string title;
  public string Title{
   get { return title; }
  }
  internal string description;
  public string Description{
   get { return description; }
  }
  internal string link;
  public string Link{
   get { return link; }
  }
  internal string language;
  public string Language{
   get { return language; }
  }
        IDictionary IFeedDetails.OptionalElements {
            get { return this.optionalElements; }
        }
  Dictionary<XmlQualifiedName, string> FeedDetailsInternal.OptionalElements{
   get{ return this.optionalElements; }
  }
  internal FeedType type;
  public FeedType Type { get{ return this.type; } }
  public void WriteTo(XmlWriter writer){
   this.WriteTo(writer, NewsItemSerializationFormat.RssFeed, true);
  }
  public void WriteTo(XmlWriter writer, bool noDescriptions){
   this.WriteTo(writer, NewsItemSerializationFormat.RssFeed, true, noDescriptions);
  }
  public void WriteTo(XmlWriter writer, NewsItemSerializationFormat format){
   this.WriteTo(writer, format, true, false);
  }
  public void WriteTo(XmlWriter writer, NewsItemSerializationFormat format, bool useGMTDate){
   this.WriteTo(writer, format, useGMTDate, false);
  }
  public void WriteTo(XmlWriter writer, NewsItemSerializationFormat format, bool useGMTDate, bool noDescriptions){
   if(format == NewsItemSerializationFormat.NewsPaper){
    writer.WriteStartElement("newspaper");
    writer.WriteAttributeString("type", "channel");
    writer.WriteElementString("title", this.title);
   }else if(format != NewsItemSerializationFormat.Channel){
    writer.WriteStartElement("rss");
    writer.WriteAttributeString("version", "2.0");
   }
   writer.WriteStartElement("channel");
   writer.WriteElementString("title", this.Title);
   writer.WriteElementString("link", this.Link);
   writer.WriteElementString("description", this.Description);
   foreach(string s in this.optionalElements.Values){
    writer.WriteRaw(s);
   }
   foreach(NewsItem item in this.itemsList){
    writer.WriteRaw(item.ToString(NewsItemSerializationFormat.RssItem, useGMTDate, noDescriptions));
   }
   writer.WriteEndElement();
   if(format != NewsItemSerializationFormat.Channel){
    writer.WriteEndElement();
   }
  }
  public string ToString(NewsItemSerializationFormat format){
   return this.ToString(format, true);
  }
  public string ToString(NewsItemSerializationFormat format, bool useGMTDate){
   StringBuilder sb = new StringBuilder("");
   XmlTextWriter writer = new XmlTextWriter(new StringWriter(sb));
   this.WriteTo(writer, format, useGMTDate);
   writer.Flush();
   writer.Close();
   return sb.ToString();
  }
  public override string ToString(){
    return this.ToString(NewsItemSerializationFormat.RssFeed);
  }
  public FeedInfo Clone(bool includeNewsItems)
  {
   FeedInfo toReturn = new FeedInfo(this.id, this.feedLocation,
                (includeNewsItems ? new List<NewsItem>(this.itemsList) : new List<NewsItem>()),
    this.title, this.link, this.description,
    new Dictionary<XmlQualifiedName, string>(this.optionalElements), this.language);
   return toReturn;
  }
  public object Clone()
  {
   return this.Clone(true);
  }
  public void WriteItemContents(BinaryReader reader, BinaryWriter writer){
   StringCollection inMemoryDescriptions = new StringCollection();
   byte[] content;
   int count;
   foreach(NewsItem item in this.itemsList){
    if(item.HasContent){
     writer.Write(item.Id);
     writer.Write(item.GetContent().Length);
     writer.Write(item.GetContent());
     inMemoryDescriptions.Add(item.Id);
    }
   }
   if(reader != null){
    id = reader.ReadString();
    while(!id.Equals(FileHelper.EndOfBinaryFileMarker) && !string.IsNullOrEmpty(id)){
     count = reader.ReadInt32();
     content = reader.ReadBytes(count);
     if(!inMemoryDescriptions.Contains(id) && this.ContainsItemWithId(id)){
      writer.Write(id);
      writer.Write(count);
      writer.Write(content);
     }
     id = reader.ReadString();
    }
   }
  }
  private bool ContainsItemWithId(string id){
   foreach(NewsItem item in this.ItemsList.ToArray()){
    if(item.Id.Equals(id)){
     return true;
    }
   }
   return false;
  }
  public int GetSize() {
   int iSize = StringHelper.SizeOfStr(this.link);
   iSize += StringHelper.SizeOfStr(this.title);
   iSize += StringHelper.SizeOfStr(this.title);
   iSize += StringHelper.SizeOfStr(this.description);
   return iSize;
  }
  public string GetSizeDetails() {
   return this.GetSize().ToString();
  }
 }
 public class FeedInfoList: IEnumerable, ICollection
 {
  private ArrayList feeds = new ArrayList();
  private string title;
  public FeedInfoList(string title){
   this.title = title;
  }
  public string Title{
   get { return this.title;}
  }
  public IList<NewsItem> GetAllNewsItems(){
            List<NewsItem> allItems = new List<NewsItem>();
   foreach(FeedInfo fi in this.feeds){
    allItems.InsertRange(0, fi.ItemsList);
   }
   return allItems;
  }
  public int Add(FeedInfo feed){
   return this.feeds.Add(feed);
  }
  public void AddRange(ICollection feedCollection)
  {
   this.feeds.AddRange(feedCollection);
  }
  public void Clear(){
   this.feeds.Clear();
  }
  public int NewsItemCount {
   get {
    int count = 0;
    foreach(FeedInfo fi in this.feeds){
     count += fi.ItemsList.Count;
    }
    return count;
   }
  }
  public int Count {
   get { return this.feeds.Count; }
  }
  public override string ToString(){
   StringBuilder sb = new StringBuilder("");
   XmlTextWriter writer = new XmlTextWriter(new StringWriter(sb));
   this.WriteTo(writer);
   writer.Flush();
   writer.Close();
   return sb.ToString();
  }
  public void WriteTo(XmlWriter writer){
   writer.WriteStartElement("newspaper");
   writer.WriteAttributeString("type", "group");
   writer.WriteElementString("title", this.title);
   foreach(FeedInfo feed in this.feeds){
    feed.WriteTo(writer, NewsItemSerializationFormat.Channel, false);
   }
   writer.WriteEndElement();
  }
  public IEnumerator GetEnumerator(){
   return this.feeds.GetEnumerator();
  }
  public void CopyTo(Array array, int index)
  {
   feeds.CopyTo(array, index);
  }
  public bool IsSynchronized
  {
   get { return feeds.IsSynchronized; }
  }
  public object SyncRoot
  {
   get { return feeds.SyncRoot; }
  }
 }
}
