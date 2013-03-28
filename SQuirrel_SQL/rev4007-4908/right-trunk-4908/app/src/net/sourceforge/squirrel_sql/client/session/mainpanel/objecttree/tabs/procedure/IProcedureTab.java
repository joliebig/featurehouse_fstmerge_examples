package net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.procedure;

import net.sourceforge.squirrel_sql.fw.sql.IProcedureInfo;

import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.IObjectTab;

public interface IProcedureTab extends IObjectTab
{
	
	void setProcedureInfo(IProcedureInfo value);
}

