using System;
using System.IO;
using System.Xml;
namespace Microsoft.Office.OneNote
{
 [Serializable]
 public abstract class Data : ImportNode
 {
  protected Data()
  {
  }
  public override object Clone()
  {
   return MemberwiseClone();
  }
  public override bool Equals(object obj)
  {
   Data other = obj as Data;
   if (other == null)
    return data.Equals(obj);
   return other.data.Equals(data);
  }
  public override int GetHashCode()
  {
   return data.GetHashCode();
  }
  public override string ToString()
  {
   return data.ToString();
  }
  protected internal abstract override void SerializeToXml(XmlNode parentNode);
  protected internal string data = null;
 }
 [Serializable]
 public class FileData : Data
 {
  public FileData(FileInfo file)
  {
   data = file.FullName;
  }
  protected internal override void SerializeToXml(XmlNode parentNode)
  {
   XmlDocument xmlDocument = parentNode.OwnerDocument;
   XmlElement element = xmlDocument.CreateElement("File");
   element.SetAttribute("path", data);
   parentNode.AppendChild(element);
  }
 }
 [Serializable]
 public class BinaryData : Data
 {
  public BinaryData(byte[] buffer)
  {
   data = Convert.ToBase64String(buffer);
  }
  protected internal override void SerializeToXml(XmlNode parentNode)
  {
   XmlDocument xmlDocument = parentNode.OwnerDocument;
   XmlElement element = xmlDocument.CreateElement("Data");
   parentNode.AppendChild(element);
   XmlText text = xmlDocument.CreateTextNode(data);
   element.AppendChild(text);
  }
 }
 [Serializable]
 public class StringData : Data
 {
  public StringData(String data)
  {
   this.data = data;
  }
  protected internal override void SerializeToXml(XmlNode parentNode)
  {
   XmlDocument xmlDocument = parentNode.OwnerDocument;
   XmlElement element = xmlDocument.CreateElement("Data");
   parentNode.AppendChild(element);
   XmlCDataSection cdata = xmlDocument.CreateCDataSection(data);
   element.AppendChild(cdata);
  }
 }
}
