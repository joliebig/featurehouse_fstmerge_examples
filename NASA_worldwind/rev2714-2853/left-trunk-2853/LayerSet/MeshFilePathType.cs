using Altova.Types;
namespace LayerSet
{
 public class MeshFilePathType : SchemaString
 {
  public MeshFilePathType() : base()
  {
  }
  public MeshFilePathType(string newValue) : base(newValue)
  {
   Validate();
  }
  public MeshFilePathType(SchemaString newValue) : base(newValue)
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
