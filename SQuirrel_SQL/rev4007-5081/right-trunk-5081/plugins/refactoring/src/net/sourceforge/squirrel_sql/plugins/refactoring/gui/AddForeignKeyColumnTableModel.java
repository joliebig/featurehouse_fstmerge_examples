
package net.sourceforge.squirrel_sql.plugins.refactoring.gui;

import java.util.Vector;

import javax.swing.table.AbstractTableModel;

import net.sourceforge.squirrel_sql.plugins.refactoring.gui.AddForeignKeyDialog.i18n;

public class AddForeignKeyColumnTableModel extends AbstractTableModel
{
	private static final long serialVersionUID = -1809722908124102411L;

	private final Vector<String[]> rowData = new Vector<String[]>();

	private final String[] columnNames =
		new String[] { i18n.COLUMNS_LOCAL_COLUMN_HEADER, i18n.COLUMNS_REFERENCED_HEADER };

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
		return rowData.get(row)[col];
	}

	public boolean isCellEditable(int row, int col)
	{
		return false;
	}

	public void addColumn(String[] column)
	{
		rowData.add(column);
		fireTableDataChanged();
	}

	public String[] deleteRow(int row)
	{
		String[] removedRow = rowData.remove(row);
		fireTableDataChanged();
		return removedRow;
	}

	public Vector<String[]> getRowData()
	{
		return rowData;
	}
}