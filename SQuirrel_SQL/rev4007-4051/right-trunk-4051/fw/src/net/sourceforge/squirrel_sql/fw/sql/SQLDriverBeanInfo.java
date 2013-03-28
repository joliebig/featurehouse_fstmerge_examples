package net.sourceforge.squirrel_sql.fw.sql;

import java.beans.IndexedPropertyDescriptor;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;

public class SQLDriverBeanInfo extends SimpleBeanInfo
{
	private interface IPropertyNames extends ISQLDriver.IPropertyNames
	{
		
	}

	private final static Class<?> CLAZZ = SQLDriver.class;
	
	private static PropertyDescriptor[] s_descr;

	public SQLDriverBeanInfo() throws IntrospectionException
	{
		super();
		if (s_descr == null)
		{
			s_descr = new PropertyDescriptor[7];
			s_descr[0] = new PropertyDescriptor(IPropertyNames.NAME, CLAZZ, "getName", "setName");
			s_descr[1] = new PropertyDescriptor(IPropertyNames.DRIVER_CLASS, CLAZZ, "getDriverClassName", "setDriverClassName");
			s_descr[2] = new PropertyDescriptor(IPropertyNames.ID, CLAZZ, "getIdentifier", "setIdentifier");
			s_descr[3] = new PropertyDescriptor(IPropertyNames.URL, CLAZZ, "getUrl", "setUrl");
			s_descr[4] = new PropertyDescriptor(IPropertyNames.JARFILE_NAME, CLAZZ, "getJarFileName", "setJarFileName");
			s_descr[5] = new IndexedPropertyDescriptor(IPropertyNames.JARFILE_NAMES, CLAZZ,
								"getJarFileNameWrappers", "setJarFileNameWrappers",
								"getJarFileNameWrapper", "setJarFileNameWrapper");
            s_descr[6] = new PropertyDescriptor(IPropertyNames.WEBSITE_URL, CLAZZ, "getWebSiteUrl", "setWebSiteUrl");
		}
	}

	public PropertyDescriptor[] getPropertyDescriptors()
	{
		return s_descr;
	}
}
