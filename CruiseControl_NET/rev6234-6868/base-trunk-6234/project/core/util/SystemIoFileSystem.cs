using System.IO;
using System.Text;
using System.Runtime.InteropServices;
using System;
using ThoughtWorks.CruiseControl.Core.Tasks;
namespace ThoughtWorks.CruiseControl.Core.Util
{
 public class SystemIoFileSystem : IFileSystem
 {
  public void Copy(string sourcePath, string destPath)
  {
   if (!File.Exists(sourcePath) && !Directory.Exists(sourcePath))
   {
    throw new IOException(string.Format("Source Path [{0}] doesn't exist", sourcePath));
   }
   if (Directory.Exists(sourcePath))
    CopyDirectoryToDirectory(sourcePath, destPath);
   else
    CopyFile(sourcePath, destPath);
  }
  private void CopyDirectoryToDirectory(string sourcePath, string destPath)
  {
   foreach (DirectoryInfo subdir in new DirectoryInfo(sourcePath).GetDirectories())
   {
    CopyDirectoryToDirectory(subdir.FullName, Path.Combine(destPath, subdir.Name));
   }
   foreach (string file in Directory.GetFiles(sourcePath))
   {
    CopyFileToDirectory(Path.Combine(sourcePath, file), destPath);
   }
  }
  private void CopyFile(string sourcePath, string destPath)
  {
   if (Directory.Exists(destPath))
    CopyFileToDirectory(sourcePath, destPath);
   else
    CopyFileToFile(sourcePath, destPath);
  }
  private void CopyFileToDirectory(string sourcePath, string destPath)
  {
   CopyFileToFile(sourcePath, Path.Combine(destPath, Path.GetFileName(sourcePath)));
  }
  private void CopyFileToFile(string sourcePath, string destPath)
  {
   string destDir = Path.GetDirectoryName(destPath);
   if (! Directory.Exists(destDir))
    Directory.CreateDirectory(destDir);
   if (File.Exists(destPath))
    File.SetAttributes(destPath, FileAttributes.Normal);
   File.Copy(sourcePath, destPath, true);
  }
  public void Save(string file, string content)
  {
   using (StreamWriter stream = File.CreateText(file))
   {
    stream.Write(content);
   }
  }
        public void AtomicSave(string file, string content)
        {
            AtomicSave(file, content, Encoding.UTF8);
        }
        public void AtomicSave(string file, string content, Encoding encoding)
        {
            FileStream newFile = null;
            string targetFilePath = file;
            string newFilePath = targetFilePath + "-NEW";
            string oldFilePath = targetFilePath + "-OLD";
            try
            {
                DeleteFile(newFilePath);
                DeleteFile(oldFilePath);
                newFile = new FileStream(newFilePath, FileMode.CreateNew, FileAccess.Write, FileShare.None,
                    4096, FileOptions.WriteThrough | FileOptions.SequentialScan);
                byte[] fileBytes = encoding.GetBytes(content);
                newFile.Write(fileBytes, 0, fileBytes.Length);
                newFile.Close();
                newFile.Dispose();
                newFile = null;
                if (File.Exists(targetFilePath))
                    File.Move(targetFilePath, oldFilePath);
                File.Move(newFilePath, targetFilePath);
                if (File.Exists(oldFilePath))
                    DeleteFile(oldFilePath);
            }
            catch
            {
                if (newFile != null)
                {
                    newFile.Close();
                    newFile.Dispose();
                }
                throw;
            }
        }
        public TextReader Load(string file)
  {
   using (TextReader reader = new StreamReader(file))
   {
    return new StringReader(reader.ReadToEnd());
   }
  }
  public bool FileExists(string file)
  {
   return File.Exists(file);
  }
  public bool DirectoryExists(string folder)
  {
   return Directory.Exists(folder);
  }
        private static void DeleteFile(string filePath)
        {
            FileInfo fileInfo = new FileInfo(filePath);
            if (fileInfo.Exists)
            {
                if ((fileInfo.Attributes & FileAttributes.ReadOnly) != 0)
                    fileInfo.Attributes ^= FileAttributes.ReadOnly;
                fileInfo.Delete();
            }
        }
        public void EnsureFolderExists(string fileName)
        {
            string directory = Path.GetDirectoryName(fileName);
            if (!Directory.Exists(directory)) Directory.CreateDirectory(directory);
        }
        public long GetFreeDiskSpace(string driveName)
        {
   DriveInfo drive = new DriveInfo(driveName);
         return drive.AvailableFreeSpace;
        }
        public string[] GetFilesInDirectory(string directory)
        {
            return Directory.GetFiles(directory);
        }
        public DateTime GetLastWriteTime(string fileName)
        {
            var writeTime = DateTime.MinValue;
            var fileInfo = new FileInfo(fileName);
            if (fileInfo.Exists) writeTime = fileInfo.LastWriteTime;
            return writeTime;
        }
        public ITaskResult GenerateTaskResultFromFile(string fileName)
        {
            return new FileTaskResult(fileName);
        }
        public Stream OpenOutputStream(string fileName)
        {
            var stream = File.Open(fileName, FileMode.Create, FileAccess.Write, FileShare.None);
            return stream;
        }
        public Stream OpenInputStream(string fileName)
        {
            var stream = File.OpenRead(fileName);
            return stream;
        }
    }
}
