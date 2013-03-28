package net.sourceforge.squirrel_sql.fw.sql;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetDefinition;
import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSet;
import net.sourceforge.squirrel_sql.fw.util.IMessageHandler;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

public class MetaDataListDataSet implements IDataSet
{
	
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(MetaDataListDataSet.class);

	private static interface IStrings
	{
		String NAME_COLUMN = s_stringMgr.getString("MetaDataListDataSet.propname");
	}

	private final static String[] s_hdgs = new String[] { IStrings.NAME_COLUMN };
	private DataSetDefinition _dsDef;
	private Iterator<String> _rowIter;
	private List<String> _row = new ArrayList<String>();
	private String _rowElem;

	public MetaDataListDataSet(String functionList)
	{
		super();
		_dsDef = new DataSetDefinition(createColumnDefinitions());
		load(functionList);
	}

	public int getColumnCount()
	{
		return s_hdgs.length;
	}

	public DataSetDefinition getDataSetDefinition()
	{
		return _dsDef;
	}

	public synchronized boolean next(IMessageHandler msgHandler)
	{
		if (_rowIter.hasNext())
		{
			_rowElem = _rowIter.next();
			return true;
		}
		return false;
	}

	public synchronized Object get(int columnIndex)
	{
		if (columnIndex == 0)
		{
			return _rowElem;
		}
		return null;
	}

	private ColumnDisplayDefinition[] createColumnDefinitions()
	{
		final int columnCount = getColumnCount();
		ColumnDisplayDefinition[] columnDefs =
			new ColumnDisplayDefinition[columnCount];
		for (int i = 0; i < columnCount; ++i)
		{
			columnDefs[i] = new ColumnDisplayDefinition(200, s_hdgs[i]);
		}
		return columnDefs;
	}

	private void load(String functionList)
	{
		if (functionList != null)
		{
			StringTokenizer st = new StringTokenizer(functionList, ",");
			while (st.hasMoreTokens())
			{
				_row.add(st.nextToken());
			}
			Collections.sort(_row);
		}
		_rowIter = _row.iterator();
	}
}
