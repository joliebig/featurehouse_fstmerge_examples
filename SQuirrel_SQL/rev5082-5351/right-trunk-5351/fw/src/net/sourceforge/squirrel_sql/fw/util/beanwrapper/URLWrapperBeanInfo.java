package net.sourceforge.squirrel_sql.fw.util.beanwrapper;


import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;


public class URLWrapperBeanInfo extends SimpleBeanInfo
{

	
	@Override
	public PropertyDescriptor[] getPropertyDescriptors()
	{
		try
		{
			PropertyDescriptor[] s_descriptors = new PropertyDescriptor[1];
			s_descriptors[0] =
				new PropertyDescriptor(URLWrapper.IURLWrapperPropertyNames.URL, URLWrapper.class,
					"getExternalForm", "setExternalForm");
			return s_descriptors;
		}
		catch (IntrospectionException e)
		{
			throw new Error(e);
		}

	}
}
