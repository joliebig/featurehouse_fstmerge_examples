using System; namespace  ShellLib {
	
 public class  ShellExecute {
		
  public enum  ShowWindowCommands 
  {
   SW_HIDE = 0,
   SW_SHOWNORMAL = 1,
   SW_NORMAL = 1,
   SW_SHOWMINIMIZED = 2,
   SW_SHOWMAXIMIZED = 3,
   SW_MAXIMIZE = 3,
   SW_SHOWNOACTIVATE = 4,
   SW_SHOW = 5,
   SW_MINIMIZE = 6,
   SW_SHOWMINNOACTIVE = 7,
   SW_SHOWNA = 8,
   SW_RESTORE = 9,
   SW_SHOWDEFAULT = 10,
  } 
  public enum  ShellExecuteReturnCodes 
  {
   ERROR_OUT_OF_MEMORY = 0,
   ERROR_FILE_NOT_FOUND = 2,
   ERROR_PATH_NOT_FOUND = 3,
   ERROR_BAD_FORMAT = 11,
   SE_ERR_ACCESSDENIED = 5,
   SE_ERR_ASSOCINCOMPLETE = 27,
   SE_ERR_DDEBUSY = 30,
   SE_ERR_DDEFAIL = 29,
   SE_ERR_DDETIMEOUT = 28,
   SE_ERR_DLLNOTFOUND = 32,
   SE_ERR_FNF = 2,
   SE_ERR_NOASSOC = 31,
   SE_ERR_OOM = 8,
   SE_ERR_PNF = 3,
   SE_ERR_SHARE = 26,
  } 
  [Flags] 
  public enum  ShellExecuteFlags 
  {
   SEE_MASK_CLASSNAME = 0x00000001,
   SEE_MASK_CLASSKEY = 0x00000003,
   SEE_MASK_IDLIST = 0x00000004,
   SEE_MASK_INVOKEIDLIST = 0x0000000c,
   SEE_MASK_ICON = 0x00000010,
   SEE_MASK_HOTKEY = 0x00000020,
   SEE_MASK_NOCLOSEPROCESS = 0x00000040,
   SEE_MASK_CONNECTNETDRV = 0x00000080,
   SEE_MASK_FLAG_DDEWAIT = 0x00000100,
   SEE_MASK_DOENVSUBST = 0x00000200,
   SEE_MASK_FLAG_NO_UI = 0x00000400,
   SEE_MASK_UNICODE = 0x00004000,
   SEE_MASK_NO_CONSOLE = 0x00008000,
   SEE_MASK_ASYNCOK = 0x00100000,
   SEE_MASK_HMONITOR = 0x00200000,
   SEE_MASK_NOQUERYCLASSSTORE = 0x01000000,
   SEE_MASK_WAITFORINPUTIDLE = 0x02000000,
   SEE_MASK_FLAG_LOG_USAGE = 0x04000000
  } 
  public  const string OpenFile = "open"; 
  public  const string EditFile = "edit"; 
  public  const string ExploreFolder = "explore"; 
  public  const string FindInFolder = "find"; 
  public  const string PrintFile = "print"; 
  public  IntPtr OwnerHandle;
 
  public  string Verb;
 
  public  string Path;
 
  public  string Parameters;
 
  public  string WorkingFolder;
 
  public  ShowWindowCommands ShowMode;
 
  public  ShellExecute()
  {
   OwnerHandle = IntPtr.Zero;
   Verb = OpenFile;
   Path = "";
   Parameters = "";
   WorkingFolder = "";
   ShowMode = ShowWindowCommands.SW_SHOWNORMAL;
  }
 
  public  bool Execute()
  {
   int iRetVal;
   iRetVal = (int)ShellLib.ShellApi.ShellExecute(
    OwnerHandle,
    Verb,
    Path,
    Parameters,
    WorkingFolder,
    (int)ShowMode);
   return (iRetVal > 32) ? true : false;
  }

	}

}
