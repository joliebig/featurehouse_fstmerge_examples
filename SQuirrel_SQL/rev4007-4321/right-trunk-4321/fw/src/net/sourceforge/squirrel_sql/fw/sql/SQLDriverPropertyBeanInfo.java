package net.sourceforge.squirrel_sql.fw.sql;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;

public class SQLDriverPropertyBeanInfo extends SimpleBeanInfo
							implements SQLDriverProperty.IPropertyNames
{
	private static PropertyDescriptor[] s_dscrs;

	public SQLDriverPropertyBeanInfo() throws IntrospectionException
	{
		super();
		synchronized (getClass())
		{
			if (s_dscrs == null)
			{
				s_dscrs = new PropertyDescriptor[3];
				final Class<SQLDriverProperty> clazz = SQLDriverProperty.class;
				s_dscrs[0] = new PropertyDescriptor(NAME, clazz, "getName", "setName");
				s_dscrs[1] = new PropertyDescriptor(VALUE, clazz, "getValue", "setValue");
				s_dscrs[2] = new PropertyDescriptor(IS_SPECIFIED, clazz, "isSpecified", "setIsSpecified");
			}
		}
	}

	public PropertyDescriptor[] getPropertyDescriptors()
	{
		return s_dscrs;
	}
}
