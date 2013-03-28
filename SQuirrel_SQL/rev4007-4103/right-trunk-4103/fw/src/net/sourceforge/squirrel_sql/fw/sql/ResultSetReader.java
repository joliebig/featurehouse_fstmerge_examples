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
import net.sourceforge.squirrel_sql.fw.dialects.DialectType;
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

    
   private DialectType _dialectType = null;
   
	
	public ResultSetReader(ResultSet rs, DialectType dialectType)
		throws SQLException
	{
		this(rs, null, dialectType);
	}



	
	public ResultSetReader(ResultSet rs, int[] columnIndices,
         DialectType dialectType) throws SQLException
	{
		super();
		if (rs == null)
		{
			throw new IllegalArgumentException("ResultSet == null");
		}
		_dialectType = dialectType;
		_rs = rs;

		if (columnIndices != null && columnIndices.length == 0)
		{
			columnIndices = null;
		}
		_columnIndices = columnIndices;

		_rsmd = rs.getMetaData();

		_columnCount = columnIndices != null ? columnIndices.length
            : _rsmd.getColumnCount();
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

            
            row[i] = 
                CellComponentFactory.readResultWithPluginRegisteredDataType(_rs, 
                                                             columnType, 
                                                             columnTypeName, 
                                                             idx,
                                                             _dialectType);
            if (row[i] == null) {
				
               switch (columnType) {
               case Types.NULL:
                  row[i] = null;
                  break;

               case Types.BIT:
               case Types.BOOLEAN:
                  row[i] = readBoolean(idx);
                  break;

               case Types.TIME:
                  row[i] = _rs.getTime(idx);
                  break;

               case Types.DATE:
                  row[i] = readDate(idx);
                  break;

               case Types.TIMESTAMP:
               case -101: 
               case -102: 
                  
                  row[i] = _rs.getTimestamp(idx);
                  break;

               case Types.BIGINT:
                  row[i] = readBigint(idx);
                  break;

               case Types.DOUBLE:
               case Types.FLOAT:
               case Types.REAL:
                  row[i] = readFloat(idx);
                  break;

               case Types.DECIMAL:
               case Types.NUMERIC:
                  row[i] = readNumeric(idx);
                  break;

               case Types.INTEGER:
               case Types.SMALLINT:
               case Types.TINYINT:
                  row[i] = readInt(idx);
                  break;

               
               
               
               
               case Types.CHAR:
               case Types.VARCHAR:
               case Types.LONGVARCHAR:
               case -9:
               case -8:
                  row[i] = _rs.getString(idx);
                  if (_rs.wasNull()) {
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
                  row[i] = readObject(idx);
                  break;
               

               case Types.OTHER:
                  row[i] = readOther(idx);
                  break;

               default:
                  if (row[i] == null) {
                     Integer colTypeInteger = Integer.valueOf(columnType);
                     row[i] = s_stringMgr.getString("ResultSetReader.unknown",
                                                    colTypeInteger);
                  }
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

   private Object readNumeric(int columnIdx) throws SQLException {
      Object result = _rs.getObject(columnIdx);
      if (result != null && !(result instanceof BigDecimal)) {
         if (result instanceof Number) {
            Number nbr = (Number) result;
            result = new BigDecimal(nbr.doubleValue());
         } else {
            result = new BigDecimal(result.toString());
         }
      }
      return result;
   }
   
   private Object readOther(int columnIdx) throws SQLException {
         
         
       
       
       
      
      
      
      
      
      
      
      
      
      
      
      return s_stringMgr.getString("ResultSetReader.other");
      
      
   }
   
   private Object readObject(int columnIdx) throws SQLException {
      Object result = _rs.getObject(columnIdx);
      if (_rs.wasNull()) {
         result = null;
      }
      return result;
   }
   
   private Object readInt(int columnIdx) throws SQLException {
      Object result = _rs.getObject(columnIdx);
      if (_rs.wasNull()) {
         result = null;
      }
      if (result != null && !(result instanceof Integer)) {
         if (result instanceof Number) {
            result = Integer.valueOf(((Number) result).intValue());
         } else {
            result = new Integer(result.toString());
         }
      }
      return result;
   }
   
   private Object readFloat(int columnIdx) throws SQLException {
      Object result = _rs.getObject(columnIdx);
      if (result != null && !(result instanceof Double)) {
         if (result instanceof Number) {
            Number nbr = (Number) result;
            result = new Double(nbr.doubleValue());
         } else {
            result = new Double(result.toString());
         }
      }
      return result;
   }
   
   private Object readDate(int columnIdx) throws SQLException {
      Object result = null;
      if (DataTypeDate.getReadDateAsTimestamp()) {
         result = _rs.getTimestamp(columnIdx);
      } else {
         result = DataTypeDate.staticReadResultSet(_rs, columnIdx, false);
      }
      return result;
   }
   
   private Object readBigint(int columnIdx) throws SQLException {
      Object result = _rs.getObject(columnIdx);
      if (result != null
         && !(result instanceof Long))
      {
         if (result instanceof Number)
         {
            result = Long.valueOf(((Number)result).longValue());
         }
         else
         {
            result = Long.valueOf(result.toString());
         }
      }
      return result;
   }
   
   private Object readBoolean(int columnIdx) throws SQLException {
      Object result = null;
      result = _rs.getObject(columnIdx);

      if (result != null && !(result instanceof Boolean)) {
         if (result instanceof Number) {
            if (((Number) result).intValue() == 0) {
               result = Boolean.FALSE;
            } else {
               result = Boolean.TRUE;
            }
         } else {
            result = Boolean.valueOf(result.toString());
         }
      }
      return result;
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
