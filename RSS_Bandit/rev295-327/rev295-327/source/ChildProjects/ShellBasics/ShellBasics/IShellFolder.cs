using System; 
using System.Runtime.InteropServices; namespace  ShellLib {
	
 [ComImport]
 [InterfaceType(ComInterfaceType.InterfaceIsIUnknown)]
 [Guid("000214E6-0000-0000-C000-000000000046")] 
 public interface  IShellFolder {
		
  [PreserveSig]
  Int32 ParseDisplayName(
   IntPtr hwnd,
   IntPtr pbc,
   [MarshalAs(UnmanagedType.LPWStr)]
   String pszDisplayName,
   ref UInt32 pchEaten,
   out IntPtr ppidl,
   ref UInt32 pdwAttributes); 
  [PreserveSig]
  Int32 EnumObjects(
   IntPtr hwnd,
   Int32 grfFlags,
   out IntPtr ppenumIDList); 
  [PreserveSig]
  Int32 BindToObject(
   IntPtr pidl,
   IntPtr pbc,
   Guid riid,
   out IntPtr ppv); 
  [PreserveSig]
  Int32 BindToStorage(
   IntPtr pidl,
   IntPtr pbc,
   Guid riid,
   out IntPtr ppv); 
  [PreserveSig]
  Int32 CompareIDs(
   Int32 lParam,
   IntPtr pidl1,
   IntPtr pidl2); 
  [PreserveSig]
  Int32 CreateViewObject(
   IntPtr hwndOwner,
   Guid riid,
   out IntPtr ppv); 
  [PreserveSig]
  Int32 GetAttributesOf(
   UInt32 cidl,
   [MarshalAs(UnmanagedType.LPArray, SizeParamIndex=0)]
   IntPtr[] apidl,
   ref UInt32 rgfInOut); 
  [PreserveSig]
  Int32 GetUIObjectOf(
   IntPtr hwndOwner,
   UInt32 cidl,
   IntPtr[] apidl,
   Guid riid,
   ref UInt32 rgfReserved,
   out IntPtr ppv); 
  [PreserveSig]
  Int32 GetDisplayNameOf(
   IntPtr pidl,
   UInt32 uFlags,
   out ShellApi.STRRET pName); 
  [PreserveSig]
  Int32 SetNameOf(
   IntPtr hwnd,
   IntPtr pidl,
   [MarshalAs(UnmanagedType.LPWStr)]
   String pszName,
   UInt32 uFlags,
   out IntPtr ppidlOut);
	}

}
