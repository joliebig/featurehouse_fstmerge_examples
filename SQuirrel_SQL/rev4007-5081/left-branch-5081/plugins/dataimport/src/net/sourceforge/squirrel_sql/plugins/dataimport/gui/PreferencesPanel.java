package net.sourceforge.squirrel_sql.plugins.dataimport.gui;


import java.awt.Component;

import javax.swing.JCheckBox;
import javax.swing.JPanel;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.plugins.dataimport.prefs.DataImportPreferenceBean;
import net.sourceforge.squirrel_sql.plugins.dataimport.prefs.PreferencesManager;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;


public class PreferencesPanel extends JPanel {
	private static final long serialVersionUID = 7648092437088098470L;

	DataImportPreferenceBean prefs = null;
    
    JCheckBox truncateCheckBox = null;
    
    
    private final static ILogger log = 
        LoggerController.createLogger(PreferencesPanel.class);    
    
    
    private static final StringManager stringMgr =
        StringManagerFactory.getStringManager(PreferencesPanel.class);
    
    
    public PreferencesPanel(DataImportPreferenceBean prefs) {
        super();
        this.prefs = prefs;
        createGUI();
        loadData();
    }
    
    private void createGUI() {
		final FormLayout layout = new FormLayout(
				
				"left:pref:grow",
				
				"12dlu");

		PanelBuilder builder = new PanelBuilder(layout);
		CellConstraints cc = new CellConstraints();
		builder.setDefaultDialogBorder();
		
		
		truncateCheckBox = new JCheckBox(stringMgr.getString("PreferencesPanel.truncateTable"));

		int y = 1;
		builder.add(truncateCheckBox, cc.xy(1, y));

		add(builder.getPanel());
    }
    
    private void loadData() {
    	truncateCheckBox.setSelected(prefs.isUseTruncate());
    }
    
    private void save() {
        prefs.setUseTruncate(truncateCheckBox.isSelected());
        
        PreferencesManager.savePrefs();
    }

    
    public void applyChanges() {
        save();
    }
    
    
    public Component getPanelComponent() {
        return this;
    }



}
