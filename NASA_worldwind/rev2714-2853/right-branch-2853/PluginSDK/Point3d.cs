using System;
namespace WorldWind
{
 public class Point3d
 {
  public double X, Y, Z;
  public Point3d()
  {
  }
  public Point3d cross(Point3d p)
  {
   return new Point3d(
    Y * p.Z - Z * p.Y,
    Z * p.X - X * p.Z,
    X * p.Y - Y * p.X
    );
  }
  public double dotProduct(Point3d p)
  {
   return X * p.X + Y * p.Y + Z * p.Z;
  }
  public Point3d (double xi, double yi, double zi)
  {
   X = xi; Y = yi; Z = zi;
  }
  public Point3d (Point3d P)
  {
   X = P.X;
   Y = P.Y;
   X = P.Z;
  }
  public double norm()
  {
   return Math.Sqrt(norm2());
  }
  public double norm2()
  {
   return X*X + Y*Y + Z*Z;
  }
  public Point3d normalize()
  {
   double n = norm();
   return new Point3d(X / n, Y / n, Z / n);
  }
  public double Length
  {
   get
   {
    return Math.Sqrt(X * X + Y * Y + Z * Z);
   }
  }
  public static Angle GetAngle(Point3d p1, Point3d p2)
  {
   Angle returnAngle = new Angle();
   returnAngle.Radians = Math.Acos(Point3d.dot(p1, p2) / (p1.Length * p2.Length));
   return returnAngle;
  }
  public static Point3d operator +(Point3d P1, Point3d P2)
  {
   return new Point3d (P1.X + P2.X, P1.Y + P2.Y, P1.Z + P2.Z);
  }
  public static Point3d operator -(Point3d P1, Point3d P2)
  {
   return new Point3d (P1.X - P2.X, P1.Y - P2.Y, P1.Z - P2.Z);
  }
  public static Point3d operator *(Point3d P, double k)
  {
   return new Point3d (P.X * k, P.Y * k, P.Z * k);
  }
  public static Point3d operator *(double k, Point3d P)
  {
   return new Point3d (P.X * k, P.Y * k, P.Z * k);
  }
  public static Point3d operator /(Point3d P, double k)
  {
   return new Point3d (P.X / k, P.Y / k, P.Z / k);
  }
  public override bool Equals(object o)
  {
   try
   {
    return (bool)(this == (Point3d)o);
   }
   catch
   {
    return false;
   }
  }
  public override int GetHashCode()
  {
   return (int)(X * Y * Z);
  }
  public static bool operator ==(Point3d P1, Point3d P2)
  {
   return (P1.X == P2.X && P1.Y == P2.Y && P1.Z == P2.Z);
  }
  public static bool operator !=(Point3d P1, Point3d P2)
  {
   return (P1.X != P2.X || P1.Y != P2.Y || P1.Z != P2.Z);
  }
  public static double dot(Point3d P1, Point3d P2)
  {
   return (P1.X * P2.X + P1.Y * P2.Y + P1.Z * P2.Z);
  }
  public static Point3d operator *(Point3d P1, Point3d P2)
  {
   return new Point3d (P1.Y * P2.Z - P1.Z * P2.Y,
    P1.Z * P2.X - P1.X * P2.Z, P1.X * P2.Y - P1.Y * P2.X);
  }
  public static Point3d operator - ( Point3d P)
  {
   return new Point3d (-P.X, -P.Y, -P.Z);
  }
  public static Point3d cross(Point3d P1, Point3d P2)
  {
   return P1 * P2;
  }
  public Point3d unit_normal (Point3d P0, Point3d P1, Point3d P2)
  {
   Point3d p = (P1 - P0) * (P2 - P0);
   double l = p.norm ();
   return new Point3d (p.X / l, p.Y / l, p.Z / l);
  }
 }
}
