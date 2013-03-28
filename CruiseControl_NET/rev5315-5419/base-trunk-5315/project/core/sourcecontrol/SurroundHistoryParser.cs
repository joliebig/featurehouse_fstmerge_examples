using System;
using System.Collections;
using System.Globalization;
using System.IO;
using System.Text.RegularExpressions;
namespace ThoughtWorks.CruiseControl.Core.Sourcecontrol
{
 public class SurroundHistoryParser : IHistoryParser
 {
  public const string TO_SSCM_DATE_FORMAT = "yyyyMMddHHmmss";
  public Modification[] Parse(TextReader sscmLog, DateTime from_, DateTime to)
  {
   string line = sscmLog.ReadLine();
   int totalLines = int.Parse(line.Split('-')[1]);
   ArrayList modList = new ArrayList(totalLines);
   for (int i = 0; i < totalLines; i++)
   {
    line = sscmLog.ReadLine();
    modList.Add(ParseModificationLine(line));
   }
   return (Modification[]) modList.ToArray(typeof (Modification));
  }
  private Modification ParseModificationLine(string line)
  {
   Match match = Regex.Match(line, @"^<([^>]*)><([^>]*)><([^>]*)><([^>]*)><([^>]*)><([^>]*)><([^>]*)><([^>]*)>$");
   if (!match.Success)
   {
    throw new ArgumentException("Unable to parse line: " + line);
   }
   Modification modification = new Modification();
   modification.FolderName = match.Groups[1].ToString();
   modification.FileName = match.Groups[2].ToString();
   modification.ChangeNumber = Int32.Parse(match.Groups[3].ToString());
   modification.Type = match.Groups[4].ToString();
   modification.ModifiedTime = DateTime.ParseExact(match.Groups[5].ToString(), TO_SSCM_DATE_FORMAT, CultureInfo.InvariantCulture);
   modification.Comment = match.Groups[6].ToString();
   modification.UserName = match.Groups[7].ToString();
   modification.EmailAddress = match.Groups[8].ToString();
   return modification;
  }
 }
}
