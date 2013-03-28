package net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.procedure;

import net.sourceforge.squirrel_sql.fw.sql.IProcedureInfo;

import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.BaseDataSetTab;

public abstract class BaseProcedureTab extends BaseDataSetTab
											implements IProcedureTab
{
	
	public void setProcedureInfo(IProcedureInfo value)
	{
		setDatabaseObjectInfo(value);
	}

	
	public final IProcedureInfo getProcedureInfo()
	{
		return (IProcedureInfo) getDatabaseObjectInfo();
	}

}
