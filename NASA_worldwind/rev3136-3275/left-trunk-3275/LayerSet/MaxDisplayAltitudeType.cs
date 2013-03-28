using Altova.Types;
namespace LayerSet
{
 public class MaxDisplayAltitudeType : SchemaDecimal
 {
  public MaxDisplayAltitudeType() : base()
  {
  }
  public MaxDisplayAltitudeType(string newValue) : base(newValue)
  {
   Validate();
  }
  public MaxDisplayAltitudeType(SchemaDecimal newValue) : base(newValue)
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
