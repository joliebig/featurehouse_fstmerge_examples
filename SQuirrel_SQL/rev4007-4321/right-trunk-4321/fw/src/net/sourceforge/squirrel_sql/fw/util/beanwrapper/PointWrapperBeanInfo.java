package net.sourceforge.squirrel_sql.fw.util.beanwrapper;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;

public class PointWrapperBeanInfo extends SimpleBeanInfo
{
	private static PropertyDescriptor[] s_descriptors;

	public PointWrapperBeanInfo() throws IntrospectionException
	{
		super();
		if (s_descriptors == null)
		{
			s_descriptors = new PropertyDescriptor[2];
			s_descriptors[0] =
				new PropertyDescriptor(
					PointWrapper.IPropertyNames.X,
					PointWrapper.class,
					"getX",
					"setX");
			s_descriptors[1] =
				new PropertyDescriptor(
					PointWrapper.IPropertyNames.Y,
					PointWrapper.class,
					"getY",
					"setY");
		}
	}

	public PropertyDescriptor[] getPropertyDescriptors()
	{
		return s_descriptors;
	}
}
