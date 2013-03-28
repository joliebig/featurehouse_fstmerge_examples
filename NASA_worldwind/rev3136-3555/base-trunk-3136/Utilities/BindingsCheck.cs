using System;
using System.Runtime.InteropServices;
using System.Windows.Forms;
namespace Utility
{
 public sealed class BindingsCheck
 {
  private BindingsCheck()
  {
  }
  [DllImport("Ws2_32.dll")]
  static extern int WSCEnumProtocols(IntPtr lpiProtocols, IntPtr lpProtocolBuffer, ref long lpdwBufferLength, ref int lpErrno);
  private const int WSAENOBUFS = 10055;
  static int DetermineProtocolBindings()
  {
   int errorNumber = 0;
   const long sizeOfOneProtocolInfoStructure = 628;
   long sizeOfAllProtocolInfoStructures = 0;
   WSCEnumProtocols(IntPtr.Zero, IntPtr.Zero, ref sizeOfAllProtocolInfoStructures, ref errorNumber);
   if(errorNumber != WSAENOBUFS) return -1;
   return (int)(sizeOfAllProtocolInfoStructures / sizeOfOneProtocolInfoStructure);
  }
  static bool IsBindingsHotFixApplied()
  {
   return System.Environment.Version >= new Version("1.1.4322.946");
  }
  public static bool FiftyBindingsWarning()
  {
   if(DetermineProtocolBindings() <= 50 || IsBindingsHotFixApplied()) return false;
   DialogResult userChoice =
    MessageBox.Show(
    "Your computer has a large number of protocols (over 50) installed,\n" +
    "which is not supported by the currently installed .NET version.\n\n" +
    "As a result, World Wind may not be able to download imagery.\n\n" +
    "Service pack 1 for the .NET framework 1.1 fixes this problem -\n" +
    "do you want to go to the download page now?\n\n" +
    "(Click No to continue launching World Wind)",
    "NASA World Wind: Fifty protocol bindings problem detected",
    MessageBoxButtons.YesNo
    );
   if(userChoice == DialogResult.Yes)
   {
    System.Diagnostics.Process.Start("iexplore", "http://msdn.microsoft.com/netframework/downloads/updates/default.aspx");
    return true;
   }
   return false;
  }
 }
}
