using System;
using System.IO;
using Utility;
namespace WorldWind.Net
{
 public class WebDownloadRequest : DownloadRequest
 {
  protected WebDownload download;
  protected static string TemporaryExtension = ".tmp";
  public WebDownloadRequest(object owner) : base(owner)
  {
   download = new WebDownload("");
  }
  public WebDownloadRequest(object owner, string url) : base(owner)
  {
   download = new WebDownload(url);
  }
  public string SaveFilePath
  {
   get
   {
    if(download.SavedFilePath == null)
     return null;
    return download.SavedFilePath.Substring(0,
     download.SavedFilePath.Length - TemporaryExtension.Length);
   }
   set
   {
    download.SavedFilePath = value + TemporaryExtension;
   }
  }
  public override float Progress
  {
   get
   {
    if(download==null)
     return 1;
    float total = download.ContentLength;
    if(download.ContentLength==0)
     total = 50*1024;
    float percent = (float)(download.BytesProcessed % (total+1))/total;
    return percent;
   }
  }
  public override string Key
  {
   get
   {
    if(download==null)
     return null;
    return download.Url;
   }
  }
  public override bool IsDownloading
  {
   get
   {
    if(download==null)
     return false;
    return download.IsDownloadInProgress;
   }
  }
  public override void Start()
  {
   download.CompleteCallback += new DownloadCompleteHandler(InternalDownloadComplete);
   if(download.SavedFilePath!=null && download.SavedFilePath.Length > 0)
    download.BackgroundDownloadFile();
   else
    download.BackgroundDownloadMemory();
  }
  public override float CalculateScore()
  {
   return 0;
  }
  protected virtual void InternalDownloadComplete(WebDownload download)
  {
   try
   {
    DownloadComplete();
   }
   catch(Exception caught)
   {
    Log.Write(Log.Levels.Error, "QUEU", download.Url + ": " + caught.Message);
   }
   OnComplete();
  }
  protected virtual void DownloadComplete()
  {
  }
  public override string ToString()
  {
   return download.Url;
  }
  public override void Dispose()
  {
   try
   {
    if(download != null)
    {
     download.Dispose();
     download = null;
    }
   }
   finally
   {
    base.Dispose();
   }
  }
 }
}
