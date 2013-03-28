using System;
using System.Collections.Generic;
using System.IO;
using System.Text;
using System.Text.RegularExpressions;
namespace ThoughtWorks.CruiseControl.Core.Sourcecontrol
{
 public class VstsHistoryParser : IHistoryParser
 {
        private DateTime startTime;
        private DateTime endTime;
  public Modification[] Parse(TextReader vstsLog, DateTime from_, DateTime to)
  {
            startTime = from_;
            endTime = to;
            string logFileLine;
            StringBuilder changeSet = new StringBuilder(null);
            var mods = new List<Modification>();
            if (vstsLog.Peek() != -1 && Convert.ToChar(vstsLog.Peek()) == Convert.ToChar("-"))
            {
                while ((logFileLine = vstsLog.ReadLine()) != null)
                {
                    if (logFileLine == "-------------------------------------------------------------------------------")
                    {
                        if (changeSet.Length != 0)
                        {
                            Modification[] tempMods = ParseChangeSet(changeSet);
                            foreach (Modification change in tempMods)
                            {
                                mods.Add(change);
                            }
                            changeSet = new StringBuilder(null);
                        }
                    }
                    else if (logFileLine == "No history entries were found for the item and version combination specified")
                    {
                        return new Modification[0];
                    }
                    else
                    {
                        changeSet.AppendLine(logFileLine);
                    }
                }
                if (!string.IsNullOrEmpty(changeSet.ToString()))
                {
                    Modification[] tempMods = ParseChangeSet(changeSet);
                    foreach (Modification change in tempMods)
                    {
                        mods.Add(change);
                    }
                }
            }
            return mods.ToArray();
  }
        private Modification[] ParseChangeSet(StringBuilder changeSet)
        {
            Regex parser = new Regex(@"Changeset:[ \t](?<changenumber>[0-9]*)\r\nUser:[ \t](?<author>.*)\r\nDate:[ \t](?<date>.*)\r\n\r\nComment:(?<comment>(?:\r\n.*)*)\r\n\r\nItems:(?<items>(?:\r\n.*)*)\r\n\r\n");
            Regex itemParser = new Regex("\n  (?<type>[^$]+) (?<item>\\$/.*)", RegexOptions.Multiline);
            Match ChangeSet = parser.Match(changeSet.ToString(), 0);
            string changeNumber = ChangeSet.Groups["changenumber"].Value;
            string author = ChangeSet.Groups["author"].Value;
            DateTime changeTime = DateTime.Parse(ChangeSet.Groups["date"].Value);
            string comment = ChangeSet.Groups["comment"].Value.Trim();
            var mods = new List<Modification>();
            if ((changeTime >= startTime) && (changeTime <= endTime))
            {
                MatchCollection items = itemParser.Matches(ChangeSet.Groups["items"].Value);
                foreach (Match item in items)
                {
                    string[] fileAndFolderNames = parseItem(item.Groups["item"].Value.Trim());
                    Modification newMod = new Modification();
                    newMod.ChangeNumber = changeNumber;
                    newMod.UserName = author;
                    newMod.ModifiedTime = changeTime;
                    newMod.Comment = comment;
                    newMod.Type = item.Groups["type"].Value;
                    newMod.FolderName = fileAndFolderNames[1];
                    newMod.FileName = fileAndFolderNames[0];
                    mods.Add(newMod);
                }
            }
            return mods.ToArray();
        }
        private string[] parseItem(string itemName)
        {
            string[] returnValues = new string[2];
            string[] newpieces = itemName.Split(';');
            int seperator = newpieces[0].LastIndexOf("/") + 1;
            returnValues[0] = newpieces[0].Substring(seperator, (newpieces[0].Length - seperator));
            string folderName = string.Empty;
            if (returnValues[0].Contains("."))
            {
                returnValues[1] = newpieces[0].Substring(0, (seperator - 1));
            }
            else
            {
                returnValues[0] = string.Empty;
                returnValues[1] = newpieces[0];
            }
            return returnValues;
        }
 }
}
