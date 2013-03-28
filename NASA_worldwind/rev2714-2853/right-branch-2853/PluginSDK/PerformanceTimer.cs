using System;
using System.Runtime.InteropServices;
namespace WorldWind
{
 public sealed class PerformanceTimer
 {
  public static long TicksPerSecond;
   private PerformanceTimer()
  {
  }
  static PerformanceTimer()
  {
   long tickFrequency = 0;
   if (!QueryPerformanceFrequency(ref tickFrequency))
    throw new NotSupportedException("The machine doesn't appear to support high resolution timer.");
   TicksPerSecond = tickFrequency;
   System.Diagnostics.Debug.WriteLine("tickFrequency = " + tickFrequency);
  }
  [System.Security.SuppressUnmanagedCodeSecurity]
  [DllImport("kernel32")]
  private static extern bool QueryPerformanceFrequency(ref long PerformanceFrequency);
  [System.Security.SuppressUnmanagedCodeSecurity]
  [DllImport("kernel32")]
  public static extern bool QueryPerformanceCounter(ref long PerformanceCount);
 }
}
