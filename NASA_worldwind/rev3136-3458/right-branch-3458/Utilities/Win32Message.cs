using System;
using System.Collections;
using System.Runtime.InteropServices;
namespace Utility
{
 public class Win32Message
 {
  private Win32Message()
  {
  }
  const int FORMAT_MESSAGE_ALLOCATE_BUFFER = 0x100;
  const int FORMAT_MESSAGE_IGNORE_INSERTS = 0x200;
  const int FORMAT_MESSAGE_FROM_SYSTEM = 0x1000;
  const int FORMAT_MESSAGE_FROM_HMODULE = 0x800;
  const int LOAD_LIBRARY_AS_DATAFILE = 0x02;
  const int WINHTTP_ERROR_BASE = 12000;
  const int WINHTTP_ERROR_LAST = WINHTTP_ERROR_BASE + 184;
  const int NERR_BASE = 2100;
  const int MAX_NERR = NERR_BASE+899;
  internal class DllDescriptor
  {
   public int firstMessage;
   public int lastMessage;
   public string dllName;
   public DllDescriptor(int first, int last, string dll)
   {
    firstMessage = first;
    lastMessage = last;
    dllName = dll;
   }
  }
  private static ArrayList m_dllDescriptors = new ArrayList();
  static Win32Message()
  {
   m_dllDescriptors.Add(new DllDescriptor(WINHTTP_ERROR_BASE, WINHTTP_ERROR_LAST, "winhttp"));
   m_dllDescriptors.Add(new DllDescriptor(NERR_BASE, MAX_NERR, "netmsg.dll"));
  }
  [DllImport("kernel32.dll")]
  private static extern IntPtr LoadLibraryEx(
   string lpFileName,
   int[] hFile,
   uint dwFlags
   );
  [DllImport("kernel32.dll")]
  private static extern int FreeLibrary(
   IntPtr hModule
   );
  [DllImport("kernel32.dll", SetLastError=true, CharSet=CharSet.Auto)]
  private static extern int FormatMessage(
   int dwFlags,
   IntPtr lpSource,
   int dwMessageId,
   int dwLanguageId,
   out IntPtr MsgBuffer,
   int nSize,
   IntPtr Arguments
   );
  public static string GetMessage(int lastError)
  {
   IntPtr hModule = IntPtr.Zero;
   IntPtr pMessageBuffer;
   int dwBufferLength;
   string errorMessage = String.Format("Last Win32 Error #{0:X8}", lastError);
   int dwFormatFlags =
    FORMAT_MESSAGE_ALLOCATE_BUFFER |
    FORMAT_MESSAGE_IGNORE_INSERTS |
    FORMAT_MESSAGE_FROM_SYSTEM ;
   foreach(DllDescriptor dllDesc in m_dllDescriptors)
   {
    if(lastError >= dllDesc.firstMessage && lastError <= dllDesc.lastMessage)
    {
     hModule = LoadLibraryEx(dllDesc.dllName, null, LOAD_LIBRARY_AS_DATAFILE);
     if(hModule != IntPtr.Zero) dwFormatFlags |= FORMAT_MESSAGE_FROM_HMODULE;
     break;
    }
   }
   dwBufferLength = FormatMessage(dwFormatFlags,
    hModule,
    lastError,
    1024,
    out pMessageBuffer,
    0,
    IntPtr.Zero);
   if(dwBufferLength > 0)
   {
    errorMessage = Marshal.PtrToStringUni(pMessageBuffer);
    Marshal.FreeHGlobal(pMessageBuffer);
   }
   if(hModule != IntPtr.Zero) FreeLibrary(hModule);
   return errorMessage;
  }
 }
}
