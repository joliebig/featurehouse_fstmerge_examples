package net.sourceforge.squirrel_sql.client.plugin;

import java.sql.SQLException;
import java.sql.Statement;

import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;

import net.sourceforge.squirrel_sql.client.session.ISession;


public interface IPluginDatabaseObjectType {
	
	String getName();

	
	IPluginDatabaseObjectPanelWrapper createPanel();

	
	public IPluginDatabaseObject[] getObjects(ISession session, ISQLConnection conn, Statement stmt)
			throws SQLException;
}