package net.sourceforge.squirrel_sql.client.gui.db;



import net.sourceforge.squirrel_sql.fw.sql.ISQLAlias;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;


public class SQLAliasBeanInfo extends SimpleBeanInfo
{

	private interface IPropNames extends ISQLAlias.IPropertyNames
	{
		
	}

	
	@Override
	public PropertyDescriptor[] getPropertyDescriptors()
	{
		try
		{
			PropertyDescriptor[] result =
				new PropertyDescriptor[] {
						new PropertyDescriptor(IPropNames.ID, SQLAlias.class, "getIdentifier", "setIdentifier"),
						new PropertyDescriptor(IPropNames.NAME, SQLAlias.class, "getName", "setName"),
						new PropertyDescriptor(IPropNames.URL, SQLAlias.class, "getUrl", "setUrl"),
						new PropertyDescriptor(IPropNames.USER_NAME, SQLAlias.class, "getUserName", "setUserName"),
						new PropertyDescriptor(IPropNames.DRIVER, SQLAlias.class, "getDriverIdentifier",
							"setDriverIdentifier"),
						new PropertyDescriptor(IPropNames.USE_DRIVER_PROPERTIES, SQLAlias.class,
							"getUseDriverProperties", "setUseDriverProperties"),
						new PropertyDescriptor(IPropNames.DRIVER_PROPERTIES, SQLAlias.class,
							"getDriverPropertiesClone", "setDriverProperties"),
						new PropertyDescriptor(IPropNames.PASSWORD, SQLAlias.class, "getPassword", "setPassword"),
						new PropertyDescriptor(IPropNames.AUTO_LOGON, SQLAlias.class, "isAutoLogon", "setAutoLogon"),
						new PropertyDescriptor(IPropNames.CONNECT_AT_STARTUP, SQLAlias.class, "isConnectAtStartup",
							"setConnectAtStartup"),
						new PropertyDescriptor(IPropNames.SCHEMA_PROPERTIES, SQLAlias.class, "getSchemaProperties",
							"setSchemaProperties") };
			return result;
		}
		catch (IntrospectionException e)
		{
			throw new Error(e);
		}
	}
}
