package net.sourceforge.squirrel_sql.plugins.dataimport.gui;


import java.util.Vector;

import javax.swing.table.AbstractTableModel;

import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;


public class ColumnMappingTableModel extends AbstractTableModel {
	private static final long serialVersionUID = -7080889957246771971L;

	private static final StringManager stringMgr =
		StringManagerFactory.getStringManager(ColumnMappingTableModel.class);

	private TableColumnInfo[] columns = null;
	private Vector<String> mapping = new Vector<String>();
	private Vector<String> defaults = new Vector<String>();

	
	public ColumnMappingTableModel(TableColumnInfo[] columns) {
		this.columns = columns;
		for (int i = 0; i < columns.length; i++) {
			mapping.add(SpecialColumnMapping.SKIP.getVisibleString());
			defaults.add("");
		}
	}

	
	public int getColumnCount() {
		return 3;
	}

	
	public int getRowCount() {
		return columns.length;
	}

	
	public Object getValueAt(int rowIndex, int columnIndex) {
		if (columnIndex == ColumnMappingConstants.INDEX_TABLE_COLUMN) {
			return columns[rowIndex].getColumnName();
		} else if (columnIndex == ColumnMappingConstants.INDEX_IMPORTFILE_COLUMN) {
			return mapping.get(rowIndex);
		} else if (columnIndex == ColumnMappingConstants.INDEX_FIXEDVALUE_COLUMN) {
			return defaults.get(rowIndex);
		}
		return null;
	}
	
	
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		if (columnIndex == ColumnMappingConstants.INDEX_IMPORTFILE_COLUMN || 
				columnIndex == ColumnMappingConstants.INDEX_FIXEDVALUE_COLUMN) {
			return true;
		}
		return false;
	}

	
	@Override
	public void setValueAt(Object value, int row, int col) {
		if (col == ColumnMappingConstants.INDEX_IMPORTFILE_COLUMN) {
			mapping.set(row, value.toString());
		} else if (col == ColumnMappingConstants.INDEX_FIXEDVALUE_COLUMN) {
			defaults.set(row, value.toString());
		}
		fireTableCellUpdated(row, col);
	}	
	
	
	@Override
	public String getColumnName(int column) {
		if (column == ColumnMappingConstants.INDEX_TABLE_COLUMN) {
			
			return stringMgr.getString("ImportFileDialog.tableColumn");
		} else if (column == ColumnMappingConstants.INDEX_IMPORTFILE_COLUMN) {
			
			return stringMgr.getString("ImportFileDialog.importFileColumn");
		} else if (column == ColumnMappingConstants.INDEX_FIXEDVALUE_COLUMN) {
			
			return stringMgr.getString("ImportFileDialog.fixedValue");
		}
		return null;
	}

	
	public int findTableColumn(String columnName) {
		int i = 0;
		for (i = 0; i < columns.length; i++) {
			if (columnName.equals(columns[i].getColumnName()))
				return i;
		}
		return -1;
	}
	
	
	public void resetMappings() {
		mapping.clear();
		defaults.clear();
		for (int i = 0; i < columns.length; i++) {
			mapping.add(SpecialColumnMapping.SKIP.getVisibleString());
			defaults.add("");
		}
		fireTableDataChanged();
	}
}