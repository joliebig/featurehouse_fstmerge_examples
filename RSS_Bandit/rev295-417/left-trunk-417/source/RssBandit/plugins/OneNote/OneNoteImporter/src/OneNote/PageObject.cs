using System;
using System.Xml;
namespace Microsoft.Office.OneNote
{
 [Serializable]
 public abstract class PageObject : ImportNode
 {
  protected PageObject()
  {
   Id = new ObjectId();
   Position = new Position();
  }
  protected internal override void SerializeToXml(XmlNode parentNode)
  {
   XmlDocument xmlDocument = parentNode.OwnerDocument;
   XmlElement objectElement = xmlDocument.CreateElement("Object");
   objectElement.SetAttribute("guid", Id.ToString());
   parentNode.AppendChild(objectElement);
   if (DeletePending)
   {
    XmlElement deleteElement = xmlDocument.CreateElement("Delete");
    objectElement.AppendChild(deleteElement);
   }
   else
   {
    Position.SerializeToXml(objectElement);
    SerializeObjectToXml(objectElement);
   }
  }
  protected internal abstract void SerializeObjectToXml(XmlNode parentNode);
  public override bool Equals(object obj)
  {
   PageObject pageObject = obj as PageObject;
   if (pageObject == null)
    return false;
   return pageObject.Id.Equals(id);
  }
  public override int GetHashCode()
  {
   return id.GetHashCode();
  }
  protected internal ObjectId Id
  {
   get
   {
    return id;
   }
   set
   {
    id = value;
   }
  }
  public Position Position
  {
   get
   {
    return (Position) GetChild("Position");
   }
   set
   {
    Position position = Position;
    if (position == value)
     return;
    if (value == null)
     throw new ArgumentNullException("Position");
    if (position != null)
     RemoveChild(position);
    AddChild(value, "Position");
   }
  }
  public virtual Size Width
  {
   get
   {
    return (Size) GetChild("width");
   }
   set
   {
    Size width = Width;
    if (value == width)
     return;
    if (width != null)
     RemoveChild(width);
    if (value != null)
     AddChild(value, "width");
   }
  }
  public virtual Size Height
  {
   get
   {
    return (Size) GetChild("height");
   }
   set
   {
    Size height = Height;
    if (value == height)
     return;
    if (height != null)
     RemoveChild(height);
    if (value != null)
     AddChild(value, "height");
   }
  }
  protected internal bool DeletePending
  {
   get
   {
    return deletePending;
   }
   set
   {
    if (value == deletePending)
     return;
    deletePending = true;
    CommitPending = true;
   }
  }
  private ObjectId id;
  private bool deletePending;
 }
}
