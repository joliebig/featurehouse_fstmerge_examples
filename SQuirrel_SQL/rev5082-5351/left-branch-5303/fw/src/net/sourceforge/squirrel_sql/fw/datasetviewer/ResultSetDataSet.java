package net.sourceforge.squirrel_sql.fw.datasetviewer;


import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.squirrel_sql.fw.dialects.DialectType;
import net.sourceforge.squirrel_sql.fw.sql.JDBCTypeMapper;
import net.sourceforge.squirrel_sql.fw.sql.ResultSetReader;
import net.sourceforge.squirrel_sql.fw.util.IMessageHandler;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

public class ResultSetDataSet implements IDataSet {
   private final static ILogger s_log = LoggerController.createLogger(ResultSetDataSet.class);

   
   private int _iCurrent = -1;

   private Object[] _currentRow;

   private int _columnCount;

   private DataSetDefinition _dataSetDefinition;

   private List<Object[]> _alData;

   
   private volatile boolean _cancel = false;

   
   private ResultSetReader rdr = null;

      
   private DialectType _dialectType = null;

   
   public ResultSetDataSet() {
      super();
   }

   
   public void setResultSet(ResultSet rs, DialectType dialectType)
         throws DataSetException {
      setResultSet(rs, null, false, dialectType);
   }

   
   public void setContentsTabResultSet(ResultSet rs, String fullTableName,
         DialectType dialectType) throws DataSetException {
      setResultSet(rs, fullTableName, null, false, true, dialectType);
   }

   
   public void setResultSet(ResultSet rs, int[] columnIndices,
         DialectType dialectType) throws DataSetException {
      setResultSet(rs, columnIndices, false, dialectType);
   }

   
   public void setResultSet(ResultSet rs, int[] columnIndices,
         boolean computeWidths, DialectType dialectType) throws DataSetException {
      setResultSet(rs, null, columnIndices, computeWidths, false, dialectType);
   }

   
   private void setResultSet(ResultSet rs, String fullTableName,
         int[] columnIndices, boolean computeWidths, boolean useColumnDefs,
         DialectType dialectType) throws DataSetException {
      reset();
      _dialectType = dialectType;
      if (columnIndices != null && columnIndices.length == 0) {
         columnIndices = null;
      }
      _iCurrent = -1;
      _alData = new ArrayList<Object[]>();

      if (rs != null) {
         try {
            ResultSetMetaData md = rs.getMetaData();
            _columnCount = columnIndices != null ? columnIndices.length
                  : md.getColumnCount();

            
            
            
            
            
            
            
            ColumnDisplayDefinition[] colDefs = createColumnDefinitions(md,
                                                                        fullTableName,
                                                                        columnIndices,
                                                                        computeWidths);
            _dataSetDefinition = new DataSetDefinition(colDefs);

            
            
            rdr = new ResultSetReader(rs, dialectType);
            Object[] row = null;

            while (true) {
               if (useColumnDefs)
                  row = rdr.readRow(colDefs);
               else
                  row = rdr.readRow();

               if (row == null)
                  break;

               if (_cancel) {
                  return;
               }

               
               if (columnIndices != null) {
                  Object[] newRow = new Object[_columnCount];
                  for (int i = 0; i < _columnCount; i++) {
                     if (columnIndices[i] - 1 < row.length) {
                        newRow[i] = row[columnIndices[i] - 1];
                     } else {
                        newRow[i] = "Unknown";
                     }
                  }
                  row = newRow;
               }
               _alData.add(row);
            }

            
            
            
         } catch (SQLException ex) {
            
            
            
            
            throw new DataSetException(ex);
         }
      }
   }

   public final int getColumnCount() {
      return _columnCount;
   }

   public DataSetDefinition getDataSetDefinition() {
      return _dataSetDefinition;
   }

   public synchronized boolean next(IMessageHandler msgHandler)
         throws DataSetException {
      
      if (++_iCurrent < _alData.size()) {
         _currentRow = _alData.get(_iCurrent);
         return true;
      }
      return false;
   }

   
   public Object get(int columnIndex) {
      if (_currentRow != null) {
         return _currentRow[columnIndex];
      } else {
         return null;
      }
   }

