package net.sourceforge.squirrel_sql.fw.datasetviewer;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.squirrel_sql.fw.sql.JDBCTypeMapper;
import net.sourceforge.squirrel_sql.fw.sql.SQLUtilities;
import net.sourceforge.squirrel_sql.fw.util.IMessageHandler;

public class DatabaseTypesDataSet implements IDataSet
{

	private int[] _columnIndices;
	private int _columnCount;
	private DataSetDefinition _dataSetDefinition;
    private List<Object[]> _allData = new ArrayList<Object[]>();
    private int _currentRowIdx = -1;

	public DatabaseTypesDataSet(ResultSet rs) throws DataSetException
	{
		this(rs, null);
	}

	public DatabaseTypesDataSet(ResultSet rs, int[] columnIndices)
		throws DataSetException
	{
		super();

		if (columnIndices != null && columnIndices.length == 0)
		{
			columnIndices = null;
		}
		_columnIndices = columnIndices;

		if (rs != null)
		{
			try
			{
				ResultSetMetaData md = rs.getMetaData();
				_columnCount = columnIndices != null ? columnIndices.length : md.getColumnCount();
				_dataSetDefinition = new DataSetDefinition(createColumnDefinitions(md, columnIndices));
			}
			catch (SQLException ex)
			{
				throw new DataSetException(ex);
			}
		}
        setResultSet(rs);
	}
    
	public final int getColumnCount()
	{
		return _columnCount;
	}

	public DataSetDefinition getDataSetDefinition()
	{
        return _dataSetDefinition;
	}

    public synchronized boolean next(IMessageHandler msgHandler)
        throws DataSetException
    {
        if (_currentRowIdx < _allData.size() - 1) {
            _currentRowIdx++;
            return true;
        } else {
            return false;
        }
    }
    
	
	private Object[] getNextRow(ResultSet rs)
		throws SQLException
	{
        Object[] _row = new Object[_columnCount];
		for (int i = 0; i < _columnCount; ++i)
		{
			int idx = _columnIndices != null ? _columnIndices[i] : i + 1;
			switch (idx)
			{
				case 2 :
					
					
                    int data = rs.getInt(idx);
					StringBuffer buf = new StringBuffer();
					buf.append(String.valueOf(data))
						.append(" [")
						.append(JDBCTypeMapper.getJdbcTypeName(data))
						.append("]");
					_row[i] = buf.toString();
					break;

				case 3:
				case 14:
				case 15:
				case 18:
					_row[i] = rs.getObject(idx);
					if (_row[i] != null
						&& !(_row[i] instanceof Integer))
					{
						if (_row[i] instanceof Number)
						{
							_row[i] = ((Number)_row[i]).intValue();
						}
						else
						{
							_row[i] = new Integer(_row[i].toString());
						}
					}
					break;

				case 7:
					
					short nullable = rs.getShort(idx);
					switch (nullable)
					{
						case DatabaseMetaData.typeNoNulls :
							_row[i] = "false";
							break;
						case DatabaseMetaData.typeNullable :
							_row[i] = "true";
							break;
						case DatabaseMetaData.typeNullableUnknown :
							_row[i] = "unknown";
							break;
						default :
							_row[i] = nullable + "[error]";
							break;
					}
					break;

				case 8:
				case 10:
				case 11:
				case 12:
					

					_row[i] = rs.getObject(idx);
					if (_row[i] != null
						&& !(_row[i] instanceof Boolean))
					{
						if (_row[i] instanceof Number)
						{
							if (((Number) _row[i]).intValue() == 0)
							{
								_row[i] = Boolean.FALSE;
							}
							else
							{
								_row[i] = Boolean.TRUE;
							}
						}
						else
						{
							_row[i] = Boolean.valueOf(_row[i].toString());
						}
					}
					break;

				case 9:
					
					short searchable = rs.getShort(idx);
					switch (searchable)
					{
						case DatabaseMetaData.typePredNone :
							_row[i] = "no support";
							break;
						case DatabaseMetaData.typePredChar :
							_row[i] = "only supports 'WHERE...like'";
							break;
						case DatabaseMetaData.typePredBasic :
							_row[i] = "supports all except 'WHERE...LIKE'";
							break;
						case DatabaseMetaData.typeSearchable :
							_row[i] = "supports all WHERE";
							break;
						default :
							_row[i] = searchable + "[error]";
							break;
					}
					break;

				case 16:
				case 17:
					
					break;

				default :
					_row[i] = rs.getString(idx);
					break;

			}
		}
		return _row;
	}

    
	public Object get(int columnIndex)
	{
        Object[] currentRow = _allData.get(_currentRowIdx);
		return currentRow[columnIndex];
	}

	private ColumnDisplayDefinition[] createColumnDefinitions(
		ResultSetMetaData md,
		int[] columnIndices)
		throws SQLException
	{

		ColumnDisplayDefinition[] columnDefs =
			new ColumnDisplayDefinition[_columnCount];
		for (int i = 0; i < _columnCount; ++i)
		{
			int idx = columnIndices != null ? columnIndices[i] : i + 1;
			columnDefs[i] =
				new ColumnDisplayDefinition(
					md.getColumnDisplaySize(idx),
					md.getColumnLabel(idx));
		}
		return columnDefs;
	}
    
    
    private void setResultSet(ResultSet rs) 
        throws DataSetException
    {
        if (rs == null) {
            return;
        }
        try {
            while (rs.next()) {
                Object[] row = getNextRow(rs);
                _allData.add(row);
            }
        } catch (SQLException e) {
            throw new DataSetException(e);
        } finally {
            SQLUtilities.closeResultSet(rs);
        }
    }
}
