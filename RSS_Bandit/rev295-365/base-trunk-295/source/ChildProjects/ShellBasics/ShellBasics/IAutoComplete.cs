using System;
using System.Runtime.InteropServices;
namespace ShellLib
{
 [ComImport]
 [InterfaceType(ComInterfaceType.InterfaceIsIUnknown)]
 [Guid("00BB2762-6A77-11D0-A535-00C04FD7D062")]
 public interface IAutoComplete
 {
  [PreserveSig]
  Int32 Init(
   IntPtr hwndEdit,
   [MarshalAs(UnmanagedType.IUnknown)]
   Object punkACL,
   [MarshalAs(UnmanagedType.LPWStr)]
   String pwszRegKeyPath,
   [MarshalAs(UnmanagedType.LPWStr)]
   String pwszQuickComplete);
  [PreserveSig]
  Int32 Enable(
   Int32 fEnable);
 }
}
