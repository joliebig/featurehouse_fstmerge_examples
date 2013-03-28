package net.sourceforge.squirrel_sql.fw.util.beanwrapper;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;

public class DimensionWrapperBeanInfo extends SimpleBeanInfo
{

	private static PropertyDescriptor[] s_descriptors;

	public DimensionWrapperBeanInfo() throws IntrospectionException
	{
		super();
		if (s_descriptors == null)
		{
			s_descriptors = new PropertyDescriptor[2];
			s_descriptors[0] =
				new PropertyDescriptor(
					DimensionWrapper.IPropertyNames.WIDTH,
					DimensionWrapper.class,
					"getWidth",
					"setWidth");
			s_descriptors[1] =
				new PropertyDescriptor(
					DimensionWrapper.IPropertyNames.HEIGHT,
					DimensionWrapper.class,
					"getHeight",
					"setHeight");
		}
	}

	public PropertyDescriptor[] getPropertyDescriptors()
	{
		return s_descriptors;
	}
}
