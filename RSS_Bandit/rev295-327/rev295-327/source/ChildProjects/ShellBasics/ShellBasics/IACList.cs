using System; 
using System.Runtime.InteropServices; namespace  ShellLib {
	
 [ComImport]
 [InterfaceType(ComInterfaceType.InterfaceIsIUnknown)]
 [Guid("77A130B0-94FD-11D0-A544-00C04FD7D062")] 
 public interface  IACList {
		
  [PreserveSig]
  Int32 Expand(
   [MarshalAs(UnmanagedType.LPWStr)]
   String pszExpand);
	}

}
