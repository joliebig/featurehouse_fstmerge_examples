package net.sourceforge.squirrel_sql.fw.sql;

import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetDefinition;
import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSet;
import net.sourceforge.squirrel_sql.fw.util.IMessageHandler;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

public class TableInfoDataSet implements IDataSet
{
	
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(TableInfoDataSet.class);

	private final static String[] s_hdgs =
		new String[]
		{
			s_stringMgr.getString("TableInfoDataSet.property"),
			s_stringMgr.getString("TableInfoDataSet.value"),
		};

	private DataSetDefinition _dsDef;

	private int _curRow = -1;

	private String[][] _data = new String[][]
	{
		{
			s_stringMgr.getString("TableInfoDataSet.rowheading.name"), null
		},
		{
			s_stringMgr.getString("TableInfoDataSet.rowheading.qualname"), null
		},
		{
			s_stringMgr.getString("TableInfoDataSet.rowheading.catalog"), null
		},
		{
			s_stringMgr.getString("TableInfoDataSet.rowheading.schema"), null
		},
		{
			s_stringMgr.getString("TableInfoDataSet.rowheading.type"), null
		},
		{
			s_stringMgr.getString("TableInfoDataSet.rowheading.remarks"), null
		},
	};

	
	public TableInfoDataSet()
	{
		this(null);
	}

	
	public TableInfoDataSet(ITableInfo ti)
	{
		super();
		_dsDef = new DataSetDefinition(createColumnDefinitions());
		setTableInfo(ti);
	}

	public final int getColumnCount()
	{
		return s_hdgs.length;
	}

	public DataSetDefinition getDataSetDefinition()
	{
		return _dsDef;
	}

	public synchronized void setTableInfo(ITableInfo ti)
	{
		if (ti != null)
		{
			load(ti);
		}
		else
		{
			for (int i = 0; i < _data.length; ++i)
			{
				_data[i][1] = "";
			}
		}
	}

	public synchronized boolean next(IMessageHandler msgHandler)
	{
		if (_curRow >= _data.length - 1)
		{
			return false;
		}
		++_curRow;
		return true;
	}

	public synchronized Object get(int columnIndex)
	{
		return _data[_curRow][columnIndex];
	}

	private ColumnDisplayDefinition[] createColumnDefinitions()
	{
		final int columnCount = getColumnCount();
		ColumnDisplayDefinition[] columnDefs =
			new ColumnDisplayDefinition[columnCount];
		for (int i = 0; i < columnCount; ++i)
		{
			columnDefs[i] = new ColumnDisplayDefinition(100, s_hdgs[i]);
		}
		return columnDefs;
	}

	private void load(ITableInfo ti)
	{
		_data[0][1] = ti.getSimpleName();
		_data[1][1] = ti.getQualifiedName();
		_data[2][1] = ti.getCatalogName();
		_data[3][1] = ti.getSchemaName();
		_data[4][1] = ti.getType();
		_data[5][1] = ti.getRemarks();

		_curRow = -1;
	}
}
