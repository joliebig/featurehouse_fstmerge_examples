using Altova.Types;
namespace LayerSet
{
 public class ValueType3 : SchemaDecimal
 {
  public ValueType3() : base()
  {
  }
  public ValueType3(string newValue) : base(newValue)
  {
   Validate();
  }
  public ValueType3(SchemaDecimal newValue) : base(newValue)
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
   return new SchemaDecimal("-90");
  }
  public SchemaDecimal GetMaxInclusive()
  {
   return new SchemaDecimal("90");
  }
 }
}
