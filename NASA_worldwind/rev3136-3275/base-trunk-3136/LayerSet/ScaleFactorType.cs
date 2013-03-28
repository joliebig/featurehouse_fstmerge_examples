using Altova.Types;
namespace LayerSet
{
 public class ScaleFactorType : SchemaDecimal
 {
  public ScaleFactorType() : base()
  {
  }
  public ScaleFactorType(string newValue) : base(newValue)
  {
   Validate();
  }
  public ScaleFactorType(SchemaDecimal newValue) : base(newValue)
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
