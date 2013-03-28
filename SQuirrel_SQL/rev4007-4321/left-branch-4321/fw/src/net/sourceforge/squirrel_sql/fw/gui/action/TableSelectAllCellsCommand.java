package net.sourceforge.squirrel_sql.fw.gui.action;

import javax.swing.JTable;

import net.sourceforge.squirrel_sql.fw.util.ICommand;

public class TableSelectAllCellsCommand implements ICommand
{
	private JTable _table;

	public TableSelectAllCellsCommand(JTable table)
	{
		super();
		if (table == null)
		{
			throw new IllegalArgumentException("JTable == null");
		}
		_table = table;
	}

	public void execute()
	{
		
		_table.setRowSelectionInterval(0, _table.getRowCount() - 1);
		_table.setColumnSelectionInterval(0, _table.getColumnCount() - 1);
	}
}
