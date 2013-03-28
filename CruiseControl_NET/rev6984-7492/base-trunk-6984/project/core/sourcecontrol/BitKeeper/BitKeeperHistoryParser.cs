using System;
using System.Collections.Generic;
using System.Globalization;
using System.IO;
using System.Text.RegularExpressions;
namespace ThoughtWorks.CruiseControl.Core.Sourcecontrol.BitKeeper
{
 public class BitKeeperHistoryParser : IHistoryParser
 {
        private enum HistoryType
        {
            Unknown,
            Pre40NonVerbose,
            Pre40Verbose,
            Post40Verbose
        };
  private static readonly string BK_CHANGESET_LINE = "ChangeSet";
  private string currentLine = string.Empty;
        private HistoryType fileHistory = HistoryType.Unknown;
  public Modification[] Parse(TextReader bkLog, DateTime from_, DateTime to)
  {
   currentLine = ReadToNotPast(bkLog, BK_CHANGESET_LINE, null);
   fileHistory = DetermineHistoryType();
            var mods = new List<Modification>();
   while (currentLine != null)
   {
    Modification mod;
                if (fileHistory == HistoryType.Pre40Verbose)
                    mod = ParsePre40VerboseEntry(bkLog);
                else if (fileHistory == HistoryType.Pre40NonVerbose)
                    mod = ParsePre40NonVerboseEntry(bkLog);
                else
                    mod = ParsePost40VerboseEntry(bkLog);
    mods.Add(mod);
    currentLine = bkLog.ReadLine();
   }
   return mods.ToArray();
  }
  private Modification ParsePre40VerboseEntry(TextReader bkLog)
  {
   Regex regex = new Regex(@"(?<version>[\d.]+)\s+(?<datetime>\d{2,4}/\d{2}/\d{2} \d{2}:\d{2}:\d{2})\s+(?<username>\S+).*");
   currentLine = currentLine.TrimStart(new char[2] {' ', '\t'});
   string filename = ParseFileName(currentLine);
   string folder = ParseFolderName(currentLine);
   currentLine = bkLog.ReadLine();
   return ParseModification(regex, filename, folder, bkLog);
  }
  private Modification ParsePre40NonVerboseEntry(TextReader bkLog)
  {
   Regex regex = new Regex(@"ChangeSet@(?<version>[\d.]+),\s+(?<datetime>\d{2,4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}[-+]\d{2}:\d{2}),\s+(?<username>\S+).*");
   return ParseModification(regex, "ChangeSet",string.Empty, bkLog);
  }
        private Modification ParsePost40VerboseEntry(TextReader bkLog)
        {
            Regex regex = new Regex(@"(?<filename>.+)@(?<version>[\d.]+),\s+(?<datetime>\d{2,4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}[-+]\d{2}:\d{2}),\s+(?<username>\S+).*");
            currentLine = currentLine.TrimStart(new char[2] { ' ', '\t' });
            Match match = regex.Match(currentLine);
            if (!match.Success)
                throw new Exception("Unable to parse line: " + currentLine);
            string filename = ParseFileName(match.Result("${filename}"));
            string folder = ParseFolderName(match.Result("${filename}"));
            return ParseModification(regex, filename, folder, bkLog);
        }
  private Modification ParseModification(Regex regex, string filename, string folder, TextReader bkLog)
  {
   Match match = regex.Match(currentLine);
   if (!match.Success)
    throw new Exception("Unable to parse line: " + currentLine);
   Modification mod = new Modification();
   mod.FileName = filename;
   mod.FolderName = folder;
   mod.ModifiedTime = ParseDate(match.Result("${datetime}"));
   mod.Type = "Modified";
   mod.UserName = match.Result("${username}");
   mod.Version = match.Result("${version}");
   mod.Comment = ParseComment(bkLog);
   if (mod.FileName == "ChangeSet")
   {
    mod.Type = "ChangeSet";
   }
   else if (mod.Comment.IndexOf("Delete: ") != -1
    && mod.FolderName.IndexOf("BitKeeper/deleted") == 0)
   {
    string fullFilePath = mod.Comment.Substring(mod.Comment.IndexOf("Delete: ") + 8);
    mod.Type = "Deleted";
    mod.FileName = ParseFileName(fullFilePath);
    mod.FolderName = ParseFolderName(fullFilePath);
   }
   else if (mod.Comment.IndexOf("BitKeeper file") != -1 || mod.Version == "1.0")
   {
    mod.Type = "Added";
   }
   else if (mod.Comment.IndexOf("Rename: ") != -1)
   {
    mod.Type = "Renamed";
   }
   return mod;
  }
  private string ReadToNotPast(TextReader reader, string startsWith, string notPast)
  {
   currentLine = reader.ReadLine();
   while (currentLine != null && !currentLine.StartsWith(startsWith))
   {
    if ((notPast != null) && currentLine.StartsWith(notPast))
    {
     return null;
    }
    currentLine = reader.ReadLine();
   }
   return currentLine;
  }
  private DateTime ParseDate(string date)
  {
   string sep = (fileHistory == HistoryType.Pre40Verbose) ? "/" : "-";
   int firstSep = date.IndexOf(sep);
   string dateFormat = (firstSep == 4) ? "yyyy" : "yy";
   dateFormat += string.Format("'{0}'MM'{0}'dd HH:mm:ss", sep);
   if (fileHistory != HistoryType.Pre40Verbose)
    dateFormat += "zzz";
   return DateTime.ParseExact(date, dateFormat, DateTimeFormatInfo.InvariantInfo);
  }
  private string ParseComment(TextReader bkLog)
  {
   string message = string.Empty;
   bool multiLine = false;
   currentLine = bkLog.ReadLine();
   while (currentLine != null && currentLine.Length != 0)
   {
    if (multiLine)
    {
     message += Environment.NewLine;
    }
    else
    {
     multiLine = true;
    }
    message += currentLine;
    currentLine = bkLog.ReadLine();
   }
   return message;
  }
  private HistoryType DetermineHistoryType()
  {
   if (currentLine == null)
    return HistoryType.Unknown;
            if (currentLine.StartsWith("ChangeSet@") && (currentLine.IndexOf("+") != -1))
                return HistoryType.Post40Verbose;
            if (currentLine.StartsWith("ChangeSet@"))
                return HistoryType.Pre40NonVerbose;
            return HistoryType.Pre40Verbose;
  }
  private string ParseFileName(string workingFileName)
  {
   int lastSlashIndex = workingFileName.LastIndexOf("/");
   return workingFileName.Substring(lastSlashIndex + 1);
  }
  private string ParseFolderName(string workingFileName)
  {
   int lastSlashIndex = workingFileName.LastIndexOf("/");
   string folderName = string.Empty;
   if (lastSlashIndex != -1)
   {
    folderName = workingFileName.Substring(0, lastSlashIndex);
   }
   return folderName;
  }
 }
}
