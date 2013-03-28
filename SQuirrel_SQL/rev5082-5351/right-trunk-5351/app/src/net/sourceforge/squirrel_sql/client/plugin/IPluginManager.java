
package net.sourceforge.squirrel_sql.client.plugin;

import java.net.URL;
import java.util.Iterator;

import net.sourceforge.squirrel_sql.client.gui.db.SQLAlias;
import net.sourceforge.squirrel_sql.client.gui.db.aliasproperties.IAliasPropertiesPanelController;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.util.ClassLoaderListener;

public interface IPluginManager
{

	
	public abstract void sessionCreated(ISession session);

	
	public abstract void sessionStarted(final ISession session);

	
	public abstract void sessionEnding(ISession session);

	public abstract void unloadPlugin(String pluginInternalName);

	
	public abstract void unloadPlugins();

	public abstract PluginInfo[] getPluginInformation();

	public abstract SessionPluginInfo[] getPluginInformation(ISession session);

	public abstract IPluginDatabaseObjectType[] getDatabaseObjectTypes(ISession session);

	
	public abstract URL[] getPluginURLs();

	public abstract PluginStatus[] getPluginStatuses();

	public abstract void setPluginStatuses(PluginStatus[] values);

	
	public abstract Iterator<SessionPluginInfo> getSessionPluginIterator();

	
	public abstract void loadPlugins();

	
	public abstract void initializePlugins();

	
	public abstract void setClassLoaderListener(ClassLoaderListener listener);

	
	public abstract Iterator<PluginLoadInfo> getPluginLoadInfoIterator();

	
	public abstract Object bindExternalPluginService(String internalNameOfPlugin, Class<?> toBindTo);

	public abstract IAliasPropertiesPanelController[] getAliasPropertiesPanelControllers(SQLAlias alias);

	public abstract void aliasCopied(SQLAlias source, SQLAlias target);

	public abstract void aliasRemoved(SQLAlias alias);

}