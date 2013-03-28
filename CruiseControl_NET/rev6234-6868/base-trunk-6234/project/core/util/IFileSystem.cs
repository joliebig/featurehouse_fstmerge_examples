using System.IO;
using System.Text;
using System;
namespace ThoughtWorks.CruiseControl.Core.Util
{
 public interface IFileSystem
 {
  void Copy(string sourcePath, string destPath);
  void Save(string file, string content);
        void AtomicSave(string file, string content);
        void AtomicSave(string file, string content, Encoding encoding);
        TextReader Load(string file);
  bool FileExists(string file);
  bool DirectoryExists(string folder);
        void EnsureFolderExists(string fileName);
        long GetFreeDiskSpace(string driveName);
        string[] GetFilesInDirectory(string directory);
        DateTime GetLastWriteTime(string fileName);
        ITaskResult GenerateTaskResultFromFile(string fileName);
        Stream OpenOutputStream(string fileName);
        Stream OpenInputStream(string fileName);
    }
}
