package net.sourceforge.squirrel_sql.fw.datasetviewer;


import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Component;
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Point;
import java.awt.Dimension;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;

import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.CellComponentFactory;
import net.sourceforge.squirrel_sql.fw.sql.JDBCTypeMapper;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;


public class RowDataInputFrame extends JDialog
	implements ActionListener {

	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(RowDataInputFrame.class);

	
	DataSetViewerEditableTablePanel _caller;

	
	RowDataJTable table;

	
	public RowDataInputFrame(JFrame parent, ColumnDisplayDefinition[] colDefs,
									 Object[] initialValues,
									 DataSetViewerEditableTablePanel caller) {

		
		super(parent, s_stringMgr.getString("rowDataInputFrame.propName"), false);

		
		Container pane = getContentPane();

		
		_caller = caller;

		
		pane.setLayout(new BorderLayout());

		
		table = new RowDataJTable(colDefs, initialValues);
		
		
		Dimension tableDim = table.getPreferredSize();
		tableDim.setSize(tableDim.getWidth(), tableDim.getHeight() + 15);
		table.setPreferredScrollableViewportSize(tableDim);
		JScrollPane scrollPane = new JScrollPane(table);

		
		JPanel rowHeaderPanel = new JPanel();
		rowHeaderPanel.setLayout(new BorderLayout());
		
		JTextArea r1 = new JTextArea(s_stringMgr.getString("rowDataInputFrame.data"), 1, 10);
		r1.setBackground(Color.lightGray);
		r1.setBorder(BorderFactory.createLineBorder(Color.black));
		r1.setEditable(false);
		rowHeaderPanel.add(r1, BorderLayout.NORTH);
		
		JTextArea r2 = new JTextArea(s_stringMgr.getString("rowDataInputFrame.colDescription"), 4, 10);
		r2.setBackground(Color.lightGray);
		r2.setBorder(BorderFactory.createLineBorder(Color.black));
		r2.setEditable(false);
		rowHeaderPanel.add(r2, BorderLayout.CENTER);
		scrollPane.setRowHeaderView(rowHeaderPanel);

		pane.add(scrollPane, BorderLayout.NORTH);

		
		JPanel buttonPanel = new JPanel();


		
		JButton insertButton = new JButton(s_stringMgr.getString("rowDataInputFrame.insert"));
		buttonPanel.add(insertButton);
		insertButton.setActionCommand("insert");
		insertButton.addActionListener(this);

		
		JButton cancelButton = new JButton(s_stringMgr.getString("rowDataInputFrame.cancel"));
		buttonPanel.add(cancelButton);
		cancelButton.setActionCommand("cancel");
		cancelButton.addActionListener(this);

		pane.add(buttonPanel, BorderLayout.SOUTH);

		
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		
		pack();
		setVisible(true);

	}

	
	public void actionPerformed(ActionEvent e) {

		if (e.getActionCommand().equals("cancel")) {
			setVisible(false);
			dispose();
			return;
		}
		else if ( ! e.getActionCommand().equals("insert")) {
			return;	
		}

		
		
		

		
		
		
		if (table.isEditing()) {
			int col = table.getEditingColumn();
			table.getCellEditor(0, col).stopCellEditing();
		}

		Object[] rowData = new Object[table.getModel().getColumnCount()];
		for (int i=0; i< table.getModel().getColumnCount(); i++) {
			rowData[i] = table.getValueAt(0, i);
		}

		
		
		
		
		if (_caller.insertRow(rowData) == null) {
			
			setVisible(false);
			dispose();
		}
	}

	
	class RowDataJTable extends JTable {

		private ColumnDisplayDefinition[] _colDefs = null;

		
		protected  RowDataJTable(ColumnDisplayDefinition[] colDefs, Object[] initalValues) {

			super();
			setModel(new RowDataModel(colDefs, initalValues));

			

			final String data = "THE QUICK BROWN FOX JUMPED OVER THE LAZY DOG";
			final int _multiplier =
				getFontMetrics(getFont()).stringWidth(data) / data.length();

			TableColumnModel cm = new DefaultTableColumnModel();
			for (int i = 0; i < colDefs.length; ++i)
			{
				ColumnDisplayDefinition colDef = colDefs[i];
				int colWidth = colDef.getDisplayWidth() * _multiplier;
				if (colWidth > IDataSetViewer.MAX_COLUMN_WIDTH * _multiplier)
				{
					colWidth = IDataSetViewer.MAX_COLUMN_WIDTH * _multiplier;
				}

				TableColumn col = new TableColumn(i, colWidth,
					CellComponentFactory.getTableCellRenderer(colDefs[i]), null);
				col.setHeaderValue(colDef.getLabel());
				cm.addColumn(col);
			}

			setColumnModel(cm);

			_colDefs = colDefs;

			
			for (int i=0; i< colDefs.length; i++) {
				cm.getColumn(i).setCellEditor(
					CellComponentFactory.getInCellEditor(this, _colDefs[i]));
			}


			
			
			setRowHeight(1, 80);

			setRowSelectionAllowed(false);
			setColumnSelectionAllowed(false);
			setCellSelectionEnabled(true);
			getTableHeader().setResizingAllowed(true);
			getTableHeader().setReorderingAllowed(true);
			setAutoCreateColumnsFromModel(false);
			setAutoResizeMode(JTable.AUTO_RESIZE_OFF);



			
			MouseAdapter m = new MouseAdapter()
			{
				public void mousePressed(MouseEvent evt)
				{
					if (evt.isPopupTrigger())
					{
						
						
					}
					else if (evt.getClickCount() == 2)
					{
						
						

						Point pt = evt.getPoint();
						int col = RowDataJTable.this.columnAtPoint(pt);
						CellDataPopup.showDialog(RowDataJTable.this, _colDefs[col], evt, true);
					}
				}
				public void mouseReleased(MouseEvent evt)
				{
					if (evt.isPopupTrigger())
					{
						
						
					}
				}
			};
			addMouseListener(m);
		}

		public boolean isCellEditable(int row, int col) {
			if (row > 0)
				return false;	
			return CellComponentFactory.isEditableInCell(_colDefs[col], getValueAt(row,col));
		}

		public TableCellRenderer getCellRenderer(int row, int column) {
			if (row == 0)
				return CellComponentFactory.getTableCellRenderer(_colDefs[column]);
			
			return new RowDataDescriptionRenderer();
		}

		
		public void setValueAt(Object newValueString, int row, int col)
		{
			if (! (newValueString instanceof java.lang.String))
			{
				
				super.setValueAt(newValueString, row, col);
				return;
			}

			
			StringBuffer messageBuffer = new StringBuffer();
			ColumnDisplayDefinition colDef = _colDefs[col];
			Object newValueObject = CellComponentFactory.validateAndConvert(
				colDef, getValueAt(row, col), (String) newValueString, messageBuffer);
			if (messageBuffer.length() > 0)
			{
				

				
				String msg = s_stringMgr.getString("rowInputDataFrame.conversionToInternErr", messageBuffer);
				JOptionPane.showMessageDialog(this,
					msg,
					
					s_stringMgr.getString("rowInputDataFrame.conversionErr"),
					JOptionPane.ERROR_MESSAGE);
			}
			else
			{
				
				super.setValueAt(newValueObject, row, col);
			}
		}


	}

	
	class RowDataModel extends DefaultTableModel {

		
		protected RowDataModel(ColumnDisplayDefinition[] colDefs, Object[] initalValues) {
			super();

			
			String[] colNames = new String[colDefs.length];
			Object[][] rowData = new Object[2][colDefs.length];
			for (int i=0; i<colDefs.length; i++) {
				colNames[i] = colDefs[i].getLabel();	
				rowData[0][i] = initalValues[i];	

				
				rowData[1][i] = getColumnDescription(colDefs[i]);
                    




			}

			
			setDataVector(rowData, colNames);
		}
        
        
        private String getColumnDescription(ColumnDisplayDefinition def) {
            StringBuilder result = new StringBuilder();
            result.append(def.getSqlTypeName());
            result.append("\n");
            if (def.isNullable()) {
                result.append("nullable");
            } else {
                result.append("not nullable");
            }
            result.append("\n");
            if (JDBCTypeMapper.isNumberType(def.getSqlType())) {
                result.append("prec=");
                result.append(def.getPrecision());
                result.append("\n");
                result.append("scale=");
                result.append(def.getScale());
                if (def.isAutoIncrement()) {
                    result.append("\n");
                    result.append("(auto-incr)");
                }
            } else {
                result.append("length=");
                result.append(def.getColumnSize());
            }
            return result.toString();
        }
	}

	
	class RowDataDescriptionRenderer implements TableCellRenderer {

		public Component getTableCellRendererComponent(JTable table, 
                                                       Object value, 
                                                       boolean isSelected, 
                                                       boolean hasFocus, 
                                                       int row, 
                                                       int column) {

				JTextArea ta = new JTextArea((String)value, 8, 20);
				ta.setBackground(Color.lightGray);
				return ta;
			}
	}

}
