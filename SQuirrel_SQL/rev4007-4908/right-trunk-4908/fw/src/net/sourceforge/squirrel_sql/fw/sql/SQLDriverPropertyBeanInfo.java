package net.sourceforge.squirrel_sql.fw.sql;


import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;


public class SQLDriverPropertyBeanInfo extends SimpleBeanInfo implements SQLDriverProperty.IPropertyNames
{

	
	@Override	
	public PropertyDescriptor[] getPropertyDescriptors()
	{
		try
		{
			PropertyDescriptor[] result = new PropertyDescriptor[3];
			result[0] = new PropertyDescriptor(NAME, SQLDriverProperty.class, "getName", "setName");
			result[1] = new PropertyDescriptor(VALUE, SQLDriverProperty.class, "getValue", "setValue");
			result[2] =
				new PropertyDescriptor(IS_SPECIFIED, SQLDriverProperty.class, "isSpecified", "setIsSpecified");
			return result;
		}
		catch (IntrospectionException e)
		{
			throw new Error(e);
		}
	}
}
