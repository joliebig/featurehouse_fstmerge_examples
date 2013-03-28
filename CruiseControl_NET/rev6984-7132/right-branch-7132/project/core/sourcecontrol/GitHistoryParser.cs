using System;
using System.Collections;
using System.Collections.Generic;
using System.IO;
using System.Text.RegularExpressions;
using ThoughtWorks.CruiseControl.Core.Util;
namespace ThoughtWorks.CruiseControl.Core.Sourcecontrol
{
 public class GitHistoryParser : IHistoryParser
 {
  private static readonly Regex modificationList =
   new Regex(
    "Commit:(?<Hash>[a-z0-9]{40})(?:\n|\r\n)Time:(?<Time>.+?)(?:\n|\r\n)Author:(?<Author>.+?)(?:\n|\r\n)E-Mail:(?<Mail>.+?)(?:\n|\r\n)Message:(?<Message>.*?)(?:\n|\r\n)Changes:(?:\n|\r\n)(?<Changes>.*?)(?:(?:\n|\r\n){2}|(?:\n|\r\n)$)",
    RegexOptions.Compiled | RegexOptions.Singleline);
  private static readonly Regex changeList = new Regex("(?<Type>[A-Z]{1})\t(?<FileName>.*)", RegexOptions.Compiled | RegexOptions.CultureInvariant);
  public Modification[] Parse(TextReader history, DateTime from_, DateTime to)
  {
   List<Modification> result = new List<Modification>();
   if (history.Peek() < 1)
    return result.ToArray();
   foreach (Match mod in modificationList.Matches(history.ReadToEnd()))
   {
    result.AddRange(GetCommitModifications(mod, from_, to));
   }
   return result.ToArray();
  }
  private static IList<Modification> GetCommitModifications(Match commitMatch, DateTime from_, DateTime to)
  {
   IList<Modification> result = new List<Modification>();
   string hash = commitMatch.Groups["Hash"].Value;
   DateTime modifiedTime = DateTime.Parse(commitMatch.Groups["Time"].Value);
   string username = commitMatch.Groups["Author"].Value;
   string emailAddress = commitMatch.Groups["Mail"].Value;
   string comment = commitMatch.Groups["Message"].Value.TrimEnd('\r', '\n');
   string changes = commitMatch.Groups["Changes"].Value;
   if (modifiedTime < from_ || modifiedTime > to)
   {
    Log.Debug(string.Concat("[Git] Ignore commit '", hash, "' from_ '", modifiedTime.ToUniversalTime(),
          "' because it is older then '",
          from_.ToUniversalTime(), "' or newer then '", to.ToUniversalTime(), "'."));
    return result;
   }
   foreach (Match change in changeList.Matches(changes))
   {
    Modification mod = new Modification();
    mod.ChangeNumber = hash;
    mod.Comment = comment;
    mod.EmailAddress = emailAddress;
    mod.ModifiedTime = modifiedTime;
    mod.UserName = username;
    mod.Type = GetModificationType(change.Groups["Type"].Value);
    string fullFilePath = change.Groups["FileName"].Value.TrimEnd('\r', '\n');
    mod.FileName = GetFileFromPath(fullFilePath);
    mod.FolderName = GetFolderFromPath(fullFilePath);
    result.Add(mod);
   }
   return result;
  }
  private static string GetModificationType(string actionAbbreviation)
  {
   switch (actionAbbreviation.ToLowerInvariant())
   {
    case "a":
     return "Added";
    case "d":
     return "Deleted";
    case "m":
     return "Modified";
    default:
     return actionAbbreviation;
   }
  }
  private static string GetFolderFromPath(string fullFileName)
  {
   if (fullFileName.LastIndexOf("/") <= 0)
    return string.Empty;
   return fullFileName.Substring(0, fullFileName.LastIndexOf("/"));
  }
  private static string GetFileFromPath(string fullFileName)
  {
   return fullFileName.Substring(fullFileName.LastIndexOf("/") + 1);
  }
 }
}