   public void cancelProcessing() {
      rdr.setStopExecution(true);
      _cancel = true;
   }

   
   private ColumnDisplayDefinition[] createColumnDefinitions(
         ResultSetMetaData md, String fullTableName, int[] columnIndices,
         boolean computeWidths) throws SQLException {
      
      
      int[] colWidths = null;

      
      if (computeWidths) {
         colWidths = new int[_columnCount];
         for (int i = 0; i < _alData.size(); i++) {
            Object[] row = _alData.get(i);
            for (int col = 0; i < _columnCount; i++) {
               if (row[col] != null) {
                  int colWidth = row[col].toString().length();
                  if (colWidth > colWidths[col]) {
                     colWidths[col] = colWidth + 2;
                  }
               }
            }
         }
      }

      ColumnDisplayDefinition[] columnDefs = new ColumnDisplayDefinition[_columnCount];
      for (int i = 0; i < _columnCount; ++i) {
         int idx = columnIndices != null ? columnIndices[i] : i + 1;

         
         
         
         
         
         
         
         
         
         
         
         
         
         
         boolean isNullable = true;
         if (md.isNullable(idx) == ResultSetMetaData.columnNoNulls)
            isNullable = false;

         int precis;
         try {
            precis = md.getPrecision(idx);
         } catch (NumberFormatException ignore) {
            precis = Integer.MAX_VALUE; 
                                          
         }

         boolean isSigned = true;
         try {
            isSigned = md.isSigned(idx); 
         } catch (SQLException ignore) {
            
         }

         boolean isCurrency = false;

         try {
            
            
            isCurrency = md.isCurrency(idx);
         } catch (SQLException e) {
            s_log.error("Failed to call ResultSetMetaData.isCurrency()", e);
         }

         boolean isAutoIncrement = false;
         try {
            isAutoIncrement = md.isAutoIncrement(idx);
         } catch (SQLException e) {
            s_log.error("Failed to call ResultSetMetaData.isAutoIncrement()", e);
         }
         
         String columnName = md.getColumnName(idx);
         String columnTypeName = md.getColumnTypeName(idx);
         int columnType = fixColumnType(columnName, md.getColumnType(idx), columnTypeName);
         
         columnDefs[i] = new ColumnDisplayDefinition(computeWidths ? colWidths[i]
                                                           : md.getColumnDisplaySize(idx),
                                                     fullTableName
                                                           + ":"
                                                           + md.getColumnLabel(idx),
                                                     columnName,
                                                     md.getColumnLabel(idx),
                                                     columnType,
                                                     columnTypeName,
                                                     isNullable,
                                                     md.getColumnDisplaySize(idx),
                                                     precis,
                                                     md.getScale(idx),
                                                     isSigned,
                                                     isCurrency,
                                                     isAutoIncrement,
                                                     _dialectType);
      }
      return columnDefs;
   }

   
   private int fixColumnType(String columnName, int columnType, String columnTypeName) {
   	int result = columnType;
   	if (columnType == Types.NULL) {
   		result = JDBCTypeMapper.getJdbcType(columnTypeName);
   		if (result == Types.NULL) {
   			result = Types.VARCHAR;
   		}
   	}
   	if (result != columnType) {
			if (s_log.isDebugEnabled()) {
				s_log.debug("Converting type code for column "+columnName+
					". Original column type code and name were Types.NULL and "+columnTypeName+
					"; New type code is "+JDBCTypeMapper.getJdbcTypeName(result));
			}   		
   	}
   	return result;
   }
   
   private void reset() {
      _iCurrent = -1;
      _currentRow = null;
      _columnCount = 0;
      _dataSetDefinition = null;
      _alData = null;
   }

   public void resetCursor() {
      _iCurrent = -1;
      _currentRow = null;
   }

   
   public Object removeRow(int index) {
      if (_alData.size() > index) {
         return _alData.remove(index);
      } else {
         return null;
      }
   }
}
