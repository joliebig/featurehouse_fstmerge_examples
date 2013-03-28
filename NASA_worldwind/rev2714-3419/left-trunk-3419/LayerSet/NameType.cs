using Altova.Types;
namespace LayerSet
{
 public class NameType : SchemaString
 {
  public NameType() : base()
  {
  }
  public NameType(string newValue) : base(newValue)
  {
   Validate();
  }
  public NameType(SchemaString newValue) : base(newValue)
  {
   Validate();
  }
  public void Validate()
  {
  }
 }
}
