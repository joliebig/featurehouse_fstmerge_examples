package net.sourceforge.squirrel_sql.client.db;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;

public class AliasGroupBeanInfo extends SimpleBeanInfo
{
	private static final Class<AliasGroup> CLAZZ = AliasGroup.class;

	private static interface IPropertyNames extends AliasGroup.IPropertyNames
	{
		
	}
	
	private static PropertyDescriptor[] s_descr;

	public AliasGroupBeanInfo() throws IntrospectionException
	{
		super();
		if (s_descr == null)
		{
			s_descr = new PropertyDescriptor[2];
			s_descr[0] = new PropertyDescriptor(IPropertyNames.ID, CLAZZ, "getIdentifier", "setIdentifier");
			s_descr[0] = new PropertyDescriptor(IPropertyNames.NAME, CLAZZ, "getName", "setName");
		}
	}

	public PropertyDescriptor[] getPropertyDescriptors()
	{
		return s_descr;
	}
}

