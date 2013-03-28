using Altova.Types;
namespace LayerSet
{
 public class MeshFilePathType2 : SchemaString
 {
  public MeshFilePathType2() : base()
  {
  }
  public MeshFilePathType2(string newValue) : base(newValue)
  {
   Validate();
  }
  public MeshFilePathType2(SchemaString newValue) : base(newValue)
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
