package net.sourceforge.squirrel_sql.fw.util.beanwrapper;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;

public class PointWrapperBeanInfo extends SimpleBeanInfo
{
	
	
	@Override
	public PropertyDescriptor[] getPropertyDescriptors()
	{
		try {
			PropertyDescriptor[] result = new PropertyDescriptor[2];
			result[0] =
				new PropertyDescriptor(
					PointWrapper.IPropertyNames.X,
					PointWrapper.class,
					"getX",
					"setX");
			result[1] =
				new PropertyDescriptor(
					PointWrapper.IPropertyNames.Y,
					PointWrapper.class,
					"getY",
					"setY");
			return result;
		} catch (IntrospectionException e) {
			throw new Error(e);
		}
	}
}
