using System;
using System.IO;
using System.Net;
using System.Runtime.InteropServices;
namespace NewsComponents.News {
 public enum NntpStatusCode {
 OK,
 Error
 }
 [ComVisible(false)]
 public class NntpWebResponse: WebResponse, IDisposable{
 private NntpStatusCode statusCode;
       private long contentLength = 0;
  private string contentType = null;
  private WebHeaderCollection headers = null;
  private Uri responseUri = null;
  private Stream responseStream = null;
  private NntpWebResponse(){;}
  internal NntpWebResponse(NntpStatusCode status){this.statusCode = status;}
  internal NntpWebResponse(NntpStatusCode status, Stream stream):this(status){
    this.responseStream = stream;
  }
  public NntpStatusCode StatusCode {
   get{ return statusCode; }
  }
  public override WebHeaderCollection Headers {
   get {
    return this.headers;
   }
  }
  public override string ContentType {
   get {
    return this.contentType;
   }
  }
  public override long ContentLength {
   get {
    return this.contentLength;
   }
  }
  public override Uri ResponseUri {
   get {
    return this.responseUri;
   }
  }
  public void Dispose(){
   try{
    responseStream.Close();
   }catch(Exception){}
   responseStream = null;
  }
  public override Stream GetResponseStream() {
   return this.responseStream;
  }
  public override void Close() {
   this.responseStream.Close();
  }
 }
}
