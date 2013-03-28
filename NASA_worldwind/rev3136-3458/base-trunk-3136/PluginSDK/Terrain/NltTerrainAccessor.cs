using System;
using System.Collections;
using System.Diagnostics;
using System.IO;
using System.Threading;
using WorldWind.Net.Wms;
namespace WorldWind.Terrain
{
 public class NltTerrainAccessor : TerrainAccessor
 {
  public static int CacheSize = 100;
  protected TerrainTileService m_terrainTileService;
  protected WmsImageStore m_wmsElevationSet;
  protected TerrainAccessor[] m_higherResolutionSubsets;
  protected Hashtable m_tileCache = new Hashtable();
  public WmsImageStore WmsElevationStore
  {
   get
   {
    return m_wmsElevationSet;
   }
   set
   {
    m_wmsElevationSet = value;
   }
  }
  public TerrainAccessor this[int index]
  {
   get
   {
    return m_higherResolutionSubsets[index];
   }
  }
  public NltTerrainAccessor(string name, double west, double south, double east, double north,
   TerrainTileService terrainTileService, TerrainAccessor[] higherResolutionSubsets)
  {
   m_name = name;
   m_west = west;
   m_south = south;
   m_east = east;
   m_north = north;
   m_terrainTileService = terrainTileService;
   m_higherResolutionSubsets = higherResolutionSubsets;
  }
  public override float GetElevationAt(double latitude, double longitude, double targetSamplesPerDegree)
  {
   try
   {
    if (m_terrainTileService == null || targetSamplesPerDegree < World.Settings.MinSamplesPerDegree)
     return 0;
    if (m_higherResolutionSubsets != null)
    {
     foreach (TerrainAccessor higherResSub in m_higherResolutionSubsets)
     {
      if (latitude > higherResSub.South && latitude < higherResSub.North &&
       longitude > higherResSub.West && longitude < higherResSub.East)
      {
       return higherResSub.GetElevationAt(latitude, longitude, targetSamplesPerDegree);
      }
     }
    }
    TerrainTile tt = m_terrainTileService.GetTerrainTile(latitude, longitude, targetSamplesPerDegree);
    TerrainTileCacheEntry ttce = (TerrainTileCacheEntry)m_tileCache[tt.TerrainTileFilePath];
    if (ttce == null)
    {
     ttce = new TerrainTileCacheEntry(tt);
     AddToCache(ttce);
    }
    if (!ttce.TerrainTile.IsInitialized)
     ttce.TerrainTile.Initialize();
    ttce.LastAccess = DateTime.Now;
    return ttce.TerrainTile.GetElevationAt(latitude, longitude);
   }
   catch (Exception)
   {
   }
   return 0;
  }
  public override float GetElevationAt(double latitude, double longitude)
  {
   return GetElevationAt(latitude, longitude, m_terrainTileService.SamplesPerTile / m_terrainTileService.LevelZeroTileSizeDegrees);
  }
  public override TerrainTile GetElevationArray(double north, double south, double west, double east,
   int samples)
  {
   TerrainTile res = null;
   if (m_higherResolutionSubsets != null)
   {
    foreach (TerrainAccessor higherResSub in m_higherResolutionSubsets)
    {
     if (north <= higherResSub.North && south >= higherResSub.South &&
      west >= higherResSub.West && east <= higherResSub.East)
     {
      res = higherResSub.GetElevationArray(north, south, west, east, samples);
      return res;
     }
    }
   }
   res = new TerrainTile(m_terrainTileService);
   res.North = north;
   res.South = south;
   res.West = west;
   res.East = east;
   res.SamplesPerTile = samples;
   res.IsInitialized = true;
   res.IsValid = true;
   double samplesPerDegree = (double)samples / (double)(north - south);
   double latrange = Math.Abs(north - south);
   double lonrange = Math.Abs(east - west);
   TerrainTileCacheEntry ttce = null;
   float[,] data = new float[samples, samples];
   if(samplesPerDegree < World.Settings.MinSamplesPerDegree)
   {
    res.ElevationData = data;
    return res;
   }
   double scaleFactor = (double)1 / (samples - 1);
   for (int x = 0; x < samples; x++)
   {
    for (int y = 0; y < samples; y++)
    {
     double curLat = north - scaleFactor * latrange * x;
     double curLon = west + scaleFactor * lonrange * y;
     if (ttce == null ||
      curLat < ttce.TerrainTile.South ||
      curLat > ttce.TerrainTile.North ||
      curLon < ttce.TerrainTile.West ||
      curLon > ttce.TerrainTile.East)
     {
      TerrainTile tt = m_terrainTileService.GetTerrainTile(curLat, curLon, samplesPerDegree);
      ttce = (TerrainTileCacheEntry)m_tileCache[tt.TerrainTileFilePath];
      if (ttce == null)
      {
       ttce = new TerrainTileCacheEntry(tt);
       AddToCache(ttce);
      }
      if (!ttce.TerrainTile.IsInitialized)
       ttce.TerrainTile.Initialize();
      ttce.LastAccess = DateTime.Now;
      if (!tt.IsValid)
       res.IsValid = false;
     }
     data[x, y] = ttce.TerrainTile.GetElevationAt(curLat, curLon);
    }
   }
   res.ElevationData = data;
   return res;
  }
  protected void AddToCache(TerrainTileCacheEntry ttce)
  {
   if (!m_tileCache.ContainsKey(ttce.TerrainTile.TerrainTileFilePath))
   {
    if (m_tileCache.Count >= CacheSize)
    {
     TerrainTileCacheEntry oldestTile = null;
     foreach (TerrainTileCacheEntry curEntry in m_tileCache.Values)
     {
      if (oldestTile == null)
       oldestTile = curEntry;
      else
      {
       if (curEntry.LastAccess < oldestTile.LastAccess)
        oldestTile = curEntry;
      }
     }
     m_tileCache.Remove(oldestTile);
    }
    m_tileCache.Add(ttce.TerrainTile.TerrainTileFilePath, ttce);
   }
  }
  public class TerrainTileCacheEntry
  {
   DateTime m_lastAccess = DateTime.Now;
   TerrainTile m_terrainTile;
   public TerrainTileCacheEntry(TerrainTile tile)
   {
    m_terrainTile = tile;
   }
   public TerrainTile TerrainTile
   {
    get
    {
     return m_terrainTile;
    }
    set
    {
     m_terrainTile = value;
    }
   }
   public DateTime LastAccess
   {
    get
    {
     return m_lastAccess;
    }
    set
    {
     m_lastAccess = value;
    }
   }
  }
  public override void Dispose()
  {
   if (m_terrainTileService != null)
   {
    m_terrainTileService.Dispose();
    m_terrainTileService = null;
   }
  }
 }
}
