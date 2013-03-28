using Altova.Types;
namespace capabilities_1_3_0.wms
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
  public new void Validate()
  {
  }
 }
}
