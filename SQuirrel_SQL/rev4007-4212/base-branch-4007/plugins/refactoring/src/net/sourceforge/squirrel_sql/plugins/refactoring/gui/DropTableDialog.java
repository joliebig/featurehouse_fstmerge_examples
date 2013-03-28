

package net.sourceforge.squirrel_sql.plugins.refactoring.gui;

import java.awt.GridBagConstraints;
import java.util.Arrays;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

public class DropTableDialog extends AbstractRefactoringDialog {

    
    private static final long serialVersionUID = 1L;

    
    private static final StringManager s_stringMgr =
        StringManagerFactory.getStringManager(DropTableDialog.class);
    
    static interface i18n {
        
        String CASCADE_LABEL = 
            s_stringMgr.getString("DropTableDialog.cascadeLabel");        
        
        
        String CATALOG_LABEL = 
            s_stringMgr.getString("DropTableDialog.catalogLabel");

        
        String SCHEMA_LABEL = 
            s_stringMgr.getString("DropTableDialog.schemaLabel");

        
        String TABLE_LABEL = 
            s_stringMgr.getString("DropTableDialog.tableLabel");
        
        
        String TITLE = s_stringMgr.getString("DropTableDialog.title");
        
        
    }
                
    private JLabel catalogLabel = null;
    private JLabel schemaLabel = null;
    private JTextField catalogTF = null;
    private JTextField schemaTF = null;
    private JList tableList = null;
    private JLabel tableListLabel = null;
    private JLabel cascadeConstraintsLabel = null;
    
    private JCheckBox cascadeCB = null; 
    
    private ITableInfo[] tableInfos = null;
    
    public DropTableDialog(ITableInfo[] tables) {
        super(false);
        setTitle(i18n.TITLE);
        tableInfos = tables;
        init();
    }
    
    public ITableInfo[] getTableInfos() {
        return tableInfos;
    }
        
    public List<ITableInfo> getTableInfoList() {
        return Arrays.asList(tableInfos);
    }
    
    public boolean getCascadeConstraints() {
        return cascadeCB.isSelected();
    }
    
    protected void init() {
        
        catalogLabel = getBorderedLabel(i18n.CATALOG_LABEL + " ", emptyBorder);
        pane.add(catalogLabel, getLabelConstraints(c));
        
        catalogTF = new JTextField();
        catalogTF.setPreferredSize(mediumField);
        catalogTF.setEditable(false);
        catalogTF.setText(tableInfos[0].getCatalogName());
        pane.add(catalogTF, getFieldConstraints(c));
        
        
        schemaLabel = getBorderedLabel(i18n.SCHEMA_LABEL+" ", emptyBorder);
        pane.add(schemaLabel, getLabelConstraints(c));
        
        schemaTF = new JTextField();
        schemaTF.setPreferredSize(mediumField);
        schemaTF.setEditable(false);
        schemaTF.setText(tableInfos[0].getSchemaName());
        pane.add(schemaTF, getFieldConstraints(c));
        
        
        tableListLabel = getBorderedLabel(i18n.TABLE_LABEL+" ", emptyBorder);
        tableListLabel.setVerticalAlignment(JLabel.NORTH);
        pane.add(tableListLabel, getLabelConstraints(c));
        
        tableList = new JList(getSimpleNames(tableInfos));
        tableList.setEnabled(false);

        JScrollPane sp = new JScrollPane(tableList);
        c = getFieldConstraints(c);
        c.weightx = 1;
        c.weighty = 1;        
        c.fill=GridBagConstraints.BOTH;
        pane.add(sp, c);
        
        
        cascadeConstraintsLabel = new JLabel(i18n.CASCADE_LABEL+" ");
        cascadeConstraintsLabel.setBorder(emptyBorder);
        pane.add(cascadeConstraintsLabel, getLabelConstraints(c));        
        
        cascadeCB = new JCheckBox();
        cascadeCB.setPreferredSize(mediumField);
        pane.add(cascadeCB, getFieldConstraints(c));
        super.executeButton.setRequestFocusEnabled(true);
    }
    
    private String[] getSimpleNames(ITableInfo[] tableInfos) {
        String[] result = new String[tableInfos.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = tableInfos[i].getSimpleName();
        }
        return result;
    }
    
    
}
