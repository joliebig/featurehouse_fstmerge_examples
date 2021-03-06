using System;
namespace Microsoft.Office.OneNote
{
 [Serializable]
 public class ObjectId
 {
  public ObjectId()
  {
   guid = Guid.NewGuid();
  }
  public ObjectId(Guid guid)
  {
   if (guid.Equals(Guid.Empty))
   {
    throw (new ArgumentException("Invalid GUID argument."));
   }
   this.guid = guid;
  }
  public ObjectId(string serialized)
  {
   if (serialized.Length == 0)
   {
    throw (new ArgumentException("Unable to parse emptry string."));
   }
   char[] delimiters = {'{', '}'};
   serialized.TrimStart(delimiters);
   serialized.TrimEnd(delimiters);
   Guid deserialized = new Guid(serialized);
   if (deserialized.Equals(Guid.Empty))
   {
    throw (new ArgumentException("Invalid GUID argument."));
   }
   this.guid = deserialized;
  }
  public override string ToString()
  {
   return '{' + guid.ToString() + '}';
  }
  public override bool Equals(object obj)
  {
   ObjectId objId = obj as ObjectId;
   if (objId == null)
    return false;
   return guid.Equals(objId.guid);
  }
  public override int GetHashCode()
  {
   return guid.GetHashCode();
  }
  private Guid guid;
 }
}
