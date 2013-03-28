package net.sourceforge.squirrel_sql.plugins.refactoring.gui;


import net.sourceforge.squirrel_sql.fw.sql.JDBCTypeMapper;
import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.*;
import java.sql.Types;


public class AddAutoIncrementDialog extends AbstractRefactoringDialog {


    private static final long serialVersionUID = 1L;

    
    private static final StringManager s_stringMgr =
            StringManagerFactory.getStringManager(AddAutoIncrementDialog.class);

    private final TableColumnInfo dbInfo;


    static interface i18n {
        String CATALOG_LABEL =
                s_stringMgr.getString("AddAutoIncrementDialog.catalogLabel");

        String SCHEMA_LABEL =
                s_stringMgr.getString("AddAutoIncrementDialog.schemaLabel");

        String TABLE_LABEL =
                s_stringMgr.getString("AddAutoIncrementDialog.tableLabel");

        String COLUMN_LABEL =
                s_stringMgr.getString("AddAutoIncrementDialog.columnLabel");
    }

    
    public AddAutoIncrementDialog(TableColumnInfo dbInfo) {
        this.dbInfo = dbInfo;
        setTitle(s_stringMgr.getString("AddAutoIncrementDialog.title", dbInfo.getColumnName()));
        init();
    }

    
    protected void init() {

        setSize(400, 200);
        
        JLabel catalogLabel = getBorderedLabel(AddAutoIncrementDialog.i18n.CATALOG_LABEL + " ", emptyBorder);
        pane.add(catalogLabel, getLabelConstraints(c));

        JTextField catalogTF = new JTextField();
        catalogTF.setPreferredSize(mediumField);
        catalogTF.setEditable(false);
        catalogTF.setText(dbInfo.getCatalogName());
        pane.add(catalogTF, getFieldConstraints(c));

        
        JLabel schemaLabel = getBorderedLabel(AddAutoIncrementDialog.i18n.SCHEMA_LABEL + " ", emptyBorder);
        pane.add(schemaLabel, getLabelConstraints(c));

        JTextField schemaTF = new JTextField();
        schemaTF.setPreferredSize(mediumField);
        schemaTF.setEditable(false);
        schemaTF.setText(dbInfo.getSchemaName());
        pane.add(schemaTF, getFieldConstraints(c));

        
        JLabel tableLabel = getBorderedLabel(AddAutoIncrementDialog.i18n.TABLE_LABEL + " ", emptyBorder);
        pane.add(tableLabel, getLabelConstraints(c));

        JTextField tableTF = new JTextField();
        tableTF.setPreferredSize(mediumField);
        tableTF.setEditable(false);
        tableTF.setText(dbInfo.getTableName());
        pane.add(tableTF, getFieldConstraints(c));

        
        JLabel columnLabel = getBorderedLabel(AddAutoIncrementDialog.i18n.COLUMN_LABEL + " ", emptyBorder);
        columnLabel.setVerticalAlignment(JLabel.NORTH);
        pane.add(columnLabel, getLabelConstraints(c));

        JTextField columnTF = new JTextField(dbInfo.getColumnName());
        columnTF.setPreferredSize(mediumField);
        columnTF.setEditable(false);
        pane.add(columnTF, getFieldConstraints(c));

        super.executeButton.setRequestFocusEnabled(true);
    }

    public static void main(String[] args) {
        
        String catalog = null;
        String schema = "public";
        String employeIdentifactionTable = "EmployeeIdentification";

        String[] employeeColumns = {"EmployeeNumber", "Name", "PhoneNumber"};
        TableColumnInfo newColumn = new TableColumnInfo(
                catalog, schema, employeIdentifactionTable, employeeColumns[0],
                Types.VARCHAR, JDBCTypeMapper.getJdbcTypeName(Types.VARCHAR), 30,
                0, 0, 1, null, null, 0, 1, "YES");

        new AddAutoIncrementDialog(newColumn).setVisible(true);
    }


}
