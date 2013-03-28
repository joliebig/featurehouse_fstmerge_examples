using Altova.Types;
namespace LayerSet
{
 public class MaximumDisplayAltitudeType : SchemaDecimal
 {
  public MaximumDisplayAltitudeType() : base()
  {
  }
  public MaximumDisplayAltitudeType(string newValue) : base(newValue)
  {
   Validate();
  }
  public MaximumDisplayAltitudeType(SchemaDecimal newValue) : base(newValue)
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
