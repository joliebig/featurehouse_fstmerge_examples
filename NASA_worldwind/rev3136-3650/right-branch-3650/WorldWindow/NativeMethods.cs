using System;
using System.Security.Permissions;
using System.Runtime.InteropServices;
namespace WorldWind.Interop
{
 public sealed class NativeMethods
 {
  private NativeMethods()
  {
  }
  [StructLayout(LayoutKind.Sequential)]
  public struct Message
  {
   public IntPtr hWnd;
   public uint msg;
   public IntPtr wParam;
   public IntPtr lParam;
   public uint time;
   public System.Drawing.Point p;
  }
  [System.Security.SuppressUnmanagedCodeSecurity]
  [DllImport("User32.dll", CharSet=CharSet.Auto)]
  public static extern bool PeekMessage(out Message msg, IntPtr hWnd, uint messageFilterMin, uint messageFilterMax, uint flags);
 }
}
