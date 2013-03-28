package org.firebirdsql.squirrel.util;


import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;


public class IndexInfoBeanInfo extends SimpleBeanInfo
{
	
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(IndexInfoBeanInfo.class);

	private static interface IPropertyNames extends IndexInfo.IPropertyNames
	{
		
	}

	
	@Override
	public PropertyDescriptor[] getPropertyDescriptors()
	{
		try
		{
			PropertyDescriptor[] result = new PropertyDescriptor[10];
			result[0] = new PropertyDescriptor(IPropertyNames.NAME, IndexInfo.class);
			result[0].setDisplayName(s_stringMgr.getString("IndexInfoBeanInfo.name"));
			result[1] = new PropertyDescriptor(IPropertyNames.DESCRIPTION, IndexInfo.class);
			result[1].setDisplayName(s_stringMgr.getString("IndexInfoBeanInfo.description"));
			result[2] = new PropertyDescriptor(IPropertyNames.RELATION_NAME, IndexInfo.class);
			result[2].setDisplayName(s_stringMgr.getString("IndexInfoBeanInfo.relationname"));
			result[3] = new PropertyDescriptor(IPropertyNames.ID, IndexInfo.class);
			result[3].setDisplayName(s_stringMgr.getString("IndexInfoBeanInfo.id"));
			result[4] = new PropertyDescriptor(IPropertyNames.UNIQUE, IndexInfo.class);
			result[4].setDisplayName(s_stringMgr.getString("IndexInfoBeanInfo.unique"));
			result[5] = new PropertyDescriptor(IPropertyNames.SEGMENT_COUNT, IndexInfo.class);
			result[5].setDisplayName(s_stringMgr.getString("IndexInfoBeanInfo.segmentcount"));
			result[6] = new PropertyDescriptor(IPropertyNames.ACTIVE, IndexInfo.class);
			result[6].setDisplayName(s_stringMgr.getString("IndexInfoBeanInfo.active"));
			result[7] = new PropertyDescriptor(IPropertyNames.EXPRESSION_SOURCE, IndexInfo.class);
			result[7].setDisplayName(s_stringMgr.getString("IndexInfoBeanInfo.expressionsource"));
			result[8] = new PropertyDescriptor(IPropertyNames.FOREIGN_KEY_CONSTRAINT, IndexInfo.class);
			result[8].setDisplayName(s_stringMgr.getString("IndexInfoBeanInfo.foreignkeyconstraint"));
			result[9] = new PropertyDescriptor(IPropertyNames.SYSTEM_DEFINED, IndexInfo.class);
			result[9].setDisplayName(s_stringMgr.getString("IndexInfoBeanInfo.systemdefined"));

			return result;
		}
		catch (IntrospectionException e)
		{
			throw new Error(e);
		}
	}
}
