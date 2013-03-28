using System;
using System.Runtime.InteropServices;
namespace ShellLib
{
 public class ShellAutoComplete
 {
  public static Boolean DoAutoComplete(IntPtr hwndEdit, AutoCompleteFlags flags)
  {
   Int32 hRet;
   hRet = ShellApi.SHAutoComplete(hwndEdit, (UInt32)flags);
            return (hRet == 0);
  }
  [Flags]
  public enum AutoCompleteFlags : uint
  {
   Default = 0x00000000,
   FileSystem = 0x00000001,
   UrlAll = (UrlHistory | UrlMRU),
   UrlHistory = 0x00000002,
   UrlMRU = 0x00000004,
   UseTab = 0x00000008,
   FileSys_Only = 0x00000010,
   FileSys_Dirs = 0x00000020,
   AutoSuggest_Force_On = 0x10000000,
   AutoSuggest_Force_Off = 0x20000000,
   AutoAppend_Force_On = 0x40000000,
   AutoAppend_Force_Off = 0x80000000
  }
  [Flags]
  public enum AutoCompleteListOptions
  {
   None = 0,
   CurrentDir = 1,
   MyComputer = 2,
   Desktop = 4,
   Favorites = 8,
   FileSysOnly = 16,
   FileSysDirs = 32
  }
  [Flags]
  public enum AutoCompleteOptions
  {
   None = 0,
   AutoSuggest = 0x1,
   AutoAppend = 0x2,
   Search = 0x4,
   FilterPreFixes = 0x8,
   UseTab = 0x10,
   UpDownKeyDropsList = 0x20,
   RtlReading = 0x40
  }
  public ShellAutoComplete()
  {
  }
  public IntPtr EditHandle = IntPtr.Zero;
  public Object ListSource = null;
  public AutoCompleteOptions ACOptions = AutoCompleteOptions.AutoSuggest | AutoCompleteOptions.AutoAppend;
  private Object GetAutoComplete()
  {
   Type typeAutoComplete = Type.GetTypeFromCLSID(ShellGUIDs.CLSID_AutoComplete);
   Object obj;
   obj = Activator.CreateInstance(typeAutoComplete);
   return obj;
  }
  public static Object GetACLHistory()
  {
   Type typeACLHistory = Type.GetTypeFromCLSID(ShellGUIDs.CLSID_ACLHistory);
   Object obj;
   obj = Activator.CreateInstance(typeACLHistory);
   return obj;
  }
  public static Object GetACLMRU()
  {
   Type typeACLMRU = Type.GetTypeFromCLSID(ShellGUIDs.CLSID_ACLMRU);
   Object obj;
   obj = Activator.CreateInstance(typeACLMRU);
   return obj;
  }
  public static Object GetACListISF()
  {
   Type typeACListISF = Type.GetTypeFromCLSID(ShellGUIDs.CLSID_ACListISF);
   Object obj;
   obj = Activator.CreateInstance(typeACListISF);
   return obj;
  }
  public static Object GetACLMulti()
  {
   Type typeACLMulti = Type.GetTypeFromCLSID(ShellGUIDs.CLSID_ACLMulti);
   Object obj;
   obj = Activator.CreateInstance(typeACLMulti);
   return obj;
  }
  public void SetAutoComplete(Boolean enable) {
   this.SetAutoComplete(enable, "");
  }
  public void SetAutoComplete(Boolean enable, string quickComplete)
  {
   Int32 ret;
   IAutoComplete2 iac2 = (IAutoComplete2)GetAutoComplete();
   if (EditHandle == IntPtr.Zero)
    throw new Exception("EditHandle must not be zero!");
   if (ListSource == null)
    throw new Exception("ListSource must not be null!");
   ret = iac2.Init(EditHandle,ListSource,"", quickComplete);
   ret = iac2.SetOptions((UInt32)ACOptions);
   ret= iac2.Enable(enable ? 1 : 0);
   Marshal.ReleaseComObject(iac2);
  }
 }
}
