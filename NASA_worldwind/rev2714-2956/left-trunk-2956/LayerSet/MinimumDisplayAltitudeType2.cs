using Altova.Types;
namespace LayerSet
{
 public class MinimumDisplayAltitudeType2 : SchemaDecimal
 {
  public MinimumDisplayAltitudeType2() : base()
  {
  }
  public MinimumDisplayAltitudeType2(string newValue) : base(newValue)
  {
   Validate();
  }
  public MinimumDisplayAltitudeType2(SchemaDecimal newValue) : base(newValue)
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
