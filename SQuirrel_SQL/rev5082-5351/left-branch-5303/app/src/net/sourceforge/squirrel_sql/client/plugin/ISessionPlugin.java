package net.sourceforge.squirrel_sql.client.plugin;

import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.INodeExpander;
import net.sourceforge.squirrel_sql.client.session.properties.ISessionPropertiesPanel;

public interface ISessionPlugin extends IPlugin
{
	
	void sessionCreated(ISession session);

   
   boolean allowsSessionStartedInBackground();

   
	PluginSessionCallback sessionStarted(ISession session);

	
	void sessionEnding(ISession session);

	
	ISessionPropertiesPanel[] getSessionPropertiesPanels(ISession session);

	
	

	
	IPluginDatabaseObjectType[] getObjectTypes(ISession session);

	
	INodeExpander getDefaultNodeExpander(ISession session, DatabaseObjectType type);
}
