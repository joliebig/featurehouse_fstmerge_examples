
package net.sourceforge.squirrel_sql.client.plugin.gui;

import java.awt.Component;

import javax.swing.JScrollPane;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.preferences.IGlobalPreferencesPanel;

public class PluginGlobalPreferencesTab implements IGlobalPreferencesPanel {

    protected PluginQueryTokenizerPreferencesPanel _prefs = null;

    private JScrollPane _myscrolledPanel;

    private String _title = null;
    
    private String _hint = null;
    
    public PluginGlobalPreferencesTab(PluginQueryTokenizerPreferencesPanel prefsPanel) {
        _myscrolledPanel = new JScrollPane(prefsPanel);
        _prefs = prefsPanel;
    }

    public void initialize(IApplication app) {
        
    }

    public void uninitialize(IApplication app) {
        
    }

    public void applyChanges() {
        if (_prefs != null) {
            _prefs.applyChanges();
        }
    }

    
    public String getTitle() {
        return _title;
    }

    public void setTitle(String title) {
        this._title = title;
    }
    
    
    public String getHint() {
        return _hint;
    }

    public void setHint(String hint) {
        this._hint = hint;
    }
    
    public Component getPanelComponent() {
        return _myscrolledPanel;
    }

}
