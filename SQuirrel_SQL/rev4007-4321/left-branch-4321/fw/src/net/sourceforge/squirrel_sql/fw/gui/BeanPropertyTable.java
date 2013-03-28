package net.sourceforge.squirrel_sql.fw.gui;

import javax.swing.JTable;

import net.sourceforge.squirrel_sql.fw.util.BaseException;

public class BeanPropertyTable extends JTable
{
	private BeanPropertyTableModel _model;

	public BeanPropertyTable() throws BaseException
	{
		this(null);
	}

	public BeanPropertyTable(Object bean) throws BaseException
	{
		super(new BeanPropertyTableModel());
		_model = (BeanPropertyTableModel) getModel();
		_model.setBean(bean);
		getTableHeader().setResizingAllowed(true);
	}

	public void refresh() throws BaseException
	{
		_model.refresh();
	}

	public void setBean(Object bean) throws BaseException
	{
		_model.setBean(bean);
	}

	public void setModel(BeanPropertyTableModel model) throws BaseException
	{
		super.setModel(model);
		_model = model;
		refresh();
	}
}
