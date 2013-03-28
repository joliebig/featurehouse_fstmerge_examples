package net.sourceforge.squirrel_sql.fw.util.beanwrapper;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;

public class RectangleWrapperBeanInfo extends SimpleBeanInfo
{
	private static PropertyDescriptor[] s_descriptors;

	public RectangleWrapperBeanInfo() throws IntrospectionException
	{
		super();
		if (s_descriptors == null)
		{
			s_descriptors = new PropertyDescriptor[4];
			s_descriptors[0] =
				new PropertyDescriptor(
					RectangleWrapper.IPropertyNames.WIDTH,
					RectangleWrapper.class,
					"getWidth",
					"setWidth");
			s_descriptors[1] =
				new PropertyDescriptor(
					RectangleWrapper.IPropertyNames.HEIGHT,
					RectangleWrapper.class,
					"getHeight",
					"setHeight");
			s_descriptors[2] =
				new PropertyDescriptor(
					RectangleWrapper.IPropertyNames.X,
					RectangleWrapper.class,
					"getX",
					"setX");
			s_descriptors[3] =
				new PropertyDescriptor(
					RectangleWrapper.IPropertyNames.Y,
					RectangleWrapper.class,
					"getY",
					"setY");
		}
	}

	public PropertyDescriptor[] getPropertyDescriptors()
	{
		return s_descriptors;
	}
}
