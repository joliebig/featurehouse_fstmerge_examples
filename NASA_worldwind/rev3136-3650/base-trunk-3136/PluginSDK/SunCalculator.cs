using System;
namespace WorldWind
{
 public class SunCalculator
 {
  public static Point3d GetGeocentricPosition(System.DateTime utcDateTime)
  {
   double JD = getJulianDay(utcDateTime);
   double T = (JD - 2451545.0) / 36525;
   double k = Math.PI / 180.0;
   double M = 357.52910 + 35999.05030 * T - 0.0001559 * T * T - 0.00000048 * T * T * T;
   double L0 = 280.46645 + 36000.76983 * T + 0.0003032 * T * T;
   double DL = (1.914600 - 0.004817 * T - 0.000014 * T * T) * Math.Sin(k * M) + (0.019993 - 0.000101 * T) * Math.Sin(k * 2 * M) + 0.000290 * Math.Sin(k * 3 * M);
   double L = L0 + DL;
   L = L % 360;
   double eps = 23.0 + 26.0 / 60.0 + 21.448 / 3600.0 - (46.8150 * T + 0.00059 * T * T - 0.001813 * T * T * T) / 3600;
   double X = Math.Cos(k * L);
   double Y = Math.Cos(k * eps)*Math.Sin(k * L);
   double Z = Math.Sin(k * eps)*Math.Sin(k * L);
   double R = Math.Sqrt(1.0 - Z * Z);
   double dec = (180 / Math.PI) * Math.Atan(Z / R);
   double RA = (24 / Math.PI) * Math.Atan(Y / (X + R));
   double theta0 = 280.46061837 + 360.98564736629*(JD-2451545.0) + 0.000387933*T*T - T*T*T/((double)38710000);
   theta0 = theta0 % 360;
   RA *= 15;
   double tau = theta0 - RA;
   Point3d pos = MathEngine.SphericalToCartesianD(
    Angle.FromDegrees(-dec),
    Angle.FromDegrees(-(tau - 180)),
    1);
   return pos;
  }
  private static double getJulianDay(System.DateTime gregorianDate)
  {
   double d = gregorianDate.Day +
    (double)gregorianDate.Hour / 24.0 +
    (double)gregorianDate.Minute / (24.0 * 60.0) +
    (double)gregorianDate.Second / (24.0 * 60.0 * 60.0) +
    (double)gregorianDate.Millisecond / (24.0 * 60.0 * 60.0 * 1000.0);
   int m = (gregorianDate.Month < 3 ? gregorianDate.Month + 12 : gregorianDate.Month);
   int j = (gregorianDate.Month < 3 ? gregorianDate.Year - 1 : gregorianDate.Year);
   int c = 2 - (int)Math.Floor((double)(j / 100)) + (int)Math.Floor((double)(j / 400));
   return (int)Math.Floor((double)(1461 * (j + 4716)) / 4) + (int)Math.Floor((double)(153 * (m + 1) / 5)) + d + c - 1524.5;
  }
 }
}
