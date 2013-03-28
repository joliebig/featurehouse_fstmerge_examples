package net.sourceforge.squirrel_sql.client.plugin;

import java.io.File;
import java.io.IOException;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.gui.db.aliasproperties.IAliasPropertiesPanelController;
import net.sourceforge.squirrel_sql.client.gui.db.SQLAlias;
import net.sourceforge.squirrel_sql.client.preferences.IGlobalPreferencesPanel;
import net.sourceforge.squirrel_sql.client.preferences.INewSessionPropertiesPanel;

public interface IPlugin
{
	
	void load(IApplication app) throws PluginException;

	
	void initialize() throws PluginException;

	
	void unload(); 

	
	String getInternalName();

	
	String getDescriptiveName();

	
	String getAuthor();

	
	String getContributors();

	
	String getWebSite();

	
	String getVersion();

	
	String getHelpFileName();

	
	String getChangeLogFileName();

	
	String getLicenceFileName();

	
	IGlobalPreferencesPanel[] getGlobalPreferencePanels();


   
   IAliasPropertiesPanelController[] getAliasPropertiesPanelControllers(SQLAlias alias);

   
   void aliasCopied(SQLAlias source, SQLAlias target);

   
   void aliasRemoved(SQLAlias alias);



   
	INewSessionPropertiesPanel[] getNewSessionPropertiesPanels();

	
	File getPluginAppSettingsFolder() throws IOException, IllegalStateException;

	
	File getPluginUserSettingsFolder() throws IllegalStateException, IOException;

   
   Object getExternalService();

}
