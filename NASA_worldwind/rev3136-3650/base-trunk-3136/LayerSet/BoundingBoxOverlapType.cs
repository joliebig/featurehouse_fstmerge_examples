using Altova.Types;
namespace LayerSet
{
 public class BoundingBoxOverlapType : SchemaDecimal
 {
  public BoundingBoxOverlapType() : base()
  {
  }
  public BoundingBoxOverlapType(string newValue) : base(newValue)
  {
   Validate();
  }
  public BoundingBoxOverlapType(SchemaDecimal newValue) : base(newValue)
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
