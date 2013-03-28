package net.sourceforge.squirrel_sql.fw.datasetviewer;

import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sourceforge.squirrel_sql.fw.util.IMessageHandler;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

public class ResultSetMetaDataDataSet implements IDataSet
{

	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(ResultSetMetaDataDataSet.class);


	private interface i18n
	{
		String UNSUPPORTED = "<Unsupported>";
		
		String NAME_COLUMN = s_stringMgr.getString("resultSetMentaDataSet.propName");

		
		String VALUE_COLUMN = s_stringMgr.getString("resultSetMentaDataSet.val");
	}

	
	private static ILogger s_log =
		LoggerController.createLogger(ResultSetMetaDataDataSet.class);

	private DataSetDefinition _dsDef;
	private boolean[] _propertyMethodIndicators;
	private Iterator<Object[]> _rowsIter;
	private Object[] _row;

	
	private List<Object[]> _data = new ArrayList<Object[]>();

	
	private static final Map<String, Object> s_propNames = 
        new HashMap<String, Object>();

	static
	{
		s_propNames.put("getCatalogName", null);
		s_propNames.put("getColumnClassName", null);
		s_propNames.put("getColumnDisplaySize", null);
		s_propNames.put("getColumnLabel", null);
		s_propNames.put("getColumnName", null);
		s_propNames.put("getColumnType", null);
		s_propNames.put("getColumnTypeName", null);
		s_propNames.put("getPrecision", null);
		s_propNames.put("getScale", null);
		s_propNames.put("getSchemaName", null);
		s_propNames.put("getTableName", null);
		s_propNames.put("isAutoIncrement", null);
		s_propNames.put("isCaseSensitive", null);
		s_propNames.put("isCurrency", null);
		s_propNames.put("isDefinitelyWritable", null);
		s_propNames.put("isNullable", null);
		s_propNames.put("isReadOnly", null);
		s_propNames.put("isSearchable", null);
		s_propNames.put("isSigned", null);
		s_propNames.put("isWritable", null);
	}

	public ResultSetMetaDataDataSet(ResultSet rs)
		throws IllegalArgumentException, DataSetException
	{
		this(getMetaDataFromResultSet(rs));
	}

	public ResultSetMetaDataDataSet(ResultSetMetaData md)
		throws IllegalArgumentException, DataSetException
	{
		super();
		setResultSetMetaData(md);
	}

	public synchronized void setResultSetMetaData(ResultSetMetaData md)
		throws DataSetException
	{
		_dsDef = new DataSetDefinition(createColumnDefinitions());
		load(md);
	}

	public final int getColumnCount()
	{
		return _dsDef.getColumnDefinitions().length;
	}

	public DataSetDefinition getDataSetDefinition()
	{
		return _dsDef;
	}

	public synchronized boolean next(IMessageHandler msgHandler)
		throws DataSetException
	{
		if (_rowsIter.hasNext())
		{
			_row = _rowsIter.next();
		}
		else
		{
			_row = null;
		}
		return _row != null;
	}

	public synchronized Object get(int columnIndex)
	{
		return _row[columnIndex];
	}

	private ColumnDisplayDefinition[] createColumnDefinitions()
	{
		final Method[] methods = ResultSetMetaData.class.getMethods();
		_propertyMethodIndicators = new boolean[methods.length];
		final List<ColumnDisplayDefinition> colDefs = 
            new ArrayList<ColumnDisplayDefinition>();
		for (int i = 0; i < methods.length; ++i)
		{
			if (isPropertyMethod(methods[i]))
			{
				colDefs.add(new ColumnDisplayDefinition(200, methods[i].getName()));
				_propertyMethodIndicators[i] = true;
			}
			else
			{
				_propertyMethodIndicators[i] = false;
			}
		}
		return colDefs.toArray(new ColumnDisplayDefinition[colDefs.size()]);
	}

	private void load(ResultSetMetaData md) throws DataSetException
	{
		try
		{
			final Method[] methods = ResultSetMetaData.class.getMethods();
			final ArrayList<Object> line = new ArrayList<Object>();
			for (int metaIdx = 1, metaLimit = md.getColumnCount() + 1;
				metaIdx < metaLimit;
				++metaIdx)
			{
				Object[] methodParms = new Object[] { Integer.valueOf(metaIdx), };
				line.clear();
				line.ensureCapacity(methods.length);
				for (int methodIdx = 0; methodIdx < methods.length; ++methodIdx)
				{
					try
					{

						if (_propertyMethodIndicators[methodIdx])
						{
							Object obj = executeGetter(md, methods[methodIdx], methodParms);
							line.add(obj);
						}
					}
					catch (Throwable th)
					{
						line.add("<Error>");
						s_log.error("Error reading column metadata", th);
					}

				}

				_data.add(line.toArray(new Object[line.size()]));
			}

			_rowsIter = _data.iterator();
		}
		catch (SQLException ex)
		{
			s_log.error("Error occured processing result set", ex);
			throw new DataSetException(ex);
		}
	}

	
	protected boolean isPropertyMethod(Method method)
	{
		return s_propNames.containsKey(method.getName());



	}

	protected Object executeGetter(Object bean, Method getter, Object[] parms)
	{
		try
		{
			return getter.invoke(bean, parms);
		}
		catch (Throwable th)
		{
			return i18n.UNSUPPORTED;
		}
	}

	private static ResultSetMetaData getMetaDataFromResultSet(ResultSet rs)
		throws IllegalArgumentException, DataSetException
	{
		if (rs == null)
		{
			throw new IllegalArgumentException("Null ResultSet passed");
		}
		try
		{
			return rs.getMetaData();
		}
		catch (SQLException ex)
		{
			throw new DataSetException(ex);
		}
	}
}
