using System;
using System.Diagnostics;
using System.Globalization;
using System.IO;
using System.Threading;
using System.Windows.Forms;
using WorldWind.Net;
using WorldWind.DataSource;
using Utility;
namespace WorldWind.Terrain
{
 public class TerrainTileService : IDisposable
 {
  string m_serverUrl;
  string m_dataSet;
  double m_levelZeroTileSizeDegrees;
  int m_samplesPerTile;
  int m_numberLevels;
  string m_fileExtension;
  string m_terrainTileDirectory;
  TimeSpan m_terrainTileRetryInterval;
  string m_dataType;
  public string ServerUrl
  {
   get
   {
    return m_serverUrl;
   }
  }
  public string DataSet
  {
   get
   {
    return m_dataSet;
   }
  }
  public double LevelZeroTileSizeDegrees
  {
   get
   {
    return m_levelZeroTileSizeDegrees;
   }
  }
  public int SamplesPerTile
  {
   get
   {
    return m_samplesPerTile;
   }
  }
  public string FileExtension
  {
   get
   {
    return m_fileExtension;
   }
  }
  public string TerrainTileDirectory
  {
   get
   {
    return m_terrainTileDirectory;
   }
  }
  public TimeSpan TerrainTileRetryInterval
  {
   get
   {
    return m_terrainTileRetryInterval;
   }
  }
  public string DataType
  {
   get
   {
    return m_dataType;
   }
  }
  public TerrainTileService(
   string serverUrl,
   string dataSet,
   double levelZeroTileSizeDegrees,
   int samplesPerTile,
   string fileExtension,
   int numberLevels,
   string terrainTileDirectory,
   TimeSpan terrainTileRetryInterval,
   string dataType)
  {
   m_serverUrl = serverUrl;
   m_dataSet = dataSet;
   m_levelZeroTileSizeDegrees = levelZeroTileSizeDegrees;
   m_samplesPerTile = samplesPerTile;
   m_numberLevels = numberLevels;
   m_fileExtension = fileExtension.Replace(".","");
   m_terrainTileDirectory = terrainTileDirectory;
   if(!Directory.Exists(m_terrainTileDirectory))
    Directory.CreateDirectory(m_terrainTileDirectory);
   m_terrainTileRetryInterval = terrainTileRetryInterval;
   m_dataType = dataType;
  }
  public TerrainTile GetTerrainTile(double latitude, double longitude, double samplesPerDegree)
  {
   TerrainTile tile = new TerrainTile(this);
   tile.TargetLevel = m_numberLevels - 1;
   for(int i = 0; i < m_numberLevels; i++)
   {
    if(samplesPerDegree <= m_samplesPerTile / (m_levelZeroTileSizeDegrees * Math.Pow(0.5, i)))
    {
     tile.TargetLevel = i;
     break;
    }
   }
   tile.Row = GetRowFromLatitude(latitude, m_levelZeroTileSizeDegrees * Math.Pow(0.5, tile.TargetLevel));
   tile.Col = GetColFromLongitude(longitude, m_levelZeroTileSizeDegrees * Math.Pow(0.5, tile.TargetLevel));
   tile.TerrainTileFilePath = string.Format( CultureInfo.InvariantCulture,
    @"{0}\{4}\{1:D4}\{1:D4}_{2:D4}.{3}",
    m_terrainTileDirectory, tile.Row, tile.Col, m_fileExtension, tile.TargetLevel);
   tile.SamplesPerTile = m_samplesPerTile;
   tile.TileSizeDegrees = m_levelZeroTileSizeDegrees * Math.Pow(0.5, tile.TargetLevel);
   tile.North = -90.0 + tile.Row * tile.TileSizeDegrees + tile.TileSizeDegrees;
   tile.South = -90.0 + tile.Row * tile.TileSizeDegrees;
   tile.West = -180.0 + tile.Col * tile.TileSizeDegrees;
   tile.East = -180.0 + tile.Col * tile.TileSizeDegrees + tile.TileSizeDegrees;
   return tile;
  }
  public static int GetColFromLongitude(double longitude, double tileSize)
  {
   return (int)System.Math.Floor((System.Math.Abs(-180.0 - longitude)%360)/tileSize);
  }
  public static int GetRowFromLatitude(double latitude, double tileSize)
  {
   return (int)System.Math.Floor((System.Math.Abs(-90.0 - latitude)%180)/tileSize);
  }
  public void Dispose()
  {
   if(DrawArgs.DownloadQueue!=null)
    DrawArgs.DownloadQueue.Clear(this);
  }
 }
 public class TerrainTile : IDisposable
 {
  public string TerrainTileFilePath;
  public double TileSizeDegrees;
  public int SamplesPerTile;
  public double South;
  public double North;
  public double West;
  public double East;
  public int Row;
  public int Col;
  public int TargetLevel;
  public TerrainTileService m_owner;
  public bool IsInitialized;
  public bool IsValid;
  public float[,] ElevationData;
        private DataRequest m_request = null;
        private DataRequestDescriptor m_drd;
  public TerrainTile( TerrainTileService owner )
  {
   m_owner = owner;
  }
  public void Initialize()
  {
   if(IsInitialized)
    return;
            if (!File.Exists(TerrainTileFilePath))
            {
                if (m_request == null)
                {
                    string url = String.Format(CultureInfo.InvariantCulture,
                        "{0}?T={1}&L={2}&X={3}&Y={4}",
                        m_owner.ServerUrl,
                        m_owner.DataSet,
                        TargetLevel, Col, Row);
                    m_drd = new DataRequestDescriptor(url, TerrainTileFilePath);
                    m_drd.Description = "Terrain Tile " + m_owner.DataSet + " Level " + TargetLevel + " Tile " + Row + "/" + Col;
                    m_request = DataStore.Request(m_drd);
                    return;
                }
                if (m_request.State == DataRequestState.Finished)
                {
                    Directory.CreateDirectory(Path.GetDirectoryName(Path.GetFullPath(TerrainTileFilePath)));
                    m_request.Stream.Seek(0, SeekOrigin.Begin);
                    FileStream cacheFile = new FileStream(TerrainTileFilePath, FileMode.Create);
                    int Length = 256;
                    Byte[] buffer = new Byte[Length];
                    int bytesRead = m_request.Stream.Read(buffer, 0, Length);
                    while (bytesRead > 0)
                    {
                        cacheFile.Write(buffer, 0, bytesRead);
                        bytesRead = m_request.Stream.Read(buffer, 0, Length);
                    }
                    cacheFile.Close();
                    string ContentType7z = "application/x-7z-compressed";
                    string ContentTypeXCompressed = "application/x-compressed";
                    if (m_request.Headers["Content-Type"] == ContentType7z || m_request.Headers["Content-Type"] == ContentTypeXCompressed)
                    {
                        string compressedPath = TerrainTileFilePath + ".7z";
                        string tempDirectory = Path.GetDirectoryName(TerrainTileFilePath);
                        if (File.Exists(compressedPath))
                            File.Delete(compressedPath);
                        File.Move(TerrainTileFilePath, compressedPath);
                        ProcessStartInfo psi = new ProcessStartInfo(Path.GetDirectoryName(Application.ExecutablePath) + @"\System\7za.exe");
                        psi.UseShellExecute = false;
                        psi.CreateNoWindow = true;
                        psi.Arguments = String.Format(CultureInfo.InvariantCulture,
                            " x -y -o\"{1}\" \"{0}\"", compressedPath, tempDirectory);
                        using (Process p = Process.Start(psi))
                        {
                            p.WaitForExit();
                            if (p.ExitCode != 0)
                                throw new ApplicationException(string.Format("7z decompression of file '{0}' failed.", compressedPath));
                        }
                        File.Delete(compressedPath);
                    }
                    m_request = null;
                }
                return;
            }
   if(ElevationData==null)
    ElevationData = new float[SamplesPerTile, SamplesPerTile];
   if(File.Exists(TerrainTileFilePath))
   {
    try
    {
     try
     {
      FileInfo tileInfo = new FileInfo(TerrainTileFilePath);
      if(tileInfo.Length == 0)
      {
       TimeSpan age = DateTime.Now.Subtract( tileInfo.LastWriteTime );
       if(age < m_owner.TerrainTileRetryInterval)
       {
        IsInitialized = true;
       }
       else
       {
        File.Delete(TerrainTileFilePath);
       }
       return;
      }
     }
     catch
     {
     }
     using( Stream s = File.OpenRead(TerrainTileFilePath))
     {
      BinaryReader reader = new BinaryReader(s);
      if(m_owner.DataType=="Int16")
      {
       for(int y = 0; y < SamplesPerTile; y++)
        for(int x = 0; x < SamplesPerTile; x++)
         ElevationData[x,y] = reader.ReadInt16();
      }
      if(m_owner.DataType=="Float32")
      {
       for(int y = 0; y < SamplesPerTile; y++)
        for(int x = 0; x < SamplesPerTile; x++)
        {
         ElevationData[x,y] = reader.ReadSingle();
        }
      }
      IsInitialized = true;
      IsValid = true;
     }
     return;
    }
    catch(IOException)
    {
     try
     {
      File.Delete(TerrainTileFilePath);
     }
     catch(Exception ex)
     {
      throw new ApplicationException(String.Format("Error while trying to delete corrupt terrain tile {0}", TerrainTileFilePath), ex);
     }
    }
    catch(Exception ex)
    {
     throw new ApplicationException(String.Format("Error while trying to read terrain tile {0}", TerrainTileFilePath), ex);
    }
   }
  }
  public float GetElevationAt(double latitude, double longitude)
  {
            if (ElevationData == null)
                return 0;
   try
   {
    double deltaLat = North - latitude;
    double deltaLon = longitude - West;
    double df2 = (SamplesPerTile-1) / TileSizeDegrees;
    float lat_pixel = (float)(deltaLat * df2);
    float lon_pixel = (float)(deltaLon * df2);
    int lat_min = (int)lat_pixel;
    int lat_max = (int)Math.Ceiling(lat_pixel);
    int lon_min = (int)lon_pixel;
    int lon_max = (int)Math.Ceiling(lon_pixel);
    if(lat_min >= SamplesPerTile)
     lat_min = SamplesPerTile - 1;
    if(lat_max >= SamplesPerTile)
     lat_max = SamplesPerTile - 1;
    if(lon_min >= SamplesPerTile)
     lon_min = SamplesPerTile - 1;
    if(lon_max >= SamplesPerTile)
     lon_max = SamplesPerTile - 1;
    if(lat_min < 0)
     lat_min = 0;
    if(lat_max < 0)
     lat_max = 0;
    if(lon_min < 0)
     lon_min = 0;
    if(lon_max < 0)
     lon_max = 0;
    float delta = lat_pixel - lat_min;
    float westElevation =
     ElevationData[lon_min, lat_min]*(1-delta) +
     ElevationData[lon_min, lat_max]*delta;
    float eastElevation =
     ElevationData[lon_max, lat_min]*(1-delta) +
     ElevationData[lon_max, lat_max]*delta;
    delta = lon_pixel - lon_min;
    float interpolatedElevation =
     westElevation*(1-delta) +
     eastElevation*delta;
    return interpolatedElevation;
   }
   catch
   {
   }
   return 0;
  }
  public void Dispose()
  {
   GC.SuppressFinalize(this);
  }
 }
}
