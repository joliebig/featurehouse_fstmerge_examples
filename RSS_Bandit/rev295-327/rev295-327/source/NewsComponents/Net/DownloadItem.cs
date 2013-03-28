using NewsComponents.Feed; 
using System; 
using System.Net; 
using System.Runtime.Serialization; 
using System.Security.Permissions; namespace  NewsComponents.Net {
	
 [Serializable] 
 public class  DownloadItem  : ISerializable {
		
  private  IDownloadInfoProvider downloadInfo;
 
  private  DownloadFile file;
 
  private  Enclosure enclosure;
 
  private  string ownerFeedId;
 
  private  string ownerItemId;
 
  private  Guid downloadItemId = Guid.Empty;
 
     
  public  DownloadItem( string ownerFeedId, string ownerItemId, Enclosure enclosure, IDownloadInfoProvider downloadInfo ) {
   this.ownerFeedId = ownerFeedId;
   this.ownerItemId = ownerItemId;
   this.enclosure = enclosure;
   this.file = new DownloadFile(enclosure);
   this.downloadInfo = downloadInfo;
  }
 
  public  Guid ItemId {
   get {
    if ( downloadItemId == Guid.Empty ) {
     downloadItemId = Guid.NewGuid();
    }
    return downloadItemId;
   }
  }
 
  public  string OwnerFeedId {
   get {
    return this.ownerFeedId;
   }
  }
 
     
  public  string OwnerItemId {
   get {
    return this.ownerItemId;
   }
  }
 
  public  string TargetFolder{
  get{
   return this.downloadInfo.GetTargetFolder(this);
   }
  }
 
  public  Enclosure Enclosure{
   get{
    return this.enclosure;
   }
  }
 
  public  DownloadFile File{
   get{
    return this.file;
   }
  }
 
  public  ICredentials Credentials {
   get {
    return this.downloadInfo.GetCredentials(this);
   }
  }
 
  public  IWebProxy Proxy{
   get {
    return this.downloadInfo.Proxy;
   }
  }
 
  [System.Security.Permissions.SecurityPermission(SecurityAction.Demand, SerializationFormatter=true)] 
  protected  DownloadItem(SerializationInfo info, StreamingContext context) {
   this.downloadItemId = (Guid)info.GetValue("_id", typeof(Guid));
   this.ownerItemId = info.GetString("_itemId");
   this.ownerFeedId = info.GetString("_ownerId");
   this.enclosure = new Enclosure(info.GetString("_mimetype"), info.GetInt64("_length"), info.GetString("_url"), info.GetString("_description"));
   this.file = new DownloadFile(enclosure);
  }
 
  [SecurityPermissionAttribute(SecurityAction.Demand, SerializationFormatter=true)] 
  public  void GetObjectData(SerializationInfo info, StreamingContext context) {
   info.AddValue("_id", this.downloadItemId);
   info.AddValue( "_itemId", this.OwnerItemId );
   info.AddValue("_ownerId", this.OwnerFeedId );
   info.AddValue("_url", this.enclosure.Url);
   info.AddValue("_mimetype", this.enclosure.MimeType);
   info.AddValue("_length", this.enclosure.Length);
   info.AddValue("_description", this.enclosure.Description);
  }
 
  public  void Init(IDownloadInfoProvider downloadInfo){
   this.downloadInfo = downloadInfo;
  }
 
  private  INewsFeed ownerFeed; 
  public  INewsFeed OwnerFeed{
   get {
    return this.ownerFeed;
   }
   set{ this.ownerFeed = value;}
  }
	}

}
