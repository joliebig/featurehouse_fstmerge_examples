
package net.sourceforge.squirrel_sql.plugins.mssql.gui;

import java.io.File;
import java.io.IOException;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.gui.db.SQLAlias;
import net.sourceforge.squirrel_sql.client.gui.db.aliasproperties.IAliasPropertiesPanelController;
import net.sourceforge.squirrel_sql.client.plugin.IPlugin;
import net.sourceforge.squirrel_sql.client.plugin.PluginException;
import net.sourceforge.squirrel_sql.client.preferences.IGlobalPreferencesPanel;
import net.sourceforge.squirrel_sql.client.preferences.INewSessionPropertiesPanel;
import net.sourceforge.squirrel_sql.fw.util.IJavaPropertyNames;

public class DummyPlugin implements IPlugin {

    public String getChangeLogFileName() { return null; }
    public String getContributors() { return null; }

    
    public Object getExternalService() {
        
        return null;
    }

    
    public IGlobalPreferencesPanel[] getGlobalPreferencePanels() {
        
        return null;
    }

    
    public String getHelpFileName() {
        
        return null;
    }

    
    public String getLicenceFileName() {
        
        return null;
    }

    
    public INewSessionPropertiesPanel[] getNewSessionPropertiesPanels() {
        
        return null;
    }

    
    public File getPluginAppSettingsFolder() throws IOException, IllegalStateException {
        
        String filename = System.getProperty(IJavaPropertyNames.USER_HOME)
                          + File.separator + ".squirrel-sql" + File.separator + "plugins"
                          + File.separator + "mssql" + File.separator;
        System.out.println("filename="+filename);
        return new File(filename); 
    }

    
    public File getPluginUserSettingsFolder() throws IllegalStateException, IOException {
        
        return null;
    }

    
    public String getWebSite() {
        
        return null;
    }

    
    public void initialize() throws PluginException {
        
        
    }

    
    public void load(IApplication app) throws PluginException {
        
        
    }

    
    public void unload() {
        
        
    }

    
    public String getInternalName() {
        return "dbcopy";
    }

    
    public String getDescriptiveName() {
        return "DBCopy Plugin";
    }

    
    public String getAuthor() {
        return "Rob Manning";
    }

    
    public String getVersion() {
        return "0.13";
    }
	public void aliasCopied(SQLAlias source, SQLAlias target) {
		
		
	}
	public void aliasRemoved(SQLAlias alias) {
		
		
	}
	public IAliasPropertiesPanelController[] getAliasPropertiesPanelControllers(SQLAlias alias) {
		
		return null;
	}
    
}
