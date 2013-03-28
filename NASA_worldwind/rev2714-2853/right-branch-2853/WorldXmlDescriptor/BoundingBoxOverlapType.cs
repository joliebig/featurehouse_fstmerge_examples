using Altova.Types;
namespace WorldXmlDescriptor
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
  public new void Validate()
  {
   if (CompareTo(GetMinInclusive()) < 0)
    throw new System.Exception("Out of range");
  }
  public new SchemaDecimal GetMinInclusive()
  {
   return new SchemaDecimal("0");
  }
 }
}
