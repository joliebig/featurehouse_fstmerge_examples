package net.sourceforge.squirrel_sql.plugins.mssql.gui;



import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.DefaultTableModel;

import net.sourceforge.squirrel_sql.client.gui.builders.UIFactory;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.schemainfo.ObjFilterMatcher;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.IProcedureInfo;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.IUDTInfo;
import net.sourceforge.squirrel_sql.fw.sql.SQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.plugins.mssql.MssqlPlugin;
import net.sourceforge.squirrel_sql.plugins.mssql.util.DatabaseObjectInfoRenderer;
import net.sourceforge.squirrel_sql.plugins.mssql.util.DatabaseObjectInfoTableModel;
import net.sourceforge.squirrel_sql.plugins.mssql.util.MssqlIntrospector;

import com.jgoodies.forms.builder.ButtonBarBuilder;
import com.jgoodies.forms.factories.Borders;

public class GenerateSqlDialog extends JDialog {

	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(GenerateSqlDialog.class);


    private ISession _session;
    private MssqlPlugin _plugin;
    private IDatabaseObjectInfo[] _dbObjs;
    
    private JTable _availableTable;
    private JTable _selectedTable;
    private JCheckBox _generateCreateCheckbox;
    private JCheckBox _generateDropCheckbox;
    private JCheckBox _generateDependentCheckbox;
    private JCheckBox _includeHeadersCheckbox;
    private JCheckBox _extendedPropsCheckbox;
    private JCheckBox _onlySevenCheckbox;
    private JTextArea _templateArea;
    private JCheckBox _scriptDatabaseCheckbox;
    private JCheckBox _scriptUsersAndRolesCheckbox;
    private JCheckBox _scriptLoginsCheckbox;
    private JCheckBox _scriptPermissionsCheckbox;
    private JCheckBox _scriptIndexesCheckbox;
    private JCheckBox _scriptFTIndexesCheckbox;
    private JCheckBox _scriptTriggersCheckbox;
    private JCheckBox _scriptConstraintsCheckbox;
    private JRadioButton _OEMRadio;
    private JRadioButton _ANSIRadio;
    private JRadioButton _UnicodeRadio;
    private JRadioButton _oneFileRadio;
    private JRadioButton _separateFilesRadio;
    
    private boolean _wasCompleted;

    public GenerateSqlDialog(ISession session, MssqlPlugin plugin, IDatabaseObjectInfo[] dbObjs) throws SQLException {
		super(ctorHelper(session, plugin, dbObjs), true);

		_session = session;
        _plugin = plugin;
        _dbObjs = dbObjs;
        
        _wasCompleted = false;
        
        createGUI();
	}

	private void createGUI() throws SQLException {
		
		setTitle(s_stringMgr.getString("mssql.generateSqlScript"));
		setContentPane(buildContentPane());
	}

	private JComponent buildContentPane() throws SQLException {
		final JPanel pnl = new JPanel(new BorderLayout());
		pnl.add(buildMainPanel(), BorderLayout.CENTER);
		pnl.add(buildToolBar(), BorderLayout.SOUTH);
		pnl.setBorder(Borders.TABBED_DIALOG_BORDER);

		return pnl;
    }

	private JTabbedPane buildMainPanel() throws SQLException {
		final JTabbedPane tabPanel = UIFactory.getInstance().createTabbedPane();
		
		tabPanel.addTab(s_stringMgr.getString("mssql.general"),null,buildGeneralPanel());
		
        tabPanel.addTab(s_stringMgr.getString("mssql.foematting"),null,buildFormattingPanel());
		
		  tabPanel.addTab(s_stringMgr.getString("mssql.options"),null,buildOptionsPanel());
		return tabPanel;
	}
    
