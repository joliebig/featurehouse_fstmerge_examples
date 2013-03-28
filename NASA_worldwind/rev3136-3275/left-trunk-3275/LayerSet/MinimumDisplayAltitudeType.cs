using Altova.Types;
namespace LayerSet
{
 public class MinimumDisplayAltitudeType : SchemaDecimal
 {
  public MinimumDisplayAltitudeType() : base()
  {
  }
  public MinimumDisplayAltitudeType(string newValue) : base(newValue)
  {
   Validate();
  }
  public MinimumDisplayAltitudeType(SchemaDecimal newValue) : base(newValue)
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
