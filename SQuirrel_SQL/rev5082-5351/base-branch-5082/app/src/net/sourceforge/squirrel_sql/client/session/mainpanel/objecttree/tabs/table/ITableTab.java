package net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.table;

import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;

import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.IObjectTab;

public interface ITableTab extends IObjectTab
{
	
	void setTableInfo(ITableInfo value);
}