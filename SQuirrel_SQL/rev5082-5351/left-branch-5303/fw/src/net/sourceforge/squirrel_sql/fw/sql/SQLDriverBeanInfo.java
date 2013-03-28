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

	
	@Override
	public PropertyDescriptor[] getPropertyDescriptors()
	{
		try
		{
			PropertyDescriptor[] result = new PropertyDescriptor[7];
			result[0] = new PropertyDescriptor(IPropertyNames.NAME, SQLDriver.class, "getName", "setName");
			result[1] =
				new PropertyDescriptor(IPropertyNames.DRIVER_CLASS, SQLDriver.class, "getDriverClassName",
					"setDriverClassName");
			result[2] =
				new PropertyDescriptor(IPropertyNames.ID, SQLDriver.class, "getIdentifier", "setIdentifier");
			result[3] = new PropertyDescriptor(IPropertyNames.URL, SQLDriver.class, "getUrl", "setUrl");
			result[4] =
				new PropertyDescriptor(IPropertyNames.JARFILE_NAME, SQLDriver.class, "getJarFileName",
					"setJarFileName");
			result[5] =
				new IndexedPropertyDescriptor(IPropertyNames.JARFILE_NAMES, SQLDriver.class,
					"getJarFileNameWrappers", "setJarFileNameWrappers", "getJarFileNameWrapper",
					"setJarFileNameWrapper");
			result[6] =
				new PropertyDescriptor(IPropertyNames.WEBSITE_URL, SQLDriver.class, "getWebSiteUrl",
					"setWebSiteUrl");
			return result;
		}
		catch (IntrospectionException e)
		{
			throw new Error(e);
		}

	}
}
