package net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs;

import java.awt.Component;

import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;

import net.sourceforge.squirrel_sql.client.session.ISession;

public interface IObjectTab
{
	
	String getTitle();

	
	String getHint();

	
	Component getComponent();

	
	void setSession(ISession session);

	
	void setDatabaseObjectInfo(IDatabaseObjectInfo value);

	
	void select();

	
	void clear();

	
	void rebuild();
}
