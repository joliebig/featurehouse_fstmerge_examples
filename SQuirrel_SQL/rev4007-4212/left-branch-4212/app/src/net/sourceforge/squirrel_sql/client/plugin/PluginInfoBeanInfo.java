package net.sourceforge.squirrel_sql.client.plugin;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;

public final class PluginInfoBeanInfo extends SimpleBeanInfo
{
	private static PropertyDescriptor[] s_descr;

	private interface IPropNames extends PluginInfo.IPropertyNames
	{
		
	}

	public PluginInfoBeanInfo() throws IntrospectionException
	{
		super();
		if (s_descr == null)
		{
			final Class<PluginInfo> CLAZZ = PluginInfo.class;
			s_descr = new PropertyDescriptor[8];

			s_descr[0] = new PropertyDescriptor(IPropNames.PLUGIN_CLASS_NAME, CLAZZ,
												"getPluginClassName", null);
			s_descr[1] = new PropertyDescriptor(IPropNames.IS_LOADED, CLAZZ,
												"isLoaded", null);
			s_descr[2] = new PropertyDescriptor(IPropNames.INTERNAL_NAME, CLAZZ,
												"getInternalName", null);
			s_descr[3] = new PropertyDescriptor(IPropNames.DESCRIPTIVE_NAME, CLAZZ,
												"getDescriptiveName", null);
			s_descr[4] = new PropertyDescriptor(IPropNames.AUTHOR, CLAZZ,
												"getAuthor", null);
			s_descr[5] = new PropertyDescriptor(IPropNames.CONTRIBUTORS, CLAZZ,
												"getContributors", null);
			s_descr[6] = new PropertyDescriptor(IPropNames.WEB_SITE, CLAZZ,
												"getWebSite", null);
			s_descr[7] = new PropertyDescriptor(IPropNames.VERSION, CLAZZ,
												"getVersion", null);
		}
	}

	public PropertyDescriptor[] getPropertyDescriptors()
	{
		return s_descr;
	}
}
