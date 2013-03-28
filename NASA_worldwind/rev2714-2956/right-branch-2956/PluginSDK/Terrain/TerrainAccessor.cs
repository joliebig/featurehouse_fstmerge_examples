using System;
namespace WorldWind.Terrain
{
 public abstract class TerrainAccessor : IDisposable
 {
  protected string m_name;
  protected double m_north;
  protected double m_south;
  protected double m_east;
  protected double m_west;
  public string Name
  {
   get
   {
    return m_name;
   }
   set
   {
    m_name = value;
   }
  }
  public double North
  {
   get
   {
    return m_north;
   }
   set
   {
    m_north = value;
   }
  }
  public double South
  {
   get
   {
    return m_south;
   }
   set
   {
    m_south = value;
   }
  }
  public double West
  {
   get
   {
    return m_west;
   }
   set
   {
    m_west = value;
   }
  }
  public double East
  {
   get
   {
    return m_east;
   }
   set
   {
    m_east = value;
   }
  }
  public abstract float GetElevationAt(double latitude, double longitude, double targetSamplesPerDegree);
  public virtual float GetElevationAt(double latitude, double longitude)
  {
   return GetElevationAt(latitude, longitude, 0);
  }
  public virtual TerrainTile GetElevationArray(double north, double south, double west, double east, int samples)
  {
   TerrainTile res = null;
   res = new TerrainTile(null);
   res.North = north;
   res.South = south;
   res.West = west;
   res.East = east;
   res.SamplesPerTile = samples;
   res.IsInitialized = true;
   res.IsValid = true;
   double latrange = Math.Abs(north - south);
   double lonrange = Math.Abs(east - west);
   float[,] data = new float[samples,samples];
   float scaleFactor = (float)1/(samples - 1);
   for(int x = 0; x < samples; x++)
   {
    for(int y = 0; y < samples; y++)
    {
     double curLat = north - scaleFactor * latrange * x;
     double curLon = west + scaleFactor * lonrange * y;
     data[x,y] = GetElevationAt(curLat, curLon, 0);
    }
   }
   res.ElevationData = data;
   return res;
  }
  public virtual void Dispose()
  {
  }
 }
}
