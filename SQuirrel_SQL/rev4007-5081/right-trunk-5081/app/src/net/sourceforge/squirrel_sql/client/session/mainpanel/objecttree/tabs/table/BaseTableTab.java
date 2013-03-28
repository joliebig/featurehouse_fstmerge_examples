package net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.table;

import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;

import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.BaseDataSetTab;

public abstract class BaseTableTab
		extends BaseDataSetTab
		implements ITableTab
{
	
	public void setTableInfo(ITableInfo value)
	{
		setDatabaseObjectInfo(value);
	}

	
	public final ITableInfo getTableInfo()
	{
		return (ITableInfo)getDatabaseObjectInfo();
	}
}
