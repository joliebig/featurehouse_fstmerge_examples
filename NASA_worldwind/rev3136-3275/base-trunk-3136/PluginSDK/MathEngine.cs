using System;
using Microsoft.DirectX;
namespace WorldWind
{
 public sealed class MathEngine
 {
  private MathEngine()
  {
  }
  public static Vector3 SphericalToCartesian(
   double latitude,
   double longitude,
   double radius
   )
  {
   latitude *= System.Math.PI / 180.0;
   longitude *= System.Math.PI /180.0;
   double radCosLat = radius * Math.Cos(latitude);
   return new Vector3(
    (float)(radCosLat * Math.Cos(longitude)),
    (float)(radCosLat * Math.Sin(longitude)),
    (float)(radius * Math.Sin(latitude)) );
  }
  public static Vector3 SphericalToCartesian(
   Angle latitude,
   Angle longitude,
   double radius )
  {
   double latRadians = latitude.Radians;
   double lonRadians = longitude.Radians;
   double radCosLat = radius * Math.Cos(latRadians);
   return new Vector3(
    (float)(radCosLat * Math.Cos(lonRadians)),
    (float)(radCosLat * Math.Sin(lonRadians)),
    (float)(radius * Math.Sin(latRadians)));
  }
  public static Point3d SphericalToCartesianD(
   Angle latitude,
   Angle longitude,
   double radius )
  {
   double latRadians = latitude.Radians;
   double lonRadians = longitude.Radians;
   double radCosLat = radius * Math.Cos(latRadians);
   return new Point3d(
    radCosLat * Math.Cos(lonRadians),
    radCosLat * Math.Sin(lonRadians),
    radius * Math.Sin(latRadians));
  }
  public static Vector3 CartesianToSpherical(float x, float y, float z)
  {
   double rho = Math.Sqrt((double)(x * x + y * y + z * z));
   float longitude = (float)Math.Atan2(y,x);
   float latitude = (float)(Math.Asin(z / rho));
   return new Vector3((float)rho, latitude, longitude);
  }
  public static Point3d CartesianToSphericalD(double x, double y, double z)
  {
   double rho = Math.Sqrt((double)(x * x + y * y + z * z));
   double longitude = Math.Atan2(y,x);
   double latitude = (Math.Asin(z / rho));
   return new Point3d(rho, latitude, longitude);
  }
  public static double DegreesToRadians(double degrees)
  {
   return Math.PI * degrees / 180.0;
  }
  public static double RadiansToDegrees(double radians)
  {
   return radians * 180.0 / Math.PI;
  }
  public static double SphericalDistanceDegrees(double latA, double lonA, double latB, double lonB)
  {
   double radLatA = MathEngine.DegreesToRadians(latA);
   double radLatB = MathEngine.DegreesToRadians(latB);
   double radLonA = MathEngine.DegreesToRadians(lonA);
   double radLonB = MathEngine.DegreesToRadians(lonB);
   return MathEngine.RadiansToDegrees(
    Math.Acos(Math.Cos(radLatA)*Math.Cos(radLatB)*Math.Cos(radLonA-radLonB)+Math.Sin(radLatA)*Math.Sin(radLatB)));
  }
  public static Angle SphericalDistance(Angle latA, Angle lonA, Angle latB, Angle lonB)
  {
   double radLatA = latA.Radians;
   double radLatB = latB.Radians;
   double radLonA = lonA.Radians;
   double radLonB = lonB.Radians;
   return Angle.FromRadians( Math.Acos(
    Math.Cos(radLatA)*Math.Cos(radLatB)*Math.Cos(radLonA-radLonB)+
    Math.Sin(radLatA)*Math.Sin(radLatB)) );
  }
  public static Angle Azimuth( Angle latA, Angle lonA, Angle latB, Angle lonB )
  {
   double cosLatB = Math.Cos(latB.Radians);
   Angle tcA = Angle.FromRadians( Math.Atan2(
    Math.Sin(lonA.Radians - lonB.Radians) * cosLatB,
    Math.Cos(latA.Radians) * Math.Sin(latB.Radians) -
    Math.Sin(latA.Radians) * cosLatB *
    Math.Cos(lonA.Radians - lonB.Radians)));
   if(tcA.Radians < 0)
    tcA.Radians = tcA.Radians + Math.PI*2;
   tcA.Radians = Math.PI*2 - tcA.Radians;
   return tcA;
  }
  public static Quaternion EulerToQuaternion(double yaw, double pitch, double roll)
  {
   double cy = Math.Cos(yaw * 0.5);
   double cp = Math.Cos(pitch * 0.5);
   double cr = Math.Cos(roll * 0.5);
   double sy = Math.Sin(yaw * 0.5);
   double sp = Math.Sin(pitch * 0.5);
   double sr = Math.Sin(roll * 0.5);
   double qw = cy*cp*cr + sy*sp*sr;
   double qx = sy*cp*cr - cy*sp*sr;
   double qy = cy*sp*cr + sy*cp*sr;
   double qz = cy*cp*sr - sy*sp*cr;
   return new Quaternion((float)qx, (float)qy, (float)qz, (float)qw);
  }
  public static Vector3 QuaternionToEuler(Quaternion q)
  {
   double q0 = q.W;
   double q1 = q.X;
   double q2 = q.Y;
   double q3 = q.Z;
   double x = Math.Atan2( 2 * (q2*q3 + q0*q1), (q0*q0 - q1*q1 - q2*q2 + q3*q3));
   double y = Math.Asin( -2 * (q1*q3 - q0*q2));
   double z = Math.Atan2( 2 * (q1*q2 + q0*q3), (q0*q0 + q1*q1 - q2*q2 - q3*q3));
   return new Vector3((float)x, (float)y, (float)z);
  }
  public static int GetRowFromLatitude(double latitude, double tileSize)
  {
   return (int)System.Math.Round((System.Math.Abs(-90.0 - latitude)%180)/tileSize, 1);
  }
  public static int GetRowFromLatitude(Angle latitude, double tileSize)
  {
   return (int)System.Math.Round((System.Math.Abs(-90.0 - latitude.Degrees)%180)/tileSize, 1);
  }
  public static int GetColFromLongitude(double longitude, double tileSize)
  {
   return (int)System.Math.Round((System.Math.Abs(-180.0 - longitude)%360)/tileSize, 1);
  }
  public static int GetColFromLongitude(Angle longitude, double tileSize)
  {
   return (int)System.Math.Round((System.Math.Abs(-180.0 - longitude.Degrees)%360)/tileSize, 1);
  }
  public static float DistancePlaneToPoint(Plane p, Vector3 v)
  {
   return p.A * v.X + p.B * v.Y + p.C + v.Z + p.D;
  }
  public static double Hypot( double x, double y )
  {
   return Math.Sqrt(x*x + y*y);
  }
 }
}
