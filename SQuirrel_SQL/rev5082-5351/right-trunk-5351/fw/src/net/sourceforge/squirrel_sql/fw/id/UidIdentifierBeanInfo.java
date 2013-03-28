package net.sourceforge.squirrel_sql.fw.id;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;

public class UidIdentifierBeanInfo extends SimpleBeanInfo
{

	
	@Override		
	public PropertyDescriptor[] getPropertyDescriptors()
	{
		try
		{
			PropertyDescriptor[] result = new PropertyDescriptor[1];
			result[0] =
				new PropertyDescriptor(
					UidIdentifier.IPropertyNames.STRING,
					UidIdentifier.class,
					"toString",
					"setString");
			
			return result;
		}
		catch (IntrospectionException e)
		{
			throw new Error(e);
		}
	}
}
