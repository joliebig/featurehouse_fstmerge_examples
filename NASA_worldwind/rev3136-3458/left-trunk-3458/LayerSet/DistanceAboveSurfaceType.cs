using Altova.Types;
namespace LayerSet
{
 public class DistanceAboveSurfaceType : SchemaDecimal
 {
  public DistanceAboveSurfaceType() : base()
  {
  }
  public DistanceAboveSurfaceType(string newValue) : base(newValue)
  {
   Validate();
  }
  public DistanceAboveSurfaceType(SchemaDecimal newValue) : base(newValue)
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
