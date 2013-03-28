using System; 
using System.Runtime.InteropServices; namespace  ShellLib {
	
 [ComImport]
 [InterfaceType(ComInterfaceType.InterfaceIsIUnknown)]
 [Guid("C0A651F5-B48B-11d2-B5ED-006097C686F6")] 
 public interface  IFolderFilterSite {
		
  [PreserveSig]
  Int32 SetFilter(
   [MarshalAs(UnmanagedType.Interface)]Object punk);
	}

}
