using System;
using System.IO;
namespace ICSharpCode.SharpZipLib.Core
{
 public class ScanEventArgs : EventArgs
 {
  public ScanEventArgs(string name)
  {
   name_ = name;
  }
  public string Name
  {
   get { return name_; }
  }
  public bool ContinueRunning
  {
   get { return continueRunning_; }
   set { continueRunning_ = value; }
  }
  string name_;
  bool continueRunning_ = true;
 }
 public class ProgressEventArgs : EventArgs
 {
  public ProgressEventArgs(string name, long processed, long target)
  {
   name_ = name;
   processed_ = processed;
   target_ = target;
  }
  public string Name
  {
   get { return name_; }
  }
  public bool ContinueRunning
  {
   get { return continueRunning_; }
   set { continueRunning_ = value; }
  }
  public float PercentComplete
  {
   get
   {
    if (target_ <= 0)
    {
     return 0;
    }
    else
    {
     return ((float)processed_ / (float)target_) * 100.0f;
    }
   }
  }
  public long Processed
  {
   get { return processed_; }
  }
  public long Target
  {
   get { return target_; }
  }
  string name_;
  long processed_;
  long target_;
  bool continueRunning_ = true;
 }
 public class DirectoryEventArgs : ScanEventArgs
 {
  public DirectoryEventArgs(string name, bool hasMatchingFiles)
   : base (name)
  {
   hasMatchingFiles_ = hasMatchingFiles;
  }
  public bool HasMatchingFiles
  {
   get { return hasMatchingFiles_; }
  }
  bool hasMatchingFiles_;
 }
 public class ScanFailureEventArgs
 {
  public ScanFailureEventArgs(string name, Exception e)
  {
   name_ = name;
   exception_ = e;
   continueRunning_ = true;
  }
  public string Name
  {
   get { return name_; }
  }
  public Exception Exception
  {
   get { return exception_; }
  }
  public bool ContinueRunning
  {
   get { return continueRunning_; }
   set { continueRunning_ = value; }
  }
  string name_;
  Exception exception_;
  bool continueRunning_;
 }
 public delegate void ProcessDirectoryHandler(object sender, DirectoryEventArgs e);
 public delegate void ProcessFileHandler(object sender, ScanEventArgs e);
 public delegate void ProgressHandler(object sender, ProgressEventArgs e);
 public delegate void CompletedFileHandler(object sender, ScanEventArgs e);
 public delegate void DirectoryFailureHandler(object sender, ScanFailureEventArgs e);
 public delegate void FileFailureHandler(object sender, ScanFailureEventArgs e);
 public class FileSystemScanner
 {
  public FileSystemScanner(string filter)
  {
   fileFilter_ = new PathFilter(filter);
  }
  public FileSystemScanner(string fileFilter, string directoryFilter)
  {
   fileFilter_ = new PathFilter(fileFilter);
   directoryFilter_ = new PathFilter(directoryFilter);
  }
  public FileSystemScanner(IScanFilter fileFilter)
  {
   fileFilter_ = fileFilter;
  }
  public FileSystemScanner(IScanFilter fileFilter, IScanFilter directoryFilter)
  {
   fileFilter_ = fileFilter;
   directoryFilter_ = directoryFilter;
  }
  public ProcessDirectoryHandler ProcessDirectory;
  public ProcessFileHandler ProcessFile;
  public CompletedFileHandler CompletedFile;
  public DirectoryFailureHandler DirectoryFailure;
  public FileFailureHandler FileFailure;
  void OnDirectoryFailure(string directory, Exception e)
  {
   if ( DirectoryFailure == null ) {
    alive_ = false;
   } else {
    ScanFailureEventArgs args = new ScanFailureEventArgs(directory, e);
    DirectoryFailure(this, args);
    alive_ = args.ContinueRunning;
   }
  }
  void OnFileFailure(string file, Exception e)
  {
   if ( FileFailure == null ) {
    alive_ = false;
   } else {
    ScanFailureEventArgs args = new ScanFailureEventArgs(file, e);
    FileFailure(this, args);
    alive_ = args.ContinueRunning;
   }
  }
  void OnProcessFile(string file)
  {
   if ( ProcessFile != null ) {
    ScanEventArgs args = new ScanEventArgs(file);
    ProcessFile(this, args);
    alive_ = args.ContinueRunning;
   }
  }
  void OnCompleteFile(string file)
  {
   if (CompletedFile != null)
   {
    ScanEventArgs args = new ScanEventArgs(file);
    CompletedFile(this, args);
    alive_ = args.ContinueRunning;
   }
  }
  void OnProcessDirectory(string directory, bool hasMatchingFiles)
  {
   if ( ProcessDirectory != null ) {
    DirectoryEventArgs args = new DirectoryEventArgs(directory, hasMatchingFiles);
    ProcessDirectory(this, args);
    alive_ = args.ContinueRunning;
   }
  }
  public void Scan(string directory, bool recurse)
  {
   alive_ = true;
   ScanDir(directory, recurse);
  }
  void ScanDir(string directory, bool recurse)
  {
   try {
    string[] names = System.IO.Directory.GetFiles(directory);
    bool hasMatch = false;
    for (int fileIndex = 0; fileIndex < names.Length; ++fileIndex) {
     if ( !fileFilter_.IsMatch(names[fileIndex]) ) {
      names[fileIndex] = null;
     } else {
      hasMatch = true;
     }
    }
    OnProcessDirectory(directory, hasMatch);
    if ( alive_ && hasMatch ) {
     foreach (string fileName in names) {
      try {
       if ( fileName != null ) {
        OnProcessFile(fileName);
        if ( !alive_ ) {
         break;
        }
       }
      }
      catch (Exception e) {
       OnFileFailure(fileName, e);
      }
     }
    }
   }
   catch (Exception e) {
    OnDirectoryFailure(directory, e);
   }
   if ( alive_ && recurse ) {
    try {
     string[] names = System.IO.Directory.GetDirectories(directory);
     foreach (string fulldir in names) {
      if ((directoryFilter_ == null) || (directoryFilter_.IsMatch(fulldir))) {
       ScanDir(fulldir, true);
       if ( !alive_ ) {
        break;
       }
      }
     }
    }
    catch (Exception e) {
     OnDirectoryFailure(directory, e);
    }
   }
  }
  IScanFilter fileFilter_;
  IScanFilter directoryFilter_;
  bool alive_;
 }
}
