package net.sourceforge.squirrel_sql.client.gui.mainframe;


import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;

public class MainFrameWindowStateBeanInfo extends SimpleBeanInfo
{
	public BeanInfo[] getAdditionalBeanInfo()
	{
		try
		{
			BeanInfo superBeanInfo = Introspector.getBeanInfo(MainFrameWindowState.class.getSuperclass());
			return new BeanInfo[] { superBeanInfo };
		}
		catch (IntrospectionException ex)
		{
			return new BeanInfo[0];
		}
	}

	
	@Override
	public PropertyDescriptor[] getPropertyDescriptors()
	{
		try
		{
			PropertyDescriptor[] result = new PropertyDescriptor[2];
			result[0] =
				new PropertyDescriptor(MainFrameWindowState.IPropertyNames.ALIASES_WINDOW_STATE,
					MainFrameWindowState.class, "getAliasesWindowState", "setAliasesWindowState");
			result[1] =
				new PropertyDescriptor(MainFrameWindowState.IPropertyNames.DRIVERS_WINDOW_STATE,
					MainFrameWindowState.class, "getDriversWindowState", "setDriversWindowState");
			return result;
		}
		catch (IntrospectionException e)
		{
			throw new Error(e);
		}
	}
}
