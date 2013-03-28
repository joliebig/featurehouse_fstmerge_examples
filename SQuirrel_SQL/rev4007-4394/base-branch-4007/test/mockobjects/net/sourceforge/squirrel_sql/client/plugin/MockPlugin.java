
package net.sourceforge.squirrel_sql.client.plugin;

import java.io.File;
import java.io.IOException;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.gui.db.SQLAlias;
import net.sourceforge.squirrel_sql.client.gui.db.aliasproperties.IAliasPropertiesPanelController;
import net.sourceforge.squirrel_sql.client.plugin.IPlugin;
import net.sourceforge.squirrel_sql.client.plugin.PluginException;
import net.sourceforge.squirrel_sql.client.preferences.IGlobalPreferencesPanel;
import net.sourceforge.squirrel_sql.client.preferences.INewSessionPropertiesPanel;

public class MockPlugin implements IPlugin {

    private File pluginAppSettingsFolder = null;
    
    public MockPlugin(String settingsFolder) {
        pluginAppSettingsFolder = new File(settingsFolder);
    }
    
    public void load(IApplication app) throws PluginException {
        

    }

    public void initialize() throws PluginException {
        

    }

    public void unload() {
        

    }

    public String getInternalName() {
        
        return null;
    }

    public String getDescriptiveName() {
        
        return null;
    }

    public String getAuthor() {
        
        return null;
    }

    public String getContributors() {
        
        return null;
    }

    public String getWebSite() {
        
        return null;
    }

    public String getVersion() {
        
        return null;
    }

    public String getHelpFileName() {
        
        return null;
    }

    public String getChangeLogFileName() {
        
        return null;
    }

    public String getLicenceFileName() {
        
        return null;
    }

    public IGlobalPreferencesPanel[] getGlobalPreferencePanels() {
        
        return null;
    }

    public INewSessionPropertiesPanel[] getNewSessionPropertiesPanels() {
        
        return null;
    }

    public File getPluginAppSettingsFolder() 
        throws IOException, IllegalStateException 
    {
        return pluginAppSettingsFolder;
    }

    public File getPluginUserSettingsFolder() throws IllegalStateException,
            IOException {
        return pluginAppSettingsFolder;
    }

    public Object getExternalService() {
        
        return null;
    }

	public void aliasCopied(SQLAlias source, SQLAlias target) {
		
		
	}

	public void aliasRemoved(SQLAlias alias) {
		
		
	}

	public IAliasPropertiesPanelController[] getAliasPropertiesPanelControllers(SQLAlias alias) {
		
		return null;
	}

}
