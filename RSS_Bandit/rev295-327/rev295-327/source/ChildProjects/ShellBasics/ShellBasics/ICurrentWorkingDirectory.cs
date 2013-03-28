using System; 
using System.Runtime.InteropServices; namespace  ShellLib {
	
 [ComImport]
 [InterfaceType(ComInterfaceType.InterfaceIsIUnknown)]
 [Guid("91956D21-9276-11D1-921A-006097DF5BD4")] 
 public interface  ICurrentWorkingDirectory {
		
  [PreserveSig]
  Int32 GetDirectory(
   [MarshalAs(UnmanagedType.LPWStr)]
   String pwzPath,
   UInt32 cchSize); 
  [PreserveSig]
  Int32 SetDirectory(
   [MarshalAs(UnmanagedType.LPWStr)]
   String pwzPath);
	}

}
