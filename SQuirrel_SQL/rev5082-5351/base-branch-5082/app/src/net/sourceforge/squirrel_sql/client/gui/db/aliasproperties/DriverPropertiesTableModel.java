package net.sourceforge.squirrel_sql.client.gui.db.aliasproperties;

import java.sql.DriverPropertyInfo;

import javax.swing.table.AbstractTableModel;

import net.sourceforge.squirrel_sql.fw.sql.SQLDriverProperty;
import net.sourceforge.squirrel_sql.fw.sql.SQLDriverPropertyCollection;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

class DriverPropertiesTableModel extends AbstractTableModel
{
    private static final long serialVersionUID = 1L;

    
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(DriverPropertiesTableModel.class);

	interface IColumnIndexes
	{
		int IDX_NAME = 0;
		int IDX_SPECIFY = 1;
		int IDX_VALUE = 2;
		int IDX_REQUIRED = 3;
		int IDX_DESCRIPTION = 4;
	}

	
	private static final int COLUMN_COUNT = 5;

	
	private static final ILogger s_log =
		LoggerController.createLogger(DriverPropertiesTableModel.class);

	transient private SQLDriverPropertyCollection _props = new SQLDriverPropertyCollection();

	DriverPropertiesTableModel(SQLDriverPropertyCollection props)
	{
		super();
		if (props == null)
		{
			throw new IllegalArgumentException("SQLDriverPropertyCollection[] == null");
		}

		load(props);
	}

	public Object getValueAt(int row, int col)
	{
		final SQLDriverProperty sdp = _props.getDriverProperty(row);
		switch (col)
		{
			case IColumnIndexes.IDX_NAME:
				return sdp.getName();

			case IColumnIndexes.IDX_SPECIFY:
				return Boolean.valueOf(sdp.isSpecified());

			case IColumnIndexes.IDX_VALUE:
				return sdp.getValue();

			case IColumnIndexes.IDX_REQUIRED:
			{
				
				
				DriverPropertyInfo dpi = sdp.getDriverPropertyInfo();
				if (dpi != null)
				{
					return Boolean.valueOf(dpi.required);
				}
				return Boolean.FALSE;
			}

			case IColumnIndexes.IDX_DESCRIPTION:
			{
				DriverPropertyInfo dpi = sdp.getDriverPropertyInfo();
				if (dpi != null)
				{
					return dpi.description;
				}
				return s_stringMgr.getString("DriverPropertiesTableModel.unknown");
			}

			default:
				s_log.error("Invalid column index: " + col);
				return "???????";
		}
	}

	public int getRowCount()
	{
		return _props.size();
	}

	public int getColumnCount()
	{
		return COLUMN_COUNT;
	}

	public Class<?> getColumnClass(int col)
	{
		switch (col)
		{
			case IColumnIndexes.IDX_NAME:
				return String.class;
			case IColumnIndexes.IDX_SPECIFY:
				return Boolean.class;
			case IColumnIndexes.IDX_VALUE:
				return String.class;
			case IColumnIndexes.IDX_REQUIRED:

				return Object.class;	
			case IColumnIndexes.IDX_DESCRIPTION:
				return String.class;
			default:
				s_log.error("Invalid column index: " + col);
				return Object.class;
		}
	}

	public boolean isCellEditable(int row, int col)
	{
		return col == IColumnIndexes.IDX_SPECIFY || col == IColumnIndexes.IDX_VALUE;
	}

	public void setValueAt(Object value, int row, int col)
	{
		if (col == IColumnIndexes.IDX_VALUE)
		{
			final SQLDriverProperty sdp = _props.getDriverProperty(row);
			sdp.setValue(value.toString());
		}
		else if (col == IColumnIndexes.IDX_SPECIFY)
		{
			final SQLDriverProperty sdp = _props.getDriverProperty(row);
			Boolean bool = Boolean.valueOf(value.toString());
			sdp.setIsSpecified(bool.booleanValue());
		}
		else
		{
			throw new IllegalStateException("Can only edit value/specify column. Trying to edit " + col);
		}
	}

	SQLDriverPropertyCollection getSQLDriverProperties()
	{
		return _props;
	}

	private final void load(SQLDriverPropertyCollection props)
	{
		final int origSize = getRowCount();
		if (origSize > 0)
		{
			fireTableRowsDeleted(0, origSize - 1);
		}

		_props = props;
		final int newSize = getRowCount();
		if (newSize > 0)
		{
			fireTableRowsInserted(0, newSize - 1);
		}
	}
}

