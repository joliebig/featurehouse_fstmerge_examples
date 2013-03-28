
package net.sourceforge.squirrel_sql.fw.dialects;

import java.lang.reflect.Field;
import java.sql.Types;

import net.sourceforge.squirrel_sql.BaseSQuirreLTestCase;
import net.sourceforge.squirrel_sql.fw.sql.JDBCTypeMapper;

import org.hibernate.MappingException;

public class DialectTestCase extends BaseSQuirreLTestCase
{

	protected void testAllTypes(HibernateDialect d)
	{
		try
		{
			Field[] fields = java.sql.Types.class.getDeclaredFields();
			for (int i = 0; i < fields.length; i++)
			{
				Field field = fields[i];
				Integer jdbcType = field.getInt(null);
				testType(jdbcType, d);
			}
		}
		catch (Exception e)
		{
			fail(e.getMessage());
		}
	}

	protected void testType(int type, HibernateDialect dialect)
	{
		try
		{
			dialect.getTypeName(type, 10, 0, 0);
		}
		catch (MappingException e)
		{
			
			
			
			
			
			if (type != Types.NULL 
				&& type != Types.DATALINK
				&& type != Types.OTHER
				&& type != Types.JAVA_OBJECT
				&& type != Types.DISTINCT
				&& type != Types.STRUCT
				&& type != Types.ARRAY
				&& type != Types.REF
				
				&& type != -8   
				&& type != -9   
				&& type != -15  
				&& type != -16  
				&& type != 2011 
				&& type != 2009 
				) 
			{
				fail("No mapping for type: " + type + "=" + JDBCTypeMapper.getJdbcTypeName(type));
			}
		}
	}

}
