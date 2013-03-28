using System;
using System.Globalization;
using Altova;
namespace Altova.Types
{
 public class SchemaTypeFactory
 {
  public static ISchemaType CreateInstanceByString(string newvalue)
  {
   if( newvalue.ToLower().CompareTo("false")==0 )
    return new SchemaBoolean( false );
   if( newvalue.ToLower().CompareTo("true")==0 )
    return new SchemaBoolean( true );
   try
   {
    SchemaDateTime result = new SchemaDateTime( newvalue );
    return result;
   }
   catch( StringParseException ) {}
   try
   {
    SchemaDuration result = new SchemaDuration( newvalue );
    return result;
   }
   catch( StringParseException ) {}
   try
   {
    SchemaDate result = new SchemaDate( newvalue );
    return result;
   }
   catch( StringParseException ) {}
   try
   {
    SchemaTime result = new SchemaTime( newvalue );
    return result;
   }
   catch( StringParseException ) {}
   try
   {
    decimal tmp = Convert.ToDecimal(newvalue, CultureInfo.InvariantCulture);
    if( newvalue.IndexOf(".") < 0 )
    {
     if( tmp <= Int32.MaxValue && tmp >= Int32.MinValue )
      return new SchemaInt( (int)tmp );
     return new SchemaLong( (long)tmp );
    }
    else
    {
     return new SchemaDecimal( tmp );
    }
   }
   catch (FormatException )
   {
    return new SchemaString( newvalue );
   }
  }
  public static ISchemaType CreateInstanceByObject( Object newvalue )
  {
   switch( Type.GetTypeCode( newvalue.GetType() ) )
   {
    case TypeCode.Boolean:
     return new SchemaBoolean( (bool)newvalue );
    case TypeCode.Byte:
    case TypeCode.Char:
    case TypeCode.Int16:
    case TypeCode.Int32:
    case TypeCode.SByte:
    case TypeCode.UInt16:
     return new SchemaInt( (int)newvalue );
    case TypeCode.DateTime:
     return new SchemaDateTime( (DateTime)newvalue );
    case TypeCode.Decimal:
     return new SchemaDecimal( (decimal)newvalue );
    case TypeCode.Double:
    case TypeCode.Single:
     return new SchemaDouble( (double)newvalue );
    case TypeCode.Empty:
    case TypeCode.DBNull:
     return new SchemaBoolean();
    case TypeCode.Int64:
    case TypeCode.UInt32:
    case TypeCode.UInt64:
     return new SchemaLong( (Int64)newvalue );
    case TypeCode.String:
     return new SchemaString( (string)newvalue );
   }
   if( newvalue is TimeSpan )
    return new SchemaDuration( (TimeSpan)newvalue );
   return CreateInstanceByString( newvalue.ToString() );
  }
 }
}
