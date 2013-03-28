using Altova.Types;
namespace LayerSet
{
 public class NameType2 : SchemaString
 {
  public NameType2() : base()
  {
  }
  public NameType2(string newValue) : base(newValue)
  {
   Validate();
  }
  public NameType2(SchemaString newValue) : base(newValue)
  {
   Validate();
  }
  public void Validate()
  {
   if (Value.Length < GetMinLength())
    throw new System.Exception("Too short");
  }
  public int GetMinLength()
  {
   return 1;
  }
 }
}
