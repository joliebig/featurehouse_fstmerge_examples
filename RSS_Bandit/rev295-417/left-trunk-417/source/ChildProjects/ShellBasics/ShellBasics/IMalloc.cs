using System;
using System.Runtime.InteropServices;
namespace ShellLib
{
 [ComImport]
 [InterfaceType(ComInterfaceType.InterfaceIsIUnknown)]
 [Guid("00000002-0000-0000-C000-000000000046")]
 public interface IMalloc
 {
  [PreserveSig]
  IntPtr Alloc(
   UInt32 cb);
  [PreserveSig]
  IntPtr Realloc(
   IntPtr pv,
   UInt32 cb);
  [PreserveSig]
  void Free(
   IntPtr pv);
  [PreserveSig]
  UInt32 GetSize(
   IntPtr pv);
  [PreserveSig]
  Int16 DidAlloc(
   IntPtr pv);
  [PreserveSig]
  void HeapMinimize();
 }
}
