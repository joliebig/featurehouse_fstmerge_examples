using System;
using Altova;
namespace Altova.Types
{
 public class SchemaTypeException : AltovaException
 {
  public SchemaTypeException(string text)
   : base( text )
  {
  }
  public SchemaTypeException(Exception other)
   : base( other )
  {
  }
 }
 public class StringParseException : SchemaTypeException
 {
  int position;
  public StringParseException(string text, int newposition)
   : base( text )
  {
   position = newposition;
  }
  public StringParseException(Exception other)
   : base( other )
  {
  }
 }
 public class NotANumberException : SchemaTypeException
 {
  public NotANumberException(string text)
   : base( text )
  {
  }
  public NotANumberException(Exception other)
   : base( other )
  {
  }
 }
 public class TypesIncompatibleException : SchemaTypeException
 {
  protected ISchemaType object1;
  protected ISchemaType object2;
  public TypesIncompatibleException(ISchemaType newobj1, ISchemaType newobj2)
   : base("Incompatible schema-types")
  {
   object1 = newobj1;
   object2 = newobj2;
  }
  public TypesIncompatibleException(Exception other)
   : base( other )
  {
  }
 }
 public class ValuesNotConvertableException : SchemaTypeException
 {
  protected ISchemaType object1;
  protected ISchemaType object2;
  public ValuesNotConvertableException(ISchemaType newobj1, ISchemaType newobj2)
   : base("Values could not be converted")
  {
   object1 = newobj1;
   object2 = newobj2;
  }
  public ValuesNotConvertableException(Exception other)
   : base( other )
  {
  }
 }
}
