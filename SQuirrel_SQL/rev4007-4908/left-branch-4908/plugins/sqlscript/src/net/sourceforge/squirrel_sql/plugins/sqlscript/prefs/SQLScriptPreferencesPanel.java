
package net.sourceforge.squirrel_sql.plugins.sqlscript.prefs;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

public class SQLScriptPreferencesPanel extends JPanel  {                              

    private static final StringManager s_stringMgr =
        StringManagerFactory.getStringManager(SQLScriptPreferencesPanel.class);    
	
    
    private final static ILogger log = 
        LoggerController.createLogger(SQLScriptPreferencesPanel.class);    

    
    SQLScriptPreferenceBean _prefs = null;
    
    JCheckBox qualifyTableNamesCheckBox = null;
    
    JCheckBox deleteReferentialActionCheckbox = null;
    
    JCheckBox updateReferentialActionCheckbox = null;
    
    JComboBox deleteActionComboBox = null;
    
    JComboBox updateActionComboBox = null;
    
    JLabel deleteActionLabel = null;
    
    JLabel updateActionLabel = null;
    
    public SQLScriptPreferencesPanel(SQLScriptPreferenceBean prefs) {
        super();
        _prefs = prefs;
        createGUI();
        loadData();
    }
    
    private void createGUI() {
        this.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;   
        c.gridy = 0;   
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1;
        c.weighty = .60;
        add(createBottomPanel(), c);
    }
    
    private JPanel createBottomPanel() {
        JPanel result = new JPanel(new GridBagLayout());
        
        String borderTitle = s_stringMgr.getString("SQLScriptPreferencesPanel.borderTitle");
        result.setBorder(getTitledBorder(borderTitle));
        addQualifyTableNamesCheckBox(result, 0, 0); 
        addDeleteRefActionCheckBox(result, 0, 1);
        addDeleteActionComboBox(result, 0, 2);
        addUpdateRefActionCheckBox(result, 0, 3);
        addUpdateActionComboBox(result, 0, 4);
        return result;
    }
    
    private void addQualifyTableNamesCheckBox(JPanel panel, int col, int row) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = col;
        c.gridy = row;  
        c.anchor = GridBagConstraints.WEST;
        
        String cbLabelStr = 
        	s_stringMgr.getString("SQLScriptPreferencesPanel.qualifyCheckboxLabel");
        
