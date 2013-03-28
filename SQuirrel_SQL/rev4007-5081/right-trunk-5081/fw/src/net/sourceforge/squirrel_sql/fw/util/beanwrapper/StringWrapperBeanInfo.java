package net.sourceforge.squirrel_sql.fw.util.beanwrapper;


import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;


public class StringWrapperBeanInfo extends SimpleBeanInfo
{

	
	@Override
	public PropertyDescriptor[] getPropertyDescriptors()
	{
		try
		{
			PropertyDescriptor[] result = new PropertyDescriptor[1];
			result[0] =
				new PropertyDescriptor(StringWrapper.IPropertyNames.STRINGS, StringWrapper.class, "getString",
					"setString");

			return result;
		}
		catch (IntrospectionException e)
		{
			throw new Error(e);
		}

	}
}
