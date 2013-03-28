using Altova.Types;
namespace LayerSet
{
 public class ValueType4 : SchemaDecimal
 {
  public ValueType4() : base()
  {
  }
  public ValueType4(string newValue) : base(newValue)
  {
   Validate();
  }
  public ValueType4(SchemaDecimal newValue) : base(newValue)
  {
   Validate();
  }
  public void Validate()
  {
   if (CompareTo(GetMinInclusive()) < 0)
    throw new System.Exception("Out of range");
   if (CompareTo(GetMaxInclusive()) > 0)
    throw new System.Exception("Out of range");
  }
  public SchemaDecimal GetMinInclusive()
  {
   return new SchemaDecimal("-180");
  }
  public SchemaDecimal GetMaxInclusive()
  {
   return new SchemaDecimal("180");
  }
 }
}
