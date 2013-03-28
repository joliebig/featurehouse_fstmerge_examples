using System;
using System.Collections.Generic;
using System.Globalization;
using System.IO;
using System.Text.RegularExpressions;
namespace ThoughtWorks.CruiseControl.Core.Sourcecontrol
{
 public class RobocopyHistoryParser : IHistoryParser
 {
  public Modification[] Parse(TextReader reader, DateTime from_, DateTime to)
  {
            var mods = new List<Modification>();
   string currentLine = reader.ReadLine();
   while (currentLine != null)
   {
    if (currentLine.Contains(DELETED_DIR_TAG))
    {
     mods.Add(ParseDeletedDirectory(currentLine));
    }
    else if (currentLine.Contains(DELETED_FILE_TAG))
    {
     mods.Add(ParseDeletedFile(currentLine));
    }
    else if (currentLine.Contains(ADDED_FILE_TAG))
    {
     mods.Add(ParseAddedFile(currentLine));
    }
    else if (currentLine.Contains(UPDATED_FILE_TAG))
    {
     mods.Add(ParseUpdatedFile(currentLine));
    }
    currentLine = reader.ReadLine();
   }
   return mods.ToArray();
  }
  private static string DELETED_DIR_TAG = "*EXTRA Dir";
  private static string DELETED_FILE_TAG = "*EXTRA File";
  private static string ADDED_FILE_TAG = "New File";
  private static string UPDATED_FILE_TAG = "Newer";
  private static readonly Regex ParseDeletedDirectoryRegex = new Regex(@"\s+\*EXTRA Dir\s+(?'Path'.*)");
  Modification ParseDeletedDirectory(
   string logLine)
  {
   Match match = ParseDeletedDirectoryRegex.Match(logLine);
   if (match.Success)
   {
    if (match.Groups.Count == 2)
    {
     string path = match.Groups["Path"].Captures[0].ToString();
     Modification mod = new Modification();
     mod.Type = "deleted";
     mod.FolderName = Path.GetDirectoryName(path);
     return mod;
    }
   }
   throw new Exception("Failed to match regex");
  }
  private static readonly Regex ParseDeletedFileRegex = new Regex(@"\s+\*EXTRA File\s+(?'Date'\d{4}/\d{2}/\d{2} \d{2}:\d{2}:\d{2})\s+(?'Path'.*)");
  Modification ParseDeletedFile(
   string logLine)
  {
   Match match = ParseDeletedFileRegex.Match(logLine);
   if (match.Success)
   {
    if (match.Groups.Count == 3)
    {
     string path = match.Groups["Path"].Captures[0].ToString();
     Modification mod = new Modification();
     mod.Type = "deleted";
     mod.FileName = Path.GetFileName(path);
     mod.FolderName = Path.GetDirectoryName(path);
     return mod;
    }
   }
   throw new Exception("Failed to match regex");
  }
  private static readonly Regex ParseAddedFileRegex = new Regex(@"\s+New File\s+(?'Date'\d{4}/\d{2}/\d{2} \d{2}:\d{2}:\d{2})\s+(?'Path'.*)");
  Modification ParseAddedFile(
   string logLine)
  {
   Match match = ParseAddedFileRegex.Match(logLine);
   if (match.Success)
   {
    if (match.Groups.Count == 3)
    {
     string date = match.Groups["Date"].Captures[0].ToString();
     string path = match.Groups["Path"].Captures[0].ToString();
     Modification mod = new Modification();
     mod.Type = "added";
     mod.FileName = Path.GetFileName(path);
     mod.FolderName = Path.GetDirectoryName(path);
     mod.ModifiedTime = CreateDate(date);
     return mod;
    }
   }
   throw new Exception("Failed to match regex");
  }
  private static readonly Regex ParseUpdatedFileRegex = new Regex(@"\s+Newer\s+(?'Date'\d{4}/\d{2}/\d{2} \d{2}:\d{2}:\d{2})\s+(?'Path'.*)");
  Modification ParseUpdatedFile(
   string logLine)
  {
   Match match = ParseUpdatedFileRegex.Match(logLine);
   if (match.Success)
   {
    if (match.Groups.Count == 3)
    {
     string date = match.Groups["Date"].Captures[0].ToString();
     string path = match.Groups["Path"].Captures[0].ToString();
     Modification mod = new Modification();
     mod.Type = "modified";
     mod.FileName = Path.GetFileName(path);
     mod.FolderName = Path.GetDirectoryName(path);
     mod.ModifiedTime = CreateDate(date);
     return mod;
    }
   }
   throw new Exception("Failed to match regex");
  }
  private DateTime CreateDate(
   string dateTimeString)
  {
   DateTime date = DateTime.ParseExact(dateTimeString, "yyyy/MM/dd HH:mm:ss", DateTimeFormatInfo.GetInstance(CultureInfo.InvariantCulture));
   return date;
  }
 }
}
