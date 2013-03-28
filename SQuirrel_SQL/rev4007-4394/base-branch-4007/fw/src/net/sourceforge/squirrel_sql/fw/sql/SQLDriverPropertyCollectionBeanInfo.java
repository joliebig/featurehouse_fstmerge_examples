package net.sourceforge.squirrel_sql.fw.sql;

import java.beans.IndexedPropertyDescriptor;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;

public class SQLDriverPropertyCollectionBeanInfo extends SimpleBeanInfo
{
	private static PropertyDescriptor[] s_desc;

	private interface IPropNames extends SQLDriverPropertyCollection.IPropertyNames
	{
		
	}

	public SQLDriverPropertyCollectionBeanInfo() throws IntrospectionException
	{
		super();
		if (s_desc == null)
		{
			final Class<SQLDriverPropertyCollection> clazz = 
			    SQLDriverPropertyCollection.class;
			s_desc = new PropertyDescriptor[1];
			s_desc[0] = new IndexedPropertyDescriptor(IPropNames.DRIVER_PROPERTIES,
							clazz,
							"getDriverProperties", "setDriverProperties",
							"getDriverProperty", "setDriverProperty");
		}
	}

	public PropertyDescriptor[] getPropertyDescriptors()
	{
		return s_desc;
	}
}

