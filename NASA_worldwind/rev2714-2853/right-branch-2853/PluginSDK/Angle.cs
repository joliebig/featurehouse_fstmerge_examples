using System;
using System.Globalization;
namespace WorldWind
{
 public struct Angle
 {
  [NonSerialized]
  public double Radians;
  public static Angle FromRadians(double radians)
  {
   Angle res = new Angle();
   res.Radians = radians;
   return res;
  }
  public static Angle FromDegrees(double degrees)
  {
   Angle res = new Angle();
   res.Radians = Math.PI * degrees / 180.0;
   return res;
  }
  public static readonly Angle Zero;
  public static readonly Angle MinValue = Angle.FromRadians(double.MinValue);
  public static readonly Angle MaxValue = Angle.FromRadians(double.MaxValue);
  public static readonly Angle NaN = Angle.FromRadians(double.NaN);
  public double Degrees
  {
   get { return MathEngine.RadiansToDegrees(this.Radians);}
   set { this.Radians = MathEngine.DegreesToRadians(value); }
  }
  public static Angle Abs( Angle a )
  {
   return Angle.FromRadians(Math.Abs(a.Radians));
  }
  public static bool IsNaN(Angle a)
  {
   return double.IsNaN(a.Radians);
  }
  public override bool Equals(object obj)
  {
   if (obj == null || GetType() != obj.GetType())
    return false;
   Angle a = (Angle)obj;
   return Math.Abs(Radians - a.Radians) < Single.Epsilon;
  }
  public static bool operator ==(Angle a, Angle b) {
   return Math.Abs(a.Radians - b.Radians) < Single.Epsilon;
  }
  public static bool operator !=(Angle a, Angle b) {
   return Math.Abs(a.Radians - b.Radians) > Single.Epsilon;
  }
  public static bool operator <(Angle a, Angle b)
  {
   return a.Radians < b.Radians;
  }
  public static bool operator >(Angle a, Angle b)
  {
   return a.Radians > b.Radians;
  }
  public static Angle operator +(Angle a, Angle b)
  {
   double res = a.Radians + b.Radians;
   return Angle.FromRadians(res);
  }
  public static Angle operator -(Angle a, Angle b)
  {
   double res = a.Radians - b.Radians;
   return Angle.FromRadians(res);
  }
  public static Angle operator *(Angle a, double times)
  {
   return Angle.FromRadians(a.Radians * times);
  }
  public static Angle operator *(double times, Angle a)
  {
   return Angle.FromRadians(a.Radians * times);
  }
  public static Angle operator /(double divisor, Angle a)
  {
   return Angle.FromRadians(a.Radians / divisor);
  }
  public static Angle operator /(Angle a, double divisor)
  {
   return Angle.FromRadians(a.Radians / divisor);
  }
  public override int GetHashCode()
  {
   return (int)(Radians*100000);
  }
  public void Normalize()
  {
   if(Radians>Math.PI*2)
    Radians -= Math.PI*2;
   if(Radians<-Math.PI*2)
    Radians += Math.PI*2;
  }
  public string ToStringDms()
  {
   double decimalDegrees = this.Degrees;
   double d = Math.Abs(decimalDegrees);
   double m = (60*(d-Math.Floor(d)));
   double s = (60*(m-Math.Floor(m)));
   return String.Format("{0}{1}'{2:f3}\"",
    (int)d*Math.Sign(decimalDegrees),
    (int)m,
    s);
  }
  public override string ToString()
  {
   return Degrees.ToString(CultureInfo.InvariantCulture)+"";
  }
 }
}
