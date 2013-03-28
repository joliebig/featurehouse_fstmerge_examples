using System;
using System.Collections;
using System.Collections.Generic;
using System.Diagnostics;
using System.Globalization;
using System.IO;
using System.Text.RegularExpressions;
namespace ThoughtWorks.CruiseControl.Core.Sourcecontrol.Telelogic
{
 public class SynergyParser : IHistoryParser
 {
  private const string TaskFormat = @"(?imn:" +
   @"(?<displayname>.*)" +
   @"(?<sep>\ \#{4}\ )" +
   @"(?<task_number>\d+)" +
   @"\k<sep>" +
   @"(?<completion_date>.*)" +
   @"\k<sep>" +
   @"(?<resolver>.*)" +
   @"\k<sep>" +
   @"(?<task_synopsis>.*)" +
   @"\k<sep>)";
  private const string ObjectFormat = @"(?imn:" +
   @"(?<displayname>\S+)\s+" +
   @"(?<status>\S+)\s+" +
   @"(?<resolver>\S+)\s+" +
   @"(?<cvtype>\S+)\s+" +
   @"(?<project>\S+)\s+" +
   @"(?<instance>(\D+\W)?\d+)\s+" +
   @"((?<task>[^,\r]+),?)+\r\n" +
   @"(\t" +
   @"(?<folder>\S+(?=\\\k<displayname>@))?" +
   @"[^\r]+\r\n)+" +
   @")";
  Modification[] IHistoryParser.Parse(TextReader history, DateTime from_, DateTime to)
  {
   return Parse(String.Empty, history.ReadToEnd(), from_);
  }
  public virtual Modification[] Parse(string newTasks, string newObjects, DateTime from_)
  {
            var modifications = new List<Modification>();
   Hashtable tasks = new Hashtable();
            if (string.IsNullOrEmpty(newObjects)) return new Modification[0];
            if (!string.IsNullOrEmpty(newTasks))
   {
    tasks = ParseTasks(newTasks);
   }
   Regex grep = new Regex(ObjectFormat, RegexOptions.CultureInvariant);
   MatchCollection matches = grep.Matches(newObjects);
   foreach (Match match in matches)
   {
    Modification modification = new Modification();
    modification.FolderName = match.Groups["folder"].Value;
    modification.FileName = match.Groups["displayname"].Value;
    modification.Type = match.Groups["cvtype"].Value;
    modification.EmailAddress = match.Groups["resolver"].Value;
    modification.UserName = match.Groups["resolver"].Value;
    if (modification.FolderName.Length > 0)
    {
     modification.FolderName = String.Concat("$/", modification.FolderName.Replace('\\', '/'));
    }
    CaptureCollection captures = match.Groups["task"].Captures;
    if (null != captures)
    {
     foreach (Capture capture in captures)
     {
      SynergyTaskInfo info = (SynergyTaskInfo) tasks[capture.Value];
      if (info == null)
      {
       modification.ChangeNumber = Regex.Match(capture.Value, @"\d+").Value;
      }
      else
      {
       modification.ChangeNumber = info.TaskNumber.ToString();
       modification.ModifiedTime = info.CompletionDate;
       if (null != info.TaskSynopsis)
        modification.Comment = info.TaskSynopsis;
      }
     }
    }
    modifications.Add(modification);
   }
   return modifications.ToArray();
  }
  public Hashtable ParseTasks(string comments)
  {
   Hashtable retVal = new Hashtable();
   Regex grep = new Regex(TaskFormat, RegexOptions.CultureInvariant);
   MatchCollection matches = grep.Matches(comments);
   foreach (Match match in matches)
   {
    try
    {
     SynergyTaskInfo info = new SynergyTaskInfo();
     info.DisplayName = match.Groups["displayname"].Value;
     info.TaskNumber = int.Parse(match.Groups["task_number"].Value, CultureInfo.InvariantCulture);
     if (null != match.Groups["completion_date"] && "<void>" != match.Groups["completion_date"].Value)
      info.CompletionDate = DateTime.Parse(match.Groups["completion_date"].Value, CultureInfo.InvariantCulture);
     if (null != match.Groups["resolver"])
      info.Resolver = match.Groups["resolver"].Value;
     if (null != match.Groups["task_synopsis"])
      info.TaskSynopsis = match.Groups["task_synopsis"].Value;
     retVal.Add(info.DisplayName, info);
    }
    catch (FormatException ex)
    {
     Debug.Assert(false, "Failed to parse task " + match.Groups["displayname"].Value, ex.Message);
    }
   }
   return retVal;
  }
  public class SynergyTaskInfo
  {
   public string DisplayName = String.Empty;
   public int TaskNumber = int.MinValue;
   public string TaskSynopsis = String.Empty;
   public DateTime CompletionDate = DateTime.MinValue;
   public string Resolver = String.Empty;
  }
 }
}
