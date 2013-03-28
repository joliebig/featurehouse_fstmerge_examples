using System;
using System.Runtime.InteropServices;
namespace ShellLib
{
 [ComImport]
 [InterfaceType(ComInterfaceType.InterfaceIsIUnknown)]
 [Guid("470141A0-5186-11D2-BBB6-0060977B464C")]
 public interface IACList2
 {
  [PreserveSig]
  Int32 SetOptions(
   UInt32 dwFlag);
  [PreserveSig]
  Int32 GetOptions(
   out UInt32 pdwFlag);
 }
}
