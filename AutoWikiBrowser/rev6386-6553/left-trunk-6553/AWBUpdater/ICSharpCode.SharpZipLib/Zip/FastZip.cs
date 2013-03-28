using System;
using System.IO;
using ICSharpCode.SharpZipLib.Core;
namespace ICSharpCode.SharpZipLib.Zip
{
 public class FastZipEvents
 {
  public ProcessDirectoryHandler ProcessDirectory;
  public ProcessFileHandler ProcessFile;
  public ProgressHandler Progress;
  public CompletedFileHandler CompletedFile;
  public DirectoryFailureHandler DirectoryFailure;
  public FileFailureHandler FileFailure;
  public bool OnDirectoryFailure(string directory, Exception e)
  {
   bool result = false;
   if ( DirectoryFailure != null ) {
    ScanFailureEventArgs args = new ScanFailureEventArgs(directory, e);
    DirectoryFailure(this, args);
    result = args.ContinueRunning;
   }
   return result;
  }
  public bool OnFileFailure(string file, Exception e)
  {
   bool result = false;
   if ( FileFailure != null ) {
    ScanFailureEventArgs args = new ScanFailureEventArgs(file, e);
    FileFailure(this, args);
    result = args.ContinueRunning;
   }
   return result;
  }
  public bool OnProcessFile(string file)
  {
   bool result = true;
   if ( ProcessFile != null ) {
    ScanEventArgs args = new ScanEventArgs(file);
    ProcessFile(this, args);
    result = args.ContinueRunning;
   }
   return result;
  }
  public bool OnCompletedFile(string file)
  {
   bool result = true;
   if ( CompletedFile != null ) {
    ScanEventArgs args = new ScanEventArgs(file);
    CompletedFile(this, args);
    result = args.ContinueRunning;
   }
   return result;
  }
  public bool OnProcessDirectory(string directory, bool hasMatchingFiles)
  {
   bool result = true;
   if ( ProcessDirectory != null ) {
    DirectoryEventArgs args = new DirectoryEventArgs(directory, hasMatchingFiles);
    ProcessDirectory(this, args);
    result = args.ContinueRunning;
   }
   return result;
  }
  public TimeSpan ProgressInterval
  {
   get { return progressInterval_; }
   set { progressInterval_ = value; }
  }
  TimeSpan progressInterval_ = TimeSpan.FromSeconds(3);
 }
 public class FastZip
 {
  public enum Overwrite
  {
   Prompt,
   Never,
   Always
  }
  public FastZip()
  {
  }
  public FastZip(FastZipEvents events)
  {
   events_ = events;
  }
  public bool CreateEmptyDirectories
  {
   get { return createEmptyDirectories_; }
   set { createEmptyDirectories_ = value; }
  }
  public string Password
  {
   get { return password_; }
   set { password_ = value; }
  }
  public INameTransform NameTransform
  {
   get { return entryFactory_.NameTransform; }
   set {
    entryFactory_.NameTransform = value;
   }
  }
  public IEntryFactory EntryFactory
  {
   get { return entryFactory_; }
   set {
    if ( value == null ) {
     entryFactory_ = new ZipEntryFactory();
    }
    else {
     entryFactory_ = value;
    }
   }
  }
  public bool RestoreDateTimeOnExtract
  {
   get {
    return restoreDateTimeOnExtract_;
   }
   set {
    restoreDateTimeOnExtract_ = value;
   }
  }
  public bool RestoreAttributesOnExtract
  {
   get { return restoreAttributesOnExtract_; }
   set { restoreAttributesOnExtract_ = value; }
  }
  public delegate bool ConfirmOverwriteDelegate(string fileName);
  public void CreateZip(string zipFileName, string sourceDirectory,
   bool recurse, string fileFilter, string directoryFilter)
  {
   CreateZip(File.Create(zipFileName), sourceDirectory, recurse, fileFilter, directoryFilter);
  }
  public void CreateZip(string zipFileName, string sourceDirectory, bool recurse, string fileFilter)
  {
   CreateZip(File.Create(zipFileName), sourceDirectory, recurse, fileFilter, null);
  }
  public void CreateZip(Stream outputStream, string sourceDirectory, bool recurse, string fileFilter, string directoryFilter)
  {
   NameTransform = new ZipNameTransform(sourceDirectory);
   sourceDirectory_ = sourceDirectory;
   using ( outputStream_ = new ZipOutputStream(outputStream) ) {
    if ( password_ != null ) {
     outputStream_.Password = password_;
    }
    FileSystemScanner scanner = new FileSystemScanner(fileFilter, directoryFilter);
    scanner.ProcessFile += new ProcessFileHandler(ProcessFile);
    if ( this.CreateEmptyDirectories ) {
     scanner.ProcessDirectory += new ProcessDirectoryHandler(ProcessDirectory);
    }
    if (events_ != null) {
     if ( events_.FileFailure != null ) {
      scanner.FileFailure += events_.FileFailure;
     }
     if ( events_.DirectoryFailure != null ) {
      scanner.DirectoryFailure += events_.DirectoryFailure;
     }
    }
    scanner.Scan(sourceDirectory, recurse);
   }
  }
  public void ExtractZip(string zipFileName, string targetDirectory, string fileFilter)
  {
   ExtractZip(zipFileName, targetDirectory, Overwrite.Always, null, fileFilter, null, restoreDateTimeOnExtract_);
  }
  public void ExtractZip(string zipFileName, string targetDirectory,
          Overwrite overwrite, ConfirmOverwriteDelegate confirmDelegate,
          string fileFilter, string directoryFilter, bool restoreDateTime)
  {
   if ( (overwrite == Overwrite.Prompt) && (confirmDelegate == null) ) {
    throw new ArgumentNullException("confirmDelegate");
   }
   continueRunning_ = true;
   overwrite_ = overwrite;
   confirmDelegate_ = confirmDelegate;
   targetDirectory_ = targetDirectory;
   fileFilter_ = new NameFilter(fileFilter);
   directoryFilter_ = new NameFilter(directoryFilter);
   restoreDateTimeOnExtract_ = restoreDateTime;
   using ( zipFile_ = new ZipFile(zipFileName) ) {
    if (password_ != null) {
     zipFile_.Password = password_;
    }
    System.Collections.IEnumerator enumerator = zipFile_.GetEnumerator();
    while ( continueRunning_ && enumerator.MoveNext()) {
     ZipEntry entry = (ZipEntry) enumerator.Current;
     if ( entry.IsFile )
     {
      if ( directoryFilter_.IsMatch(Path.GetDirectoryName(entry.Name)) && fileFilter_.IsMatch(entry.Name) ) {
       ExtractEntry(entry);
      }
     }
     else if ( entry.IsDirectory ) {
      if ( directoryFilter_.IsMatch(entry.Name) && CreateEmptyDirectories ) {
       ExtractEntry(entry);
      }
     }
     else {
     }
    }
   }
  }
  void ProcessDirectory(object sender, DirectoryEventArgs e)
  {
   if ( !e.HasMatchingFiles && CreateEmptyDirectories ) {
    if ( events_ != null ) {
     events_.OnProcessDirectory(e.Name, e.HasMatchingFiles);
    }
    if ( e.ContinueRunning ) {
     if (e.Name != sourceDirectory_) {
      ZipEntry entry = entryFactory_.MakeDirectoryEntry(e.Name);
      outputStream_.PutNextEntry(entry);
     }
    }
   }
  }
  void ProcessFile(object sender, ScanEventArgs e)
  {
   if ( (events_ != null) && (events_.ProcessFile != null) ) {
    events_.ProcessFile(sender, e);
   }
   if ( e.ContinueRunning ) {
    ZipEntry entry = entryFactory_.MakeFileEntry(e.Name);
    outputStream_.PutNextEntry(entry);
    AddFileContents(e.Name);
   }
  }
  void AddFileContents(string name)
  {
   if ( buffer_ == null ) {
    buffer_ = new byte[4096];
   }
   using (FileStream stream = File.OpenRead(name)) {
    if ((events_ != null) && (events_.Progress != null)) {
     StreamUtils.Copy(stream, outputStream_, buffer_,
      events_.Progress, events_.ProgressInterval, this, name);
    }
    else {
     StreamUtils.Copy(stream, outputStream_, buffer_);
    }
   }
   if (events_ != null) {
    continueRunning_ = events_.OnCompletedFile(name);
   }
  }
  void ExtractFileEntry(ZipEntry entry, string targetName)
  {
   bool proceed = true;
   if ( overwrite_ != Overwrite.Always ) {
    if ( File.Exists(targetName) ) {
     if ( (overwrite_ == Overwrite.Prompt) && (confirmDelegate_ != null) ) {
      proceed = confirmDelegate_(targetName);
     }
     else {
      proceed = false;
     }
    }
   }
   if ( proceed ) {
    if ( events_ != null ) {
     continueRunning_ = events_.OnProcessFile(entry.Name);
    }
    if ( continueRunning_ ) {
     try {
      using ( FileStream outputStream = File.Create(targetName) ) {
       if ( buffer_ == null ) {
        buffer_ = new byte[4096];
       }
       if ((events_ != null) && (events_.Progress != null))
       {
        StreamUtils.Copy(zipFile_.GetInputStream(entry), outputStream, buffer_,
         events_.Progress, events_.ProgressInterval, this, entry.Name);
       }
       else
       {
        StreamUtils.Copy(zipFile_.GetInputStream(entry), outputStream, buffer_);
       }
       if (events_ != null) {
        continueRunning_ = events_.OnCompletedFile(entry.Name);
       }
      }
      if ( restoreDateTimeOnExtract_ ) {
       File.SetLastWriteTime(targetName, entry.DateTime);
      }
      if ( RestoreAttributesOnExtract && entry.IsDOSEntry && (entry.ExternalFileAttributes != -1)) {
       FileAttributes fileAttributes = (FileAttributes) entry.ExternalFileAttributes;
       fileAttributes &= (FileAttributes.Archive | FileAttributes.Normal | FileAttributes.ReadOnly | FileAttributes.Hidden);
       File.SetAttributes(targetName, fileAttributes);
      }
     }
     catch(Exception ex) {
      if ( events_ != null ) {
       continueRunning_ = events_.OnFileFailure(targetName, ex);
      }
      else {
       continueRunning_ = false;
      }
     }
    }
   }
  }
  void ExtractEntry(ZipEntry entry)
  {
   bool doExtraction = false;
   string nameText = entry.Name;
   if ( entry.IsFile ) {
    doExtraction = NameIsValid(nameText) && entry.IsCompressionMethodSupported();
   }
   else if ( entry.IsDirectory ) {
    doExtraction = NameIsValid(nameText);
   }
   string dirName = null;
   string targetName = null;
   if ( doExtraction ) {
    if (Path.IsPathRooted(nameText)) {
     string workName = Path.GetPathRoot(nameText);
     nameText = nameText.Substring(workName.Length);
    }
    if ( nameText.Length > 0 ) {
     targetName = Path.Combine(targetDirectory_, nameText);
     if ( entry.IsDirectory ) {
      dirName = targetName;
     }
     else {
      dirName = Path.GetDirectoryName(Path.GetFullPath(targetName));
     }
    }
    else {
     doExtraction = false;
    }
   }
   if ( doExtraction && !Directory.Exists(dirName) ) {
    if ( !entry.IsDirectory || CreateEmptyDirectories ) {
     try {
      Directory.CreateDirectory(dirName);
     }
     catch (Exception ex) {
      doExtraction = false;
      if ( events_ != null ) {
       if ( entry.IsDirectory ) {
        continueRunning_ = events_.OnDirectoryFailure(targetName, ex);
       }
       else {
        continueRunning_ = events_.OnFileFailure(targetName, ex);
       }
      }
      else {
       continueRunning_ = false;
      }
     }
    }
   }
   if ( doExtraction && entry.IsFile ) {
    ExtractFileEntry(entry, targetName);
   }
  }
  static int MakeExternalAttributes(FileInfo info)
  {
   return (int)info.Attributes;
  }
  static bool NameIsValid(string name)
  {
   return (name != null) &&
    (name.Length > 0) &&
    (name.IndexOfAny(Path.GetInvalidPathChars()) < 0);
  }
  bool continueRunning_;
  byte[] buffer_;
  ZipOutputStream outputStream_;
  ZipFile zipFile_;
  string targetDirectory_;
  string sourceDirectory_;
  NameFilter fileFilter_;
  NameFilter directoryFilter_;
  Overwrite overwrite_;
  ConfirmOverwriteDelegate confirmDelegate_;
  bool restoreDateTimeOnExtract_;
  bool restoreAttributesOnExtract_;
  bool createEmptyDirectories_;
  FastZipEvents events_;
  IEntryFactory entryFactory_ = new ZipEntryFactory();
  string password_;
 }
}
