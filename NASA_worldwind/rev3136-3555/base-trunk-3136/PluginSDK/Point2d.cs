using System;
namespace WorldWind
{
 public class Point2d
 {
  public double X, Y;
  public Point2d()
  {
  }
  public Point2d (double xi, double yi)
  {
   X = xi; Y = yi;
  }
  public Point2d (Point2d P)
  {
   X = P.X;
   Y = P.Y;
  }
  public double norm()
  {
   return Math.Sqrt(norm2());
  }
  public double norm2()
  {
   return X*X + Y*Y;
  }
  public Point2d normalize()
  {
   double n = norm();
   return new Point2d(X / n, Y / n);
  }
  public double Length
  {
   get
   {
    return Math.Sqrt(X * X + Y * Y);
   }
  }
  public static Point2d operator +(Point2d P1, Point2d P2)
  {
   return new Point2d (P1.X + P2.X, P1.Y + P2.Y);
  }
  public static Point2d operator -(Point2d P1, Point2d P2)
  {
   return new Point2d (P1.X - P2.X, P1.Y - P2.Y);
  }
  public static Point2d operator *(Point2d P, double k)
  {
   return new Point2d (P.X * k, P.Y * k);
  }
  public static Point2d operator *(double k, Point2d P)
  {
   return new Point2d (P.X * k, P.Y * k);
  }
  public static Point2d operator /(Point2d P, double k)
  {
   return new Point2d (P.X / k, P.Y / k);
  }
  public override bool Equals(object o)
  {
   try
   {
    return (bool)(this == (Point2d)o);
   }
   catch
   {
    return false;
   }
  }
  public override int GetHashCode()
  {
   return (int)(X * Y);
  }
  public static bool operator ==(Point2d P1, Point2d P2)
  {
   return (P1.X == P2.X && P1.Y == P2.Y);
  }
  public static bool operator !=(Point2d P1, Point2d P2)
  {
   return (P1.X != P2.X || P1.Y != P2.Y);
  }
  public static double dot(Point2d P1, Point2d P2)
  {
   return (P1.X * P2.X + P1.Y * P2.Y);
  }
  public static Point2d operator - ( Point2d P)
  {
   return new Point2d (-P.X, -P.Y);
  }
 }
}
