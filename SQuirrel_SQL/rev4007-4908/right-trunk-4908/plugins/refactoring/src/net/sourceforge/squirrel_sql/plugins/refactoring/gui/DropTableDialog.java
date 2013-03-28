package net.sourceforge.squirrel_sql.plugins.refactoring.gui;


import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.List;

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

    private JCheckBox cascadeCB = null;

    private ITableInfo[] tableInfos = null;

    public DropTableDialog(ITableInfo[] tables) {
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

        
        JLabel catalogLabel = getBorderedLabel(i18n.CATALOG_LABEL + " ", emptyBorder);
        pane.add(catalogLabel, getLabelConstraints(c));

        JTextField catalogTF = new JTextField();
        catalogTF.setPreferredSize(mediumField);
        catalogTF.setEditable(false);
        catalogTF.setText(tableInfos[0].getCatalogName());
        pane.add(catalogTF, getFieldConstraints(c));

        
        JLabel schemaLabel = getBorderedLabel(i18n.SCHEMA_LABEL + " ", emptyBorder);
        pane.add(schemaLabel, getLabelConstraints(c));

        JTextField schemaTF = new JTextField();
        schemaTF.setPreferredSize(mediumField);
        schemaTF.setEditable(false);
        schemaTF.setText(tableInfos[0].getSchemaName());
        pane.add(schemaTF, getFieldConstraints(c));

        
        JLabel tableListLabel = getBorderedLabel(i18n.TABLE_LABEL + " ", emptyBorder);
        tableListLabel.setVerticalAlignment(JLabel.NORTH);
        pane.add(tableListLabel, getLabelConstraints(c));

        JList tableList = new JList(getSimpleNames(tableInfos));
        tableList.setEnabled(false);

        JScrollPane sp = new JScrollPane(tableList);
        c = getFieldConstraints(c);
        c.weightx = 1;
        c.weighty = 1;
        c.fill = GridBagConstraints.BOTH;
        pane.add(sp, c);

        
        JLabel cascadeConstraintsLabel = new JLabel(i18n.CASCADE_LABEL + " ");
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
