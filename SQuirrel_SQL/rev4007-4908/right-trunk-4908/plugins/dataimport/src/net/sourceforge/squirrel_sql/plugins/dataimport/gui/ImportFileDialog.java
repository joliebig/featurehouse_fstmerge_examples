package net.sourceforge.squirrel_sql.plugins.dataimport.gui;


import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import net.sourceforge.squirrel_sql.client.gui.IOkClosePanelListener;
import net.sourceforge.squirrel_sql.client.gui.OkClosePanel;
import net.sourceforge.squirrel_sql.client.gui.OkClosePanelEvent;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.DialogWidget;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.plugins.dataimport.ImportDataIntoTableExecutor;
import static net.sourceforge.squirrel_sql.plugins.dataimport.gui.SpecialColumnMapping.*;
import net.sourceforge.squirrel_sql.plugins.dataimport.importer.IFileImporter;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Vector;


public class ImportFileDialog extends DialogWidget
{
	private static final long serialVersionUID = 3470927611018381204L;

	private static final StringManager stringMgr =
		StringManagerFactory.getStringManager(ImportFileDialog.class);
	
	private String[][] previewData = null;
	private List<String> importerColumns = new Vector<String>();
	
	private JTable previewTable = null;
	private JTable mappingTable = null;
	private JCheckBox headersIncluded = null;
	private OkClosePanel btnsPnl = new OkClosePanel();
	
	private ISession session = null;
	private IFileImporter importer = null;
	private ITableInfo table = null;
	private TableColumnInfo[] columns = null;

	
	public ImportFileDialog(ISession session, IFileImporter importer, ITableInfo table, TableColumnInfo[] columns) {
        
		super(stringMgr.getString("ImportFileDialog.fileImport"), true, session.getApplication());
		this.session = session;
		this.importer = importer;
		this.table = table;
		this.columns = columns;

		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		makeToolWindow(true);

		final JPanel content = new JPanel(new BorderLayout());
		content.add(createMainPanel(), BorderLayout.CENTER);
        setContentPane(content);
		btnsPnl.makeOKButtonDefault();
		btnsPnl.getRootPane().setDefaultButton(btnsPnl.getOKButton());
        pack();
	}
	
	private Component createMainPanel()
	{
		btnsPnl.addListener(new MyOkClosePanelListener());

		final FormLayout layout = new FormLayout(
				
				"left:pref:grow",
				
				"12dlu, 6dlu, 12dlu, 6dlu, 80dlu:grow, 6dlu, 80dlu:grow, 6dlu, pref");
		
		PanelBuilder builder = new PanelBuilder(layout);
		CellConstraints cc = new CellConstraints();
		builder.setDefaultDialogBorder();
		
		previewTable = new JTable();
		JScrollPane scrollPane = new JScrollPane(previewTable);
		
		mappingTable = new JTable(new ColumnMappingTableModel(columns));
		JScrollPane scrollPane2 = new JScrollPane(mappingTable);
		
        
		headersIncluded = new JCheckBox(stringMgr.getString("ImportFileDialog.headersIncluded"));
		headersIncluded.setSelected(true);
		headersIncluded.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				GUIUtils.processOnSwingEventThread(new Runnable() {
					public void run() {
						ImportFileDialog.this.updatePreviewData();
					}
				});
			}
		});

		int y = 1;
        
		builder.add(new JLabel(stringMgr.getString("ImportFileDialog.dataPreview")), cc.xy(1, y));
		
		y += 2;
		builder.add(headersIncluded, cc.xy(1, y));

		y += 2;
		builder.add(scrollPane, cc.xy(1, y));
		
		y += 2;
		builder.add(scrollPane2, cc.xy(1, y));

		y += 2;
		builder.add(btnsPnl, cc.xywh(1, y, 1, 1));

		return builder.getPanel();
	}
	
	
	public void setPreviewData(String[][] data) {
		previewData = data;
		updatePreviewData();
	}
	
	private void updatePreviewData() {
		JComboBox editBox = new JComboBox();
		editBox.addItem(SKIP.getVisibleString());
		editBox.addItem(FIXED_VALUE.getVisibleString());
		editBox.addItem(AUTO_INCREMENT.getVisibleString());
		editBox.addItem(NULL.getVisibleString());
		
		editBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				int selectedRow = mappingTable.getSelectedRow();
				if (selectedRow == -1) {
					return;
				}
				TableModel model = mappingTable.getModel();
				String comboValue = ((JComboBox)e.getSource()).getSelectedItem().toString();
				int fixedValueColumnIdx = 2;
				if (comboValue.equals(AUTO_INCREMENT.getVisibleString())) {
					
					
					if (model.getValueAt(selectedRow, fixedValueColumnIdx) == null || 
						 "".equals(model.getValueAt(selectedRow, fixedValueColumnIdx) )) 
					{
						model.setValueAt("0", selectedRow, fixedValueColumnIdx);
					}
					
				} else if (!comboValue.equals(FIXED_VALUE.getVisibleString())) {
					
					
					model.setValueAt("", selectedRow, fixedValueColumnIdx);
				}
				mappingTable.clearSelection();
			}
			
		});
		
		if (previewData != null && previewData.length > 0) {
			String[] headers = new String[previewData[0].length];
			String[][] data = previewData;

			if (headersIncluded.isSelected()) {
				for (int i = 0; i < headers.length; i++) {
					headers[i] = data[0][i];
				}
				data = new String[previewData.length-1][];
				for (int i = 1; i < previewData.length; i++) {
					data[i-1] = previewData[i];
				}
			} else {
				for (int i = 0; i < headers.length; i++) {
					
					headers[i] = stringMgr.getString("ImportFileDialog.column") + i;
				}
			}

			importerColumns.clear();
			for (int i = 0; i < headers.length; i++) {
				importerColumns.add(headers[i]);
			}

			for (String header : headers) {
				editBox.addItem(header);
			}
			previewTable.setModel(new DefaultTableModel(data, headers));
		}
		mappingTable.getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(editBox));
		((ColumnMappingTableModel) mappingTable.getModel()).resetMappings();
	}
	
	
	public void ok() {
		dispose();
		ImportDataIntoTableExecutor executor = new ImportDataIntoTableExecutor(session, table, columns, importerColumns, (ColumnMappingTableModel) mappingTable.getModel(), importer);
		executor.setSkipHeader(headersIncluded.isSelected());
		executor.execute();
	}
	
	private final class MyOkClosePanelListener implements IOkClosePanelListener
	{
		
		public void okPressed(OkClosePanelEvent evt)
		{
			ImportFileDialog.this.ok();
		}

		
		public void closePressed(OkClosePanelEvent evt)
		{
			ImportFileDialog.this.dispose();
		}

		
		public void cancelPressed(OkClosePanelEvent evt)
		{
			ImportFileDialog.this.dispose();
		}
	}
	
}



