using System;
using System.Collections;
using System.Globalization;
using System.IO;
using System.Xml;
using ThoughtWorks.CruiseControl.Core.Util;
namespace ThoughtWorks.CruiseControl.Core.Sourcecontrol
{
 public class SvnHistoryParser : IHistoryParser
 {
  public Modification[] Parse(TextReader svnLog, DateTime from_, DateTime to)
  {
   ArrayList mods = new ArrayList();
   XmlNode svnLogRoot = ReadSvnLogIntoXmlNode(svnLog);
   XmlNodeList logEntries = svnLogRoot.SelectNodes("/log/logentry");
            if (logEntries == null || logEntries.Count == 0)
                Log.Debug("No <logentry>s found under <log>.");
            else
            {
                foreach (XmlNode logEntry in logEntries)
                {
                    mods.AddRange(ParseModificationsFromLogEntry(logEntry, from_, to));
                }
            }
            return (Modification[]) mods.ToArray(typeof(Modification));
  }
  private XmlNode ReadSvnLogIntoXmlNode(TextReader svnLog)
  {
   string logText = svnLog.ReadToEnd();
   XmlDocument log = new XmlDocument();
   try
   {
    log.LoadXml(logText);
   }
   catch (XmlException ex)
   {
    throw new CruiseControlException(string.Format("Unable to load the output from_ svn: {0}", logText), ex);
   }
   return log.DocumentElement;
  }
        private ArrayList ParseModificationsFromLogEntry(XmlNode logEntry, DateTime from_, DateTime to)
  {
            try
            {
                DateTime changeTime = ParseDate(logEntry);
                if (changeTime == DateTime.MinValue || changeTime < from_ || to < changeTime)
                {
                    return new ArrayList();
                }
                int changeNumber = ParseChangeNumber(logEntry);
                string author = ParseAuthor(logEntry);
                string message = ParseMessage(logEntry);
                XmlNodeList paths = logEntry.SelectNodes("paths/path");
                if (paths == null)
                    return new ArrayList();
                ArrayList mods = new ArrayList();
                foreach (XmlNode path in paths)
                {
                    Modification mod = new Modification();
                    mod.ChangeNumber = changeNumber;
                    mod.ModifiedTime = changeTime;
                    mod.UserName = author;
                    mod.Comment = message;
                    mod.Type = ModificationType(path);
                    string fullFileName = path.InnerText;
                    mod.FolderName = GetFolderFromPath(fullFileName);
                    mod.FileName = GetFileFromPath(fullFileName);
                    mods.Add(mod);
                }
                return mods;
            }
            catch (XmlException e)
            {
                throw new CruiseControlException("Invalid XML received from_ \"svn log --xml\" output: \"" + logEntry + "\".", e);
            }
  }
  private string ModificationType(XmlNode path)
  {
   string action = GetAttributeFromNode(path, "action");
   switch (action)
   {
    case "A":
     return "Added";
    case "D":
     return "Deleted";
    case "M":
     return "Modified";
    case "R":
     return "Replaced";
    default:
     return "Unknown action: " + action;
   }
  }
        private static string ParseMessage(XmlNode logEntry)
  {
      String msg = "";
   XmlNode msgNode = logEntry.SelectSingleNode("msg");
            if (msgNode != null)
                msg = msgNode.InnerText;
      return msg;
  }
        private string ParseAuthor(XmlNode logEntry)
  {
   String author = "";
   XmlNode authorNode = logEntry.SelectSingleNode("author");
   if (authorNode != null)
   {
    author = authorNode.InnerText;
   }
   return author;
  }
  private DateTime ParseDate(XmlNode logEntry)
  {
      DateTime date = DateTime.MinValue;
   XmlNode dateNode = logEntry.SelectSingleNode("date");
            if (dateNode != null)
                date = ParseDate(dateNode.InnerText);
            return date;
  }
  private int ParseChangeNumber(XmlNode logEntry)
  {
   String revision = GetAttributeFromNode(logEntry, "revision");
   return int.Parse(revision);
  }
  private string GetFolderFromPath(string fullFileName)
  {
   int lastSlashIdx = fullFileName.LastIndexOf("/");
   return fullFileName.Substring(0, lastSlashIdx);
  }
        private string GetFileFromPath(string fullFileName)
  {
   int lastSlashIdx = fullFileName.LastIndexOf("/");
   return fullFileName.Substring(lastSlashIdx + 1);
  }
  private string GetAttributeFromNode(XmlNode node, string attributeName)
  {
   XmlAttributeCollection attributes = node.Attributes;
   XmlAttribute attribute = (XmlAttribute) attributes.GetNamedItem(attributeName);
   return attribute.InnerText;
  }
        private DateTime ParseDate(string date)
  {
   return DateTime.Parse(date, CultureInfo.InvariantCulture);
  }
 }
}
