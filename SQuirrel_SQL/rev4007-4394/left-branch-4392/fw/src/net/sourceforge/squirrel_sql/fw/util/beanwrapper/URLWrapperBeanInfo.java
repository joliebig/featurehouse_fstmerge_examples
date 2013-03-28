package net.sourceforge.squirrel_sql.fw.util.beanwrapper;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;

public class URLWrapperBeanInfo
{
	private static PropertyDescriptor[] s_descriptors;
	private final static Class<URLWrapper> s_cls = URLWrapper.class;

	public URLWrapperBeanInfo() throws IntrospectionException
	{
		super();
		if (s_descriptors == null)
		{
			s_descriptors = new PropertyDescriptor[1];
			s_descriptors[0] =
				new PropertyDescriptor(
					URLWrapper.IURLWrapperPropertyNames.URL,
					s_cls,
					"getExternalForm",
					"setExternalForm");
		}
	}

	public PropertyDescriptor[] getPropertyDescriptors()
	{
		return s_descriptors;
	}
}
