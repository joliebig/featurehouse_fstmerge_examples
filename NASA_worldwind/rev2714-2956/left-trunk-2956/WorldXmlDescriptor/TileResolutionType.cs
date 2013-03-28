using Altova.Types;
namespace WorldXmlDescriptor
{
 public class TileResolutionType : SchemaInt
 {
  public TileResolutionType() : base()
  {
  }
  public TileResolutionType(string newValue) : base(newValue)
  {
   Validate();
  }
  public new void Validate()
  {
   if (CompareTo(GetMinExclusive()) <= 0)
    throw new System.Exception("Out of range");
  }
  public new SchemaInt GetMinExclusive()
  {
   return new SchemaInt("0");
  }
 }
}
