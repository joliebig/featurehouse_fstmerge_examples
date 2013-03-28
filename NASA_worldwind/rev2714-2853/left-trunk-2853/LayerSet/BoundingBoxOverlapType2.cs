using Altova.Types;
namespace LayerSet
{
 public class BoundingBoxOverlapType2 : SchemaDecimal
 {
  public BoundingBoxOverlapType2() : base()
  {
  }
  public BoundingBoxOverlapType2(string newValue) : base(newValue)
  {
   Validate();
  }
  public BoundingBoxOverlapType2(SchemaDecimal newValue) : base(newValue)
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
