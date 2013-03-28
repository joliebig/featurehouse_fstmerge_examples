using System;
using System.IO;
using System.Text;
using ICSharpCode.SharpZipLib.Core;
namespace ICSharpCode.SharpZipLib.Zip
{
 public class ZipEntryFactory : IEntryFactory
 {
  public enum TimeSetting
  {
   LastWriteTime,
   LastWriteTimeUtc,
   CreateTime,
   CreateTimeUtc,
   LastAccessTime,
   LastAccessTimeUtc,
   Fixed,
  }
  public ZipEntryFactory()
  {
   nameTransform_ = new ZipNameTransform();
  }
  public ZipEntryFactory(TimeSetting timeSetting)
  {
   timeSetting_ = timeSetting;
   nameTransform_ = new ZipNameTransform();
  }
  public ZipEntryFactory(DateTime time)
  {
   timeSetting_ = TimeSetting.Fixed;
   FixedDateTime = time;
   nameTransform_ = new ZipNameTransform();
  }
  public INameTransform NameTransform
  {
   get { return nameTransform_; }
   set
   {
    if (value == null) {
     nameTransform_ = new ZipNameTransform();
    }
    else {
     nameTransform_ = value;
    }
   }
  }
  public TimeSetting Setting
  {
   get { return timeSetting_; }
   set { timeSetting_ = value; }
  }
  public DateTime FixedDateTime
  {
   get { return fixedDateTime_; }
   set
   {
    if (value.Year < 1970) {
     throw new ArgumentException("Value is too old to be valid", "value");
    }
    fixedDateTime_ = value;
   }
  }
  public int GetAttributes
  {
   get { return getAttributes_; }
   set { getAttributes_ = value; }
  }
  public int SetAttributes
  {
   get { return setAttributes_; }
   set { setAttributes_ = value; }
  }
  public bool IsUnicodeText
  {
   get { return isUnicodeText_; }
   set { isUnicodeText_ = value; }
  }
  public ZipEntry MakeFileEntry(string fileName)
  {
   return MakeFileEntry(fileName, true);
  }
  public ZipEntry MakeFileEntry(string fileName, bool useFileSystem)
  {
   ZipEntry result = new ZipEntry(nameTransform_.TransformFile(fileName));
   result.IsUnicodeText = isUnicodeText_;
   int externalAttributes = 0;
   bool useAttributes = (setAttributes_ != 0);
   FileInfo fi = null;
   if (useFileSystem)
   {
    fi = new FileInfo(fileName);
   }
   if ((fi != null) && fi.Exists)
   {
    switch (timeSetting_)
    {
     case TimeSetting.CreateTime:
      result.DateTime = fi.CreationTime;
      break;
     case TimeSetting.CreateTimeUtc:
      result.DateTime = fi.CreationTimeUtc;
      break;
     case TimeSetting.LastAccessTime:
      result.DateTime = fi.LastAccessTime;
      break;
     case TimeSetting.LastAccessTimeUtc:
      result.DateTime = fi.LastAccessTimeUtc;
      break;
     case TimeSetting.LastWriteTime:
      result.DateTime = fi.LastWriteTime;
      break;
     case TimeSetting.LastWriteTimeUtc:
      result.DateTime = fi.LastWriteTimeUtc;
      break;
     case TimeSetting.Fixed:
      result.DateTime = fixedDateTime_;
      break;
     default:
      throw new ZipException("Unhandled time setting in MakeFileEntry");
    }
    result.Size = fi.Length;
    useAttributes = true;
    externalAttributes = ((int)fi.Attributes & getAttributes_);
   }
   else
   {
    if (timeSetting_ == TimeSetting.Fixed)
    {
     result.DateTime = fixedDateTime_;
    }
   }
   if (useAttributes)
   {
    externalAttributes |= setAttributes_;
    result.ExternalFileAttributes = externalAttributes;
   }
   return result;
  }
  public ZipEntry MakeDirectoryEntry(string directoryName)
  {
   return MakeDirectoryEntry(directoryName, true);
  }
  public ZipEntry MakeDirectoryEntry(string directoryName, bool useFileSystem)
  {
   ZipEntry result = new ZipEntry(nameTransform_.TransformDirectory(directoryName));
   result.Size=0;
   int externalAttributes = 0;
   DirectoryInfo di = null;
   if (useFileSystem)
   {
    di = new DirectoryInfo(directoryName);
   }
   if ((di != null) && di.Exists)
   {
    switch (timeSetting_)
    {
     case TimeSetting.CreateTime:
      result.DateTime = di.CreationTime;
      break;
     case TimeSetting.CreateTimeUtc:
      result.DateTime = di.CreationTimeUtc;
      break;
     case TimeSetting.LastAccessTime:
      result.DateTime = di.LastAccessTime;
      break;
     case TimeSetting.LastAccessTimeUtc:
      result.DateTime = di.LastAccessTimeUtc;
      break;
     case TimeSetting.LastWriteTime:
      result.DateTime = di.LastWriteTime;
      break;
     case TimeSetting.LastWriteTimeUtc:
      result.DateTime = di.LastWriteTimeUtc;
      break;
     case TimeSetting.Fixed:
      result.DateTime = fixedDateTime_;
      break;
     default:
      throw new ZipException("Unhandled time setting in MakeDirectoryEntry");
    }
    externalAttributes = ((int)di.Attributes & getAttributes_);
   }
   else
   {
    if (timeSetting_ == TimeSetting.Fixed)
    {
     result.DateTime = fixedDateTime_;
    }
   }
   externalAttributes |= (setAttributes_ | 16);
   result.ExternalFileAttributes = externalAttributes;
   return result;
  }
  INameTransform nameTransform_;
  DateTime fixedDateTime_ = DateTime.Now;
  TimeSetting timeSetting_;
  bool isUnicodeText_;
  int getAttributes_ = -1;
  int setAttributes_;
 }
}
