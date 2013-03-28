using Microsoft.DirectX.Direct3D;
using WorldWind.Net.Wms;
using WorldWind.Renderable;
using System;
using System.IO;
namespace WorldWind
{
 public class ImageStore
 {
  protected string m_dataDirectory;
  protected double m_levelZeroTileSizeDegrees = 36;
  protected int m_levelCount = 1;
  protected string m_imageFileExtension;
  protected string m_cacheDirectory;
  protected string m_duplicateTexturePath;
  public double LevelZeroTileSizeDegrees
  {
   get
   {
    return m_levelZeroTileSizeDegrees;
   }
   set
   {
    m_levelZeroTileSizeDegrees = value;
   }
  }
  public int LevelCount
  {
   get
   {
    return m_levelCount;
   }
   set
   {
    m_levelCount = value;
   }
  }
  public string ImageExtension
  {
   get
   {
    return m_imageFileExtension;
   }
   set
   {
    m_imageFileExtension = value.Replace(".", "");
   }
  }
  public string CacheDirectory
  {
   get
   {
    return m_cacheDirectory;
   }
   set
   {
    m_cacheDirectory = value;
   }
  }
  public string DataDirectory
  {
   get
   {
    return m_dataDirectory;
   }
   set
   {
    m_dataDirectory = value;
   }
  }
  public string DuplicateTexturePath
  {
   get
   {
    return m_duplicateTexturePath;
   }
   set
   {
    m_duplicateTexturePath = value;
   }
  }
  public virtual bool IsDownloadableLayer
  {
   get
   {
    return false;
   }
  }
  public virtual string GetLocalPath(QuadTile qt)
  {
   if(qt.Level >= m_levelCount)
    throw new ArgumentException(string.Format("Level {0} not available.",
     qt.Level));
   string relativePath = String.Format(@"{0}\{1:D4}\{1:D4}_{2:D4}.{3}",
    qt.Level, qt.Row, qt.Col, m_imageFileExtension);
   if(m_dataDirectory != null)
   {
    string rawFullPath = Path.Combine( m_dataDirectory, relativePath );
    if(File.Exists(rawFullPath))
     return rawFullPath;
   }
   string cacheFullPath = Path.Combine( m_cacheDirectory, relativePath );
   if(File.Exists(cacheFullPath))
    return cacheFullPath;
   const string ValidExtensions = ".bmp.dds.dib.hdr.jpg.jpeg.pfm.png.ppm.tga.gif.tif";
   string cacheSearchPath = Path.GetDirectoryName(cacheFullPath);
   if(Directory.Exists(cacheSearchPath))
   {
    foreach( string imageFile in Directory.GetFiles(
     cacheSearchPath,
     Path.GetFileNameWithoutExtension(cacheFullPath) + ".*") )
    {
     string extension = Path.GetExtension(imageFile).ToLower();
     if(ValidExtensions.IndexOf(extension)<0)
      continue;
     return imageFile;
    }
   }
   return cacheFullPath;
  }
  protected virtual string GetDownloadUrl(QuadTile qt)
  {
   if(m_duplicateTexturePath != null && File.Exists(m_duplicateTexturePath))
    return m_duplicateTexturePath;
   return "";
  }
  public virtual void DeleteLocalCopy(QuadTile qt)
  {
   string filename = GetLocalPath(qt);
   if(File.Exists(filename))
    File.Delete(filename);
  }
  protected virtual void ConvertImage(Texture texture, string filePath)
  {
   if(filePath.ToLower().EndsWith(".dds"))
    return;
   string convertedPath = Path.Combine(
    Path.GetDirectoryName(filePath),
    Path.GetFileNameWithoutExtension(filePath)+".dds");
   TextureLoader.Save(convertedPath, ImageFileFormat.Dds, texture );
   try
   {
    File.Delete(filePath);
   }
   catch
   {
   }
  }
  public Texture LoadFile(QuadTile qt)
  {
   string filePath = GetLocalPath(qt);
   qt.ImageFilePath = filePath;
   if(!File.Exists(filePath))
   {
    string badFlag = filePath + ".txt";
    if(File.Exists(badFlag))
    {
     FileInfo fi = new FileInfo(badFlag);
     if(DateTime.Now - fi.LastWriteTime < TimeSpan.FromDays(1))
     {
      return null;
     }
     File.Delete(badFlag);
    }
    if(IsDownloadableLayer)
    {
     QueueDownload(qt, filePath);
     return null;
    }
    if(DuplicateTexturePath == null)
     return null;
    filePath = DuplicateTexturePath;
   }
   Texture texture = null;
   if(qt.QuadTileSet.HasTransparentRange)
   {
    texture = ImageHelper.LoadTexture( filePath, qt.QuadTileSet.ColorKey,
     qt.QuadTileSet.ColorKeyMax);
   }
   else
   {
    texture = ImageHelper.LoadTexture( filePath, qt.QuadTileSet.ColorKey);
   }
   if(qt.QuadTileSet.CacheExpirationTime != TimeSpan.MaxValue)
   {
    FileInfo fi = new FileInfo(filePath);
    DateTime expiry = fi.LastWriteTimeUtc.Add(qt.QuadTileSet.CacheExpirationTime);
    if(DateTime.UtcNow > expiry)
     QueueDownload(qt, filePath);
   }
   if(World.Settings.ConvertDownloadedImagesToDds)
    ConvertImage(texture, filePath);
   return texture;
  }
  void QueueDownload(QuadTile qt, string filePath)
  {
   string url = GetDownloadUrl(qt);
   qt.QuadTileSet.AddToDownloadQueue(qt.QuadTileSet.Camera,
    new GeoSpatialDownloadRequest(qt, filePath, url));
  }
 }
}
