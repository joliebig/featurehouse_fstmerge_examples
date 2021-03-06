using System;
namespace Microsoft.Office.OneNote
{
 public sealed class UnitConversions
 {
  private UnitConversions()
  {
  }
  public static double InchesToPoints(double inches)
  {
   if (inches > (1000000 / POINTS_PER_INCH))
    throw new ArgumentOutOfRangeException("inches", "Points cannot exceed 1000000");
   return inches*POINTS_PER_INCH;
  }
  public static double PointsToInches(double points)
  {
   if (points > 1000000)
    throw new ArgumentOutOfRangeException("points", "Points cannot exceed 1000000");
   return points*((double) 1/POINTS_PER_INCH);
  }
  public const int POINTS_PER_INCH = 72;
 }
}
