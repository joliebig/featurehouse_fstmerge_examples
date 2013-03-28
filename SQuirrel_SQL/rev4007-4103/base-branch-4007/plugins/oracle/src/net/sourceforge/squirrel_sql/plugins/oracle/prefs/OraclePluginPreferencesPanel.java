
package net.sourceforge.squirrel_sql.plugins.oracle.prefs;

import java.awt.GridBagConstraints;
import java.awt.Insets;

import javax.swing.JCheckBox;
import javax.swing.JPanel;

import net.sourceforge.squirrel_sql.client.plugin.PluginQueryTokenizerPreferencesManager;
import net.sourceforge.squirrel_sql.client.plugin.gui.PluginQueryTokenizerPreferencesPanel;
import net.sourceforge.squirrel_sql.fw.preferences.IQueryTokenizerPreferenceBean;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;



public class OraclePluginPreferencesPanel extends
        PluginQueryTokenizerPreferencesPanel {

 
    private static final long serialVersionUID = 1L;

    
    private static final StringManager s_stringMgr =
        StringManagerFactory.getStringManager(OraclePluginPreferencesPanel.class);   
    
    static interface i18n {
        
        
        String HIDE_RECYCLE_BIN_CB_LABEL = 
            s_stringMgr.getString("OraclePluginPreferencesPanel.hideRecycleBinCheckBoxLabel");
        
        
        
        String HIDE_RECYCLE_BIN_CB_TT = 
            s_stringMgr.getString("OraclePluginPreferencesPanel.hideRecycleBinCheckBoxToolTip");
        
    }
    
    
    private static JCheckBox excludeRecycleBinTablesCheckBox = 
        new JCheckBox(i18n.HIDE_RECYCLE_BIN_CB_LABEL);
    
    
    public OraclePluginPreferencesPanel(PluginQueryTokenizerPreferencesManager prefsMgr) 
    {
        super(prefsMgr, "Oracle");
    }

    
    @Override
    protected JPanel createTopPanel() {
        JPanel result = super.createTopPanel();
        addRecycleBinCheckBox(result, 0, super.lastY);
        return result;
    }

    private void addRecycleBinCheckBox(JPanel result, int col, int row) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = col;
        c.gridy = row;
        c.gridwidth = 2;  
        c.anchor = GridBagConstraints.WEST;
        c.insets = new Insets(5,5,0,0);
        excludeRecycleBinTablesCheckBox.setToolTipText(i18n.HIDE_RECYCLE_BIN_CB_TT);
        result.add(excludeRecycleBinTablesCheckBox, c);        
    }
    
    
    @Override
    protected void loadData() {
        super.loadData();
        IQueryTokenizerPreferenceBean prefs = _prefsManager.getPreferences();
        OraclePreferenceBean oraclePrefs = (OraclePreferenceBean)prefs;
        excludeRecycleBinTablesCheckBox.setSelected(oraclePrefs.isExcludeRecycleBinTables());
    }

    
    @Override
    protected void save() {
        IQueryTokenizerPreferenceBean prefs = _prefsManager.getPreferences();
        OraclePreferenceBean oraclePrefs = (OraclePreferenceBean)prefs;
        oraclePrefs.setExcludeRecycleBinTables(excludeRecycleBinTablesCheckBox.isSelected());
        super.save();
    }
    
    
        
    
}
