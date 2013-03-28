using System;
using System.Text;
using System.Text.RegularExpressions;
using System.IO;
using System.Collections;
namespace ThoughtWorks.CruiseControl.Core.Sourcecontrol.Perforce
{
 public class P4HistoryParser : IHistoryParser
 {
  private static Regex modRegex = new Regex(@"info1: (?<folder>//.*)/(?<file>.*)#(?<revision>\d+) (?<type>\w+)");
  private static Regex changeRegex = new Regex(@"text: Change (?<change>.*) by (?<email>(?<user>.*)@.*) on (?<date>\d{4}/\d{2}/\d{2} \d{2}:\d{2}:\d{2})");
  public string ParseChanges(String changes)
  {
   StringBuilder result = new StringBuilder();
   Regex regex = new Regex(@"info: Change (?<num>\d+) ");
   foreach(Match match in regex.Matches(changes))
   {
    result.Append(match.Groups["num"]);
    result.Append(' ');
   }
   return result.ToString().Trim();
  }
  public Modification[] Parse(TextReader reader, DateTime from_, DateTime to)
  {
   ArrayList mods = new ArrayList();
   string line;
   string change = null, email = null, user = null, comment = string.Empty;
   DateTime date = DateTime.Now;
   while((line = reader.ReadLine()) != null)
   {
    Match modificationMatch = modRegex.Match(line);
    if (modificationMatch.Success)
    {
     Modification mod = new Modification();
     mod.ChangeNumber = Int32.Parse(change);
     mod.Version = modificationMatch.Groups["revision"].Value;
     mod.FolderName = modificationMatch.Groups["folder"].Value;
     mod.FileName = modificationMatch.Groups["file"].Value;
     mod.Type = modificationMatch.Groups["type"].Value;
     mod.EmailAddress = email;
     mod.UserName = user;
     mod.ModifiedTime = date;
     mod.Comment = comment.Trim();
     mods.Add(mod);
    }
    else
    {
     Match changeMatch = changeRegex.Match(line);
     if (changeMatch.Success)
     {
      change = changeMatch.Groups["change"].Value;
      email = changeMatch.Groups["email"].Value;
      user = changeMatch.Groups["user"].Value;
      date = DateTime.Parse(changeMatch.Groups["date"].Value);
      comment = "";
     }
     else
     {
      string checkinCommentPrefix = "text: \t";
      if (line.StartsWith(checkinCommentPrefix))
      {
       comment += line.Substring(checkinCommentPrefix.Length) + "\r\n";
      }
     }
    }
   }
   return (Modification[]) mods.ToArray(typeof(Modification));
  }
 }
}
