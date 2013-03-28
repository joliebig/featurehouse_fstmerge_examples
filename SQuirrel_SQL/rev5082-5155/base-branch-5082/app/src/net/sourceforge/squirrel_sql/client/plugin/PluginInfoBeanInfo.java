package net.sourceforge.squirrel_sql.client.plugin;


import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;


public final class PluginInfoBeanInfo extends SimpleBeanInfo
{

	private interface IPropNames extends PluginInfo.IPropertyNames
	{
		
	}

	
	@Override	
	public PropertyDescriptor[] getPropertyDescriptors()
	{
		try
		{
			PropertyDescriptor[] s_descr = new PropertyDescriptor[8];

			s_descr[0] =
				new PropertyDescriptor(IPropNames.PLUGIN_CLASS_NAME, PluginInfo.class, "getPluginClassName", null);
			s_descr[1] = new PropertyDescriptor(IPropNames.IS_LOADED, PluginInfo.class, "isLoaded", null);
			s_descr[2] =
				new PropertyDescriptor(IPropNames.INTERNAL_NAME, PluginInfo.class, "getInternalName", null);
			s_descr[3] =
				new PropertyDescriptor(IPropNames.DESCRIPTIVE_NAME, PluginInfo.class, "getDescriptiveName", null);
			s_descr[4] = new PropertyDescriptor(IPropNames.AUTHOR, PluginInfo.class, "getAuthor", null);
			s_descr[5] =
				new PropertyDescriptor(IPropNames.CONTRIBUTORS, PluginInfo.class, "getContributors", null);
			s_descr[6] = new PropertyDescriptor(IPropNames.WEB_SITE, PluginInfo.class, "getWebSite", null);
			s_descr[7] = new PropertyDescriptor(IPropNames.VERSION, PluginInfo.class, "getVersion", null);

			return s_descr;
		}
		catch (IntrospectionException e)
		{
			throw new Error(e);
		}
	}
}
