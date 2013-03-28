package net.sourceforge.squirrel_sql.fw.sql;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;

public class DatabaseObjectInfoBeanInfo extends SimpleBeanInfo
{
	private static PropertyDescriptor[] s_dscrs;

	public DatabaseObjectInfoBeanInfo() throws IntrospectionException
	{
		super();
		synchronized (getClass())
		{
			if (s_dscrs == null)
			{
				s_dscrs = new PropertyDescriptor[4];
				final Class<DatabaseObjectInfo> clazz = DatabaseObjectInfo.class;
				int idx = 0;
				s_dscrs[idx++] = new PropertyDescriptor(
								DatabaseObjectInfo.IPropertyNames.CATALOG_NAME,
								clazz, "getCatalogName", null);
				s_dscrs[idx++] = new PropertyDescriptor(
								DatabaseObjectInfo.IPropertyNames.SCHEMA_NAME,
								clazz, "getSchemaName", null);
				s_dscrs[idx++] = new PropertyDescriptor(
								DatabaseObjectInfo.IPropertyNames.SIMPLE_NAME,
								clazz, "getSimpleName", null);
				s_dscrs[idx++] = new PropertyDescriptor(
								DatabaseObjectInfo.IPropertyNames.QUALIFIED_NAME,
								clazz, "getQualifiedName", null);
			}
		}
	}

	public PropertyDescriptor[] getPropertyDescriptors()
	{
		return s_dscrs;
	}
}
