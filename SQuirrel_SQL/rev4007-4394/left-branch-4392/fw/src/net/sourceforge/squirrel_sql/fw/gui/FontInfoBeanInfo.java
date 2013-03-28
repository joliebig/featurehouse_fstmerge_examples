package net.sourceforge.squirrel_sql.fw.gui;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;


public final class FontInfoBeanInfo extends SimpleBeanInfo
{
	private static PropertyDescriptor[] s_descriptors;

	public FontInfoBeanInfo() throws IntrospectionException
	{
		super();
		if (s_descriptors == null)
		{
			s_descriptors = new PropertyDescriptor[4];
			s_descriptors[0] =
				new PropertyDescriptor(
					FontInfo.IPropertyNames.FAMILY,
					FontInfo.class,
					"getFamily",
					"setFamily");
			s_descriptors[1] =
				new PropertyDescriptor(
					FontInfo.IPropertyNames.IS_BOLD,
					FontInfo.class,
					"isBold",
					"setIsBold");
			s_descriptors[2] =
				new PropertyDescriptor(
					FontInfo.IPropertyNames.IS_ITALIC,
					FontInfo.class,
					"isItalic",
					"setIsItalic");
			s_descriptors[3] =
				new PropertyDescriptor(
					FontInfo.IPropertyNames.SIZE,
					FontInfo.class,
					"getSize",
					"setSize");
		}
	}

	public PropertyDescriptor[] getPropertyDescriptors()
	{
		return s_descriptors;
	}
}
