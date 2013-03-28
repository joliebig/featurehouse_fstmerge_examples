using System;
using System.Collections;
using System.Net;
using System.Runtime.InteropServices;
using NewsComponents.Feed;
using NewsComponents.Resources;
namespace NewsComponents.Net
{
 [ComVisible(false)]
 public class ResourceAuthorizationException: WebException {
  public ResourceAuthorizationException():base(ComponentsText.ExceptionResourceAuthorization){ }
  public ResourceAuthorizationException(string message):base(message){ }
  public ResourceAuthorizationException(string message, Exception innerException):base(message, innerException){ }
 }
 [ComVisible(false)]
 public class ResourceGoneException: WebException
 {
  public ResourceGoneException():base(ComponentsText.ExceptionResourceGone){ }
  public ResourceGoneException(string message):base(message){ }
  public ResourceGoneException(string message, Exception innerException):base(message, innerException){ }
 }
 [ComVisible(false)]
 public class FeedRequestException: WebException {
  public FeedRequestException():base(){ }
  public FeedRequestException(string message, Hashtable context):base(message){
   this.context = context;
  }
  public FeedRequestException(string message, Exception innerException, Hashtable context):base(message, innerException){
   this.context = context;
  }
  public FeedRequestException(string message, WebExceptionStatus status, Hashtable context):base(message, status){
   this.context = context;
  }
  public FeedRequestException(string message, Exception innerException, WebExceptionStatus status, Hashtable context):base(message, innerException, status, null){
   this.context = context;
  }
  private Hashtable context = new Hashtable();
  private string GetValue(string key) {
   if (this.context != null && this.context.ContainsKey(key))
    return (string)this.context[key];
   return String.Empty;
  }
  private object GetObject(string key) {
   if (this.context != null && this.context.ContainsKey(key))
    return this.context[key];
   return null;
  }
  public string TechnicalContact { get { return this.GetValue("TECH_CONTACT"); } }
  public string Publisher { get { return this.GetValue("PUBLISHER"); } }
  public string FullTitle { get { return this.GetValue("FULL_TITLE"); } }
  public string PublisherHomepage { get { return this.GetValue("PUBLISHER_HOMEPAGE"); } }
  public string Generator { get { return this.GetValue("GENERATOR"); } }
  public INewsFeed Feed { get { return (INewsFeed)this.GetObject("FAILURE_OBJECT"); } }
 }
}
