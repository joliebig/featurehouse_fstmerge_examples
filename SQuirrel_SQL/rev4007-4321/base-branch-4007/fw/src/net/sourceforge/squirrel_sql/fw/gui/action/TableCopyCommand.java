package net.sourceforge.squirrel_sql.fw.gui.action;

import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import net.sourceforge.squirrel_sql.fw.util.ICommand;
import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.SquirrelTableCellRenderer;

public class TableCopyCommand implements ICommand
{
	



	private final static String NULL_CELL = "<null>";

	private JTable _table;
	private boolean _withHeaders;

	public TableCopyCommand(JTable table, boolean withHeaders)
	{
		super();
		if (table == null)
		{
			throw new IllegalArgumentException("JTable == null");
		}
		_table = table;
		_withHeaders = withHeaders;
	}

	public void execute()
	{
		int nbrSelRows = _table.getSelectedRowCount();
		int nbrSelCols = _table.getSelectedColumnCount();
		int[] selRows = _table.getSelectedRows();
		int[] selCols = _table.getSelectedColumns();
		if (selRows.length != 0 && selCols.length != 0)
		{
			StringBuffer buf = new StringBuffer();
			
			if (_withHeaders)
			{
				for (int colIdx = 0; colIdx < nbrSelCols; ++colIdx)
				{
					buf.append(_table.getColumnName(selCols[colIdx]));
					if (colIdx < nbrSelCols - 1)
					{
						buf.append('\t');
					}
				}
				buf.append('\n');
			}
			for (int rowIdx = 0; rowIdx < nbrSelRows; ++rowIdx)
			{
				for (int colIdx = 0; colIdx < nbrSelCols; ++colIdx)
				{
					TableCellRenderer cellRenderer = _table.getCellRenderer(selRows[rowIdx], selCols[colIdx]);
					Object cellObj = _table.getValueAt(selRows[rowIdx], selCols[colIdx]);

					if(cellRenderer instanceof SquirrelTableCellRenderer)
					{
						cellObj = ((SquirrelTableCellRenderer)cellRenderer).renderValue(cellObj);
					}


					buf.append(cellObj != null ? cellObj : NULL_CELL);
					if (nbrSelCols > 1 && colIdx < nbrSelCols - 1)
					{
						buf.append('\t');
					}
				}
				if (nbrSelRows > 1)
				{
					buf.append('\n');
				}
			}
			StringSelection ss = new StringSelection(buf.toString());
			Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, ss);
		}
	}
}
