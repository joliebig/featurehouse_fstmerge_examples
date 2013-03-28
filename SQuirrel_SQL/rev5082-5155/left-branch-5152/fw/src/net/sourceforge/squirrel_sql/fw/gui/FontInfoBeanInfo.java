package net.sourceforge.squirrel_sql.fw.gui;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;


public final class FontInfoBeanInfo extends SimpleBeanInfo
{

	
	@Override
	public PropertyDescriptor[] getPropertyDescriptors()
	{
		try
		{
			PropertyDescriptor[] result = new PropertyDescriptor[4];
			result[0] =
				new PropertyDescriptor(
					FontInfo.IPropertyNames.FAMILY,
					FontInfo.class,
					"getFamily",
					"setFamily");
			result[1] =
				new PropertyDescriptor(
					FontInfo.IPropertyNames.IS_BOLD,
					FontInfo.class,
					"isBold",
					"setIsBold");
			result[2] =
				new PropertyDescriptor(
					FontInfo.IPropertyNames.IS_ITALIC,
					FontInfo.class,
					"isItalic",
					"setIsItalic");
			result[3] =
				new PropertyDescriptor(
					FontInfo.IPropertyNames.SIZE,
					FontInfo.class,
					"getSize",
					"setSize");
			
			return result;
		}
		catch (IntrospectionException e)
		{
			throw new Error(e);
		}
	}
}
