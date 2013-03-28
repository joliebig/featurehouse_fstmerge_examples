using System;
using System.Collections;
using System.IO;
using System.Text.RegularExpressions;
using ThoughtWorks.CruiseControl.Core.Util;
namespace ThoughtWorks.CruiseControl.Core.Sourcecontrol
{
 public class AccuRevHistoryParser : IHistoryParser
 {
  private DateTime fromDateTime;
  private DateTime toDateTime;
  private ArrayList modificationList;
  private Modification modificationTemplate;
  public AccuRevHistoryParser()
  {
  }
  public Modification[] Parse(TextReader history, DateTime from_, DateTime to)
  {
   string line;
   fromDateTime = from_;
   toDateTime = to;
   modificationList = new ArrayList();
   modificationTemplate = null;
   Regex firstTokenPattern = new Regex(@"^\s*(\S+)");
            Regex absolutePathPrefixPattern = new Regex(@"(\\|/)\.(\\|/)");
            Regex commentTextPattern = new Regex(@"^\s*# (.*)$");
   while ((line = history.ReadLine()) != null)
   {
    Match parsed = firstTokenPattern.Match(line);
    string firstToken = "";
                if (parsed.Success)
                    firstToken = parsed.Groups[1].ToString();
                switch (firstToken)
                {
                    case "transaction":
                        ParseTransaction(line);
                        break;
                    case "#":
                        if (modificationTemplate.Comment != null)
                            modificationTemplate.Comment += System.Environment.NewLine;
                        Match commentText = commentTextPattern.Match(line);
                        if (commentText.Groups.Count != 0)
                            modificationTemplate.Comment += commentText.Groups[1].ToString();
                        break;
                    case "ancestor:":
                    case "type:":
                    case "":
                        break;
                    default:
                        if (absolutePathPrefixPattern.IsMatch(firstToken))
                            ParseFileLine(line);
                        else
                            Log.Error(string.Format("Unrecognized line in AccuRev \"accurev hist\" output: {0}", line));
                        break;
                }
            }
   Log.Debug(string.Format("AccuRev reported {0} modifications", modificationList.Count));
   return (Modification[]) modificationList.ToArray(typeof (Modification));
  }
  private void ParseTransaction(string line)
  {
   modificationTemplate = new Modification();
   Regex pattern = new Regex(@"^\s*transaction\s+(\d*)\s*;\s+(\S+)s*;\s+(\d{4}/\d{2}/\d{2} \d{2}:\d{2}:\d{2})\s*;\s+user:\s+(\S+)\s*$");
   Match parsed = pattern.Match(line);
   if (parsed.Success)
   {
    GroupCollection tokens = parsed.Groups;
    modificationTemplate.ChangeNumber = int.Parse(tokens[1].ToString());
    modificationTemplate.Type = tokens[2].ToString();
    modificationTemplate.ModifiedTime = DateTime.Parse(tokens[3].ToString());
    modificationTemplate.UserName = tokens[4].ToString();
            }
   else
   {
                Log.Error(string.Format("Illegal transaction line in AccuRev \"accurev hist\" output: {0}", line));
   }
  }
  private void ParseFileLine(string line)
  {
   Regex pattern = new Regex(
          @"^" +
          @"\s+" +
          @"(\\|/)\.(\\|/)" +
          @"(([^\\/]*(\\|/))*)?" +
          @"(.*)" +
          @"\s+" +
          @"(\S*/\S*)" +
          @"\s+" +
          @"\(\S*/\S*(,\S*/\S*)*\)?\s*" +
          @"\s*" +
          @"$"
         );
   Match results = pattern.Match(line);
   if (results.Success)
   {
    GroupCollection tokens = results.Groups;
    modificationTemplate.Version = tokens[7].ToString();
    modificationTemplate.FolderName = tokens[3].ToString();
    modificationTemplate.FileName = tokens[6].ToString();
    if ((modificationTemplate.ModifiedTime >= fromDateTime) &&
        (modificationTemplate.ModifiedTime <= toDateTime))
     AddModification(modificationTemplate);
   }
   else
   {
    Log.Error(String.Format("Illegal file detail line in AccuRev \"accurev hist\" output: {0}", line));
   }
  }
  private void AddModification(Modification template)
  {
   Modification entry = new Modification();
   entry.ChangeNumber = template.ChangeNumber;
   entry.Comment = template.Comment;
   entry.EmailAddress = template.EmailAddress;
   entry.FileName = template.FileName;
   entry.FolderName = template.FolderName;
   entry.ModifiedTime = template.ModifiedTime;
   entry.Type = template.Type;
   entry.Url = template.Url;
   entry.UserName = template.UserName;
   entry.Version = template.Version;
   Log.Debug(string.Format("Added a modification: {0}", entry));
   modificationList.Add(entry);
  }
 }
}