    private JPanel buildGeneralPanel() {
        JPanel panel = new JPanel();
        GridBagLayout gridBag = new GridBagLayout();
        panel.setLayout(gridBag);
        
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 0.0;
        
        JLabel objectsLabel = new JLabel();
		 
		  objectsLabel.setText(s_stringMgr.getString("mssql.toScript"));
        gridBag.setConstraints(objectsLabel, c);
        panel.add(objectsLabel);

		 
		  final JCheckBox allObjectsCheckbox = new JCheckBox(s_stringMgr.getString("mssql.allObjects"));
		 
        final JCheckBox allTablesCheckbox = new JCheckBox(s_stringMgr.getString("mssql.allTables"));
		 
        final JCheckBox allRulesCheckbox = new JCheckBox(s_stringMgr.getString("mssql.allRules"));
		 
        final JCheckBox allViewsCheckbox = new JCheckBox(s_stringMgr.getString("mssql.allViews"));
		 
        final JCheckBox allUddtCheckbox = new JCheckBox(s_stringMgr.getString("mssql.allUserDef"));
		 
        final JCheckBox allProceduresCheckbox = new JCheckBox(s_stringMgr.getString("mssql.allSps"));
		 
        final JCheckBox allUdfsCheckbox = new JCheckBox(s_stringMgr.getString("mssql.allUDFs"));
		 
        final JCheckBox allDefaultsCheckbox = new JCheckBox(s_stringMgr.getString("mssql.allDefaults"));
        
        c.gridx = 0;
        c.gridy = 1;
        allObjectsCheckbox.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED)
                    transferObjectsOfType(0,(DatabaseObjectInfoTableModel) _availableTable.getModel(),(DatabaseObjectInfoTableModel) _selectedTable.getModel());
                else if (e.getStateChange() == ItemEvent.DESELECTED)
                    transferObjectsOfType(0,(DatabaseObjectInfoTableModel) _selectedTable.getModel(),(DatabaseObjectInfoTableModel) _availableTable.getModel());
                
                boolean isSelected = (e.getStateChange() == ItemEvent.SELECTED);
                
                allTablesCheckbox.setSelected(isSelected);
                allRulesCheckbox.setSelected(isSelected);
                allViewsCheckbox.setSelected(isSelected);
                allUddtCheckbox.setSelected(isSelected);
                allProceduresCheckbox.setSelected(isSelected);
                allUdfsCheckbox.setSelected(isSelected);
                allDefaultsCheckbox.setSelected(isSelected);
                
