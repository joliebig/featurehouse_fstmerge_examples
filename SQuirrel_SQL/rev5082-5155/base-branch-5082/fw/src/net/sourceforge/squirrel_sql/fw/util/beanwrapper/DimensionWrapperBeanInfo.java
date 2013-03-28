package net.sourceforge.squirrel_sql.fw.util.beanwrapper;


import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;


public class DimensionWrapperBeanInfo extends SimpleBeanInfo
{

	
	@Override
	public PropertyDescriptor[] getPropertyDescriptors()
	{
		try
		{
			PropertyDescriptor[] result = new PropertyDescriptor[2];
			result[0] =
				new PropertyDescriptor(DimensionWrapper.IPropertyNames.WIDTH, DimensionWrapper.class, "getWidth",
					"setWidth");
			result[1] =
				new PropertyDescriptor(DimensionWrapper.IPropertyNames.HEIGHT, DimensionWrapper.class,
					"getHeight", "setHeight");
			return result;
		}
		catch (IntrospectionException e)
		{
			throw new Error(e);
		}
	}
}