        String cbToolTipText = 
        	s_stringMgr.getString("SQLScriptPreferencesPanel.qualifyCheckboxToolTip");
        qualifyTableNamesCheckBox = new JCheckBox(cbLabelStr);
        qualifyTableNamesCheckBox.setToolTipText(cbToolTipText);
        panel.add(qualifyTableNamesCheckBox, c);
    }

    private void addDeleteRefActionCheckBox(JPanel panel, int col, int row) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = col;
        c.gridy = row;  
        c.anchor = GridBagConstraints.WEST;
        
        
        String cbLabelStr = 
            s_stringMgr.getString("SQLScriptPreferencesPanel.deleteRefActionCheckboxLabel");
        
        String cbToolTipText = 
            s_stringMgr.getString("SQLScriptPreferencesPanel.deleteRefActionToolTip");
        deleteReferentialActionCheckbox = new JCheckBox(cbLabelStr);
        deleteReferentialActionCheckbox.setToolTipText(cbToolTipText);

        deleteReferentialActionCheckbox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                boolean enabled = deleteReferentialActionCheckbox.isSelected();
                deleteActionLabel.setEnabled(enabled);
                deleteActionComboBox.setEnabled(enabled);
            }
        });

        panel.add(deleteReferentialActionCheckbox, c);
    }

    private void addUpdateRefActionCheckBox(JPanel panel, int col, int row) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = col;
        c.gridy = row;  
        c.anchor = GridBagConstraints.WEST;
        
        
        String cbLabelStr = 
            s_stringMgr.getString("SQLScriptPreferencesPanel.updateRefActionCheckboxLabel");
        
        String cbToolTipText = 
            s_stringMgr.getString("SQLScriptPreferencesPanel.updateRefActionToolTip");
        updateReferentialActionCheckbox = new JCheckBox(cbLabelStr);
        updateReferentialActionCheckbox.setToolTipText(cbToolTipText);

        updateReferentialActionCheckbox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                boolean enabled = updateReferentialActionCheckbox.isSelected();
                updateActionLabel.setEnabled(enabled);
                updateActionComboBox.setEnabled(enabled);
            }
        });

        panel.add(updateReferentialActionCheckbox, c);
    }
    
    private void addDeleteActionComboBox(JPanel panel, int col, int row) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = col;
        c.gridy = row;  
        c.insets = new Insets(5,25,0,0);
        c.anchor = GridBagConstraints.WEST;
        
        JPanel subpanel = new JPanel();
        
        
        String cbLabelStr = 
            s_stringMgr.getString("SQLScriptPreferencesPanel.deleteActionLabel");
        deleteActionLabel = new JLabel(cbLabelStr);
        deleteActionLabel.setHorizontalAlignment(JLabel.LEFT);
                
        deleteActionComboBox = new JComboBox();
        DefaultComboBoxModel model = 
            new DefaultComboBoxModel(new String[] { "NO ACTION", 
                                                    "CASCADE", 
                                                    "SET DEFAULT",
                                                    "SET NULL"});
        deleteActionComboBox.setModel(model);
        subpanel.add(deleteActionLabel);
        subpanel.add(deleteActionComboBox);
        panel.add(subpanel, c);
    }

    private void addUpdateActionComboBox(JPanel panel, int col, int row) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = col;
        c.gridy = row;  
        c.insets = new Insets(5,25,0,0);
        c.anchor = GridBagConstraints.WEST;
        
        JPanel subpanel = new JPanel();
        
        
        String cbLabelStr = 
            s_stringMgr.getString("SQLScriptPreferencesPanel.updateActionLabel");
        updateActionLabel = new JLabel(cbLabelStr);
        updateActionLabel.setHorizontalAlignment(JLabel.LEFT);
                
        updateActionComboBox = new JComboBox();
        DefaultComboBoxModel model = 
            new DefaultComboBoxModel(new String[] { "NO ACTION", 
                                                    "CASCADE", 
                                                    "SET DEFAULT",
                                                    "SET NULL"});
        updateActionComboBox.setModel(model);
        subpanel.add(updateActionLabel);
        subpanel.add(updateActionComboBox);
        panel.add(subpanel, c);
    }

    
    
    private Border getTitledBorder(String title) {
        CompoundBorder border = 
            new CompoundBorder(new EmptyBorder(10,10,10,10),
                               new TitledBorder(title));        
        return border;
    }
    
    private void loadData() {
        qualifyTableNamesCheckBox.setSelected(_prefs.isQualifyTableNames());
        deleteReferentialActionCheckbox.setSelected(_prefs.isDeleteRefAction());
        deleteActionComboBox.setEnabled(deleteReferentialActionCheckbox.isSelected());
        deleteActionComboBox.setSelectedIndex(_prefs.getDeleteAction());
        updateReferentialActionCheckbox.setSelected(_prefs.isUpdateRefAction());
        updateActionComboBox.setEnabled(updateReferentialActionCheckbox.isSelected());
        updateActionComboBox.setSelectedIndex(_prefs.getUpdateAction());
        
    }
    
    private void save() {
        _prefs.setQualifyTableNames(qualifyTableNamesCheckBox.isSelected());
        _prefs.setDeleteRefAction(deleteReferentialActionCheckbox.isSelected());
        _prefs.setUpdateRefAction(updateReferentialActionCheckbox.isSelected());
        int action = deleteActionComboBox.getSelectedIndex();
        _prefs.setDeleteAction(action);
        action = updateActionComboBox.getSelectedIndex();
        _prefs.setUpdateAction(action);
        SQLScriptPreferencesManager.savePrefs();
    }

    
    public void applyChanges() {
        save();
    }

    
    public Component getPanelComponent() {
        return this;
    }
}
