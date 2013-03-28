
package net.sourceforge.squirrel_sql.plugins.firebirdmanager.gui;

import java.awt.Component;

import javax.swing.JScrollPane;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.preferences.IGlobalPreferencesPanel;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.plugins.firebirdmanager.pref.PreferencesManager;

public class FirebirdManagerGlobalPreferencesTab implements IGlobalPreferencesPanel {

    private PreferencesPanel firebirdManagerPreferences = null;
    private JScrollPane scrollPanePreferences;
    
    private static final StringManager stringManager =
        StringManagerFactory.getStringManager(FirebirdManagerGlobalPreferencesTab.class);    
    
    public FirebirdManagerGlobalPreferencesTab() {
        firebirdManagerPreferences = new PreferencesPanel(PreferencesManager.getGlobalPreferences());
        scrollPanePreferences = new JScrollPane(firebirdManagerPreferences);  
        scrollPanePreferences.getVerticalScrollBar().setUnitIncrement(10);
    }

	
    private interface i18n {
    	String GLOBAL_PREFERENCES_TAB_TITLE = stringManager.getString("global.preferences.tab.title");
    	String GLOBAL_PREFERENCES_TAB_TOOLTIP = stringManager.getString("global.preferences.tab.tooltip"); 
    }
    
    public void initialize(IApplication app) {
        
    }

    public void uninitialize(IApplication app) {
        
    }    
    
    public void applyChanges() {
        if (firebirdManagerPreferences != null) {
            firebirdManagerPreferences.applyChanges();
        }
    }

    
    public String getTitle() {
        return i18n.GLOBAL_PREFERENCES_TAB_TITLE;
    }

    
    public String getHint() {
        return i18n.GLOBAL_PREFERENCES_TAB_TOOLTIP; 
    }

    public Component getPanelComponent() {
        return scrollPanePreferences;
    }

}
