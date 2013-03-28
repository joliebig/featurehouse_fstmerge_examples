using Altova.Types;
namespace LayerSet
{
 public class MaximumDisplayAltitudeType2 : SchemaDecimal
 {
  public MaximumDisplayAltitudeType2() : base()
  {
  }
  public MaximumDisplayAltitudeType2(string newValue) : base(newValue)
  {
   Validate();
  }
  public MaximumDisplayAltitudeType2(SchemaDecimal newValue) : base(newValue)
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
