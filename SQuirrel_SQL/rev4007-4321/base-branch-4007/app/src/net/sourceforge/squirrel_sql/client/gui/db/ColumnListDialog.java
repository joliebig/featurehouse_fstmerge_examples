

package net.sourceforge.squirrel_sql.client.gui.db;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.sourceforge.squirrel_sql.client.ApplicationArguments;
import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;



public class ColumnListDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    private JLabel tableNameLabel = null;
    private JLabel primaryKeyNameLabel = null;
    private JTextField tableNameTextField = null;
    private JLabel columnListLabel = null;
    private JList columnList = null;
    
    private JButton executeButton = null;
    private JButton editSQLButton = null;
    private JButton showSQLButton = null;
    private JButton cancelButton = null;
    private JTextField primaryKeyNameTF = null;
    
    
    private static final StringManager s_stringMgr =
        StringManagerFactory.getStringManager(ColumnListDialog.class);
    
    private interface i18n {
        
        String EXECUTE_BUTTON_LABEL =
            s_stringMgr.getString("ColumnListDialog.executeButtonLabel");
        
        String CANCEL_BUTTON_LABEL = 
            s_stringMgr.getString("ColumnListDialog.cancelButtonLabel");
        
        String COLUMN_NAME_LABEL = 
            s_stringMgr.getString("ColumnListDialog.columnNameLabel");
        
        
        String DROP_ERROR_MESSAGE = 
            s_stringMgr.getString("ColumnListDialog.dropErrorMessage");
        
        String DROP_ERROR_TITLE = 
            s_stringMgr.getString("ColumnListDialog.dropErrorTitle");
        
        String DROP_PRIMARY_KEY_TITLE = 
            s_stringMgr.getString("ColumnListDialog.dropPrimaryKeyTitle");
        
        String DROP_TITLE = 
            s_stringMgr.getString("ColumnListDialog.dropTitle");
        
        String MODIFY_BUTTON_LABEL = 
            s_stringMgr.getString("ColumnDetailsDialog.modifyButtonLabel");        
        
        String MODIFY_TITLE = 
            s_stringMgr.getString("ColumnListDialog.modifyTitle");
        
        String PRIMARY_KEY_TITLE =
            s_stringMgr.getString("ColumnListDialog.primaryKeyTitle");
        
        
        String PRIMARY_KEY_NAME_LABEL =
            s_stringMgr.getString("ColumnListDialog.primaryKeyNameLabel");
        
        
        String SHOWSQL_BUTTON_LABEL = 
            s_stringMgr.getString("ColumnListDialog.showSQLButtonLabel");
        
        String TABLE_NAME_LABEL = 
            s_stringMgr.getString("ColumnListDialog.tableNameLabel");
        
        String EDIT_BUTTON_LABEL = 
            s_stringMgr.getString("ColumnListDialog.editSQLButtonLabel");
    }
    
    public static final int DROP_COLUMN_MODE = 0;
    public static final int MODIFY_COLUMN_MODE = 1;
    public static final int ADD_PRIMARY_KEY_MODE = 2;
    public static final int DROP_PRIMARY_KEY_MODE = 3;
    
    private int _mode = DROP_COLUMN_MODE;
    
    private TableColumnInfo[] colInfos = null;
    
    
    public ColumnListDialog(TableColumnInfo[] columnInfos, int mode) { 
        _mode = mode;
        setColumnList(columnInfos);
    }
            
    public void setColumnList(TableColumnInfo[] columnInfos) {
        colInfos = columnInfos;
        ArrayList<String> tmp = new ArrayList<String>();
        for (int i = 0; i < colInfos.length; i++) {
            TableColumnInfo info = colInfos[i];
            tmp.add(info.getColumnName());
        }
        String[] cols = tmp.toArray(new String[tmp.size()]);
        if (columnList != null) {
            columnList.setListData(cols);
        } else {
            init(cols);
        }
        if (_mode == DROP_PRIMARY_KEY_MODE) {
            columnList.setEnabled(false);
        }
    }
    
    public void setTableName(String tableName) {
        tableNameTextField.setText(tableName);
    }
    
    public String getTableName() {
        return tableNameTextField.getText();
    }
        
    public void setPrimaryKeyName(String primaryKeyName) {
        primaryKeyNameTF.setText(primaryKeyName);
    }
    
    public String getPrimaryKeyName() {
        return primaryKeyNameTF.getText();
    }
    
    public TableColumnInfo[] getSelectedColumnList() {
        ArrayList<TableColumnInfo> result = new ArrayList<TableColumnInfo>();
        Object[] selectedColNames = columnList.getSelectedValues();
        for (int i = 0; i < selectedColNames.length; i++) {
            String columnName = (String)selectedColNames[i];
            result.add(getColInfoByName(columnName));
        }
        return result.toArray(new TableColumnInfo[result.size()]);
    }
    
    private TableColumnInfo getColInfoByName(String columnName) {
        for (int i = 0; i < colInfos.length; i++) {
            TableColumnInfo colInfo = colInfos[i];
            if (colInfo.getColumnName().equals(columnName)) {
                return colInfo;
            }
        }
        return null;
    }
    
    public void addColumnSelectionListener(ActionListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("listener cannot be null");
        }
        executeButton.addActionListener(listener);
    }
    
    public void addShowSQLListener(ActionListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("listener cannot be null");
        }
        showSQLButton.addActionListener(listener);
    }

    public void addEditSQLListener(ActionListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("listener cannot be null");
        }
        editSQLButton.addActionListener(listener);
    }    
    
    public void setMultiSelection() {
        columnList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    }
    
    public void setSingleSelection() {
        columnList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }
    
    private GridBagConstraints getLabelConstraints(GridBagConstraints c) {
        c.gridx = 0;
        c.gridy++;                
        c.anchor = GridBagConstraints.NORTHEAST;
        c.fill = GridBagConstraints.NONE;
        c.weightx = 0;
        c.weighty = 0;
        return c;
    }
    
    private GridBagConstraints getFieldConstraints(GridBagConstraints c) {
        c.gridx++;
        c.anchor = GridBagConstraints.NORTHWEST;   
        c.weightx = 0;
        c.weighty = 0;
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
        
    
    private void init(String[] columnNames) {
        super.setModal(true);        
        if (_mode == DROP_COLUMN_MODE) {
            setTitle(i18n.DROP_TITLE);
        } 
        if (_mode == MODIFY_COLUMN_MODE) {
            setTitle(i18n.MODIFY_TITLE);
        }
        if (_mode == ADD_PRIMARY_KEY_MODE) {
            setTitle(i18n.PRIMARY_KEY_TITLE);
        }
        if (_mode == DROP_PRIMARY_KEY_MODE) {
            setTitle(i18n.DROP_PRIMARY_KEY_TITLE);
        }
        setSize(425, 250);
        EmptyBorder border = new EmptyBorder(new Insets(5,5,5,5));
        Dimension mediumField = new Dimension(126, 20);
        
        JPanel pane = new JPanel();
        pane.setLayout(new GridBagLayout());
        pane.setBorder(new EmptyBorder(10,0,0,30));

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = -1;

        
        tableNameLabel = getBorderedLabel(i18n.TABLE_NAME_LABEL, border);
        pane.add(tableNameLabel, getLabelConstraints(c));
        
        tableNameTextField = new JTextField();
        tableNameTextField.setPreferredSize(mediumField);
        tableNameTextField.setEditable(false);
        pane.add(tableNameTextField, getFieldConstraints(c));
                
        
        if (_mode == ADD_PRIMARY_KEY_MODE
                || _mode == DROP_PRIMARY_KEY_MODE) {
            primaryKeyNameLabel = new JLabel(i18n.PRIMARY_KEY_NAME_LABEL);
            pane.add(primaryKeyNameLabel, getLabelConstraints(c));
            
            primaryKeyNameTF = new JTextField();
            primaryKeyNameTF.setPreferredSize(mediumField);
            if (_mode == ADD_PRIMARY_KEY_MODE) {
                primaryKeyNameTF.setEditable(true);
            } else {
                primaryKeyNameTF.setEditable(false);
            }
            pane.add(primaryKeyNameTF, getFieldConstraints(c));
        }
        
        
        columnListLabel = getBorderedLabel(i18n.COLUMN_NAME_LABEL, border);
        columnListLabel.setVerticalAlignment(JLabel.NORTH);
        pane.add(columnListLabel, getLabelConstraints(c));
        
        columnList = new JList(columnNames);
        columnList.addListSelectionListener(new ColumnListSelectionListener());

        JScrollPane sp = new JScrollPane(columnList);
        c = getFieldConstraints(c);
        c.weightx = 1;
        c.weighty = 1;        
        c.fill=GridBagConstraints.BOTH;
        pane.add(sp, c);
                
        Container contentPane = super.getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(pane, BorderLayout.CENTER);
        
        contentPane.add(getButtonPanel(), BorderLayout.SOUTH);
    }
    
    private JPanel getButtonPanel() {
        JPanel result = new JPanel();
        if (_mode == MODIFY_COLUMN_MODE) {
            executeButton = new JButton(i18n.MODIFY_BUTTON_LABEL);
        } else {
            executeButton = new JButton(i18n.EXECUTE_BUTTON_LABEL);
        }        
        result.add(executeButton);
        
        if (_mode != MODIFY_COLUMN_MODE) {
            editSQLButton = new JButton(i18n.EDIT_BUTTON_LABEL);
            result.add(editSQLButton);
            showSQLButton = new JButton(i18n.SHOWSQL_BUTTON_LABEL);
            result.add(showSQLButton);
        }
        cancelButton = new JButton(i18n.CANCEL_BUTTON_LABEL);
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
            }
        });
        result.add(cancelButton);
        if (_mode != DROP_PRIMARY_KEY_MODE) {
            executeButton.setEnabled(false);
            if (_mode != MODIFY_COLUMN_MODE) {
                editSQLButton.setEnabled(false);
                showSQLButton.setEnabled(false);
            }
        }
        return result;
    }
    
    
    public static void main(String[] args) {
        ApplicationArguments.initialize(new String[] {});
        String[] data = 
            new String [] {"A_Really_Long_Nasty_Column_Called_ColumnA", 
                           "ColumnB","ColumnC","ColumnD","ColumnE","ColumnF",
                           "ColumnG","ColumnH","ColumnI","ColumnJ","ColumnK",
                           "ColumnL","ColumnM","ColumnN","ColumnO","ColumnP",
                           "ColumnP","ColumnQ","ColumnR","ColumnS","ColumnT"};
        
        TableColumnInfo[] infos = new TableColumnInfo[data.length];
        
        for (int i = 0; i < infos.length; i++) {
            infos[i] = new TableColumnInfo("aCat", 
                                          "aSchem", 
                                          "aTab", 
                                          data[i], 
                                          java.sql.Types.CHAR,
                                          "character",
                                          10,
                                          0, 
                                          0,
                                          0,
                                          "a comment",
                                          "defVal",
                                          0,
                                          0,
                                          "YES");      
        }
        
        final ColumnListDialog c = new ColumnListDialog(infos, 0);
        c.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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
    
    private void enable(JButton button) {
        if (button != null) {
            button.setEnabled(true);
        }
    }

    private void disable(JButton button) {        
        if (button != null) {
            button.setEnabled(false);
        }
    }
    
    private class ColumnListSelectionListener implements ListSelectionListener {

        
        public void valueChanged(ListSelectionEvent e) {
            int[] selected = columnList.getSelectedIndices();
            
            
            
            if (_mode != DROP_PRIMARY_KEY_MODE) { 
                if (selected == null || selected.length == 0) {
                    disable(executeButton);
                    disable(editSQLButton);
                    disable(showSQLButton);
                    return;
                }
            } 
            
            
            if (_mode == DROP_COLUMN_MODE
                    && selected.length == columnList.getModel().getSize())
            {
                JOptionPane.showMessageDialog(ColumnListDialog.this, 
                                              i18n.DROP_ERROR_MESSAGE, 
                                              i18n.DROP_ERROR_TITLE, 
                                              JOptionPane.ERROR_MESSAGE);
                columnList.clearSelection();
                disable(executeButton);
                disable(editSQLButton);
                disable(showSQLButton);
                return;
            } 
            
            
            enable(executeButton);
            enable(editSQLButton);
            enable(showSQLButton);                
        }
        
    }    
}
