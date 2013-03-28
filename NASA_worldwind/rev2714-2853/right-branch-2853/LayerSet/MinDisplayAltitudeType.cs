using Altova.Types;
namespace LayerSet
{
 public class MinDisplayAltitudeType : SchemaDecimal
 {
  public MinDisplayAltitudeType() : base()
  {
  }
  public MinDisplayAltitudeType(string newValue) : base(newValue)
  {
   Validate();
  }
  public MinDisplayAltitudeType(SchemaDecimal newValue) : base(newValue)
  {
   Validate();
  }
  public void Validate()
  {
   if (CompareTo(GetMinInclusive()) < 0)
    throw new System.Exception("Out of range");
  }
  public SchemaDecimal GetMinInclusive()
  {
   return new SchemaDecimal("0");
  }
 }
}
