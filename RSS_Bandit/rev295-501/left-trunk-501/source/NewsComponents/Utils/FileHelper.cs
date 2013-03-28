using System;
using System.Collections.Generic;
using System.IO;
using System.Runtime.InteropServices;
using System.Threading;
using System.Diagnostics;
using ICSharpCode.SharpZipLib.Zip;
namespace NewsComponents.Utils
{
 public sealed class FileHelper {
  private static readonly int msecsBetweenRetries = 100;
  private static readonly int bufferSize = 1024 * 20;
  public static string EndOfBinaryFileMarker = ".\r\n";
  public static string CreateValidFileName(string name){
   string safe = name.Trim();
   safe = safe.Replace(Path.VolumeSeparatorChar,'_');
   safe = safe.Replace(Path.DirectorySeparatorChar,'_');
   safe = safe.Replace(Path.AltDirectorySeparatorChar,'_');
   safe = safe.Replace(Path.PathSeparator,'_');
   foreach(Char c in Path.GetInvalidFileNameChars()){
    safe = safe.Replace(c,'_');
   }
   if(safe.Length > 250)
    safe = safe.Substring(0, 249);
   char[] replace = {'-','.'};
   safe = safe.TrimStart(replace);
   safe = safe.TrimEnd(replace);
   return safe;
  }
  public static FileStream OpenForWrite(string fileName ) {
   FileStream fileStream = null;
   int retries = 10;
   while ( retries > 0 ) {
    try {
     fileStream = new FileStream(
      fileName,
      FileMode.Create,
      FileAccess.Write,
      FileShare.None,bufferSize);
    }
    catch (Exception) {
     retries--;
     if (retries <= 0)
      throw;
     Thread.Sleep(msecsBetweenRetries);
     continue;
    }
    break;
   }
   return fileStream;
  }
  public static FileStream OpenForWriteAppend(string fileName ) {
   FileStream fileStream = null;
   int retries = 10;
   while ( retries > 0 ) {
    try {
     fileStream = new FileStream(
      fileName,
      FileMode.Append,
      FileAccess.Write,
      FileShare.None,bufferSize);
    }
    catch (Exception) {
     retries--;
     if (retries <= 0)
      throw;
     Thread.Sleep(msecsBetweenRetries);
     continue;
    }
    break;
   }
   return fileStream;
  }
  public static FileStream OpenForReadWrite(string fileName) {
   FileStream fileStream = null;
   int retries = 10;
   while ( retries > 0 ) {
    try {
     fileStream = new FileStream(
      fileName,
      FileMode.OpenOrCreate,
      FileAccess.ReadWrite,
      FileShare.None,bufferSize);
    }
    catch {
     retries--;
     if (retries <= 0)
      throw;
     Thread.Sleep(msecsBetweenRetries);
     continue;
    }
    break;
   }
   return fileStream;
  }
  public static FileStream OpenForRead(string fileName) {
   FileStream fileStream = null;
   int retries = 10;
   while ( retries > 0 ) {
    try {
     fileStream = new FileStream(
      fileName,
      FileMode.Open,
      FileAccess.Read,
      FileShare.Read,bufferSize);
    }
    catch {
     retries--;
     if (retries <= 0)
      throw;
     Thread.Sleep(msecsBetweenRetries);
     continue;
    }
    break;
   }
   return fileStream;
  }
  public static void Delete(string fileName) {
   int retries = 10;
   while ( retries > 0 ) {
    try {
     File.Delete(fileName);
    } catch {
     retries--;
     if (retries <= 0)
      throw;
     Thread.Sleep(msecsBetweenRetries);
     continue;
    }
    break;
   }
  }
  public static bool WriteStreamWithBackup(string fileName, Stream stream) {
   if (string.IsNullOrEmpty(fileName))
    throw new ArgumentNullException("fileName");
   if (stream == null)
    throw new ArgumentNullException("stream");
   bool saveSuccess;
            try
            {
                if (stream.CanSeek)
                    stream.Seek(0, SeekOrigin.Begin);
                string dataPath = fileName + ".new";
                Stream fsStream = OpenForWrite(dataPath);
                if (fsStream == null)
                {
                    fsStream = new FileStream(dataPath, FileMode.Create, FileAccess.Write, FileShare.None, bufferSize);
                }
                try
                {
                    int size = 2048;
                    byte[] writeData = new byte[size];
                    while (true)
                    {
                        size = stream.Read(writeData, 0, size);
                        if (size > 0)
                        {
                            fsStream.Write(writeData, 0, size);
                        }
                        else
                        {
                            break;
                        }
                    }
                    fsStream.Flush();
                    saveSuccess = true;
                }
                finally
                {
                    fsStream.Close();
                }
                Delete(fileName + ".bak");
                if (File.Exists(fileName))
                {
                    File.Move(fileName, fileName + ".bak");
                }
                File.Move(fileName + ".new", fileName);
            }
            catch (Exception ex)
            {
                saveSuccess = false;
                Trace.WriteLine("WriteStreamWithBackup('" + fileName + "') caused exception: " + ex.Message);
            }
   return saveSuccess;
  }
  public static bool WriteStreamWithRename(string fileName, Stream stream) {
   if (string.IsNullOrEmpty(fileName))
    throw new ArgumentNullException("fileName");
   if (stream == null)
    throw new ArgumentNullException("stream");
   bool saveSuccess;
            try
            {
                if (stream.CanSeek)
                    stream.Seek(0, SeekOrigin.Begin);
                string dataPath = fileName + ".new";
                Stream fsStream = OpenForWrite(dataPath);
                if (fsStream == null)
                {
                    fsStream = new FileStream(dataPath, FileMode.Create, FileAccess.Write, FileShare.None, bufferSize);
                }
                try
                {
                    int size = 2048;
                    byte[] writeData = new byte[size];
                    while (true)
                    {
                        size = stream.Read(writeData, 0, size);
                        if (size > 0)
                        {
                            fsStream.Write(writeData, 0, size);
                        }
                        else
                        {
                            break;
                        }
                    }
                    fsStream.Flush();
                    saveSuccess = true;
                }
                finally
                {
                    fsStream.Close();
                }
                if (File.Exists(fileName))
                {
                    Delete(fileName);
                }
                File.Move(fileName + ".new", fileName);
            }
            catch (Exception ex)
            {
                saveSuccess = false;
                Trace.WriteLine("WriteStreamWithRename('" + fileName + "') caused exception: " + ex.Message);
            }
   return saveSuccess;
  }
  public static Stream StreamToMemory(Stream input) {
   const int BUFFER_SIZE = 4096;
   MemoryStream output = new MemoryStream();
   int size = BUFFER_SIZE;
   byte[] writeData = new byte[BUFFER_SIZE];
   while (true) {
    size = input.Read(writeData, 0, size);
    if (size > 0) {
     output.Write(writeData, 0, size);
    }
    else {
     break;
    }
   }
   output.Seek(0, SeekOrigin.Begin);
   return output;
  }
  public static Stream FileToMemory(string file) {
   using (Stream input = OpenForRead(file)) {
    return StreamToMemory(input);
   }
  }
  public static bool IsUncPath( string path ) {
   try {
    Uri url = new Uri( path );
    if( url.IsUnc )
     return true;
   } catch (UriFormatException) {}
   return false;
  }
  public static string AppendSlashUrlOrUnc( string path ) {
   if( IsUncPath( path ) ) {
    return AppendTerminalBackslash( path );
   }
   else {
    return AppendTerminalForwardSlash( path );
   }
  }
  public static string AppendTerminalBackslash( string path ) {
   if( path.IndexOf( Path.DirectorySeparatorChar, path.Length - 1 ) == -1 ) {
    return path + Path.DirectorySeparatorChar;
   }
   else {
    return path;
   }
  }
  public static string AppendTerminalForwardSlash( string path ) {
   if( path.IndexOf( Path.AltDirectorySeparatorChar, path.Length - 1 ) == -1 ) {
    return path + Path.AltDirectorySeparatorChar;
   }
   else {
    return path;
   }
  }
  public static string CreateTemporaryFolder() {
   return Path.Combine( Path.GetTempPath(), Path.GetFileNameWithoutExtension( Path.GetTempFileName() ) );
  }
  public static void CopyDirectory( string sourcePath, string destinationPath ) {
   CopyDirectory( sourcePath, destinationPath, true );
  }
  public static void CopyDirectory( string sourcePath, string destinationPath, bool overwrite ) {
   CopyDirRecurse( sourcePath, destinationPath, destinationPath, overwrite );
  }
  public static bool MoveFile( string existingFileName, string newFileName, MoveFileFlag flags) {
   int retries = 10;
   while ( retries > 0 ) {
    try {
     return MoveFileEx( existingFileName, newFileName, flags );
    }
    catch (Exception) {
     retries--;
     if (retries <= 0)
      throw;
     Thread.Sleep(msecsBetweenRetries);
     continue;
    }
   }
   return false;
  }
  public static void DestroyFolder( string folderPath ) {
   try {
    if ( Directory.Exists( folderPath) ) {
     Directory.Delete( folderPath, true );
    }
   }
   catch( Exception ) {
    if ( Directory.Exists( folderPath) ) {
     MoveFile(
      folderPath,
      null,
      MoveFileFlag.DelayUntilReboot );
    }
   }
  }
  public static void DestroyFile( string filePath ) {
   try {
    if ( File.Exists( filePath ) ) {
     File.Delete( filePath );
    }
   }
   catch {
    if ( File.Exists( filePath ) ) {
     MoveFile(
      filePath,
      null,
      MoveFileFlag.DelayUntilReboot );
    }
   }
  }
  public static void ZipFiles(string[] files, string zipToFile) {
   using (ZipOutputStream zipStream = OpenForWriteCompressed(zipToFile)) {
    ZipFiles(files, zipStream);
   }
  }
  private static ZipOutputStream OpenForWriteCompressed(string fileName) {
   return new ZipOutputStream(OpenForWrite(fileName));
  }
  private static void ZipFiles(IEnumerable<string> files, ZipOutputStream zos){
   byte[] buffer = new byte[bufferSize];
      zos.SetLevel(5);
   foreach(string file in files){
    string fileToProcess = file;
  try_again:
    if(File.Exists(fileToProcess)){
     try {
      using (FileStream fs = OpenForRead(fileToProcess)) {
       ZipEntry entry = new ZipEntry(Path.GetFileName(file));
       zos.PutNextEntry(entry);
          int size;
          do {
        size = fs.Read(buffer, 0, buffer.Length);
        zos.Write(buffer, 0, size);
       } while (size > 0);
      }
     } catch (IOException) {
      string newFile = Path.GetTempFileName();
      File.Copy(file, newFile, true);
      fileToProcess = newFile;
      goto try_again;
     }
    }
   }
   zos.Finish();
  }
  [DllImport("KERNEL32.DLL", CharSet = CharSet.Unicode) ]
  private static extern bool MoveFileEx(
   string lpExistingFileName,
   string lpNewFileName,
            MoveFileFlag dwFlags);
  public static long GetSize(DirectoryInfo d) {
   long Size = 0;
   FileInfo[] fis = d.GetFiles();
   foreach (FileInfo fi in fis) {
    Size += fi.Length;
   }
   DirectoryInfo[] dis = d.GetDirectories();
   foreach (DirectoryInfo di in dis) {
    Size += GetSize(di);
   }
   return(Size);
  }
  private static void CopyDirRecurse( string sourcePath, string destinationPath, string originalDestination, bool overwrite ) {
   sourcePath = AppendTerminalBackslash( sourcePath );
   destinationPath = AppendTerminalBackslash( destinationPath );
   if ( !Directory.Exists( destinationPath ) ) {
    Directory.CreateDirectory( destinationPath );
   }
   DirectoryInfo dirInfo = new DirectoryInfo( sourcePath );
      foreach( FileSystemInfo fsi in dirInfo.GetFileSystemInfos() ) {
    if ( fsi is FileInfo )
    {
        string destFileName = Path.Combine( destinationPath, fsi.Name );
     if ( File.Exists( destFileName ) ) {
      if ( overwrite ) {
       File.Copy( fsi.FullName, destFileName, true );
      }
     }
     else {
      File.Copy( fsi.FullName, destFileName );
     }
    }
    else {
     if ( fsi.FullName != originalDestination ) {
      CopyDirRecurse( fsi.FullName, destinationPath + fsi.Name, originalDestination, overwrite );
     }
    }
   }
  }
  private FileHelper() {}
 }
 [Flags]
 public enum MoveFileFlag
 {
  None = 0x00000000,
  ReplaceExisting = 0x00000001,
  CopyAllowed = 0x00000002,
  DelayUntilReboot = 0x00000004,
  WriteThrough = 0x00000008,
  CreateHardLink = 0x00000010,
  FailIfNotTrackable = 0x00000020,
 }
}
