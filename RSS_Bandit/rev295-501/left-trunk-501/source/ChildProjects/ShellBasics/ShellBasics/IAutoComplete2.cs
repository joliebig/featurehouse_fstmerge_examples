using System;
using System.Runtime.InteropServices;
namespace ShellLib
{
 [ComImport]
 [InterfaceType(ComInterfaceType.InterfaceIsIUnknown)]
 [Guid("EAC04BC0-3791-11D2-BB95-0060977B464C")]
 public interface IAutoComplete2
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
  [PreserveSig]
  Int32 SetOptions(
   UInt32 dwFlag);
  [PreserveSig]
  Int32 GetOptions(
   out UInt32 pdwFlag);
 }
}
