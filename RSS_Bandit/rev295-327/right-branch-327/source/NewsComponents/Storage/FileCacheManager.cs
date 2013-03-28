using System;
using System.Collections;
using System.IO;
using System.Xml;
using NewsComponents.Feed;
using NewsComponents.Resources;
using NewsComponents.Utils;
namespace NewsComponents.Storage {
 public class FileCacheManager : CacheManager{
  private static readonly log4net.ILog _log = RssBandit.Common.Logging.Log.GetLogger(typeof(FileCacheManager));
  private string cacheDirectory = null;
  private FileCacheManager(){;}
  public FileCacheManager(string cacheDirectory){
   if(Directory.Exists(cacheDirectory)){
    this.cacheDirectory = cacheDirectory;
   }else{
    throw new IOException(ComponentsText.ExceptionDirectoryNotExistsMessage(cacheDirectory));
   }
  }
  internal override string CacheLocation {
   get{
    return this.cacheDirectory;
   }
  }
  internal override FeedDetailsInternal GetFeed(NewsFeed feed){
   if (null == feed || null == feed.cacheurl)
    return null;
   FeedInfo fi = null;
   string cachelocation = Path.Combine(this.cacheDirectory, feed.cacheurl);
   if(File.Exists(cachelocation)){
    using (Stream feedStream = FileHelper.OpenForRead(cachelocation)) {
     fi = RssParser.GetItemsForFeed(feed, feedStream, true);
    }
    this.LoadItemContent(fi);
   }
   return fi;
  }
  internal override string SaveFeed(FeedDetailsInternal feed){
   string feedLocation = feed.FeedLocation;
    lock(this){
     if((feed.FeedLocation == null) || (feed.FeedLocation.Length == 0)){
      feed.FeedLocation = feedLocation = GetCacheUrlName(feed.Id, new Uri(feed.Link));
     }
    }
    string feedContentLocation = feedLocation.Substring(0, feedLocation.Length - 4) + ".bin";
    using (MemoryStream stream = new MemoryStream()) {
     XmlTextWriter writer = new XmlTextWriter(new StreamWriter(stream ));
     feed.WriteTo(writer,true);
     writer.Flush();
     FileHelper.WriteStreamWithRename(Path.Combine(this.cacheDirectory,feedLocation), stream);
    }
    using (MemoryStream stream = new MemoryStream()) {
     FileStream fs = null;
     BinaryReader reader = null;
     BinaryWriter writer = new BinaryWriter(stream);
     try{
      if(File.Exists(Path.Combine(this.cacheDirectory, feedContentLocation))){
       fs = new FileStream(Path.Combine(this.cacheDirectory, feedContentLocation), FileMode.OpenOrCreate);
       reader = new BinaryReader(fs);
      }
      feed.WriteItemContents(reader, writer);
      writer.Write(FileHelper.EndOfBinaryFileMarker);
      writer.Flush();
     }finally{
      if(reader != null){
       reader.Close();
       fs.Close();
      }
     }
     FileHelper.WriteStreamWithRename(Path.Combine(this.cacheDirectory,feedContentLocation), stream);
    }
   return feedLocation;
  }
  public override void RemoveFeed(NewsFeed feed){
   if (feed == null || feed.cacheurl == null)
    return;
   string cachelocation = Path.Combine(this.cacheDirectory, feed.cacheurl);
   string feedContentLocation = Path.Combine(this.cacheDirectory,
           feed.cacheurl.Substring(0, feed.cacheurl.Length - 4) + ".bin");
   try{
    if(File.Exists(cachelocation)){
     FileHelper.Delete(cachelocation);
    }
    if(File.Exists(feedContentLocation)){
     FileHelper.Delete(feedContentLocation);
    }
   }catch(IOException iox){
    _log.Debug("RemoveFeed: Could not delete " + cachelocation, iox);
   }
  }
  public override void ClearCache(){
   try{
    lock(this){
     string [] fileEntries = Directory.GetFiles(cacheDirectory);
     foreach(string fileName in fileEntries){
                        try {
                            FileHelper.Delete(fileName);
                        } catch (IOException ioe) {
                            _log.Debug("Error deleting " + fileName + " while clearing cache: ", ioe);
                        }
     }
    }
   }catch(IOException ioe){
    _log.Debug("Error Clearing Cache", ioe);
   }
  }
  public override bool FeedExists(NewsFeed feed){
   string cachelocation = Path.Combine(this.cacheDirectory, feed.cacheurl);
   return File.Exists(cachelocation);
  }
  public override void LoadItemContent(NewsItem item){
   FileStream fs = null;
   BinaryReader reader = null;
   try{
    string feedContentLocation = item.Feed.cacheurl.Substring(0, item.Feed.cacheurl.Length - 4) + ".bin";
    if(File.Exists(Path.Combine(this.cacheDirectory, feedContentLocation))){
     fs = FileHelper.OpenForRead(Path.Combine(this.cacheDirectory, feedContentLocation));
     reader = new BinaryReader(fs);
     string id = reader.ReadString();
     while(!id.Equals(FileHelper.EndOfBinaryFileMarker)){
      int count = reader.ReadInt32();
      byte[] content = reader.ReadBytes(count);
      if(item.Id.Equals(id)){
       item.SetContent(content, ContentType.Html);
       break;
      }
      id = reader.ReadString();
     }
    }
   }finally{
    if(reader != null) {
     reader.Close();
    }
    if(fs != null){
     fs.Close();
    }
   }
  }
  private void LoadItemContent(FeedInfo fi){
   Hashtable unreadItems = new Hashtable();
   FileStream fs = null;
   BinaryReader reader = null;
   foreach(NewsItem item in fi.itemsList){
    if(!item.BeenRead){
     try{
      unreadItems.Add(item.Id, item);
     }catch(ArgumentException){
     }
    }
   }
   try{
    string feedContentLocation = fi.feedLocation.Substring(0, fi.feedLocation.Length - 4) + ".bin";
    if(File.Exists(Path.Combine(this.cacheDirectory, feedContentLocation))){
     fs = FileHelper.OpenForRead(Path.Combine(this.cacheDirectory, feedContentLocation));
     reader = new BinaryReader(fs);
     string id = reader.ReadString();
     while(!string.IsNullOrEmpty(id) && !id.Equals(FileHelper.EndOfBinaryFileMarker)){
      int count = reader.ReadInt32();
      byte[] content = reader.ReadBytes(count);
      if(unreadItems.Contains(id)){
       NewsItem ni = (NewsItem) unreadItems[id];
       ni.SetContent(content, ContentType.Html);
      }
      id = reader.ReadString();
     }
    }
   }finally{
    if(reader != null) {
     reader.Close();
    }
    if(fs != null){
     fs.Close();
    }
   }
  }
  private static string GetCacheUrlName(string id, Uri uri) {
   string path = null;
   if (uri.IsFile || uri.IsUnc) {
    path = uri.GetHashCode() + "." + id + ".xml";
   }else{
    path = uri.Host + "." + uri.Port + "." + uri.GetHashCode() + "." + id + ".xml";
   }
   return path.Replace("-","");
  }
 }
}
