using System;
using System.Runtime.InteropServices;
namespace ShellLib
{
 [ComImport]
 [InterfaceType(ComInterfaceType.InterfaceIsIUnknown)]
 [Guid("9CC22886-DC8E-11d2-B1D0-00C04F8EEB3E")]
 public interface IFolderFilter
 {
  [PreserveSig]
  Int32 ShouldShow(
   [MarshalAs(UnmanagedType.Interface)]Object psf,
   IntPtr pidlFolder,
   IntPtr pidlItem);
  [PreserveSig]
  Int32 GetEnumFlags(
   [MarshalAs(UnmanagedType.Interface)]Object psf,
   IntPtr pidlFolder,
   IntPtr phwnd,
   out UInt32 pgrfFlags);
 };
}
