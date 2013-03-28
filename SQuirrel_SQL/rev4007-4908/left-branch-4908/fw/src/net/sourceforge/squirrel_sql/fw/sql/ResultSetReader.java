package net.sourceforge.squirrel_sql.fw.sql;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;

import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;
import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.CellComponentFactory;
import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.DataTypeBlob;
import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.DataTypeClob;
import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.DataTypeDate;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

public class ResultSetReader
{
	
	private final static ILogger s_log =
		LoggerController.createLogger(ResultSetReader.class);

	
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(ResultSetReader.class);

	
	private final ResultSet _rs;

	
	private final int[] _columnIndices;


	
	private int _columnCount;

	
	private boolean _errorOccured = false;

	
	private ResultSetMetaData _rsmd;

    
   private volatile boolean _stopExecution = false; 

	public ResultSetReader(ResultSet rs)
		throws SQLException
	{
		this(rs, null);
	}



	public ResultSetReader(ResultSet rs, int[] columnIndices) throws SQLException
	{
		super();
		if (rs == null)
		{
			throw new IllegalArgumentException("ResultSet == null");
		}

		_rs = rs;

		if (columnIndices != null && columnIndices.length == 0)
		{
			columnIndices = null;
		}
		_columnIndices = columnIndices;

		_rsmd = rs.getMetaData();

		_columnCount = columnIndices != null ? columnIndices.length : _rsmd.getColumnCount();
	}

	
	public Object[] readRow() throws SQLException
	{
		_errorOccured = false;
		if (_rs.next())
		{
			return doRead();
		}
		return null;
	}

	
	public Object[] readRow(ColumnDisplayDefinition colDefs[]) throws SQLException
	{
		_errorOccured = false;
		if (_rs.next())
		{
			return doContentTabRead(colDefs);
		}
		return null;
	}

	
	public boolean getColumnErrorInPreviousRow()
	{
		return _errorOccured;
	}

	
	private String safelyGetColumnTypeName(int idx) {
	    String columnTypeName = null;
        try
        {
            
           columnTypeName = _rsmd.getColumnTypeName(idx);
        }
        catch (SQLException e)
        {
           if (s_log.isInfoEnabled()) {
               s_log.info("doRead: ResultSetMetaData.getColumnTypeName("+
                   idx+") threw an unexpected exception - "+e.getMessage());
               s_log.info("Unable to determine column type name so " +
                    "any custom types provided by plugins will be " +
                    "unavailable.  This is a driver bug.");
           }
        }
        if (columnTypeName == null) {
            try {
                columnTypeName = _rsmd.getColumnClassName(idx);
            } catch (SQLException e) {
                if (s_log.isInfoEnabled()) {
                    s_log.info("doRead: ResultSetMetaData.getColumnClassName("+
                        idx+") threw an unexpected exception - "+e.getMessage());
                    
                }
            }
        }
        if (columnTypeName == null) {
            columnTypeName = "Unavailable";
        }
        return columnTypeName;
	}
	
	
	private Object[] doRead()
	{
		Object[] row = new Object[_columnCount];
		for (int i = 0; i < _columnCount && !_stopExecution; ++i)
		{
			int idx = _columnIndices != null ? _columnIndices[i] : i + 1;
			try
			{
				int columnType = _rsmd.getColumnType(idx);
				String columnTypeName = safelyGetColumnTypeName(idx);

				switch (columnType)
				{
					case Types.NULL:
						row[i] = null;
						break;

					case Types.BIT:
					case Types.BOOLEAN:
						row[i] = _rs.getObject(idx);
						if (row[i] != null
							&& !(row[i] instanceof Boolean))
						{
							if (row[i] instanceof Number)
							{
								if (((Number) row[i]).intValue() == 0)
								{
									row[i] = Boolean.FALSE;
								}
								else
								{
									row[i] = Boolean.TRUE;
								}
							}
							else
							{
								row[i] = Boolean.valueOf(row[i].toString());
							}
						}
						break;

					case Types.TIME :
						row[i] = _rs.getTime(idx);
						break;

					case Types.DATE :
                        if (DataTypeDate.getReadDateAsTimestamp()) {
                            row[i] = _rs.getTimestamp(idx);
                        } else {
                            row[i] = DataTypeDate.staticReadResultSet(_rs, idx, false);
                        }
						break;

					case Types.TIMESTAMP :
                    case -101 : 
                    case -102 : 
						row[i] = _rs.getTimestamp(idx);
						break;

					case Types.BIGINT :
						row[i] = _rs.getObject(idx);
						if (row[i] != null
							&& !(row[i] instanceof Long))
						{
							if (row[i] instanceof Number)
							{
								row[i] = Long.valueOf(((Number)row[i]).longValue());
							}
							else
							{
								row[i] = Long.valueOf(row[i].toString());
							}
						}
						break;

					case Types.DOUBLE:
					case Types.FLOAT:
					case Types.REAL:
						row[i] = _rs.getObject(idx);
						if (row[i] != null
							&& !(row[i] instanceof Double))
						{
							if (row[i] instanceof Number)
							{
								Number nbr = (Number)row[i];
								row[i] = new Double(nbr.doubleValue());
							}
							else
							{
								row[i] = new Double(row[i].toString());
							}
						}
						break;

					case Types.DECIMAL:
					case Types.NUMERIC:
						row[i] = _rs.getObject(idx);
						if (row[i] != null
							&& !(row[i] instanceof BigDecimal))
						{
							if (row[i] instanceof Number)
							{
								Number nbr = (Number)row[i];
								row[i] = new BigDecimal(nbr.doubleValue());
							}
							else
							{
								row[i] = new BigDecimal(row[i].toString());
							}
						}
						break;

					case Types.INTEGER:
					case Types.SMALLINT:
					case Types.TINYINT:
						row[i] = readInt(idx, columnTypeName);
						break;

						
						
						
						
					case Types.CHAR:
					case Types.VARCHAR:
					case Types.LONGVARCHAR:
					case -9:
					case -8:
						row[i] = _rs.getString(idx);
						if (_rs.wasNull())
						{
							row[i] = null;
						}
						break;

					case Types.BINARY:
					case Types.VARBINARY:
					case Types.LONGVARBINARY:
						row[i] = _rs.getString(idx);
						break;

					case Types.BLOB:
						
						
						
						

                        row[i] = DataTypeBlob.staticReadResultSet(_rs, idx);

						break;

					case Types.CLOB:
						
						
						
						
                        row[i] = DataTypeClob.staticReadResultSet(_rs, idx);

						break;

						
					case Types.JAVA_OBJECT:
					    row[i] = _rs.getObject(idx);
					    if (_rs.wasNull())
					    {
					        row[i] = null;
					    }
					    break;
					    


					case Types.OTHER:
					    
					    
					    
					    











					    row[i] = s_stringMgr.getString("ResultSetReader.other");

					    break;

					default:
					    
					    row[i] = 
					        CellComponentFactory.readResultWithPluginRegisteredDataType(_rs, 
					                                                     columnType, 
					                                                     columnTypeName, 
					                                                     idx);
					    if (row[i] == null) {
                            Integer colTypeInteger = Integer.valueOf(columnType);
							row[i] = s_stringMgr.getString("ResultSetReader.unknown", 
                                                            colTypeInteger);
					    }
				}
			}
			catch (Throwable th)
			{
                
                
                if (!_stopExecution) {
                    _errorOccured = true;
                    row[i] = s_stringMgr.getString("ResultSetReader.error");
                    StringBuffer msg = new StringBuffer("Error reading column data");
                    msg.append(", column index = ").append(idx);
                    s_log.error(msg.toString(), th);
                }
			}
		}

		return row;
	}

   
   private Object readInt(int columnIdx, String columnTypeName) throws SQLException {
      Object result = _rs.getObject(columnIdx);
      if (_rs.wasNull()) {
         return null;
      }
      
        
      if (result instanceof Long) {
      	return result;
      }
      if ("INTEGER UNSIGNED".equalsIgnoreCase(columnTypeName)) {
      	return Long.valueOf(result.toString());
      }
      
      if (result instanceof Integer) {
      	return result;
      }
      if (result instanceof Number) {
         	Number resultNumber = (Number)result;
         	int intValue = resultNumber.intValue();
            return Integer.valueOf( intValue );
      } 
      
      return Integer.valueOf(result.toString());
   }

	
	private Object[] doContentTabRead(ColumnDisplayDefinition colDefs[])
	{
		Object[] row = new Object[_columnCount];
		for (int i = 0; i < _columnCount && !_stopExecution; ++i)
		{
			int idx = _columnIndices != null ? _columnIndices[i] : i + 1;
			try
			{
				final int columnType = _rsmd.getColumnType(idx);
				
				switch (columnType)
				{
					case Types.NULL:
						row[i] = null;
						break;

					
                    
                    
					case Types.BIT:
					case Types.BOOLEAN:

					case Types.DECIMAL:
					case Types.NUMERIC:

					case Types.INTEGER:
					case Types.SMALLINT:
					case Types.TINYINT:
					case Types.BIGINT :

					case Types.DOUBLE:
					case Types.FLOAT:
					case Types.REAL:

					case Types.DATE :
					case Types.TIME :
					case Types.TIMESTAMP :

					
					
					
					
					case Types.CHAR:
					case Types.VARCHAR:
					case Types.LONGVARCHAR:
					case -9:
					case -8:

					
					case Types.BINARY:
					case Types.VARBINARY:
					case Types.LONGVARBINARY:

					case Types.CLOB:
					case Types.BLOB:

					case Types.OTHER:

					default:
						row[i] = CellComponentFactory.readResultSet(
								colDefs[i], _rs, idx, true);

						break;

				}
			}
			catch (Throwable th)
			{
				_errorOccured = true;
				row[i] = s_stringMgr.getString("ResultSetReader.error");
                if (!_stopExecution) {
                    StringBuffer msg = new StringBuffer("Error reading column data");
                    msg.append(", column index = ").append(idx);
                    s_log.error(msg.toString(), th);
                }
			}
		}

		return row;
	}

    
    public void setStopExecution(boolean _stopExecution) {
        this._stopExecution = _stopExecution;
    }

    
    public boolean isStopExecution() {
        return _stopExecution;
    }
}
