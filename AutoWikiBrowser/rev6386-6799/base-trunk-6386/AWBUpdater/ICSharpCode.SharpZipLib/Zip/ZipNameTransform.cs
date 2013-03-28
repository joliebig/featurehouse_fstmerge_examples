using System;
using System.IO;
using System.Text;
using ICSharpCode.SharpZipLib.Core;
namespace ICSharpCode.SharpZipLib.Zip
{
 public class ZipNameTransform : INameTransform
 {
  public ZipNameTransform()
  {
  }
  public ZipNameTransform(string trimPrefix)
  {
   TrimPrefix = trimPrefix;
  }
  static ZipNameTransform()
  {
   char[] invalidPathChars;
   invalidPathChars = Path.GetInvalidPathChars();
   int howMany = invalidPathChars.Length + 2;
   InvalidEntryCharsRelaxed = new char[howMany];
   Array.Copy(invalidPathChars, 0, InvalidEntryCharsRelaxed, 0, invalidPathChars.Length);
   InvalidEntryCharsRelaxed[howMany - 1] = '*';
   InvalidEntryCharsRelaxed[howMany - 2] = '?';
   howMany = invalidPathChars.Length + 4;
   InvalidEntryChars = new char[howMany];
   Array.Copy(invalidPathChars, 0, InvalidEntryChars, 0, invalidPathChars.Length);
   InvalidEntryChars[howMany - 1] = ':';
   InvalidEntryChars[howMany - 2] = '\\';
   InvalidEntryChars[howMany - 3] = '*';
   InvalidEntryChars[howMany - 4] = '?';
  }
  public string TransformDirectory(string name)
  {
   name = TransformFile(name);
   if (name.Length > 0) {
    if ( !name.EndsWith("/") ) {
     name += "/";
    }
   }
   else {
    throw new ZipException("Cannot have an empty directory name");
   }
   return name;
  }
  public string TransformFile(string name)
  {
   if (name != null) {
    string lowerName = name.ToLower();
    if ( (trimPrefix_ != null) && (lowerName.IndexOf(trimPrefix_) == 0) ) {
     name = name.Substring(trimPrefix_.Length);
    }
    if (Path.IsPathRooted(name) == true) {
     name = name.Substring(Path.GetPathRoot(name).Length);
    }
    name = name.Replace(@"\", "/");
    while ( (name.Length > 0) && (name[0] == '/')) {
     name = name.Remove(0, 1);
    }
    name = MakeValidName(name, '_');
   }
   else {
    name = string.Empty;
   }
   return name;
  }
  public string TrimPrefix
  {
   get { return trimPrefix_; }
   set {
    trimPrefix_ = value;
    if (trimPrefix_ != null) {
     trimPrefix_ = trimPrefix_.ToLower();
    }
   }
  }
  static string MakeValidName(string name, char replacement)
  {
   int index = name.IndexOfAny(InvalidEntryChars);
   if (index > 0) {
    StringBuilder builder = new StringBuilder(name);
    while (index >= 0 ) {
     builder[index] = replacement;
     if (index >= name.Length) {
      index = -1;
     }
     else {
      index = name.IndexOfAny(InvalidEntryChars, index + 1);
     }
    }
    name = builder.ToString();
   }
   return name;
  }
  public static bool IsValidName(string name, bool relaxed)
  {
   bool result = (name != null);
   if ( result ) {
    if ( relaxed ) {
     result = name.IndexOfAny(InvalidEntryCharsRelaxed) < 0;
    }
    else {
     result =
      (name.IndexOfAny(InvalidEntryChars) < 0) &&
      (name.IndexOf('/') != 0);
    }
   }
   return result;
  }
  public static bool IsValidName(string name)
  {
   bool result =
    (name != null) &&
    (name.IndexOfAny(InvalidEntryChars) < 0) &&
    (name.IndexOf('/') != 0)
    ;
   return result;
  }
  string trimPrefix_;
  static readonly char[] InvalidEntryChars;
  static readonly char[] InvalidEntryCharsRelaxed;
 }
}
