using System;
using System.Globalization;
using System.Text;
using System.Web;
using WorldWind;
using WorldWind.Camera;
namespace WorldWind.Net
{
 public class WorldWindUri
 {
  private string _world = "Earth";
  private Angle _bank = Angle.NaN;
  private Angle _latitude = Angle.NaN;
  private Angle _longitude = Angle.NaN;
  private Angle _viewRange = Angle.NaN;
  private Angle _tilt = Angle.NaN;
  private Angle _direction = Angle.NaN;
  private double _altitude = double.NaN;
  private string _layer;
  private string _rawUrl = "";
        private string _preserveCase = "";
  public const string Scheme = "worldwind";
  public WorldWindUri()
  {
  }
  public WorldWindUri(string worldName, CameraBase camera )
  {
   _world = worldName;
   _latitude=camera.Latitude;
   _longitude=camera.Longitude;
   _altitude=camera.Altitude;
   if (Math.Abs(camera.Heading.Degrees)>0.1)
    _direction = camera.Heading;
   if (Math.Abs(camera.Tilt.Degrees)>0.1)
    _tilt = camera.Tilt;
   if (Math.Abs(camera.Bank.Degrees)>0.1)
    _bank = camera.Bank;
  }
  public static WorldWindUri Parse( string worldWindUri )
  {
   try
   {
                WorldWindUri uri = new WorldWindUri();
                worldWindUri = worldWindUri.Trim();
                uri._preserveCase = worldWindUri;
                worldWindUri = worldWindUri.ToLower(CultureInfo.InvariantCulture);
                uri._rawUrl = worldWindUri;
    if(!worldWindUri.StartsWith( Scheme + "://"))
     throw new UriFormatException("Invalid protocol, expected " + Scheme + "://");
    string url = worldWindUri.Replace( Scheme + "://", "");
    if(url.Length == 0)
     throw new UriFormatException("Incomplete URI");
    string[] functions = url.Split('!');
    foreach(string function in functions)
    {
     if(function.IndexOf("goto/") == 0)
     {
      string functionString = function.Replace("goto/", "").Trim();
      string[] functionParameters = functionString.Split('&');
      foreach(string curParam in functionParameters)
      {
       string[] nv = curParam.Split(new char[]{'='},2);
       if (nv.Length!=2)
        continue;
       string key = nv[0].ToLower();
       string value = nv[1];
       double doubleVal = double.NaN;
       string urlDecodedValue = HttpUtility.UrlDecode(value);
       double.TryParse(value, NumberStyles.Any, CultureInfo.InvariantCulture, out doubleVal);
       switch(key.ToLower())
       {
        case "lat":
        case "latitude":
         uri.Latitude = Angle.FromDegrees(doubleVal);
         break;
        case "lon":
        case "longitude":
         uri.Longitude = Angle.FromDegrees(doubleVal);
         break;
        case "altitude":
        case "alt":
         uri.Altitude = doubleVal;
         break;
        case "view":
        case "viewrange":
         uri.ViewRange = Angle.FromDegrees(doubleVal);
         break;
        case "bank":
         uri.Bank = Angle.FromDegrees(doubleVal);
         break;
        case "dir":
        case "direction":
         uri.Direction = Angle.FromDegrees(doubleVal);
         break;
        case "tilt":
         uri.Tilt = Angle.FromDegrees(doubleVal);
         break;
        case "world":
         uri.World = value;
         break;
        case "layer":
         uri.Layer = urlDecodedValue;
         break;
       }
      }
     }
    }
    return uri;
   }
   catch(Exception caught)
   {
    throw new UriFormatException( "The worldwind:// URI could not be parsed. (The URI used was: "+worldWindUri+" and the error generated was: "+caught.Message+")");
   }
  }
  public string PreserveCase
  {
   get
   {
    return this._preserveCase;
   }
   set
   {
    this._preserveCase = value;
   }
  }
  public string RawUrl
  {
   get
   {
    return _rawUrl;
   }
   set
   {
    _rawUrl = value;
   }
  }
  public string World
  {
   get
   {
    return _world;
   }
   set
   {
    _world = value;
   }
  }
  public Angle Latitude
  {
   get
   {
    return _latitude;
   }
   set
   {
    _latitude = value;
   }
  }
  public Angle Longitude
  {
   get
   {
    return _longitude;
   }
   set
   {
    _longitude = value;
   }
  }
  public double Altitude
  {
   get
   {
    return _altitude;
   }
   set
   {
    _altitude = value;
   }
  }
  public Angle ViewRange
  {
   get
   {
    return _viewRange;
   }
   set
   {
    _viewRange = value;
   }
  }
  public Angle Direction
  {
   get
   {
    return _direction;
   }
   set
   {
    _direction = value;
   }
  }
  public Angle Tilt
  {
   get
   {
    return _tilt;
   }
   set
   {
    _tilt = value;
   }
  }
  public Angle Bank
  {
   get
   {
    return _bank;
   }
   set
   {
    _bank = value;
   }
  }
  public string Layer
  {
   get
   {
    return _layer;
   }
   set
   {
    _layer = value;
   }
  }
  public override string ToString()
  {
   return ToString(true);
  }
  public string ToString(bool bEnableFiltering)
  {
   StringBuilder sb = new StringBuilder( Scheme + "://goto/" );
   sb.Append("world=");
   sb.Append(this.World);
   sb.Append("&lat=");
   sb.Append( _latitude.Degrees.ToString("f5", CultureInfo.InvariantCulture));
   sb.Append("&lon=");
   sb.Append( _longitude.Degrees.ToString("f5", CultureInfo.InvariantCulture));
   if (!Angle.IsNaN(_viewRange))
    sb.Append("&view=" + _viewRange.Degrees.ToString("f1", CultureInfo.InvariantCulture));
   else if (!double.IsNaN(_altitude))
    sb.Append("&alt=" + _altitude.ToString("f0", CultureInfo.InvariantCulture));
   if (!Angle.IsNaN(_direction) && (Math.Abs(_direction.Degrees)>0.1 || !bEnableFiltering))
    sb.Append("&dir=" + _direction.Degrees.ToString("f1", CultureInfo.InvariantCulture));
   if (!Angle.IsNaN(_tilt) && (Math.Abs(_tilt.Degrees)<89.5 || !bEnableFiltering))
    sb.Append("&tilt=" + _tilt.Degrees.ToString("f1", CultureInfo.InvariantCulture));
   if (!Angle.IsNaN(_bank) && (Math.Abs(_bank.Degrees)>0.1 || !bEnableFiltering))
    sb.Append("&bank=" + _bank.Degrees.ToString("f1", CultureInfo.InvariantCulture));
   if(_layer != null)
    sb.Append("&layer=" + HttpUtility.UrlEncode(_layer));
   return sb.ToString();
  }
 }
}
