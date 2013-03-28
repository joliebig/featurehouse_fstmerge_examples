
package net.sourceforge.squirrel_sql.plugins.refactoring.gui;

import java.util.Vector;

import javax.swing.table.AbstractTableModel;

import net.sourceforge.squirrel_sql.plugins.refactoring.gui.AddIndexDialog.i18n;

class AddIndexColumnTableModel extends AbstractTableModel
{
	private static final long serialVersionUID = -3437369044423909295L;

	private final Vector<String> rowData = new Vector<String>();

	private final String[] columnNames = new String[] { i18n.COLUMNS_COLUMN_HEADER };

	public String getColumnName(int col)
	{
		return columnNames[col];
	}

	public int getRowCount()
	{
		return rowData.size();
	}

	public int getColumnCount()
	{
		return columnNames.length;
	}

	public Object getValueAt(int row, int col)
	{
		if (col >= columnNames.length) {
			throw new IndexOutOfBoundsException("Column index parameter was too large: "+col);
		}
		String result = rowData.get(row);
		return result;
	}

	public boolean isCellEditable(int row, int col)
	{
		return false;
	}

	public void addColumn(String column)
	{
		rowData.add(column);
		fireTableDataChanged();
	}

	public String deleteRow(int row)
	{
		String removedRow = rowData.remove(row);
		fireTableDataChanged();
		return removedRow;
	}

	public Vector<String> getRowData()
	{
		return rowData;
	}
}