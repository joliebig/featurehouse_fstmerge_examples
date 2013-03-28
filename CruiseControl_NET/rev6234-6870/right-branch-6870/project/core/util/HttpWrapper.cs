using System;
using System.Net;
namespace ThoughtWorks.CruiseControl.Core.Util
{
 public class HttpWrapper
 {
  public virtual DateTime GetLastModifiedTimeFor(Uri url, DateTime previousModifiedTime)
  {
   HttpWebRequest request = (HttpWebRequest) WebRequest.Create(url);
   request.ProtocolVersion = HttpVersion.Version11;
   request.IfModifiedSince = previousModifiedTime;
   try
   {
    using (HttpWebResponse response = (HttpWebResponse) request.GetResponse())
    {
     if (response.Headers["Last-Modified"] == null) return previousModifiedTime;
     return response.LastModified;
    }
   }
   catch (WebException ex)
   {
    if (ex.Status == WebExceptionStatus.ProtocolError)
    {
     HttpWebResponse httpResponse = (HttpWebResponse) ex.Response;
     if (httpResponse.StatusCode == HttpStatusCode.NotModified)
      return previousModifiedTime;
    }
    throw;
   }
  }
 }
}
