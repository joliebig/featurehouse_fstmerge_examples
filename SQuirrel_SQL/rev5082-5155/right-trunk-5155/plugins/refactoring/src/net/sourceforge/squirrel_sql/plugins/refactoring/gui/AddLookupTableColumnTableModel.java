
package net.sourceforge.squirrel_sql.plugins.refactoring.gui;

import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

import net.sourceforge.squirrel_sql.plugins.refactoring.gui.AddLookupTableDialog.i18n;

class AddLookupTableColumnTableModel extends AbstractTableModel
{
	private static final long serialVersionUID = 2291876910684420431L;

	private final ArrayList<String[]> _rowData = new ArrayList<String[]>();

	private final String[] _columnNames =
		{ i18n.LOOKUP_COLUMNSTABLE_HEADER1, i18n.LOOKUP_COLUMNSTABLE_HEADER2 };

	public String getColumnName(int col)
	{
		return _columnNames[col];
	}

	public int getRowCount()
	{
		return _rowData.size();
	}

	public int getColumnCount()
	{
		return _columnNames.length;
	}

	public Object getValueAt(int row, int col)
	{
		return _rowData.get(row)[col];
	}

	public boolean isCellEditable(int row, int col)
	{
		return col == 0;
	}

	public void setValueAt(Object aValue, int rowIndex, int columnIndex)
	{
		_rowData.get(rowIndex)[columnIndex] = (String) aValue;
	}

	public void addRow(String[] rowData)
	{
		_rowData.add(rowData);
		fireTableDataChanged();
	}

	public String[] deleteRow(int row)
	{
		String[] removedRow = _rowData.remove(row);
		fireTableDataChanged();
		return removedRow;
	}

	public ArrayList<String[]> getData()
	{
		return _rowData;
	}

	public void clear()
	{
		_rowData.clear();
	}
}