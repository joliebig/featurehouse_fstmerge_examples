using Altova.Types;
namespace WorldXmlDescriptor
{
 public class ValueType2 : SchemaDecimal
 {
  public ValueType2() : base()
  {
  }
  public ValueType2(string newValue) : base(newValue)
  {
   Validate();
  }
  public new void Validate()
  {
   if (CompareTo(GetMinInclusive()) < 0)
    throw new System.Exception("Out of range");
   if (CompareTo(GetMaxInclusive()) > 0)
    throw new System.Exception("Out of range");
  }
  public new SchemaDecimal GetMinInclusive()
  {
   return new SchemaDecimal("-180");
  }
  public new SchemaDecimal GetMaxInclusive()
  {
   return new SchemaDecimal("180");
  }
 }
}
