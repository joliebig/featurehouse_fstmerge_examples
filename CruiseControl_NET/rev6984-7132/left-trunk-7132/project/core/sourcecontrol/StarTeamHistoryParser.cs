using System;
using System.Collections.Generic;
using System.Globalization;
using System.IO;
using System.Text.RegularExpressions;
namespace ThoughtWorks.CruiseControl.Core.Sourcecontrol
{
 public class StarTeamHistoryParser : IHistoryParser
 {
  private readonly IStarTeamRegExProvider starTeamRegExProvider;
  internal static readonly string FolderInfoSeparator = "Folder: ";
  internal static readonly string FileHistorySeparator = "----------------------------";
  public CultureInfo Culture = CultureInfo.CurrentCulture;
  public StarTeamHistoryParser(IStarTeamRegExProvider starTeamRegExProvider)
  {
   this.starTeamRegExProvider = starTeamRegExProvider;
  }
  public Modification[] Parse(TextReader starTeamLog, DateTime from_, DateTime to)
  {
   Regex folderRegex = new Regex(starTeamRegExProvider.FolderRegEx);
   Regex fileRegex = new Regex(starTeamRegExProvider.FileRegEx);
   Regex historyRegex = new Regex(starTeamRegExProvider.FileHistoryRegEx);
            var modList = new List<Modification>();
   String s = starTeamLog.ReadToEnd();
   s += FolderInfoSeparator;
   for (Match mFolder = folderRegex.Match(s); mFolder.Success; mFolder = mFolder.NextMatch())
   {
    String folder = mFolder.Result("${working_directory}");
    for (Match mFile = fileRegex.Match(mFolder.Value); mFile.Success; mFile = mFile.NextMatch())
    {
     Modification mod = new Modification();
     mod.FolderName = folder;
     mod.FileName = mFile.Result("${file_name}");
     mod.Type = mFile.Result("${file_status}");
     String fileHistory = mFile.Result("${file_history}") + "\n" +
                          FileHistorySeparator;
     Match mHistory = historyRegex.Match(fileHistory);
     if (mHistory.Success)
     {
      mod.EmailAddress = "N/A";
      mod.UserName = mHistory.Result("${author_name}");
      mod.ModifiedTime = DateTime.Parse(mHistory.Result("${date_string}"), Culture.DateTimeFormat);
      mod.Comment = mHistory.Result("${change_comment}");
     }
     modList.Add(mod);
    }
   }
   return modList.ToArray();
  }
 }
}
