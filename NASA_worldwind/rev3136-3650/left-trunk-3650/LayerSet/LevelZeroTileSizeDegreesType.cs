using Altova.Types;
namespace LayerSet
{
 public class LevelZeroTileSizeDegreesType : SchemaDecimal
 {
  public LevelZeroTileSizeDegreesType() : base()
  {
  }
  public LevelZeroTileSizeDegreesType(string newValue) : base(newValue)
  {
   Validate();
  }
  public LevelZeroTileSizeDegreesType(SchemaDecimal newValue) : base(newValue)
  {
   Validate();
  }
  public void Validate()
  {
   if (CompareTo(GetMinExclusive()) <= 0)
    throw new System.Exception("Out of range");
   if (CompareTo(GetMaxInclusive()) > 0)
    throw new System.Exception("Out of range");
  }
  public SchemaDecimal GetMinExclusive()
  {
   return new SchemaDecimal("0");
  }
  public SchemaDecimal GetMaxInclusive()
  {
   return new SchemaDecimal("180");
  }
 }
}
