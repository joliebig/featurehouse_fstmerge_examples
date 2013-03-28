
package net.sourceforge.squirrel_sql.plugins.dbcopy.gui;

import java.awt.Component;

import javax.swing.JScrollPane;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.preferences.IGlobalPreferencesPanel;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.plugins.dbcopy.prefs.PreferencesManager;

public class DBCopyGlobalPreferencesTab implements IGlobalPreferencesPanel {

    PreferencesPanel prefs = null;
    private JScrollPane _myscrolledPanel;
    
    private static final StringManager s_stringMgr =
        StringManagerFactory.getStringManager(DBCopyGlobalPreferencesTab.class);    
    
    public DBCopyGlobalPreferencesTab() {
        prefs = new PreferencesPanel(PreferencesManager.getPreferences());
        _myscrolledPanel = new JScrollPane(prefs);  
        _myscrolledPanel.getVerticalScrollBar().setUnitIncrement(10);
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

    
    public String getTitle() {
        
        return s_stringMgr.getString("DBCopyGlobalPreferencesTab.title");
    }

    
    public String getHint() {
        
        return s_stringMgr.getString("DBCopyGlobalPreferencesTab.hint"); 
    }

    public Component getPanelComponent() {
        return _myscrolledPanel;
    }

}
