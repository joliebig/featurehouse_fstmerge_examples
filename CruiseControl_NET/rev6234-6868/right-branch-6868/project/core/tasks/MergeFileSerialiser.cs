using System;
using System.Collections.Generic;
using System.Text;
using Exortech.NetReflector;
using Exortech.NetReflector.Util;
using System.Xml;
using ThoughtWorks.CruiseControl.Core.Util;
namespace ThoughtWorks.CruiseControl.Core.Tasks
{
    public class MergeFileSerialiser
        : XmlMemberSerialiser
    {
        public MergeFileSerialiser(ReflectorMember info, ReflectorPropertyAttribute attribute)
            : base(info, attribute)
  { }
  public override object Read(XmlNode node, NetReflectorTypeTable table)
        {
         var fileList = new List<MergeFileInfo>();
         if (node != null)
         {
          if (node.Attributes.Count > 0)
     throw new NetReflectorException(string.Concat("A file list cannot directly contain attributes.", Environment.NewLine, "XML: ", node.OuterXml));
          var subNodes = node.SelectNodes("*");
          if (subNodes != null)
          {
           foreach (XmlElement fileElement in subNodes)
           {
            if (fileElement.Name == "file")
            {
             var fileSubNodes = fileElement.SelectNodes("*");
       if (fileSubNodes != null && fileSubNodes.Count > 0)
        throw new NetReflectorException(string.Concat("file cannot contain any sub-items.", Environment.NewLine, "XML: ", fileElement.OuterXml));
             var newFile = new MergeFileInfo();
             newFile.FileName = fileElement.InnerText;
             var typeAttribute = fileElement.GetAttribute("action");
             if (string.IsNullOrEmpty(typeAttribute))
             {
              newFile.MergeAction = MergeFileInfo.MergeActionType.Merge;
             }
             else
             {
              try
              {
               newFile.MergeAction = (MergeFileInfo.MergeActionType) Enum.Parse(
                                                                      typeof (MergeFileInfo.MergeActionType),
                                                                      typeAttribute);
              }
              catch (Exception error)
              {
               throw new NetReflectorConverterException(string.Concat(
                                                         "Unknown action :'", typeAttribute, Environment.NewLine,
                    "'XML: " + fileElement.InnerXml), error);
              }
             }
             Log.Debug(string.Concat("MergeFilesTask: Add '", newFile.FileName, "' to '", newFile.MergeAction.ToString(), "' file list."));
             fileList.Add(newFile);
            }
            else
            {
             throw new NetReflectorException(string.Concat(fileElement.Name, " is not a valid sub-item.",
                    Environment.NewLine, "XML: ", fileElement.OuterXml));
            }
           }
          }
         }
         return fileList.ToArray();
        }
        public override void Write(XmlWriter writer, object target)
        {
            var list = target as MergeFileInfo[];
            if (list != null)
            {
                writer.WriteStartElement(base.Attribute.Name);
                foreach (var file in list)
                {
                    writer.WriteStartElement("file");
                    writer.WriteAttributeString("action", file.MergeAction.ToString());
                    writer.WriteString(file.FileName);
                    writer.WriteEndElement();
                }
                writer.WriteEndElement();
            }
        }
    }
}
