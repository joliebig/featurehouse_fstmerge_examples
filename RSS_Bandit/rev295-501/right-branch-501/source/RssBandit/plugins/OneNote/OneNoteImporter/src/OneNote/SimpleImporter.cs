using System;
using System.IO;
using System.Runtime.InteropServices;
namespace Microsoft.Office.OneNote
{
 public class SimpleImporter
 {
  public SimpleImporter()
  {
   try
   {
    CSimpleImporter simpleImporter = new CSimpleImporter();
    importer = (ISimpleImporter) simpleImporter;
   }
   catch (COMException e)
   {
    if (0x80040154 == (uint) e.ErrorCode)
    {
     throw new ImportException("OneNote is not installed on this machine!");
    }
    throw;
   }
   catch (FileNotFoundException)
   {
    throw new ImportException("OneNote.exe cannot be found, please reinstall!");
   }
  }
  public void Import(string xml)
  {
   try
   {
    importer.Import(xml);
   }
   catch (COMException e)
   {
    throw new ImportException(e.ErrorCode);
   }
  }
  public void NavigateToPage(string sectionPath, string pageGuid)
  {
   try
   {
    importer.NavigateToPage(sectionPath, pageGuid);
   }
   catch (COMException e)
   {
    throw new ImportException(e.ErrorCode);
   }
  }
  private ISimpleImporter importer;
 }
 [Guid("F56A67BB-243F-4927-B987-F57B3D9DBEFE"), InterfaceType(ComInterfaceType.InterfaceIsDual)]
 interface ISimpleImporter
 {
  void Import([In, MarshalAs(UnmanagedType.BStr)] string xml);
  void NavigateToPage(
   [In, MarshalAs(UnmanagedType.BStr)] string path,
   [In, MarshalAs(UnmanagedType.BStr)] string guid);
 }
 [ComImport, Guid("22148139-F1FC-4EB0-B237-DFCD8A38EFFC")]
 class CSimpleImporter
 {
 }
}
