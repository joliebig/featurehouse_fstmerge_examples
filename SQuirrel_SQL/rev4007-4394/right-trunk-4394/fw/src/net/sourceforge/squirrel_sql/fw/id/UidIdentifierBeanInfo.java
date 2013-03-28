package net.sourceforge.squirrel_sql.fw.id;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;

public class UidIdentifierBeanInfo extends SimpleBeanInfo
{
	private static PropertyDescriptor[] s_dscrs;

	public UidIdentifierBeanInfo() throws IntrospectionException
	{
		super();
		if (s_dscrs == null)
		{
			s_dscrs = new PropertyDescriptor[1];
			s_dscrs[0] =
				new PropertyDescriptor(
					UidIdentifier.IPropertyNames.STRING,
					UidIdentifier.class,
					"toString",
					"setString");
		}
	}

	public PropertyDescriptor[] getPropertyDescriptors()
	{
		return s_dscrs;
	}
}
