using System;
using System.IO;
namespace ICSharpCode.SharpZipLib.Core
{
 public class PathFilter : IScanFilter
 {
  public PathFilter(string filter)
  {
   nameFilter_ = new NameFilter(filter);
  }
  public virtual bool IsMatch(string name)
  {
   bool result = false;
   if ( name != null ) {
    string cooked = (name.Length > 0) ? Path.GetFullPath(name) : "";
    result = nameFilter_.IsMatch(cooked);
   }
   return result;
  }
  NameFilter nameFilter_;
 }
 public class ExtendedPathFilter : PathFilter
 {
  public ExtendedPathFilter(string filter,
   long minSize, long maxSize)
   : base(filter)
  {
   MinSize = minSize;
   MaxSize = maxSize;
  }
  public ExtendedPathFilter(string filter,
   DateTime minDate, DateTime maxDate)
   : base(filter)
  {
   MinDate = minDate;
   MaxDate = maxDate;
  }
  public ExtendedPathFilter(string filter,
   long minSize, long maxSize,
   DateTime minDate, DateTime maxDate)
   : base(filter)
  {
   MinSize = minSize;
   MaxSize = maxSize;
   MinDate = minDate;
   MaxDate = maxDate;
  }
  public override bool IsMatch(string name)
  {
   bool result = base.IsMatch(name);
   if ( result )
   {
    FileInfo fileInfo = new FileInfo(name);
    result =
     (MinSize <= fileInfo.Length) &&
     (MaxSize >= fileInfo.Length) &&
     (MinDate <= fileInfo.LastWriteTime) &&
     (MaxDate >= fileInfo.LastWriteTime)
     ;
   }
   return result;
  }
  public long MinSize
  {
   get { return minSize_; }
   set
   {
    if ( (value < 0) || (maxSize_ < value) )
    {
     throw new ArgumentOutOfRangeException("value");
    }
    minSize_ = value;
   }
  }
  public long MaxSize
  {
   get { return maxSize_; }
   set
   {
    if ( (value < 0) || (minSize_ > value) )
    {
     throw new ArgumentOutOfRangeException("value");
    }
    maxSize_ = value;
   }
  }
  public DateTime MinDate
  {
   get
   {
    return minDate_;
   }
   set
   {
    if ( value > maxDate_ )
    {
     throw new ArgumentException("Exceeds MaxDate", "value");
    }
    minDate_ = value;
   }
  }
  public DateTime MaxDate
  {
   get
   {
    return maxDate_;
   }
   set
   {
    if ( minDate_ > value )
    {
     throw new ArgumentException("Exceeds MinDate", "value");
    }
    maxDate_ = value;
   }
  }
  long minSize_;
  long maxSize_ = long.MaxValue;
  DateTime minDate_ = DateTime.MinValue;
  DateTime maxDate_ = DateTime.MaxValue;
 }
 [Obsolete("Use ExtendedPathFilter instead")]
 public class NameAndSizeFilter : PathFilter
 {
  public NameAndSizeFilter(string filter, long minSize, long maxSize)
   : base(filter)
  {
   MinSize = minSize;
   MaxSize = maxSize;
  }
  public override bool IsMatch(string name)
  {
   bool result = base.IsMatch(name);
   if ( result )
   {
    FileInfo fileInfo = new FileInfo(name);
    long length = fileInfo.Length;
    result =
     (MinSize <= length) &&
     (MaxSize >= length);
   }
   return result;
  }
  public long MinSize
  {
   get { return minSize_; }
   set
   {
    if ( (value < 0) || (maxSize_ < value) )
    {
     throw new ArgumentOutOfRangeException("value");
    }
    minSize_ = value;
   }
  }
  public long MaxSize
  {
   get { return maxSize_; }
   set
   {
    if ( (value < 0) || (minSize_ > value) )
    {
     throw new ArgumentOutOfRangeException("value");
    }
    maxSize_ = value;
   }
  }
  long minSize_;
  long maxSize_ = long.MaxValue;
 }
}
