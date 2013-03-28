package net.sourceforge.squirrel_sql.client.db;


import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;


public class AliasGroupBeanInfo extends SimpleBeanInfo
{
	private static interface IPropertyNames extends AliasGroup.IPropertyNames
	{
		
	}

	
	@Override
	public PropertyDescriptor[] getPropertyDescriptors()
	{
		try
		{
			PropertyDescriptor[] result = new PropertyDescriptor[2];
			result[0] =
				new PropertyDescriptor(IPropertyNames.ID, AliasGroup.class, "getIdentifier", "setIdentifier");
			result[0] = new PropertyDescriptor(IPropertyNames.NAME, AliasGroup.class, "getName", "setName");
			return result;
		}
		catch (IntrospectionException e)
		{
			throw new Error(e);
		}

	}
}
