using System;
using System.Collections;
using System.IO;
using NewsComponents.Utils;
namespace NewsComponents.Net
{
 [Serializable]
 public class DownloadFilesCollection : CollectionBase {
  public DownloadFilesCollection( DownloadFile[] downloadFiles ) {
   if ( downloadFiles != null && downloadFiles.Length > 0 ) {
    for(int i=0; i<downloadFiles.Length; i++) {
     List.Add(downloadFiles[i]);
    }
   }
  }
  public DownloadFilesCollection() {}
  public DownloadFile this[ int index ] {
   get {
    return (DownloadFile)List[ index ];
   }
   set {
    List[ index ] = value;
   }
  }
  public bool Contains( DownloadFile value) {
   return List.Contains( value );
  }
  public void Add( DownloadFile value ) {
   List.Add( value );
  }
  public void Remove( DownloadFile value ) {
   List.Remove( value );
  }
  public void Insert( int index, DownloadFile value ) {
   List.Insert( index, value );
  }
 }
 public class DownloadFile {
  private string sourceLocation;
  private MimeType suggestedMimeType;
  private long expectedSize;
  private string localName;
  public DownloadFile(Enclosure enclosure ) {
   this.sourceLocation = enclosure.Url;
   this.suggestedMimeType = new MimeType(enclosure.MimeType);
   this.expectedSize = enclosure.Length;
   this.GuessLocalFileName();
  }
  public string Source {
   get {
    return sourceLocation;
   }
  }
  public MimeType SuggestedType {
   get {
    return suggestedMimeType;
   }
   set{
    this.suggestedMimeType = value;
    this.GuessLocalFileName();
   }
  }
  public long FileSize {
   get {
    return expectedSize;
   }
   set{
    this.expectedSize = value;
   }
  }
  public string LocalName {
   get {
    return localName;
   }
   set {
    localName = value;
   }
  }
  private void GuessLocalFileName() {
   try{
    int index = this.sourceLocation.LastIndexOf("/");
    if((index != -1) && (index + 1 < this.sourceLocation.Length)){
     this.localName = this.sourceLocation.Substring(index + 1);
    }else{
     this.localName = this.sourceLocation;
    }
    if(this.localName.IndexOf(".")== -1){
     this.localName = this.localName + "." + this.suggestedMimeType.GetFileExtension();
    }
   }catch(Exception){
    if(this.localName == null){
     this.localName = Guid.NewGuid().ToString() + "." + this.suggestedMimeType.GetFileExtension();
    }
   }
  }
 }
}
