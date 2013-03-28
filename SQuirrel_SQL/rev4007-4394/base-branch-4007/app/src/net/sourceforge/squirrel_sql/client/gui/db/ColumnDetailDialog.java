

package net.sourceforge.squirrel_sql.client.gui.db;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.DatabaseMetaData;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import net.sourceforge.squirrel_sql.client.ApplicationArguments;
import net.sourceforge.squirrel_sql.fw.dialects.DialectFactory;
import net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect;
import net.sourceforge.squirrel_sql.fw.sql.JDBCTypeMapper;
import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;


public class ColumnDetailDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    
    private static final StringManager s_stringMgr =
        StringManagerFactory.getStringManager(ColumnDetailDialog.class);
    
    private JLabel tableNameLabel = null;
    private JTextField tableNameTextField = null;
    private JLabel columnNameLabel = null;
    private JTextField columnNameTextField = null;
    private JLabel dialectLabel = null;
    private JComboBox dialectList = null;
    private JLabel typeLabel = null;
    private JComboBox typeList = null;
    private JLabel lengthLabel = null;
    private JSpinner lengthSpinner = null;
    private JLabel precisionLabel = null;
    private JSpinner precisionSpinner = null;
    private JLabel scaleLabel = null;
    private JSpinner scaleSpinner = null;
    private JLabel defaultLabel = null;
    private JTextField defaultTextField = null;
    private JLabel commentLabel = null;
    private JTextArea commentTextArea = null;
    private JLabel nullableLabel = null;
    private JCheckBox nullableCheckBox = null;    
    
    private JButton executeButton = null;
    private JButton editSQLButton = null;
    private JButton showSQLButton = null;
    private JButton cancelButton = null;
    
    public static final int ADD_MODE = 0;
    public static final int MODIFY_MODE = 1;
    
    private int _mode = ADD_MODE;
    
    private interface i18n {
        
        String EDIT_BUTTON_LABEL = 
            s_stringMgr.getString("ColumnDetailsDialog.editButtonLabel");
        
        String EXECUTE_BUTTON_LABEL = 
            s_stringMgr.getString("ColumnDetailsDialog.executeButtonLabel");
        
        String ADD_COLUMN_TITLE = 
            s_stringMgr.getString("ColumnDetailsDialog.addColumnTitle");  
        
        String MODIFY_COLUMN_TITLE = 
            s_stringMgr.getString("ColumnDetailsDialog.modifyColumnTitle");
        
        String CANCEL_BUTTON_LABEL = 
            s_stringMgr.getString("ColumnDetailsDialog.cancelButtonLabel");
        
        String COLUMN_NAME_LABEL=
            s_stringMgr.getString("ColumnDetailsDialog.columnNameLabel");
        
        String COMMENT_LABEL = 
            s_stringMgr.getString("ColumnDetailsDialog.commentLabel");
        
        String DEFAULT_VALUE_LABEL = 
            s_stringMgr.getString("ColumnDetailsDialog.defaultValueLabel");
        
        String DIALECT_LABEL = 
            s_stringMgr.getString("ColumnDetailsDialog.dialectLabel");
        
        String LENGTH_LABEL = 
            s_stringMgr.getString("ColumnDetailsDialog.lengthLabel");
        
        String MODIFY_TITLE = 
            s_stringMgr.getString("ColumnDetailsDialog.modifyColumnTitle");
        
        String NULLABLE_LABEL = 
            s_stringMgr.getString("ColumnDetailsDialog.nullableLabel");
        
        String NEW_COLUMN_VALUE = 
            s_stringMgr.getString("ColumnDetailsDialog.newColumnValue");
        
        String PRECISION_LABEL = 
            s_stringMgr.getString("ColumnDetailsDialog.precisionLabel");
        
        String SCALE_LABEL = 
            s_stringMgr.getString("ColumnDetailsDialog.scaleLabel");
        
        String SHOW_BUTTON_LABEL = 
            s_stringMgr.getString("ColumnDetailsDialog.showButtonLabel");
        
        String TABLE_NAME_LABEL = 
            s_stringMgr.getString("ColumnDetailsDialog.tableNameLabel");
        
        String TYPE_LABEL =
            s_stringMgr.getString("ColumnDetailsDialog.typeLabel");
    }
    
    
    public ColumnDetailDialog(int mode) {
        setMode(mode);
        init();
    }
    
    public void setMode(int mode) {
        if (mode != ADD_MODE && mode != MODIFY_MODE) {
            throw new IllegalArgumentException("Invalid mode - "+mode);
        }
        _mode = mode;
    }
    
    
    public void setSelectedDialect(String dbName) {
        dialectList.setSelectedItem(dbName);
    }
    
    public String getSelectedDBName() {
        return (String)dialectList.getSelectedItem();
    }
    
    public void setTableName(String tableName) {
        tableNameTextField.setText(tableName);
    }
    
    public String getTableName() {
        return tableNameTextField.getText();
    }
    
    public void setExistingColumnInfo(TableColumnInfo info) {
        tableNameTextField.setText(info.getTableName());
        columnNameTextField.setText(info.getColumnName());
        String dataType = JDBCTypeMapper.getJdbcTypeName(info.getDataType());
        typeList.setSelectedItem(dataType);
        nullableCheckBox.setSelected(info.isNullable().equals("YES"));
        
        if (JDBCTypeMapper.isNumberType(info.getDataType())) {
            precisionSpinner.setValue(Integer.valueOf(info.getColumnSize()));
        } else {
            lengthSpinner.setValue(Integer.valueOf(info.getColumnSize()));
        }   
        commentTextArea.setText(info.getRemarks());
        defaultTextField.setText(info.getDefaultValue());
    }
    
    public String getSelectedTypeName() {
        return (String)typeList.getSelectedItem();
    }
    
    
    public TableColumnInfo getColumnInfo() {
        String tableName = tableNameTextField.getText();
        String columnName = columnNameTextField.getText();
        String typeName = (String)typeList.getSelectedItem();
        int dataType = JDBCTypeMapper.getJdbcType(typeName);
        
        SpinnerNumberModel sizeModel = null;
        if (JDBCTypeMapper.isNumberType(dataType)) {
            sizeModel = (SpinnerNumberModel)precisionSpinner.getModel();
        } else {
            sizeModel = (SpinnerNumberModel)lengthSpinner.getModel();
        }   
        int columnSize = sizeModel.getNumber().intValue();
        SpinnerNumberModel scaleModel = 
            (SpinnerNumberModel)scaleSpinner.getModel();
        int decimalDigits = scaleModel.getNumber().intValue();
        
        int isNullAllowed = 1;
        String isNullable = null;
        if (nullableCheckBox.isSelected()) {
            isNullAllowed = DatabaseMetaData.columnNullable;
            isNullable = "YES";
        } else {
            isNullAllowed = DatabaseMetaData.columnNoNulls;
            isNullable = "NO";
        }
        String remarks = null;
        if (commentTextArea.isEditable()) {
            remarks = commentTextArea.getText();
        }
        String defaultValue = defaultTextField.getText();
        
        
        
        if ("".equals(defaultValue)) {
            defaultValue = null;
        }
        
        
        String catalog = null;
        String schema = null;
        int octetLength = 1;
        int ordinalPosition = 1;
        int radix = 1;
        
        TableColumnInfo result = 
            new TableColumnInfo(catalog, 
                                schema,
                                tableName,
                                columnName,
                                dataType,
                                typeName,
                                columnSize,
                                decimalDigits,
                                radix,
                                isNullAllowed,
                                remarks,
                                defaultValue,
                                octetLength,
                                ordinalPosition,
                                isNullable
                                );
        return result;
    }
    
    public void addExecuteListener(ActionListener listener) {
        executeButton.addActionListener(listener);
    }

    public void addEditSQLListener(ActionListener listener) {
        editSQLButton.addActionListener(listener);
    }

    public void addShowSQLListener(ActionListener listener) {
        showSQLButton.addActionListener(listener);
    }
    
    public void addDialectListListener(ItemListener listener) {
        dialectList.addItemListener(listener);
    }

    public void setVisible(boolean visible) {
        super.setVisible(visible);
        columnNameTextField.requestFocus();
        columnNameTextField.select(0, columnNameTextField.getText().length());                
    }
    
    private GridBagConstraints getLabelConstraints(GridBagConstraints c) {
        c.gridx = 0;
        c.gridy++;                
        c.anchor = GridBagConstraints.EAST;
        c.fill = GridBagConstraints.NONE;
        c.weightx = 0;
        c.weighty = 0;
        return c;
    }
    
    private GridBagConstraints getFieldConstraints(GridBagConstraints c) {
        c.gridx++;
        c.anchor = GridBagConstraints.WEST;   
        c.weightx = 1;
        c.weighty = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        return c;
    }

    private JLabel getBorderedLabel(String text, Border border) {
        JLabel result = new JLabel(text);
        result.setBorder(border);
        result.setPreferredSize(new Dimension(115, 20));
        result.setHorizontalAlignment(SwingConstants.RIGHT);
        return result;
    }
    
    private JTextField getSizedTextField(Dimension mediumField) {
        JTextField result = new JTextField();
        result.setPreferredSize(mediumField);
        return result;
    }
    
    
    private void init() {
        super.setModal(true);
        if (_mode == ADD_MODE) {
            setTitle(i18n.ADD_COLUMN_TITLE);
        } else {
            setTitle(i18n.MODIFY_COLUMN_TITLE);
        }
        
        setSize(375, 400);
        EmptyBorder border = new EmptyBorder(new Insets(5,5,5,5));
        Dimension mediumField = new Dimension(126, 20);
        Dimension largeField = new Dimension(126, 60);
        
        JPanel pane = new JPanel();
        pane.setLayout(new GridBagLayout());
        pane.setBorder(new EmptyBorder(0,0,0,10));

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = -1;

        
        tableNameLabel = getBorderedLabel(i18n.TABLE_NAME_LABEL, border);
        pane.add(tableNameLabel, getLabelConstraints(c));
        
        tableNameTextField = new JTextField();
        tableNameTextField.setPreferredSize(mediumField);
        tableNameTextField.setEditable(false);
        pane.add(tableNameTextField, getFieldConstraints(c));
        
        
        columnNameLabel = getBorderedLabel(i18n.COLUMN_NAME_LABEL, border);
        pane.add(columnNameLabel, getLabelConstraints(c));
        
        columnNameTextField = getSizedTextField(mediumField);
        columnNameTextField.setText(i18n.NEW_COLUMN_VALUE);
        columnNameTextField.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                if (columnNameTextField.getText().length() == 0) {
                    executeButton.setEnabled(false);
                    showSQLButton.setEnabled(false);
                } else {
                    executeButton.setEnabled(true);
                    showSQLButton.setEnabled(true);
                }
            }
        });
        pane.add(columnNameTextField, getFieldConstraints(c));
        
        
        dialectLabel = getBorderedLabel(i18n.DIALECT_LABEL, border);
        pane.add(dialectLabel, getLabelConstraints(c));
        
        Object[] dbNames = DialectFactory.getDbNames();
        dialectList = new JComboBox(dbNames);
        dialectList.setPreferredSize(mediumField);
        dialectList.addItemListener(new DialectTypeListListener());
        pane.add(dialectList, getFieldConstraints(c));        
        
        
        typeLabel = getBorderedLabel(i18n.TYPE_LABEL, border);
        pane.add(typeLabel, getLabelConstraints(c));
        
        String[] jdbcTypes = JDBCTypeMapper.getJdbcTypeList();
        typeList = new JComboBox(jdbcTypes);
        typeList.addItemListener(new ColumnTypeListListener());
        typeList.setPreferredSize(mediumField);
        pane.add(typeList, getFieldConstraints(c));
        
        
        lengthLabel = getBorderedLabel(i18n.LENGTH_LABEL, border);
        pane.add(lengthLabel, getLabelConstraints(c));
        
        lengthSpinner = new JSpinner();
        lengthSpinner.setPreferredSize(mediumField);
        double value = 10; 
        double min = 1;
        double max = Long.MAX_VALUE; 
        double step = 1; 
        SpinnerNumberModel model = new SpinnerNumberModel(value, min, max, step);
        lengthSpinner.setModel(model);
        lengthSpinner.setPreferredSize(mediumField);
        pane.add(lengthSpinner, getFieldConstraints(c));
        
        precisionLabel = new JLabel(i18n.PRECISION_LABEL);
        precisionLabel.setBorder(border);
        pane.add(precisionLabel, getLabelConstraints(c));

        
        precisionSpinner = new JSpinner();
        precisionSpinner.setPreferredSize(mediumField);
        value = 8; 
        min = 0;
        max = Long.MAX_VALUE; 
        step = 1; 
        SpinnerNumberModel precisionModel = 
            new SpinnerNumberModel(value, min, max, step); 
        precisionSpinner.setModel(precisionModel);
        precisionSpinner.setPreferredSize(mediumField);
        pane.add(precisionSpinner, getFieldConstraints(c));        

        
        scaleLabel = new JLabel(i18n.SCALE_LABEL);
        scaleLabel.setBorder(border);
        pane.add(scaleLabel, getLabelConstraints(c));

        scaleSpinner = new JSpinner();
        scaleSpinner.setPreferredSize(mediumField);
        value = 8; 
        min = 0;
        max = Long.MAX_VALUE; 
        step = 1; 
        SpinnerNumberModel scaleModel = 
            new SpinnerNumberModel(value, min, max, step); 
        scaleSpinner.setModel(scaleModel);
        scaleSpinner.setPreferredSize(mediumField);
        pane.add(scaleSpinner, getFieldConstraints(c));        
        
        
        defaultLabel = new JLabel(i18n.DEFAULT_VALUE_LABEL);
        defaultLabel.setBorder(border);
        pane.add(defaultLabel, getLabelConstraints(c));
        
        defaultTextField = new JTextField();
        defaultTextField.setPreferredSize(mediumField);
        pane.add(defaultTextField, getFieldConstraints(c));

        
        nullableLabel = new JLabel(i18n.NULLABLE_LABEL);
        nullableLabel.setBorder(border);
        pane.add(nullableLabel, getLabelConstraints(c));        
        
        nullableCheckBox = new JCheckBox("");
        nullableCheckBox.setSelected(true);
        pane.add(nullableCheckBox, getFieldConstraints(c));
        
        
        commentLabel = new JLabel(i18n.COMMENT_LABEL);
        commentLabel.setBorder(border);
        pane.add(commentLabel, getLabelConstraints(c));                
        
        commentTextArea = new JTextArea();
        commentTextArea.setBorder(new LineBorder(Color.DARK_GRAY, 1));
        commentTextArea.setLineWrap(true);
        c = getFieldConstraints(c);
        c.weightx = 2;
        c.weighty = 2;
        c.fill = GridBagConstraints.BOTH;
        JScrollPane scrollPane = new JScrollPane(); 
        scrollPane.getViewport().add(commentTextArea);
        scrollPane.setPreferredSize(largeField);
        pane.add(scrollPane, c);        
        
        Container contentPane = super.getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(pane, BorderLayout.CENTER);
        
        contentPane.add(getButtonPanel(), BorderLayout.SOUTH);
                
    }
    
    private JPanel getButtonPanel() {
        JPanel result = new JPanel();
        executeButton = new JButton(i18n.EXECUTE_BUTTON_LABEL);

        editSQLButton = new JButton(i18n.EDIT_BUTTON_LABEL);
        showSQLButton = new JButton(i18n.SHOW_BUTTON_LABEL);
        cancelButton = new JButton(i18n.CANCEL_BUTTON_LABEL);
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
            }
        });
        result.add(executeButton);
        result.add(editSQLButton);
        result.add(showSQLButton);
        result.add(cancelButton);
        return result;
    }
    
    
    public static void main(String[] args) {
        ApplicationArguments.initialize(new String[] {});
        final ColumnDetailDialog c = new ColumnDetailDialog(ADD_MODE);
        c.setTableName("FooTable");
        c.addComponentListener(new ComponentListener() {
            public void componentHidden(ComponentEvent e) {}
            public void componentMoved(ComponentEvent e) {}
            public void componentResized(ComponentEvent e) {
                System.out.println("Current size = "+c.getSize());
            }
            public void componentShown(ComponentEvent e) {}            
        });
        c.cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(1);
            }
        });
        c.setVisible(true);
        
    }
    private class ColumnTypeListListener implements ItemListener {

        public void itemStateChanged(ItemEvent e) {
            if (precisionSpinner == null) {
                return;
            }
            String columnType = (String)typeList.getSelectedItem();
            int jdbcType = JDBCTypeMapper.getJdbcType(columnType);
            if (JDBCTypeMapper.isNumberType(jdbcType)) {
                precisionSpinner.setEnabled(true);
                scaleSpinner.setEnabled(true);
                lengthSpinner.setEnabled(false);
            } else {
                if (JDBCTypeMapper.isDateType(jdbcType)) {
                    precisionSpinner.setEnabled(false);
                    scaleSpinner.setEnabled(false);
                    lengthSpinner.setEnabled(false);                                
                } else {
                    precisionSpinner.setEnabled(false);
                    scaleSpinner.setEnabled(false);
                    lengthSpinner.setEnabled(true);                                    
                }
            }
        }
        
    }
    
    private class DialectTypeListListener implements ItemListener {
        
        public void itemStateChanged(ItemEvent e) {
            String dbName = (String)dialectList.getSelectedItem();
            HibernateDialect dialect = DialectFactory.getDialect(dbName);
            if (!dialect.supportsColumnComment()) {
                commentTextArea.setEditable(false);
                
                
                String noColumnSupportMsg =
                    s_stringMgr.getString(
                            "ColumnDetailsDialog.columnCommentToolTip",
                            dbName);
                commentTextArea.setToolTipText(noColumnSupportMsg);
            } else {
                commentTextArea.setEditable(true);
                commentTextArea.setToolTipText(null);
            }
            if (_mode == MODIFY_MODE) {
                if (!dialect.supportsAlterColumnNull()) {
                    nullableCheckBox.setEnabled(false);
                    
                    
                    String noColumnSupportMsg = 
                        s_stringMgr.getString("ColumnDetailsDialog.columnNullToolTip",
                                              dbName);
                    nullableCheckBox.setToolTipText(noColumnSupportMsg);
                } else {
                    nullableCheckBox.setEnabled(true);
                    nullableCheckBox.setToolTipText(null);
                }
                if (!dialect.supportsRenameColumn()) {
                    
                    
                    String noColNameChange = 
                        s_stringMgr.getString("ColumnDetailsDialog.columnNameTootTip", 
                                              dbName);
                    columnNameTextField.setEditable(false);
                    columnNameTextField.setToolTipText(noColNameChange);
                } else {
                    columnNameTextField.setEditable(true);
                    columnNameTextField.setToolTipText(null);                    
                }
                if (!dialect.supportsAlterColumnType()) {
                    
                    
                    String noColTypeChange = 
                        s_stringMgr.getString("ColumnDetailsDialog.columnTypeTootTip",
                                              dbName);
                    typeList.setEnabled(false);
                    typeList.setToolTipText(noColTypeChange);
                    precisionSpinner.setEnabled(false);
                    precisionSpinner.setToolTipText(noColTypeChange);
                    lengthSpinner.setEnabled(false);
                    lengthSpinner.setToolTipText(noColTypeChange);
                    scaleSpinner.setEnabled(false);
                    scaleSpinner.setToolTipText(noColTypeChange);
                } else {
                    typeList.setEnabled(true);
                    typeList.setToolTipText(null);
                    precisionSpinner.setToolTipText(null);
                    lengthSpinner.setToolTipText(null);
                    scaleSpinner.setToolTipText(null);
                }
            }
        }
    }
}
