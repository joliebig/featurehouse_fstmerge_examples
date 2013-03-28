
package net.sourceforge.squirrel_sql.plugins.refactoring.prefs;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

public class RefactoringPreferencesPanel extends JPanel  {                              

	private static final long serialVersionUID = -4293776729533111287L;

	private static final StringManager s_stringMgr =
        StringManagerFactory.getStringManager(RefactoringPreferencesPanel.class);    
	
    
    @SuppressWarnings("unused")
	private final static ILogger log = 
        LoggerController.createLogger(RefactoringPreferencesPanel.class);    

    RefactoringPreferenceBean _prefs = null;
    
    JCheckBox qualifyTableNamesCheckBox = null;
    JCheckBox quoteIdentifersCheckBox = null;
        
    public RefactoringPreferencesPanel(RefactoringPreferenceBean prefs) {
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
        
        String borderTitle = s_stringMgr.getString("RefactoringPreferencesPanel.borderTitle");
        result.setBorder(getTitledBorder(borderTitle));
        addQualifyTableNamesCheckBox(result, 0, 0); 
        addQuoteIdentifiersCheckBox(result, 0, 1);
        return result;
    }
    
    private void addQualifyTableNamesCheckBox(JPanel panel, int col, int row) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = col;
        c.gridy = row;  
        c.anchor = GridBagConstraints.WEST;
        
        String cbLabelStr = 
        	s_stringMgr.getString("RefactoringPreferencesPanel.qualifyCheckboxLabel");
        
        String cbToolTipText = 
        	s_stringMgr.getString("RefactoringPreferencesPanel.qualifyCheckboxToolTip");
        qualifyTableNamesCheckBox = new JCheckBox(cbLabelStr);
        qualifyTableNamesCheckBox.setToolTipText(cbToolTipText);
        panel.add(qualifyTableNamesCheckBox, c);
    }

    private void addQuoteIdentifiersCheckBox(JPanel panel, int col, int row) {
       GridBagConstraints c = new GridBagConstraints();
       c.gridx = col;
       c.gridy = row;  
       c.anchor = GridBagConstraints.WEST;
       
       String cbLabelStr = 
       	s_stringMgr.getString("RefactoringPreferencesPanel.quoteCheckboxLabel");
       
       String cbToolTipText = 
       	s_stringMgr.getString("RefactoringPreferencesPanel.qualifyCheckboxToolTip");
       quoteIdentifersCheckBox = new JCheckBox(cbLabelStr);
       quoteIdentifersCheckBox.setToolTipText(cbToolTipText);
       panel.add(quoteIdentifersCheckBox, c);
   }    
    
    private Border getTitledBorder(String title) {
        CompoundBorder border = 
            new CompoundBorder(new EmptyBorder(10,10,10,10),
                               new TitledBorder(title));        
        return border;
    }
    
    private void loadData() {
        qualifyTableNamesCheckBox.setSelected(_prefs.isQualifyTableNames());  
        quoteIdentifersCheckBox.setSelected(_prefs.isQuoteIdentifiers());
    }
    
    private void save() {
        _prefs.setQualifyTableNames(qualifyTableNamesCheckBox.isSelected());
        _prefs.setQuoteIdentifiers(quoteIdentifersCheckBox.isSelected());
        RefactoringPreferencesManager.savePrefs();
    }

    
    public void applyChanges() {
        save();
    }

    
    public Component getPanelComponent() {
        return this;
    }
}
