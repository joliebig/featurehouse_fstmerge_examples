using System; 
using System.Runtime.InteropServices; namespace  ShellLib {
	
 public class  ShellFileOperation {
		
  public enum  FileOperations 
  {
   FO_MOVE = 0x0001,
   FO_COPY = 0x0002,
   FO_DELETE = 0x0003,
   FO_RENAME = 0x0004
  } 
  [Flags] 
  public enum  ShellFileOperationFlags 
  {
   FOF_MULTIDESTFILES = 0x0001,
   FOF_CONFIRMMOUSE = 0x0002,
   FOF_SILENT = 0x0004,
   FOF_RENAMEONCOLLISION = 0x0008,
   FOF_NOCONFIRMATION = 0x0010,
   FOF_WANTMAPPINGHANDLE = 0x0020,
   FOF_ALLOWUNDO = 0x0040,
   FOF_FILESONLY = 0x0080,
   FOF_SIMPLEPROGRESS = 0x0100,
   FOF_NOCONFIRMMKDIR = 0x0200,
   FOF_NOERRORUI = 0x0400,
   FOF_NOCOPYSECURITYATTRIBS = 0x0800,
   FOF_NORECURSION = 0x1000,
   FOF_NO_CONNECTED_ELEMENTS = 0x2000,
   FOF_WANTNUKEWARNING = 0x4000,
   FOF_NORECURSEREPARSE = 0x8000
  } 
  [Flags] 
  public enum  ShellChangeNotificationEvents  : uint 
  {
   SHCNE_RENAMEITEM = 0x00000001,
   SHCNE_CREATE = 0x00000002,
   SHCNE_DELETE = 0x00000004,
   SHCNE_MKDIR = 0x00000008,
   SHCNE_RMDIR = 0x00000010,
   SHCNE_MEDIAINSERTED = 0x00000020,
   SHCNE_MEDIAREMOVED = 0x00000040,
   SHCNE_DRIVEREMOVED = 0x00000080,
   SHCNE_DRIVEADD = 0x00000100,
   SHCNE_NETSHARE = 0x00000200,
   SHCNE_NETUNSHARE = 0x00000400,
   SHCNE_ATTRIBUTES = 0x00000800,
   SHCNE_UPDATEDIR = 0x00001000,
   SHCNE_UPDATEITEM = 0x00002000,
   SHCNE_SERVERDISCONNECT = 0x00004000,
   SHCNE_UPDATEIMAGE = 0x00008000,
   SHCNE_DRIVEADDGUI = 0x00010000,
   SHCNE_RENAMEFOLDER = 0x00020000,
   SHCNE_FREESPACE = 0x00040000,
   SHCNE_EXTENDED_EVENT = 0x04000000,
   SHCNE_ASSOCCHANGED = 0x08000000,
   SHCNE_DISKEVENTS = 0x0002381F,
   SHCNE_GLOBALEVENTS = 0x0C0581E0,
   SHCNE_ALLEVENTS = 0x7FFFFFFF,
   SHCNE_INTERRUPT = 0x80000000
  } 
  public enum  ShellChangeNotificationFlags 
  {
   SHCNF_IDLIST = 0x0000,
   SHCNF_PATHA = 0x0001,
   SHCNF_PRINTERA = 0x0002,
   SHCNF_DWORD = 0x0003,
   SHCNF_PATHW = 0x0005,
   SHCNF_PRINTERW = 0x0006,
   SHCNF_TYPE = 0x00FF,
   SHCNF_FLUSH = 0x1000,
   SHCNF_FLUSHNOWAIT = 0x2000
  } 
  public  FileOperations Operation;
 
  public  IntPtr OwnerWindow;
 
  public  ShellFileOperationFlags OperationFlags;
 
  public  String ProgressTitle;
 
  public  String[] SourceFiles;
 
  public  String[] DestFiles;
 
  public  ShellFileOperation()
  {
   Operation = FileOperations.FO_COPY;
   OwnerWindow = IntPtr.Zero;
   OperationFlags = ShellFileOperationFlags.FOF_ALLOWUNDO
    | ShellFileOperationFlags.FOF_MULTIDESTFILES
    | ShellFileOperationFlags.FOF_NO_CONNECTED_ELEMENTS
    | ShellFileOperationFlags.FOF_WANTNUKEWARNING;
   ProgressTitle = "";
  }
 
  public  bool DoOperation()
  {
   ShellApi.SHFILEOPSTRUCT FileOpStruct = new ShellApi.SHFILEOPSTRUCT();
   FileOpStruct.hwnd = OwnerWindow;
   FileOpStruct.wFunc = (uint)Operation;
   String multiSource = StringArrayToMultiString(SourceFiles);
   String multiDest = StringArrayToMultiString(DestFiles);
   FileOpStruct.pFrom = Marshal.StringToHGlobalUni(multiSource);
   FileOpStruct.pTo = Marshal.StringToHGlobalUni(multiDest);
   FileOpStruct.fFlags = (ushort)OperationFlags;
   FileOpStruct.lpszProgressTitle = ProgressTitle;
   FileOpStruct.fAnyOperationsAborted = 0;
   FileOpStruct.hNameMappings = IntPtr.Zero;
   int RetVal;
   RetVal = ShellApi.SHFileOperation(ref FileOpStruct);
   ShellApi.SHChangeNotify(
    (uint)ShellChangeNotificationEvents.SHCNE_ALLEVENTS,
    (uint)ShellChangeNotificationFlags.SHCNF_DWORD,
    IntPtr.Zero,
    IntPtr.Zero);
   if (RetVal!=0)
    return false;
   if (FileOpStruct.fAnyOperationsAborted != 0)
    return false;
   return true;
  }
 
  private  String StringArrayToMultiString(String[] stringArray)
  {
   String multiString = "";
   if (stringArray == null)
    return "";
   for (int i=0 ; i<stringArray.Length ; i++)
    multiString += stringArray[i] + '\0';
   multiString += '\0';
   return multiString;
  }

	}

}
