using System;
using System.Diagnostics;
namespace RssBandit.Common.Logging {
 internal class ProfilerHelper {
  private static long seqFreq = 0;
  [System.Runtime.InteropServices.DllImport("KERNEL32")]
  private static extern bool QueryPerformanceCounter(
   ref long lpPerformanceCount);
  [System.Runtime.InteropServices.DllImport("KERNEL32")]
  private static extern bool QueryPerformanceFrequency(
   ref long lpFrequency);
  static ProfilerHelper() {
   QueryPerformanceFrequency(ref seqFreq);
  }
  public static void StartMeasure(ref long secStart) {
   QueryPerformanceCounter(ref secStart);
  }
  public static double StopMeasure(long secStart) {
   long secTiming = 0;
   QueryPerformanceCounter(ref secTiming);
   if (seqFreq == 0) return 0.0;
   return (double)(secTiming - secStart) / (double)seqFreq;
  }
  public static string StopMeasureString(long secStart) {
   return String.Format("{0:0.###} sec(s)", StopMeasure(secStart));
  }
 }
}
