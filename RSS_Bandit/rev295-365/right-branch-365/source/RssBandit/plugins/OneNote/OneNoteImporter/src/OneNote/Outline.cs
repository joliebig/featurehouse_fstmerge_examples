using System;
using System.Collections;
using System.Xml;
namespace Microsoft.Office.OneNote
{
 [Serializable]
 public class OutlineObject : PageObject, IEnumerable
 {
  public OutlineObject()
  {
  }
  public override object Clone()
  {
   OutlineObject clone = new OutlineObject();
   clone.Width = this.Width;
   clone.Position = this.Position;
   foreach (OutlineContent outlineContent in this)
   {
    clone.AddContent(outlineContent);
   }
   return clone;
  }
  public void AddContent(OutlineContent outlineContent)
  {
   AddChild(outlineContent);
  }
  public void RemoveContent(OutlineContent outlineContent)
  {
   RemoveChild(outlineContent);
  }
  public IEnumerator GetEnumerator()
  {
   return new OutlineEnumerator(this);
  }
  protected internal override void SerializeObjectToXml(XmlNode parentNode)
  {
   XmlDocument xmlDocument = parentNode.OwnerDocument;
   XmlElement outlineElement = xmlDocument.CreateElement("Outline");
   if (Width != null)
    Width.SerializeToXml(outlineElement);
   parentNode.AppendChild(outlineElement);
   foreach (OutlineContent content in this)
   {
    content.SerializeToXml(outlineElement);
   }
  }
  public override Size Height
  {
   get
   {
    return null;
   }
   set
   {
    throw new NotSupportedException("Outlines do not have explicit heights.");
   }
  }
  class OutlineEnumerator : IEnumerator
  {
   protected internal OutlineEnumerator(OutlineObject outline)
   {
    this.outline = outline;
    Reset();
   }
   public void Reset()
   {
    index = -1;
   }
   public object Current
   {
    get
    {
     if (index < outline.GetChildCount())
     {
      return outline.GetChild(index);
     }
     return null;
    }
   }
   public bool MoveNext()
   {
    while (++index < outline.GetChildCount() &&
     !(outline.GetChild(index) is OutlineContent))
    {
     continue;
    }
    return (index < outline.GetChildCount());
   }
   private OutlineObject outline;
   private int index;
  }
 }
 [Serializable]
 public abstract class OutlineContent : ImportNode
 {
  protected internal override abstract void SerializeToXml(XmlNode parentNode);
 }
 public enum OutlineAlignment
 {
  DEFAULT,
  LEFT,
  CENTER,
  RIGHT
 }
}
