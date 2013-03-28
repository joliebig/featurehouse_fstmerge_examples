using System;
namespace ThoughtWorks.CruiseControl.Core.Util
{
 public class DateUtil
 {
  public const string DateOutputFormat = "yyyy-MM-dd HH:mm:ss";
  public static string FormatDate(DateTime date)
  {
   return date.ToString(DateOutputFormat);
  }
  public static string FormatDate(DateTime date, IFormatProvider formatter)
  {
   return date.ToString(DateOutputFormat, formatter);
  }
  public static DateTime MaxDate(DateTime a, DateTime b)
  {
   return (a > b) ? a : b;
  }
  public static DateTime ConvertFromUnixTimestamp(double timestamp)
  {
   return new DateTime(1970, 1, 1, 0, 0, 0, 0, DateTimeKind.Utc).AddSeconds(timestamp).ToLocalTime();
  }
  public static double ConvertToUnixTimestamp(DateTime dateTime)
  {
   return dateTime.ToUniversalTime().Subtract(new DateTime(1970, 1, 1, 0, 0, 0, 0, DateTimeKind.Utc)).TotalSeconds;
  }
 }
}
