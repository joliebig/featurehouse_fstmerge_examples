using System;
using System.IO;
using System.Net;
using WorldWind.Renderable;
namespace WorldWind.Net
{
 public class ImageTileRequest : GeoSpatialDownloadRequest
 {
  QuadTile m_quadTile;
  public ImageTileRequest(object owner, QuadTile quadTile) :
   base( owner, quadTile.ImageTileInfo.Uri )
  {
   m_quadTile = quadTile;
   download.DownloadType = DownloadType.Wms;
   SaveFilePath = QuadTile.ImageTileInfo.ImagePath;
  }
  public override float West
  {
   get
   {
    return (float)m_quadTile.West;
   }
  }
  public override float East
  {
   get
   {
    return (float)m_quadTile.East;
   }
  }
  public override float North
  {
   get
   {
    return (float)m_quadTile.North;
   }
  }
  public override float South
  {
   get
   {
    return (float)m_quadTile.South;
   }
  }
  public override int Color
  {
   get
   {
    return World.Settings.downloadProgressColor;
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
  protected override void DownloadComplete()
  {
   try
   {
    download.Verify();
    if(download.SavedFilePath != null && File.Exists(download.SavedFilePath))
     File.Move(download.SavedFilePath, SaveFilePath);
    m_quadTile.isInitialized = false;
    QuadTile.DownloadRequest = null;
   }
   catch(WebException caught)
   {
    System.Net.HttpWebResponse response = caught.Response as System.Net.HttpWebResponse;
    if(response!=null && response.StatusCode==System.Net.HttpStatusCode.NotFound)
     FlagBadFile();
   }
   catch(IOException)
   {
    FlagBadFile();
   }
  }
  void FlagBadFile()
  {
   File.Create(SaveFilePath + ".txt");
   try
   {
    if(File.Exists(SaveFilePath))
     File.Delete(SaveFilePath);
   }
   catch
   {
   }
  }
  public override float CalculateScore()
  {
   float screenArea = QuadTile.BoundingBox.CalcRelativeScreenArea(QuadTile.QuadTileArgs.Camera);
   return screenArea;
  }
 }
}
