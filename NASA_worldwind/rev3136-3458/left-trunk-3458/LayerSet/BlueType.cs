using Altova.Types;
namespace LayerSet
{
 public class BlueType : SchemaInt
 {
  public BlueType() : base()
  {
  }
  public BlueType(string newValue) : base(newValue)
  {
   Validate();
  }
  public BlueType(SchemaInt newValue) : base(newValue)
  {
   Validate();
  }
  public void Validate()
  {
   if (CompareTo(GetMinInclusive()) < 0)
    throw new System.Exception("Out of range");
   if (CompareTo(GetMaxInclusive()) > 0)
    throw new System.Exception("Out of range");
  }
  public SchemaInt GetMinInclusive()
  {
   return new SchemaInt("0");
  }
  public SchemaInt GetMaxInclusive()
  {
   return new SchemaInt("255");
  }
 }
}