                allTablesCheckbox.setEnabled(!isSelected);
                allRulesCheckbox.setEnabled(!isSelected);
                allViewsCheckbox.setEnabled(!isSelected);
                allUddtCheckbox.setEnabled(!isSelected);
                allProceduresCheckbox.setEnabled(!isSelected);
                allUdfsCheckbox.setEnabled(!isSelected);
                allDefaultsCheckbox.setEnabled(!isSelected);
            }
        });
        gridBag.setConstraints(allObjectsCheckbox, c);
        panel.add(allObjectsCheckbox);
        
        c.gridx = 0;
        c.gridy = 2;
        allTablesCheckbox.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED)
                    transferObjectsOfType(MssqlIntrospector.MSSQL_TABLE,(DatabaseObjectInfoTableModel) _availableTable.getModel(),(DatabaseObjectInfoTableModel) _selectedTable.getModel());
                else if (e.getStateChange() == ItemEvent.DESELECTED)
                    transferObjectsOfType(MssqlIntrospector.MSSQL_TABLE,(DatabaseObjectInfoTableModel) _selectedTable.getModel(),(DatabaseObjectInfoTableModel) _availableTable.getModel());
            }
        });
        gridBag.setConstraints(allTablesCheckbox, c);
        panel.add(allTablesCheckbox);
        
        c.gridx = 2;
        c.gridy = 2;
        allRulesCheckbox.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED)
                    transferObjectsOfType(MssqlIntrospector.MSSQL_RULE,(DatabaseObjectInfoTableModel) _availableTable.getModel(),(DatabaseObjectInfoTableModel) _selectedTable.getModel());
                else if (e.getStateChange() == ItemEvent.DESELECTED)
                    transferObjectsOfType(MssqlIntrospector.MSSQL_RULE,(DatabaseObjectInfoTableModel) _selectedTable.getModel(),(DatabaseObjectInfoTableModel) _availableTable.getModel());
            }
        });
        gridBag.setConstraints(allRulesCheckbox, c);
        panel.add(allRulesCheckbox);
        
        c.gridx = 0;
        c.gridy = 3;
        allViewsCheckbox.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED)
                    transferObjectsOfType(MssqlIntrospector.MSSQL_VIEW,(DatabaseObjectInfoTableModel) _availableTable.getModel(),(DatabaseObjectInfoTableModel) _selectedTable.getModel());
                else if (e.getStateChange() == ItemEvent.DESELECTED)
                    transferObjectsOfType(MssqlIntrospector.MSSQL_VIEW,(DatabaseObjectInfoTableModel) _selectedTable.getModel(),(DatabaseObjectInfoTableModel) _availableTable.getModel());
            }
        });
        gridBag.setConstraints(allViewsCheckbox, c);
        panel.add(allViewsCheckbox);
        
        c.gridx = 2;
        c.gridy = 3;
        allUddtCheckbox.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED)
                    transferObjectsOfType(MssqlIntrospector.MSSQL_UDT,(DatabaseObjectInfoTableModel) _availableTable.getModel(),(DatabaseObjectInfoTableModel) _selectedTable.getModel());
                else if (e.getStateChange() == ItemEvent.DESELECTED)
                    transferObjectsOfType(MssqlIntrospector.MSSQL_UDT,(DatabaseObjectInfoTableModel) _selectedTable.getModel(),(DatabaseObjectInfoTableModel) _availableTable.getModel());
            }
        });
        gridBag.setConstraints(allUddtCheckbox, c);
        panel.add(allUddtCheckbox);
        
        c.gridx = 0;
        c.gridy = 4;
        allProceduresCheckbox.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED)
                    transferObjectsOfType(MssqlIntrospector.MSSQL_STOREDPROCEDURE,(DatabaseObjectInfoTableModel) _availableTable.getModel(),(DatabaseObjectInfoTableModel) _selectedTable.getModel());
                else if (e.getStateChange() == ItemEvent.DESELECTED)
                    transferObjectsOfType(MssqlIntrospector.MSSQL_STOREDPROCEDURE,(DatabaseObjectInfoTableModel) _selectedTable.getModel(),(DatabaseObjectInfoTableModel) _availableTable.getModel());
            }
        });
        gridBag.setConstraints(allProceduresCheckbox, c);
        panel.add(allProceduresCheckbox);
        
        c.gridx = 2;
        c.gridy = 4;
        allUdfsCheckbox.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED)
                    transferObjectsOfType(MssqlIntrospector.MSSQL_UDF,(DatabaseObjectInfoTableModel) _availableTable.getModel(),(DatabaseObjectInfoTableModel) _selectedTable.getModel());
                else if (e.getStateChange() == ItemEvent.DESELECTED)
                    transferObjectsOfType(MssqlIntrospector.MSSQL_UDF,(DatabaseObjectInfoTableModel) _selectedTable.getModel(),(DatabaseObjectInfoTableModel) _availableTable.getModel());
            }
        });
        gridBag.setConstraints(allUdfsCheckbox, c);
        panel.add(allUdfsCheckbox);
        
        c.gridx = 0;
        c.gridy = 5;
        allDefaultsCheckbox.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED)
                    transferObjectsOfType(MssqlIntrospector.MSSQL_DEFAULT,(DatabaseObjectInfoTableModel) _availableTable.getModel(),(DatabaseObjectInfoTableModel) _selectedTable.getModel());
                else if (e.getStateChange() == ItemEvent.DESELECTED)
                    transferObjectsOfType(MssqlIntrospector.MSSQL_DEFAULT,(DatabaseObjectInfoTableModel) _selectedTable.getModel(),(DatabaseObjectInfoTableModel) _availableTable.getModel());
            }
        });
        gridBag.setConstraints(allDefaultsCheckbox, c);
        panel.add(allDefaultsCheckbox);
        
        JLabel availableLabel = new JLabel();
        c.gridx = 0;
        c.gridy = 6;

		  
 		  objectsLabel.setText(s_stringMgr.getString("mssql.objectsOn", _dbObjs[0].getCatalogName()));
        gridBag.setConstraints(availableLabel, c);
        panel.add(availableLabel);
        
        JLabel selectedLabel = new JLabel();
        c.gridx = 2;
        c.gridy = 6;
		  
		  objectsLabel.setText("Objects to be scripted:");
        gridBag.setConstraints(selectedLabel, c);
        panel.add(selectedLabel);
        
        JButton addButton = new JButton();
        c.gridx = 1;
        c.gridy = 8;
		 
        addButton.setText(s_stringMgr.getString("mssql.add"));
		 
        addButton.setMnemonic("A".charAt(0));
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                transferSelectedItems(_availableTable,_selectedTable);
            }
        });
        gridBag.setConstraints(addButton, c);
        panel.add(addButton);
        
        JButton removeButton = new JButton();
        c.gridx = 1;
        c.gridy = 9;
		  
		  removeButton.setText(s_stringMgr.getString("mssql.remove"));
		  
		  removeButton.setMnemonic(s_stringMgr.getString("mssql.remove_Mnemonic").charAt(0));
        removeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                transferSelectedItems(_selectedTable,_availableTable);
            }
        });
        gridBag.setConstraints(removeButton, c);
        panel.add(removeButton);
        
        
        
        _availableTable = new JTable(new DefaultTableModel(0,0));
        _availableTable.setModel(new DatabaseObjectInfoTableModel());
        _availableTable.getColumnModel().getColumn(0).setCellRenderer(new DatabaseObjectInfoRenderer());
        JScrollPane availablePane = new JScrollPane(_availableTable);
        c.gridx = 0;
        c.gridy = 7;
        c.gridheight = 3;
        gridBag.setConstraints(availablePane, c);
        panel.add(availablePane);
        
        populateAvailableTable(_availableTable);
        
        _selectedTable = new JTable(new DefaultTableModel(0,0));
        _selectedTable.setModel(new DatabaseObjectInfoTableModel());
        _selectedTable.getColumnModel().getColumn(0).setCellRenderer(new DatabaseObjectInfoRenderer());
        JScrollPane selectedPane = new JScrollPane(_selectedTable);
        c.gridx = 2;
        c.gridy = 7;
        c.gridheight = 3;
        gridBag.setConstraints(selectedPane, c);
        panel.add(selectedPane);
        
        return panel;
    }
    
    private void transferObjectsOfType(int mssqlType, DatabaseObjectInfoTableModel fromModel, DatabaseObjectInfoTableModel toModel) {
        int i = 0;
        while (i < fromModel.getRowCount()) {
            IDatabaseObjectInfo oi = (IDatabaseObjectInfo) fromModel.getValueAt(i,0);
            if (mssqlType == 0 || MssqlIntrospector.getObjectInfoType(oi) == mssqlType) {
                if (fromModel.removeElement(oi))
                    toModel.addElement(oi);
                
            }
            else
                i++;
        }
    }
    
    private void transferSelectedItems(JTable fromTable, JTable toTable) {
        DatabaseObjectInfoTableModel fromModel = (DatabaseObjectInfoTableModel) fromTable.getModel();
        DatabaseObjectInfoTableModel toModel = (DatabaseObjectInfoTableModel) toTable.getModel();
        int[] selectedRows = fromTable.getSelectedRows();
        
        
        for (int i = selectedRows.length - 1; i >= 0 ; i--) {
            IDatabaseObjectInfo oi = (IDatabaseObjectInfo) fromModel.getValueAt(selectedRows[i],0);
            if (fromModel.removeElement(oi))
                toModel.addElement(oi);
        }
        
        
    }
    
    private void populateAvailableTable(JTable table) {
        DatabaseObjectInfoTableModel model = (DatabaseObjectInfoTableModel) table.getModel();
        
        try {
            String catalog = _session.getSQLConnection().getCatalog();
            SQLDatabaseMetaData metaData = _session.getSQLConnection().getSQLMetaData();

            int i;

            
            ITableInfo[] tables = _session.getSchemaInfo().getITableInfos(catalog,null,new ObjFilterMatcher(),new String[] { "TABLE" });
            for (i = 0; i < tables.length; i++)
                model.addElement(tables[i]);
            
            
            ITableInfo[] views = _session.getSchemaInfo().getITableInfos(catalog,null,new ObjFilterMatcher(),new String[] { "VIEW" });
            for (i = 0; i < views.length; i++)
                model.addElement(views[i]);
            
            
            
            IProcedureInfo[] procs = _session.getSchemaInfo().getStoredProceduresInfos(catalog,null,new ObjFilterMatcher());
            for (i = 0; i < procs.length; i++)
                if (!procs[i].getSimpleName().startsWith("dt_"))
                    model.addElement(procs[i]);

            
            IUDTInfo[] udts = metaData.getUDTs(catalog,null,null,null);
            for (i = 0; i < udts.length; i++)
                model.addElement(udts[i]);
        }
        catch (SQLException ex) {
            _session.getApplication().showErrorDialog(ex.getMessage(),ex);
        }
    }
    
    private JPanel buildFormattingPanel() {
        JPanel panel = new JPanel();
        GridBagLayout gridBag = new GridBagLayout();
        panel.setLayout(gridBag);
        
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 0.0;
        
        JLabel optionsLabel = new JLabel();
		  
		  optionsLabel.setText(s_stringMgr.getString("mssql.scriptingOptionsDlg"));
        gridBag.setConstraints(optionsLabel, c);
        panel.add(optionsLabel);

		  
		  _generateCreateCheckbox = new JCheckBox(s_stringMgr.getString("mssql.genereateCreate"),true);
        c.gridx = 0;
        c.gridy = 1;
        _generateCreateCheckbox.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
               scriptOptionsChanged();
            }
        });
        gridBag.setConstraints(_generateCreateCheckbox, c);
        panel.add(_generateCreateCheckbox);

		  
		  _generateDropCheckbox = new JCheckBox(s_stringMgr.getString("mssql.generateDrop"),true);
        c.gridx = 0;
        c.gridy = 2;
        _generateDropCheckbox.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
               scriptOptionsChanged();
            }
        });
        gridBag.setConstraints(_generateDropCheckbox, c);
        panel.add(_generateDropCheckbox);

		  
		  _generateDependentCheckbox = new JCheckBox(s_stringMgr.getString("mssql.generateDependend"),false);
        c.gridx = 0;
        c.gridy = 3;
        _generateDependentCheckbox.setEnabled(false);
        _generateDependentCheckbox.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
               scriptOptionsChanged();
            }
        });
        gridBag.setConstraints(_generateDependentCheckbox, c);
        panel.add(_generateDependentCheckbox);
        
        
		  _includeHeadersCheckbox = new JCheckBox(s_stringMgr.getString("mssql.includeHeaders"),false);
        c.gridx = 0;
        c.gridy = 4;
        _includeHeadersCheckbox.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
               scriptOptionsChanged();
            }
        });
        gridBag.setConstraints(_includeHeadersCheckbox, c);
        panel.add(_includeHeadersCheckbox);

		  
		  _extendedPropsCheckbox = new JCheckBox(s_stringMgr.getString("mssql.extProps"),false);
        c.gridx = 0;
        c.gridy = 5;
        _extendedPropsCheckbox.setEnabled(false);
        _extendedPropsCheckbox.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
               scriptOptionsChanged();
            }
        });
        gridBag.setConstraints(_extendedPropsCheckbox, c);
        panel.add(_extendedPropsCheckbox);

		  
		  _onlySevenCheckbox = new JCheckBox(s_stringMgr.getString("mssql.only70compatible"),false);
        c.gridx = 0;
        c.gridy = 6;
        _onlySevenCheckbox.setEnabled(false);
        _onlySevenCheckbox.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
               scriptOptionsChanged();
            }
        });
        gridBag.setConstraints(_onlySevenCheckbox, c);
        panel.add(_onlySevenCheckbox);
        
        JLabel templateLabel = new JLabel();
        c.gridx = 0;
        c.gridy = 7;
		  
		  templateLabel.setText(s_stringMgr.getString("mssql.scriptTemplate"));
        gridBag.setConstraints(templateLabel, c);
        panel.add(templateLabel);
        
        _templateArea = new JTextArea();
        JScrollPane templatePane = new JScrollPane(_templateArea);
        c.gridx = 0;
        c.gridy = 8;
        c.weightx = 2.0;
        c.weighty = 2.0;
        _templateArea.setEditable(false);
        gridBag.setConstraints(templatePane, c);
        panel.add(templatePane);
        
        scriptOptionsChanged();
        
        return panel;
    }
    
    private void scriptOptionsChanged() {
        boolean generateCreate = _generateCreateCheckbox.isSelected();
        boolean generateDrop = _generateDropCheckbox.isSelected();
        boolean generateDependent = _generateDependentCheckbox.isSelected();
        boolean includeHeaders = _includeHeadersCheckbox.isSelected();
        
        StringBuffer buf = new StringBuffer();
        
        if (includeHeaders)
            buf.append("/* OBJECT: table: SampleTable  SCRIPT DATE: 1/1/1999 */\n\n");
        if (generateDependent)
            buf.append("sp_addtype SampleUDT, int\nGO\n\n");
        if (generateDrop)
            buf.append("DROP TABLE SampleTable\nGO\n\n");
        if (generateCreate)
            buf.append("CREATE TABLE SampleTable\n(SampleColumn1 datetime NULL,\nSampleColumn2 SampleUDT)\nGO\n\n");
       
        _templateArea.setText(buf.toString());
    }
    
    private JPanel buildOptionsPanel() {
        JPanel panel = new JPanel();
        GridBagLayout gridBag = new GridBagLayout();
        panel.setLayout(gridBag);
        
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 0.0;
        
        
        JPanel securityPanel = new JPanel();
        securityPanel.setBorder(BorderFactory.createTitledBorder("Security Scripting Options"));
        GridBagLayout securityBag = new GridBagLayout();
        securityPanel.setLayout(securityBag);
        c.gridx = 0;
        c.gridy = 0;
        gridBag.setConstraints(securityPanel,c);
        panel.add(securityPanel);

		  
		  _scriptDatabaseCheckbox = new JCheckBox(s_stringMgr.getString("mssql.scriptDatabase"));
        c.gridx = 0;
        c.gridy = 0;
        securityBag.setConstraints(_scriptDatabaseCheckbox, c);
        securityPanel.add(_scriptDatabaseCheckbox);

		  
		  _scriptUsersAndRolesCheckbox = new JCheckBox(s_stringMgr.getString("mssql.scriptUsers"));
        c.gridx = 0;
        c.gridy = 1;
        securityBag.setConstraints(_scriptUsersAndRolesCheckbox, c);
        securityPanel.add(_scriptUsersAndRolesCheckbox);

		  
		  _scriptLoginsCheckbox = new JCheckBox(s_stringMgr.getString("mssql.scriptLogins"));
        c.gridx = 0;
        c.gridy = 2;
        _scriptLoginsCheckbox.setEnabled(false);
        securityBag.setConstraints(_scriptLoginsCheckbox, c);
        securityPanel.add(_scriptLoginsCheckbox);

		  
		  _scriptPermissionsCheckbox = new JCheckBox(s_stringMgr.getString("mssql.scriptPermission"));
        c.gridx = 0;
        c.gridy = 3;
        securityBag.setConstraints(_scriptPermissionsCheckbox, c);
        securityPanel.add(_scriptPermissionsCheckbox);
        
        
        
        JPanel tablePanel = new JPanel();
		  
		  tablePanel.setBorder(BorderFactory.createTitledBorder(s_stringMgr.getString("mssql.scriptingOptions")));
        GridBagLayout tableBag = new GridBagLayout();
        tablePanel.setLayout(tableBag);
        c.gridx = 0;
        c.gridy = 1;
        gridBag.setConstraints(tablePanel,c);
        panel.add(tablePanel);

		  
		  _scriptIndexesCheckbox = new JCheckBox(s_stringMgr.getString("mssql.scriptIndexses"));
        c.gridx = 0;
        c.gridy = 0;
        tableBag.setConstraints(_scriptIndexesCheckbox, c);
        tablePanel.add(_scriptIndexesCheckbox);

		  
		  _scriptFTIndexesCheckbox = new JCheckBox(s_stringMgr.getString("mssql.scriptFullText"));
        c.gridx = 0;
        c.gridy = 1;
        _scriptFTIndexesCheckbox.setEnabled(false);
        tableBag.setConstraints(_scriptFTIndexesCheckbox, c);
        tablePanel.add(_scriptFTIndexesCheckbox);
        
        _scriptTriggersCheckbox = new JCheckBox("Script triggers");
        c.gridx = 0;
        c.gridy = 2;
        tableBag.setConstraints(_scriptTriggersCheckbox, c);
        tablePanel.add(_scriptTriggersCheckbox);

		  
		  _scriptConstraintsCheckbox = new JCheckBox(s_stringMgr.getString("mssql.scriptPrimary"));
        c.gridx = 0;
        c.gridy = 3;
        tableBag.setConstraints(_scriptConstraintsCheckbox, c);
        tablePanel.add(_scriptConstraintsCheckbox);
        
        
        
        JPanel fileOptionsPanel = new JPanel();
		  
		  fileOptionsPanel.setBorder(BorderFactory.createTitledBorder(s_stringMgr.getString("mssql.fileOptions")));
        c.gridx = 0;
        c.gridy = 2;
        gridBag.setConstraints(fileOptionsPanel,c);
        panel.add(fileOptionsPanel);
        
        JPanel fileFormatPanel = new JPanel();
		  
		  fileFormatPanel.setBorder(BorderFactory.createTitledBorder(s_stringMgr.getString("mssql.fileFormat")));
        fileOptionsPanel.add(fileFormatPanel);
        
        ButtonGroup fileFormatGroup = new ButtonGroup();
		  
		  _OEMRadio = new JRadioButton(s_stringMgr.getString("mssql.msdos"));
		  
		  _ANSIRadio = new JRadioButton(s_stringMgr.getString("mssql.windows"));
		  
		  _UnicodeRadio = new JRadioButton(s_stringMgr.getString("mssql.unicode"),true);
        fileFormatGroup.add(_OEMRadio);
        fileFormatGroup.add(_ANSIRadio);
        fileFormatGroup.add(_UnicodeRadio);
        fileFormatPanel.add(_OEMRadio);
        fileFormatPanel.add(_ANSIRadio);
        fileFormatPanel.add(_UnicodeRadio);
        _OEMRadio.setEnabled(false);
        _ANSIRadio.setEnabled(false);
        
        JPanel generatePanel = new JPanel();
		  
		  generatePanel.setBorder(BorderFactory.createTitledBorder(s_stringMgr.getString("mssql.filesToGenerate")));
        fileOptionsPanel.add(generatePanel);
        
        ButtonGroup generateGroup = new ButtonGroup();
		  
		  _oneFileRadio = new JRadioButton(s_stringMgr.getString("mssql.oneFile"),true);
		  
		  _separateFilesRadio = new JRadioButton(s_stringMgr.getString("mssql.oneFilePerObject"));
        generateGroup.add(_oneFileRadio);
        generateGroup.add(_separateFilesRadio);
        generatePanel.add(_oneFileRadio);
        generatePanel.add(_separateFilesRadio);
        
        
        return panel;
    }

	private JPanel buildToolBar() {
		final ButtonBarBuilder builder = new ButtonBarBuilder();
		builder.addGlue();
        final GenerateSqlDialog dlg = this;

		  
		  JButton okButton = new JButton(s_stringMgr.getString("mssql.ok"));
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               _wasCompleted = true;
               dlg.setVisible(false);
            }
        });
		builder.addGridded(okButton);
        
		builder.addRelatedGap();

		 
		  JButton cancelButton = new JButton(s_stringMgr.getString("mssql.cancel"));
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                _wasCompleted = false;
                dlg.setVisible(false);
            }
        });
        builder.addGridded(cancelButton);

		return builder.getPanel();
	}

	private static Frame ctorHelper(ISession session, MssqlPlugin plugin, IDatabaseObjectInfo[] dbObjs)	{
		if (session == null)
			throw new IllegalArgumentException("ISession == null");
		if (plugin == null)
			throw new IllegalArgumentException("MssqlPlugin == null");
		if (dbObjs == null)
			throw new IllegalArgumentException("IDatabaseObjectInfo[] is null");

		return session.getApplication().getMainFrame();
	}
    
    public ArrayList<IDatabaseObjectInfo> getSelectedItems() {
        return ((DatabaseObjectInfoTableModel) (_selectedTable.getModel())).getContents();
    }
    
    public boolean getGenerateCreate() {
        return _generateCreateCheckbox.isSelected();
    }
    
    public boolean getGenerateDrop() {
        return _generateDropCheckbox.isSelected();
    }
    
    public boolean getGenerateDependent() {
        return _generateDependentCheckbox.isSelected();
    }
    
    public boolean getIncludeHeaders() {
        return _includeHeadersCheckbox.isSelected();
    }
    
    public boolean getExtendedProps() {
        return _extendedPropsCheckbox.isSelected();
    }
    
    public boolean getOnlySeven() {
        return _onlySevenCheckbox.isSelected();
    }
    
    public boolean getScriptDatabase() {
        return _scriptDatabaseCheckbox.isSelected();
    }
    
    public boolean getScriptUsersAndRoles() {
		return _scriptUsersAndRolesCheckbox.isSelected();
    }

    public boolean getScriptLogins() {
		return _scriptLoginsCheckbox.isSelected();
    }

    public boolean getScriptPermissions() {
        return _scriptPermissionsCheckbox.isSelected();
    }
    
    public boolean getScriptIndexes() {
        return _scriptIndexesCheckbox.isSelected();
    }
    
    public boolean getScriptFTIndexes() {
        return _scriptFTIndexesCheckbox.isSelected();
    }
    
    public boolean getScriptTriggers() {
        return _scriptTriggersCheckbox.isSelected();
    }
    
    public boolean getScriptConstraints() {
        return _scriptConstraintsCheckbox.isSelected();
    }
    
    public boolean getOEM() {
        return _OEMRadio.isSelected();
    }
    
    public boolean getANSI() {
        return _ANSIRadio.isSelected();
    }
    
    public boolean getUnicode() {
		return _UnicodeRadio.isSelected();
    }

    public boolean getOneFile() {
        return _oneFileRadio.isSelected();
    }
    
    public boolean showGeneralSqlDialog() {
        this.setVisible(true);
        return _wasCompleted;
    }
    
    public void preselectObjects(IDatabaseObjectInfo[] objs) {
        
        
        DatabaseObjectInfoTableModel fromModel = (DatabaseObjectInfoTableModel) _availableTable.getModel();
        DatabaseObjectInfoTableModel toModel = (DatabaseObjectInfoTableModel) _selectedTable.getModel();
        
        for (int i = 0; i < objs.length; i++) {
            if (fromModel.removeElement(objs[i]))
                toModel.addElement(objs[i]);
        }
    }
}
