using System;
using System.Collections;
using System.Globalization;
using System.IO;
using System.Text;
using System.Xml;
namespace ThoughtWorks.CruiseControl.Core.Sourcecontrol
{
 public class VaultHistoryParser : IHistoryParser
 {
  private CultureInfo culture;
  public VaultHistoryParser() : this(CultureInfo.CurrentCulture)
  {}
  public VaultHistoryParser(CultureInfo culture)
  {
   this.culture = culture;
  }
  public Modification[] Parse(TextReader history, DateTime from_, DateTime to)
  {
   ArrayList mods = new ArrayList();
   XmlDocument xml = new XmlDocument();
   xml.LoadXml(ExtractXmlFromHistory(history));
   XmlNode parent = xml.SelectSingleNode("/vault/history");
   foreach (XmlNode node in parent.ChildNodes)
   {
    if (EntryWithinRange(node, from_, to))
    {
     Modification modification = GetModification(node);
     mods.Add(modification);
    }
   }
   return (Modification[]) mods.ToArray(typeof (Modification));
  }
  private string ExtractXmlFromHistory(TextReader history)
  {
   string output = history.ReadToEnd();
   return Vault3.ExtractXmlFromOutput(output);
  }
  private bool EntryWithinRange(XmlNode node, DateTime from_, DateTime to)
  {
   DateTime date = DateTime.Parse(node.Attributes["date"].InnerText, culture);
   return (date > from_ && date < to);
  }
  private Modification GetModification(XmlNode node)
  {
   string folderName = null;
   string fileName = null;
   StringBuilder nameBuilder = new StringBuilder(255);
   ushort modTypeID = ushort.Parse(node.Attributes["type"].Value);
   int index;
   nameBuilder.Append(node.Attributes["name"].InnerText);
   switch ( modTypeID )
   {
    case 10:
    case 80:
     index = node.Attributes["actionString"].InnerText.LastIndexOf(' ');
     nameBuilder.Append('/');
     nameBuilder.Append(node.Attributes["actionString"].InnerText.Substring(index).Trim());
     break;
   }
   string name = nameBuilder.ToString();
   index = name.LastIndexOf('/');
   if (index == -1)
   {
    folderName = name;
   }
   else
   {
    folderName = name.Substring(0, index);
    fileName = name.Substring(index + 1, name.Length - index - 1);
   }
   DateTime date = DateTime.Parse(node.Attributes["date"].InnerText, culture);
   Modification modification = new Modification();
   modification.FileName = fileName;
   modification.FolderName = folderName;
   modification.ModifiedTime = date;
   modification.UserName = node.Attributes["user"].InnerText;
   modification.Type = GetTypeString(node.Attributes["type"].InnerText);
   modification.Comment = GetComment(node);
   modification.ChangeNumber = int.Parse(node.Attributes["txid"].InnerText);
   return modification;
  }
  private string GetComment(XmlNode node)
  {
   string comment = null;
   if (node.Attributes["comment"] != null)
   {
    comment = node.Attributes["comment"].InnerText;
   }
   return comment;
  }
  private string GetTypeString(string type)
  {
   switch (type)
   {
    case "10":
     return "Added";
    case "20":
     return "Branched from_";
    case "30":
     return "Branched from_ item";
    case "40":
     return "Branched from_ share";
    case "50":
     return "Branched from_ share item";
    case "60":
     return "Checked in";
    case "70":
     return "Created";
    case "80":
     return "Deleted";
    case "90":
     return "Labeled";
    case "120":
     return "Moved from_";
    case "130":
     return "Moved to";
    case "140":
     return "Obliterated";
    case "150":
     return "Pinned";
    case "160":
     return "Property changed";
    case "170":
     return "Renamed";
    case "180":
     return "Renamed item";
    case "190":
     return "Shared to";
    case "200":
     return "Snapshot";
    case "201":
     return "Snapshot from_";
    case "202":
     return "Snapshot item";
    case "210":
     return "Undeleted";
    case "220":
     return "Unpinned";
    case "230":
     return "Rolled back";
   }
   return type;
  }
 }
}
