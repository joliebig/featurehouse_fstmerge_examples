using WorldWind.Net;
using System;
using System.IO;
namespace WorldWind.Renderable
{
 public class GeoSpatialDownloadRequest : IDisposable
 {
  public float ProgressPercent;
  WebDownload download;
  string m_localFilePath;
  string m_url;
  QuadTile m_quadTile;
  public GeoSpatialDownloadRequest(QuadTile quadTile, string localFilePath, string downloadUrl)
  {
   m_quadTile = quadTile;
   m_url = downloadUrl;
   m_localFilePath = localFilePath;
  }
  public bool IsDownloading
  {
   get
   {
    return (download != null);
   }
  }
  public bool IsComplete
  {
   get
   {
    if(download==null)
     return true;
    return download.IsComplete;
   }
  }
  public QuadTile QuadTile
  {
   get
   {
    return m_quadTile;
   }
  }
  public double TileWidth
  {
   get
   {
    return m_quadTile.East - m_quadTile.West;
   }
  }
  private void DownloadComplete(WebDownload downloadInfo)
  {
   try
   {
    downloadInfo.Verify();
    m_quadTile.QuadTileSet.NumberRetries = 0;
    File.Delete(m_localFilePath);
    File.Move(downloadInfo.SavedFilePath, m_localFilePath);
    m_quadTile.DownloadRequest = null;
    m_quadTile.Initialize();
   }
   catch(System.Net.WebException caught)
   {
    System.Net.HttpWebResponse response = caught.Response as System.Net.HttpWebResponse;
    if(response!=null && response.StatusCode==System.Net.HttpStatusCode.NotFound)
    {
     using(File.Create(m_localFilePath + ".txt"))
     {}
     return;
    }
    m_quadTile.QuadTileSet.NumberRetries++;
   }
   catch
   {
    using(File.Create(m_localFilePath + ".txt"))
    {}
    if(File.Exists(downloadInfo.SavedFilePath))
     File.Delete(downloadInfo.SavedFilePath);
   }
   finally
   {
    download.IsComplete = true;
    m_quadTile.QuadTileSet.RemoveFromDownloadQueue(this);
    m_quadTile.QuadTileSet.ServiceDownloadQueue();
   }
  }
  public virtual void StartDownload()
  {
   QuadTile.IsDownloadingImage = true;
   download = new WebDownload(m_url);
   download.DownloadType = DownloadType.Wms;
   download.SavedFilePath = m_localFilePath + ".tmp";
   download.ProgressCallback += new DownloadProgressHandler(UpdateProgress);
   download.CompleteCallback += new WorldWind.Net.DownloadCompleteHandler(DownloadComplete);
   download.BackgroundDownloadFile();
  }
  void UpdateProgress( int pos, int total )
  {
   if(total==0)
    total = 50*1024;
   pos = pos % (total+1);
   ProgressPercent = (float)pos/total;
  }
  public virtual void Cancel()
  {
   if (download!=null)
    download.Cancel();
  }
  public override string ToString()
  {
   return QuadTile.QuadTileSet.ImageStore.GetLocalPath(QuadTile);
  }
  public virtual void Dispose()
  {
   if(download!=null)
   {
    download.Dispose();
    download=null;
   }
   GC.SuppressFinalize(this);
  }
 }
}
