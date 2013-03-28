using Altova.Types;
namespace LayerSet
{
 public class ScaleFactorType2 : SchemaDecimal
 {
  public ScaleFactorType2() : base()
  {
  }
  public ScaleFactorType2(string newValue) : base(newValue)
  {
   Validate();
  }
  public ScaleFactorType2(SchemaDecimal newValue) : base(newValue)
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
