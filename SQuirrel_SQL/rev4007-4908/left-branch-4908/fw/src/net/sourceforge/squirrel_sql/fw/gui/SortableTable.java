package net.sourceforge.squirrel_sql.fw.gui;

import javax.swing.JTable;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

public class SortableTable extends JTable
{
	public SortableTable(TableModel model)
	{
		super(new SortableTableModel(model));
		init();
	}

	public SortableTable(TableModel model, TableColumnModel colModel)
	{
		super(new SortableTableModel(model), colModel);
		init();
	}

	public SortableTable(SortableTableModel model)
	{
		super(model);
		init();
	}

	public SortableTable(SortableTableModel model, TableColumnModel colModel)
	{
		super(model, colModel);
		init();
	}

	public SortableTableModel getSortableTableModel()
	{
		return (SortableTableModel)getModel();
	}

	public void setModel(TableModel model)
	{
		super.setModel(new SortableTableModel(model));
	}

	public void setSortableTableModel(SortableTableModel model)
	{
		super.setModel(model);
	}

	private void init()
	{
		setTableHeader(new ButtonTableHeader());
	}
}

