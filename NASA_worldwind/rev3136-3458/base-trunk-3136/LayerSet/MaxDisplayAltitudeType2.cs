using Altova.Types;
namespace LayerSet
{
 public class MaxDisplayAltitudeType2 : SchemaDecimal
 {
  public MaxDisplayAltitudeType2() : base()
  {
  }
  public MaxDisplayAltitudeType2(string newValue) : base(newValue)
  {
   Validate();
  }
  public MaxDisplayAltitudeType2(SchemaDecimal newValue) : base(newValue)
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
