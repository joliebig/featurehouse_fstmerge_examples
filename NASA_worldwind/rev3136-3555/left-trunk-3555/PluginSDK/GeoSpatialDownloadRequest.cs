using System;
using System.IO;
using System.Net;
using WorldWind.Renderable;
namespace WorldWind.Net
{
 public abstract class GeoSpatialDownloadRequest : WebDownloadRequest
 {
  protected GeoSpatialDownloadRequest(object owner, string uri) : base( owner, uri )
  {
   download.DownloadType = DownloadType.Wms;
  }
  protected GeoSpatialDownloadRequest(object owner) : this( owner, null )
  {
  }
  public abstract float West
  {
   get;
  }
  public abstract float East
  {
   get;
  }
  public abstract float North
  {
   get;
  }
  public abstract float South
  {
   get;
  }
  public abstract int Color
  {
   get;
  }
 }
}
