
package net.sourceforge.squirrel_sql.plugins.refactoring.prefs;

import java.awt.Component;

import javax.swing.JScrollPane;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.plugin.PluginResources;
import net.sourceforge.squirrel_sql.client.preferences.IGlobalPreferencesPanel;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

public class RefactoringPreferencesTab implements IGlobalPreferencesPanel {

    RefactoringPreferencesPanel prefsPanel = null;
    private JScrollPane _myscrolledPanel;
        
    private static final StringManager s_stringMgr =
        StringManagerFactory.getStringManager(RefactoringPreferencesTab.class);    
    
    
    PluginResources _resources = null;
    
    public RefactoringPreferencesTab() {
    	
    	RefactoringPreferenceBean bean = 
    		RefactoringPreferencesManager.getPreferences();
    	prefsPanel = new RefactoringPreferencesPanel(bean);
        _myscrolledPanel = new JScrollPane(prefsPanel);
    	
    }
    
    public void initialize(IApplication app) {
        
    }

    public void uninitialize(IApplication app) {
        
    }    
    
    public void applyChanges() {
        if (prefsPanel != null) {
            prefsPanel.applyChanges();
        }
    }

    
    public String getTitle() {
        
        return s_stringMgr.getString("RefactoringPreferencesTab.title");
    }

    
    public String getHint() {
        
        return s_stringMgr.getString("RefactoringPreferencesTab.hint"); 
    }

    public Component getPanelComponent() {
        return _myscrolledPanel;
    }

}
