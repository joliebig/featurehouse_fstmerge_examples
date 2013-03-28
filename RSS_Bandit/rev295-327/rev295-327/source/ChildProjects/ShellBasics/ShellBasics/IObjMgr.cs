using System; 
using System.Runtime.InteropServices; namespace  ShellLib {
	
 [ComImport]
 [InterfaceType(ComInterfaceType.InterfaceIsIUnknown)]
 [Guid("00BB2761-6A77-11D0-A535-00C04FD7D062")] 
 public interface  IObjMgr {
		
  [PreserveSig]
  Int32 Append(
   [MarshalAs(UnmanagedType.IUnknown)]
   Object punk); 
  [PreserveSig]
  Int32 Remove(
   [MarshalAs(UnmanagedType.IUnknown)]
   Object punk);
	}

}
