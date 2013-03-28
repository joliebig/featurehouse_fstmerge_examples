using System;
using System.Collections;
using System.Collections.Generic;
using System.IO;
using System.Text.RegularExpressions;
namespace ThoughtWorks.CruiseControl.Core.Sourcecontrol
{
 public class PvcsHistoryParser : IHistoryParser
 {
  private static Regex _searchRegEx = new Regex(@"(?<Archive>Archive:\s+.*?\r\n(.|\s)*?(={35}(\r\n|$)))", RegexOptions.Compiled | RegexOptions.IgnorePatternWhitespace | RegexOptions.ExplicitCapture | RegexOptions.IgnoreCase);
  private static Regex _archiveRegEx = new Regex(@"Archive:\s+(?<ArchiveName>.*?)\nWorkfile:\s+(?<Filename>.*?)\nArchive\screated:\s+(?<CreatedDate>.*?)\r\n(.|\s)*?-{35}\r\n(?<Revision>Rev\s\d+(\.\d+)*\r\n(.*\r\n)*?Author\sid:.*?\r\n((?!(={35}|-{35}))(.|\s)*?\r\n)?(-{35}|={35})(\r\n|$))+", RegexOptions.Compiled | RegexOptions.IgnorePatternWhitespace | RegexOptions.ExplicitCapture | RegexOptions.IgnoreCase);
  private static Regex _revisionRegEx = new Regex(@"Rev\s(?<Version>\d(\.\d+)*)\r\n(.*?\r\n)?Checked\sin:\s+(?<CheckIn>.*?)\r\n(.*?\r\n)?Last\smodified:\s+(?<PreviousModification>.*?)\r\n(.*?\r\n)?Author\sid:\s+(?<Author>.*?)\s.*?\r\n(Branches:\s+.*?\r\n)?(?<Comment>(((?!(={35}|-{35}))(.|\s)*?)\r\n)?)(={35}|-{35})(\r\n|$)", RegexOptions.Compiled | RegexOptions.IgnorePatternWhitespace | RegexOptions.ExplicitCapture | RegexOptions.IgnoreCase);
  public Modification[] Parse(TextReader reader, DateTime from_, DateTime to)
  {
   string modificationFile = reader.ReadToEnd();
            var mods = new List<Modification>();
   MatchCollection matches = _searchRegEx.Matches(modificationFile);
   foreach (Match archive in matches)
   {
    MatchCollection archives = _archiveRegEx.Matches(archive.Value);
    foreach (Match archiveDetails in archives)
    {
     ParseArchive(archiveDetails, mods);
    }
   }
   return AnalyzeModifications(mods);
  }
  private static void ParseArchive(Match archive, IList modifications)
  {
   string archivePath = archive.Groups["ArchiveName"].Value.Trim();
   DateTime createdDate = Pvcs.GetDate(archive.Groups["CreatedDate"].Value.Trim());
   MatchCollection revisions = _revisionRegEx.Matches(archive.Value);
   foreach (Match revision in revisions)
   {
    modifications.Add(ParseModification(revision, archivePath, createdDate));
   }
  }
  private static Modification ParseModification(Match revision, string path, DateTime createdDate)
  {
   Modification mod = new Modification();
   mod.Comment = revision.Groups["Comment"].Value.Trim();
   mod.FileName = Path.GetFileName(path);
   mod.FolderName = Path.GetDirectoryName(path).Trim();
   mod.ModifiedTime = Pvcs.GetDate(revision.Groups["CheckIn"].Value.Trim());
   mod.UserName = revision.Groups["Author"].Value.Trim();
   mod.Version = revision.Groups["Version"].Value.Trim();
   mod.Type = (mod.ModifiedTime == createdDate) ? "New" : "Checked in";
   return mod;
  }
  public static Modification[] AnalyzeModifications(IList mods)
  {
   SortedList allFiles = new SortedList();
   foreach (Modification mod in mods)
   {
    string key = mod.FolderName + mod.FileName;
    if (!allFiles.ContainsKey(key))
     allFiles.Add(key, mod);
    else
    {
     Modification compareMod = allFiles[key] as Modification;
     string[] originalVersion = compareMod.Version.Split(char.Parse("."));
     string[] currentVersion = mod.Version.Split(char.Parse("."));
     int len1 = originalVersion.Length;
     int len2 = currentVersion.Length;
     int usingLen;
     int otherLen;
     if (len1 >= len2)
     {
      usingLen = len1;
      otherLen = len2;
     }
     else
     {
      usingLen = len2;
      otherLen = len1;
     }
     for (int i = 0; i < usingLen; i++)
     {
      if (i > otherLen)
       continue;
      if (Convert.ToInt32(currentVersion[i]) > Convert.ToInt32(originalVersion[i]))
      {
       allFiles[compareMod.FolderName + compareMod.FileName] = mod;
       break;
      }
     }
    }
   }
   Modification[] validMods = new Modification[allFiles.Count];
   int count = 0;
   foreach (string key in allFiles.Keys)
   {
    validMods[count++] = allFiles[key] as Modification;
   }
   return validMods;
  }
 }
}
