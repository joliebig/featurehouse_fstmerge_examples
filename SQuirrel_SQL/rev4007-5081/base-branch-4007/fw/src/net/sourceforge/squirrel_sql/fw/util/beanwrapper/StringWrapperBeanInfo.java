package net.sourceforge.squirrel_sql.fw.util.beanwrapper;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;

public class StringWrapperBeanInfo extends SimpleBeanInfo
{

	private static PropertyDescriptor[] s_descriptors;

	public StringWrapperBeanInfo() throws IntrospectionException
	{
		super();
		if (s_descriptors == null)
		{
			s_descriptors = new PropertyDescriptor[1];
			s_descriptors[0] =
				new PropertyDescriptor(
					StringWrapper.IPropertyNames.STRINGS,
					StringWrapper.class,
					"getString",
					"setString");
		}
	}

	public PropertyDescriptor[] getPropertyDescriptors()
	{
		return s_descriptors;
	}
}
