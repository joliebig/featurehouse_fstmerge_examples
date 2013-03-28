using Altova.Types;
namespace LayerSet
{
 public class MinDisplayAltitudeType2 : SchemaDecimal
 {
  public MinDisplayAltitudeType2() : base()
  {
  }
  public MinDisplayAltitudeType2(string newValue) : base(newValue)
  {
   Validate();
  }
  public MinDisplayAltitudeType2(SchemaDecimal newValue) : base(newValue)
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
