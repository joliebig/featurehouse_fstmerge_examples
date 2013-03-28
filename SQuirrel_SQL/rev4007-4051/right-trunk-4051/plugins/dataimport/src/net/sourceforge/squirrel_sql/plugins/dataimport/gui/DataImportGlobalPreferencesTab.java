package net.sourceforge.squirrel_sql.plugins.dataimport.gui;


import java.awt.Component;

import javax.swing.JScrollPane;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.preferences.IGlobalPreferencesPanel;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.plugins.dataimport.prefs.PreferencesManager;


public class DataImportGlobalPreferencesTab implements IGlobalPreferencesPanel {

    PreferencesPanel prefs = null;
    private JScrollPane myscrolledPanel;

    private static final StringManager stringMgr =
        StringManagerFactory.getStringManager(DataImportGlobalPreferencesTab.class);    
   
    
    public DataImportGlobalPreferencesTab() {
    	prefs = new PreferencesPanel(PreferencesManager.getPreferences());
        myscrolledPanel = new JScrollPane(prefs);  
        myscrolledPanel.getVerticalScrollBar().setUnitIncrement(10);
    }
    
	
	public void initialize(IApplication app) {
        
	}

	
	public void uninitialize(IApplication app) {
        
	}

	
	public void applyChanges() {
        if (prefs != null) {
            prefs.applyChanges();
        }
	}

	
	public String getHint() {
        
        return stringMgr.getString("DataImportGlobalPreferencesTab.hint"); 
	}

	
	public Component getPanelComponent() {
        return myscrolledPanel;
	}

	
	public String getTitle() {
        
        return stringMgr.getString("DataImportGlobalPreferencesTab.title");
	}

}
