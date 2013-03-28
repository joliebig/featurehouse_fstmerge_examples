package net.sourceforge.squirrel_sql.fw.util.beanwrapper;


import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;


public class RectangleWrapperBeanInfo extends SimpleBeanInfo
{

	
	@Override
	public PropertyDescriptor[] getPropertyDescriptors()
	{
		try
		{
			PropertyDescriptor[] result = new PropertyDescriptor[4];
			result[0] =
				new PropertyDescriptor(RectangleWrapper.IPropertyNames.WIDTH, RectangleWrapper.class, "getWidth",
					"setWidth");
			result[1] =
				new PropertyDescriptor(RectangleWrapper.IPropertyNames.HEIGHT, RectangleWrapper.class,
					"getHeight", "setHeight");
			result[2] =
				new PropertyDescriptor(RectangleWrapper.IPropertyNames.X, RectangleWrapper.class, "getX", "setX");
			result[3] =
				new PropertyDescriptor(RectangleWrapper.IPropertyNames.Y, RectangleWrapper.class, "getY", "setY");
			return result;
		}
		catch (IntrospectionException e)
		{
			throw new Error(e);
		}
	}
}
