package net.sourceforge.squirrel_sql.fw.sql;

import java.beans.IndexedPropertyDescriptor;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;

public class SQLDriverPropertyCollectionBeanInfo extends SimpleBeanInfo
{

	private interface IPropNames extends SQLDriverPropertyCollection.IPropertyNames
	{
		
	}

	
	public PropertyDescriptor[] getPropertyDescriptors()
	{
		try
		{
			PropertyDescriptor[] result = new PropertyDescriptor[1];
			result[0] = new IndexedPropertyDescriptor(IPropNames.DRIVER_PROPERTIES,
				SQLDriverPropertyCollection.class,
							"getDriverProperties", "setDriverProperties",
							"getDriverProperty", "setDriverProperty");
			return result;
		}
		catch (IntrospectionException e)
		{
			throw new Error(e);
		}
		
	}
}

