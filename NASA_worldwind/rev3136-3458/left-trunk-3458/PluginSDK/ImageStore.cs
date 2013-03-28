using Microsoft.DirectX.Direct3D;
using WorldWind.Net.Wms;
using WorldWind.Renderable;
using WorldWind.DataSource;
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
        protected bool m_colorKeyEnabled = false;
        protected bool m_alphaKeyEnabled = false;
        protected int m_colorKey = 0;
        protected int m_alphaKeyMin = -1;
        protected int m_alphaKeyMax = -1;
        public bool AlphaKeyEnabled
        {
            get { return m_alphaKeyEnabled; }
            set { m_alphaKeyEnabled = value; }
        }
        public bool ColorKeyEnabled
        {
            get { return m_colorKeyEnabled; }
            set { m_colorKeyEnabled = value; }
        }
        public int ColorKey
        {
            get
            {
                return m_colorKey;
            }
            set
            {
                m_colorKey = value;
            }
        }
        public int AlphaKeyMin
        {
            get
            {
                return m_alphaKeyMin;
            }
            set
            {
                m_alphaKeyMin = value;
            }
        }
        public int AlphaKeyMax
        {
            get
            {
                return m_alphaKeyMax;
            }
            set
            {
                m_alphaKeyMax = value;
            }
        }
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
            if (m_cacheDirectory == null)
                return m_duplicateTexturePath;
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
  public virtual string GetDownloadUrl(QuadTile qt)
  {
            if (m_duplicateTexturePath != null && Directory.Exists(m_duplicateTexturePath))
    return "file://" + Path.GetFullPath(m_duplicateTexturePath);
            if (m_dataDirectory != null && Directory.Exists(m_dataDirectory))
            {
                return "file://" + Path.Combine(Path.GetFullPath(m_dataDirectory),
                    String.Format(@"{0}\{1:D4}\{1:D4}_{2:D4}.{3}",
            qt.Level, qt.Row, qt.Col, m_imageFileExtension));
            }
   return "";
  }
        public virtual void DeleteLocalCopy(QuadTile qt)
  {
   string filename = GetLocalPath(qt);
   if(File.Exists(filename))
    File.Delete(filename);
  }
        [Obsolete]
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
        [Obsolete]
  public virtual Texture LoadFile(QuadTile qt)
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
   }
   if(World.Settings.ConvertDownloadedImagesToDds && IsDownloadableLayer)
    ConvertImage(texture, filePath);
   return texture;
  }
 }
}
