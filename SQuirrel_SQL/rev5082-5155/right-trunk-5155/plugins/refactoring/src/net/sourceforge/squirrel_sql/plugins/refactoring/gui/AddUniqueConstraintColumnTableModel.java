
package net.sourceforge.squirrel_sql.plugins.refactoring.gui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import net.sourceforge.squirrel_sql.plugins.refactoring.gui.AddUniqueConstraintDialog.i18n;

class AddUniqueConstraintColumnTableModel extends AbstractTableModel
{
	private static final long serialVersionUID = 3668028756342251497L;

	private final ArrayList<String> rowData = new ArrayList<String>();

	private final String[] columnNames = new String[] { i18n.COLUMNS_LOCAL_COLUMN_HEADER };

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
		return rowData.get(row);
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

	public List<String> getRowData()
	{
		return rowData;
	}
}