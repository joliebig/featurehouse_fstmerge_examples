package net.sourceforge.squirrel_sql.fw.sql;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;

public class ForeignKeyInfoBeanInfo extends DatabaseObjectInfoBeanInfo
									implements ForeignKeyInfo.IPropertyNames
{
	private static PropertyDescriptor[] s_dscrs;

	public ForeignKeyInfoBeanInfo() throws IntrospectionException
	{
		super();
		synchronized (getClass())
		{
			if (s_dscrs == null)
			{
				PropertyDescriptor[] sub = new PropertyDescriptor[2];
				final Class<ForeignKeyInfo> clazz = ForeignKeyInfo.class;
				sub[0] = new PropertyDescriptor(PK_CATALOG_NAME,
								clazz, "getPrimaryKeyCatalogName", null);
				sub[1] = new PropertyDescriptor(PK_SCHEMA_NAME,
								clazz, "getPrimaryKeySchemaName", null);
				PropertyDescriptor[] base = super.getPropertyDescriptors();
				if (base == null)
				{
					base = new PropertyDescriptor[0];
				}
				s_dscrs = new PropertyDescriptor[base.length + sub.length];
				System.arraycopy(base, 0, s_dscrs, 0, base.length);
				System.arraycopy(sub, 0, s_dscrs, base.length, sub.length);
			}
		}
	}

	public PropertyDescriptor[] getPropertyDescriptors()
	{
		return s_dscrs;
	}
}
