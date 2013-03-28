package net.sourceforge.squirrel_sql.plugins.refactoring.gui;


import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.*;


public class RenameTableDialog extends AbstractRefactoringDialog {


    private static final long serialVersionUID = 1L;

    
    public static final int DIALOG_TYPE_VIEW = 1;

    
    public static final int DIALOG_TYPE_TABLE = 2;

    
    private static final StringManager s_stringMgr =
            StringManagerFactory.getStringManager(RenameTableDialog.class);

    private final IDatabaseObjectInfo[] dbInfo;

    
    private JTextField tableTF;


    static interface i18n {
        String CATALOG_LABEL =
                s_stringMgr.getString("RenameTableDialog.catalogLabel");

        String SCHEMA_LABEL =
                s_stringMgr.getString("RenameTableDialog.schemaLabel");

        String TABLE_LABEL =
                s_stringMgr.getString("RenameTableDialog.viewLabel");

        String TITLE_VIEW = s_stringMgr.getString("RenameTableDialog.titleView");

        String TITLE_TABLE = s_stringMgr.getString("RenameTableDialog.titleTable");
    }

    
    public RenameTableDialog(IDatabaseObjectInfo[] dbInfo, int dialogType) {
        this.dbInfo = dbInfo;
        if (dialogType == DIALOG_TYPE_TABLE) setTitle(RenameTableDialog.i18n.TITLE_TABLE);
        else if (dialogType == DIALOG_TYPE_VIEW) setTitle(RenameTableDialog.i18n.TITLE_VIEW);
        init();
    }

    
    protected void init() {

        setSize(400, 150);
        
        JLabel catalogLabel = getBorderedLabel(RenameTableDialog.i18n.CATALOG_LABEL + " ", emptyBorder);
        pane.add(catalogLabel, getLabelConstraints(c));

        JTextField catalogTF = new JTextField();
        catalogTF.setPreferredSize(mediumField);
        catalogTF.setEditable(false);
        catalogTF.setText(dbInfo[0].getCatalogName());
        pane.add(catalogTF, getFieldConstraints(c));

        
        JLabel schemaLabel = getBorderedLabel(RenameTableDialog.i18n.SCHEMA_LABEL + " ", emptyBorder);
        pane.add(schemaLabel, getLabelConstraints(c));

        JTextField schemaTF = new JTextField();
        schemaTF.setPreferredSize(mediumField);
        schemaTF.setEditable(false);
        schemaTF.setText(dbInfo[0].getSchemaName());
        pane.add(schemaTF, getFieldConstraints(c));

        
        JLabel tableLabel = getBorderedLabel(RenameTableDialog.i18n.TABLE_LABEL + " ", emptyBorder);
        tableLabel.setVerticalAlignment(JLabel.NORTH);
        pane.add(tableLabel, getLabelConstraints(c));

        tableTF = new JTextField();
        tableTF.setToolTipText(s_stringMgr.getString("RenameTableDialog.tableField.ToolTipText", dbInfo[0].getSimpleName()));
        tableTF.setPreferredSize(mediumField);
        tableTF.setEditable(true);
        tableTF.setText(dbInfo[0].getSimpleName());
        pane.add(tableTF, getFieldConstraints(c));


        super.executeButton.setRequestFocusEnabled(true);
    }


    
    public String getNewSimpleName() {
        return tableTF.getText();
    }

}
