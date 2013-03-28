using System;
using System.IO;
using System.Net;
namespace NewsComponents.Net
{
 public delegate void RequestQueuedCallback(Uri requestUri, int priority);
 public delegate void RequestStartCallback(Uri requestUri, ref bool cancel);
 public delegate void RequestExceptionCallback(Uri requestUri, Exception e, int priority);
 public delegate void RequestCompleteCallback(Uri requestUri, Stream response, Uri newUri, string eTag, DateTime lastModified, RequestResult result, int priority);
 public delegate void RequestProgressCallback(Uri requestUri, long bytesTransferred);
 public enum RequestResult {
  OK,
  NotModified
 }
 public class RequestParameter {
  public RequestParameter(Uri address, string userAgent,
   IWebProxy proxy, ICredentials credentials, DateTime ifModifiedSince, string eTag):
   this(address, userAgent, proxy, credentials, ifModifiedSince, eTag, true) {
  }
  public RequestParameter(Uri address, string userAgent,
   IWebProxy proxy, ICredentials credentials,
   DateTime ifModifiedSince, string eTag, bool setCookies) {
   this.requestUri = address;
   this.userAgent = userAgent;
   this.proxy = proxy;
   this.credentials = credentials;
   this.lastModified = ifModifiedSince;
   this.eTag = eTag;
   this.setCookies = setCookies;
  }
  public static RequestParameter Create (Uri address, string userAgent,
   IWebProxy proxy, ICredentials credentials, DateTime ifModifiedSince, string eTag) {
   return new RequestParameter(address, userAgent, proxy, credentials, ifModifiedSince, eTag );
  }
  public static RequestParameter Create(Uri address, RequestParameter p) {
   return new RequestParameter(address, p.UserAgent, p.Proxy, p.Credentials, p.LastModified, p.ETag, p.SetCookies );
  }
  public static RequestParameter Create(ICredentials credentials, RequestParameter p) {
   return new RequestParameter(p.RequestUri, p.UserAgent, p.Proxy, credentials, p.LastModified, p.ETag, p.SetCookies );
  }
  public static RequestParameter Create(bool setCookies, RequestParameter p) {
   return new RequestParameter(p.RequestUri, p.UserAgent, p.Proxy, p.Credentials, p.LastModified, p.ETag, setCookies );
  }
  public static RequestParameter Create(Uri address,ICredentials credentials, RequestParameter p) {
   return new RequestParameter(address, p.UserAgent, p.Proxy, credentials, p.LastModified, p.ETag, p.SetCookies );
  }
  private Uri requestUri;
  public Uri RequestUri { get { return this.requestUri; } }
  private string userAgent;
  public string UserAgent { get { return this.userAgent; } }
  private IWebProxy proxy;
  public IWebProxy Proxy { get { return this.proxy; } }
  private ICredentials credentials;
  public ICredentials Credentials { get { return this.credentials; } }
  private DateTime lastModified;
  public DateTime LastModified {
   get { return this.lastModified; }
   set { this.lastModified = value; }
  }
  private string eTag;
  public string ETag {
   get { return this.eTag; }
   set { this.eTag = value; }
  }
  private bool setCookies;
  public bool SetCookies {
   get { return this.setCookies; }
   set { this.setCookies = value; }
  }
 }
}
