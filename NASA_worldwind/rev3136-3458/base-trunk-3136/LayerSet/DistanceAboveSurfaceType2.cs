using Altova.Types;
namespace LayerSet
{
 public class DistanceAboveSurfaceType2 : SchemaDecimal
 {
  public DistanceAboveSurfaceType2() : base()
  {
  }
  public DistanceAboveSurfaceType2(string newValue) : base(newValue)
  {
   Validate();
  }
  public DistanceAboveSurfaceType2(SchemaDecimal newValue) : base(newValue)
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
