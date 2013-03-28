package net.sourceforge.squirrel_sql.fw.sql;

import java.lang.reflect.Method;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetDefinition;
import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSet;
import net.sourceforge.squirrel_sql.fw.util.IMessageHandler;
import net.sourceforge.squirrel_sql.fw.util.NullMessageHandler;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

public class MetaDataDataSet implements IDataSet
{
	
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(MetaDataDataSet.class);

	private final static Map<String, Object> s_ignoreMethods = 
        new HashMap<String, Object>();
	static
	{
		s_ignoreMethods.put("getCatalogs", null);
		s_ignoreMethods.put("getConnection", null);
		s_ignoreMethods.put("getSchemas", null);
		s_ignoreMethods.put("getTableTypes", null);
		s_ignoreMethods.put("getTypeInfo", null);
		s_ignoreMethods.put("fail", null);
		s_ignoreMethods.put("hashCode", null);
		s_ignoreMethods.put("toString", null);
		s_ignoreMethods.put("getNumericFunctions", null);
		s_ignoreMethods.put("getStringFunctions", null);
		s_ignoreMethods.put("getSystemFunctions", null);
		s_ignoreMethods.put("getTimeDateFunctions", null);
		s_ignoreMethods.put("getSQLKeywords", null);
	}

	private static interface IStrings
	{
		String UNSUPPORTED = s_stringMgr.getString("MetaDataDataSet.unsupported");
		String NAME_COLUMN = s_stringMgr.getString("MetaDataDataSet.propname");
		String VALUE_COLUMN = s_stringMgr.getString("MetaDataDataSet.value");
	}

	private final static String[] s_hdgs =
		new String[] { IStrings.NAME_COLUMN, IStrings.VALUE_COLUMN };
	private DataSetDefinition _dsDef;

	private Iterator<Object[]> _rowsIter;
	private Object[] _row;

	private IMessageHandler _msgHandler;

	
	private List<Object[]> _data = new ArrayList<Object[]>();

	public MetaDataDataSet(DatabaseMetaData md)
	{
		this(md, null);
	}

	public MetaDataDataSet(DatabaseMetaData md, IMessageHandler msgHandler)
	{
		super();
		_msgHandler =
			msgHandler != null ? msgHandler : NullMessageHandler.getInstance();
		_dsDef = new DataSetDefinition(createColumnDefinitions());
		load(md);
	}

	public final int getColumnCount()
	{
		return s_hdgs.length;
	}

	public DataSetDefinition getDataSetDefinition()
	{
		return _dsDef;
	}

	public synchronized boolean next(IMessageHandler msgHandler)
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
		final int columnCount = getColumnCount();
		ColumnDisplayDefinition[] columnDefs =
			new ColumnDisplayDefinition[columnCount];
		for (int i = 0; i < columnCount; ++i)
		{
			columnDefs[i] = new ColumnDisplayDefinition(200, s_hdgs[i]);
		}
		return columnDefs;
	}

	private void load(DatabaseMetaData md)
	{
		Method[] methods = DatabaseMetaData.class.getMethods();
		for (int i = 0; i < methods.length; ++i)
		{
			final Method method = methods[i];
			if (method.getParameterTypes().length == 0
				&& method.getReturnType() != Void.TYPE
				&& !s_ignoreMethods.containsKey(method.getName()))
			{
				_data.add(generateLine(md, method));
			}
		}

		


		_rowsIter = _data.iterator();
	}

	
	private Object[] generateLine(DatabaseMetaData md, Method getter)
	{
		final Object[] line = new Object[2];
		line[0] = getter.getName();
		if (line[0].equals("getDefaultTransactionIsolation"))
		{
			try
			{
				line[1] = IStrings.UNSUPPORTED;
				final int isol = md.getDefaultTransactionIsolation();
				switch (isol)
				{
					case java.sql.Connection.TRANSACTION_NONE :
						{
							line[1] = "TRANSACTION_NONE";
							break;
						}
					case java.sql.Connection.TRANSACTION_READ_COMMITTED :
						{
							line[1] = "TRANSACTION_READ_COMMITTED";
							break;
						}
					case java.sql.Connection.TRANSACTION_READ_UNCOMMITTED :
						{
							line[1] = "TRANSACTION_READ_UNCOMMITTED";
							break;
						}
					case java.sql.Connection.TRANSACTION_REPEATABLE_READ :
						{
							line[1] = "TRANSACTION_REPEATABLE_READ";
							break;
						}
					case java.sql.Connection.TRANSACTION_SERIALIZABLE :
						{
							line[1] = "TRANSACTION_SERIALIZABLE";
							break;
						}
					default :
						{
							line[1] = "" + isol + "?";
							break;
						}
				}
			}
			catch (SQLException ex)
			{
				_msgHandler.showMessage(ex, null);
			}

		}
		else
		{
			Object obj = executeGetter(md, getter);
			line[1] = obj;
		}
		return line;
	}

	protected Object executeGetter(Object bean, Method getter)
	{
		try
		{
			return getter.invoke(bean, (Object[])null);
		}
		catch (Throwable th)
		{
			return IStrings.UNSUPPORTED;
		}
	}
}
