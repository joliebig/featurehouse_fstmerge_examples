
package net.sourceforge.squirrel_sql.plugins.derby.prefs;

import java.awt.GridBagConstraints;
import java.awt.Insets;

import javax.swing.JCheckBox;
import javax.swing.JPanel;

import net.sourceforge.squirrel_sql.client.plugin.PluginQueryTokenizerPreferencesManager;
import net.sourceforge.squirrel_sql.client.plugin.gui.PluginQueryTokenizerPreferencesPanel;
import net.sourceforge.squirrel_sql.fw.preferences.IQueryTokenizerPreferenceBean;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;



public class DerbyPluginPreferencesPanel extends
        PluginQueryTokenizerPreferencesPanel {

 
    private static final long serialVersionUID = 1L;

    
    private static final StringManager s_stringMgr =
        StringManagerFactory.getStringManager(DerbyPluginPreferencesPanel.class);   
    
    static interface i18n {
       
       
       
       String READ_DERBY_CLOBS_FULLY_LABEL =  
          s_stringMgr.getString("DerbyPluginPreferencesPanel.readClobsFullyCheckBoxLabel");

       
       
       String READ_DERBY_CLOBS_FULLY_TT = 
          s_stringMgr.getString("DerbyPluginPreferencesPanel.readClobsFullyCheckBoxTT");
       
    }
    
    
    private final static JCheckBox readClobsFullyCheckBox = 
        new JCheckBox(i18n.READ_DERBY_CLOBS_FULLY_LABEL);
    
    
    
    public DerbyPluginPreferencesPanel(PluginQueryTokenizerPreferencesManager prefsMgr) 
    {
        super(prefsMgr, "Derby");
    }

    
    @Override
    protected JPanel createTopPanel() {
        JPanel result = super.createTopPanel();
        int lastY = super.lastY;
        addReadClobsFullyCheckBox(result, 0, lastY++);
        return result;
    }

    private void addReadClobsFullyCheckBox(JPanel result, int col, int row) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = col;
        c.gridy = row;
        c.gridwidth = 2;  
        c.anchor = GridBagConstraints.WEST;
        c.insets = new Insets(5,5,0,0);
        readClobsFullyCheckBox.setToolTipText(i18n.READ_DERBY_CLOBS_FULLY_TT);
        result.add(readClobsFullyCheckBox, c);        
    }

    
    @Override
    protected void loadData() {
        super.loadData();
        IQueryTokenizerPreferenceBean prefs = _prefsManager.getPreferences();
        DerbyPreferenceBean derbyPrefs = (DerbyPreferenceBean)prefs;
        readClobsFullyCheckBox.setSelected(derbyPrefs.isReadClobsFully());
    }

    
    @Override
    protected void save() {
        IQueryTokenizerPreferenceBean prefs = _prefsManager.getPreferences();
        DerbyPreferenceBean derbyPrefs = (DerbyPreferenceBean)prefs;
        derbyPrefs.setReadClobsFully(readClobsFullyCheckBox.isSelected());
        super.save();
    }
    
    
        
    
}
