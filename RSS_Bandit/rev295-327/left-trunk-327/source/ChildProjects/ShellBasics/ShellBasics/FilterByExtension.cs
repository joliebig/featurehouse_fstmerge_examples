using System;
using System.Runtime.InteropServices;
namespace ShellLib
{
 [ComVisible(true)]
 [Guid("3766C955-DA6F-4fbc-AD36-311E342EF180")]
 public class FilterByExtension : IFolderFilter
 {
  public Int32 ShouldShow(
   Object psf,
   IntPtr pidlFolder,
   IntPtr pidlItem)
  {
   ShellLib.IShellFolder isf = (ShellLib.IShellFolder)psf;
   ShellLib.ShellApi.STRRET ptrDisplayName;
   isf.GetDisplayNameOf(pidlItem,(uint)ShellLib.ShellApi.SHGNO.SHGDN_NORMAL | (uint)ShellLib.ShellApi.SHGNO.SHGDN_FORPARSING,out ptrDisplayName);
   String sDisplay;
   ShellLib.ShellApi.StrRetToBSTR(ref ptrDisplayName,(IntPtr)0,out sDisplay);
   IntPtr[] aPidl = new IntPtr[1];
   aPidl[0] = pidlItem;
   uint Attrib;
   Attrib = (uint)ShellLib.ShellApi.SFGAO.SFGAO_FOLDER;
   int temp;
   temp = isf.GetAttributesOf(1,aPidl,ref Attrib);
   if ((Attrib & (uint)ShellLib.ShellApi.SFGAO.SFGAO_FOLDER) == (uint)ShellLib.ShellApi.SFGAO.SFGAO_FOLDER)
    return 0;
   for (int i=0 ; i<ValidExtension.Length ; i++)
   {
    if (sDisplay.ToUpper().EndsWith("." + ValidExtension[i].ToUpper()))
     return 0;
   }
   return 1;
  }
  public Int32 GetEnumFlags(
   Object psf,
   IntPtr pidlFolder,
   IntPtr phwnd,
   out UInt32 pgrfFlags)
  {
   pgrfFlags = (uint)ShellApi.SHCONTF.SHCONTF_FOLDERS | (uint)ShellApi.SHCONTF.SHCONTF_NONFOLDERS;
   return 0;
  }
  public string[] ValidExtension;
 }
}
