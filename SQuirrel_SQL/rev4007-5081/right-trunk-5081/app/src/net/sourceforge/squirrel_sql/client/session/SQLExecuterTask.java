package net.sourceforge.squirrel_sql.client.session;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;

import javax.swing.SwingUtilities;

import net.sourceforge.squirrel_sql.client.session.event.ISQLExecutionListener;
import net.sourceforge.squirrel_sql.client.session.properties.SessionProperties;
import net.sourceforge.squirrel_sql.client.session.schemainfo.SchemaInfoUpdateCheck;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetException;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetUpdateableTableModelListener;
import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataModelImplementationDetails;
import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSetUpdateableTableModel;
import net.sourceforge.squirrel_sql.fw.sql.IQueryTokenizer;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.SQLUtilities;
import net.sourceforge.squirrel_sql.fw.sql.TableInfo;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;


public class SQLExecuterTask implements Runnable, IDataSetUpdateableTableModel
{


   
   private static final ILogger s_log = LoggerController.createLogger(SQLExecuterTask.class);

   private static final StringManager s_stringMgr =
       StringManagerFactory.getStringManager(SQLExecuterTask.class);


   
   private ISQLExecuterHandler _handler;

   
   private ISession _session;

   
   private String _sql;
   private Statement _stmt;
   private boolean _stopExecution = false;

   private int _currentQueryIndex = 0;
   private ISQLExecutionListener[] _executionListeners;
   private DataSetUpdateableTableModelImpl _dataSetUpdateableTableModel;
   private SchemaInfoUpdateCheck _schemaInfoUpdateCheck;
   private IQueryTokenizer _tokenizer = null;
   
   private boolean schemaCheck = true;
   
   public SQLExecuterTask(ISession session, String sql,ISQLExecuterHandler handler)
   {
      this(session, sql, handler, new ISQLExecutionListener[0]);
   }

   public SQLExecuterTask(ISession session, String sql, ISQLExecuterHandler handler, ISQLExecutionListener[] executionListeners)
   {
      if (sql == null) {
          if (s_log.isDebugEnabled()) {
              s_log.debug("init(): expected non-null sql");
              return;
          }
      }
      _session = session;
      _schemaInfoUpdateCheck = new SchemaInfoUpdateCheck(_session);
      _sql = sql;
      _tokenizer = _session.getQueryTokenizer();
      _tokenizer.setScriptToTokenize(_sql);
      _handler = handler;
      if (_handler == null) {
          _handler = new DefaultSQLExecuterHandler(session);
      }
      _executionListeners = executionListeners;
      _dataSetUpdateableTableModel = new DataSetUpdateableTableModelImpl();
      _dataSetUpdateableTableModel.setSession(_session);
   }
   
   public void setExecutionListeners(ISQLExecutionListener[] executionListeners) {
       _executionListeners = executionListeners;
   }
   
   
   public int getQueryCount() {
       return _tokenizer.getQueryCount();
   }
   
   public void setSchemaCheck(boolean aBoolean) {
       schemaCheck = aBoolean;
   }
   
   public void run()
   {
       if (_sql == null) {
           if (s_log.isDebugEnabled()) {
               s_log.debug("init(): expected non-null sql.  Skipping execution");
           }
           return;
       }
       
      String lastExecutedStatement = null;
      int statementCount = 0;
      final SessionProperties props = _session.getProperties();
      try
      {
         final ISQLConnection conn = _session.getSQLConnection();
         _stmt = conn.createStatement();

         try
         {
            final boolean correctlySupportsMaxRows = conn.getSQLMetaData()
                  .correctlySupportsSetMaxRows();
            if (correctlySupportsMaxRows && props.getSQLLimitRows())
            {
               setMaxRows(props);
            }
            
            if(_tokenizer.getQueryCount() == 0)
            {
               throw new IllegalArgumentException("No SQL selected for execution.");
            }

            _currentQueryIndex = 0;

            
            boolean maxRowsHasBeenSet = correctlySupportsMaxRows;
            int processedStatementCount = 0;
            statementCount = _tokenizer.getQueryCount();

            _handler.sqlStatementCount(statementCount);

            while (_tokenizer.hasQuery() && !_stopExecution)
            {
               String querySql = _tokenizer.nextQuery();
               if (querySql != null)
               {
                  ++processedStatementCount;
                  if (_handler != null)
                  {
                     _handler.sqlToBeExecuted(querySql);
                  }

                  
                  
                  
                  if (!correctlySupportsMaxRows
                        && props.getSQLLimitRows())
                  {
                     if (isSelectStatement(querySql))
                     {
                        if (!maxRowsHasBeenSet)
                        {
                           setMaxRows(props);
                           maxRowsHasBeenSet = true;
                        }
                     }
                     else if (maxRowsHasBeenSet)
                     {
                        _stmt.close();
                        _stmt = conn.createStatement();
                        maxRowsHasBeenSet = false;
                     }
                  }
                  try
                  {
                     lastExecutedStatement = querySql;

                     if (!processQuery(querySql, processedStatementCount, statementCount))
                     {
                        break;
                     }
                  }
                  catch (SQLException ex)
                  {
                     
                     
                     
                     
                     
                     
                     if (_stopExecution) {
                         break;
                     } else {
                         if (props.getAbortOnError())
                         {
                            throw ex;
                         }
                         else
                         {
                            if(1 < statementCount)
                            {
                               handleError(ex, "Error occured in:\n" + lastExecutedStatement);
                            }
                            else
                            {
                               handleError(ex, null);
                            }
                         }
                     }
                  }
               }
            }

         }
         finally
         {
            try
            {
               _stmt.close();
            }
            finally
            {
               _stmt = null;
            }
         }
      }
      catch (Throwable ex)
      {
         if(props.getAbortOnError() && 1 < statementCount)
         {
            handleError(ex, "Error occured in:\n" + lastExecutedStatement);
         }
         else
         {
            handleError(ex, null);
         }

         if(false == ex instanceof SQLException)
         {
            s_log.error("Unexpected exception when executing SQL: " + ex, ex);
         }

      }
      finally
      {
         if (_stopExecution)
         {
            if (_handler != null)
            {
               _handler.sqlExecutionCancelled();
            }
            try
            {
               if (_stmt != null)
               {
                  _stmt.cancel();
               }
            }
            catch (Throwable th)
            {
               s_log.error("Error occured cancelling SQL", th);
            }
         }
         if (_handler != null)
         {
            _handler.sqlCloseExecutionHandler();
         }

         if (schemaCheck) {
             try
             {
                _schemaInfoUpdateCheck.flush();
             }
             catch (Throwable t)
             {
                s_log.error("Could not update cache ", t);
             }
         }
      }
   }

	private void setMaxRows(final SessionProperties props)
	{
		try
		{
		   _stmt.setMaxRows(props.getSQLNbrRowsToShow());
		}
		catch (Exception e)
		{
		   s_log.error("Can't Set MaxRows", e);
		}
	}

	
	private boolean isSelectStatement(String querySql)
	{
		return "SELECT".length() < querySql.trim()
		      .length()
		      && "SELECT".equalsIgnoreCase(querySql
		            .trim().substring(0,
		                  "SELECT".length()));
	}

   public void cancel()
   {
      if(_stopExecution)
      {
         return;
      }
      _handler.sqlExecutionCancelled();
      
      String msg = s_stringMgr.getString("SQLResultExecuterPanel.canceleRequested");
      _session.getApplication().getMessageHandler().showMessage(msg);

      _stopExecution = true;
      if (_stmt != null)
      {
         CancelStatementThread cst = new CancelStatementThread(_stmt, _session.getApplication().getMessageHandler());
         cst.tryCancel();
      }
   }

   private boolean processQuery(String sql, int processedStatementCount, int statementCount) throws SQLException
   {
      ++_currentQueryIndex;

      final SQLExecutionInfo exInfo = new SQLExecutionInfo(	_currentQueryIndex, sql, getMaxRows(_stmt));
      boolean firstResultIsResultSet = _stmt.execute(sql);
      exInfo.sqlExecutionComplete();

      
      handleAllWarnings(_session.getSQLConnection(), _stmt);

      boolean supportsMultipleResultSets = _session.getSQLConnection().getSQLMetaData().supportsMultipleResultSets();
      boolean inFirstLoop = true;

      
      
      while (true)
      {
         
         if (_stopExecution)
         {
            return false;
         }


         int updateCount = _stmt.getUpdateCount();

         ResultSet res = null;
         if (inFirstLoop && firstResultIsResultSet)
         {
            res = _stmt.getResultSet();
         }
         else if(false == inFirstLoop)
         {
            res = _stmt.getResultSet();
         }


         if (-1 != updateCount)
         {
            if (_handler != null)
            {
               _handler.sqlDataUpdated(updateCount);
            }
         }
         if (null != res)
         {
            if (!processResultSet(res, exInfo))
            {
               return false;
            }
         }

         if (false == supportsMultipleResultSets)
         {
            
            
            
            
            
            
            
            
            
            
            
            
            
            
            
            
            
            
            break;
         }

         if (!_stmt.getMoreResults() && -1 == updateCount)
         {
            
            
            
            
            break;
         }
         inFirstLoop = false;
      }

      fireExecutionListeners(sql);

      if (_handler != null)
      {
         _handler.sqlExecutionComplete(exInfo, processedStatementCount, statementCount);
      }

      EditableSqlCheck edittableCheck = new EditableSqlCheck(exInfo);

      if (edittableCheck.allowsEditing())
      {
         TableInfo ti = getTableName(edittableCheck.getTableNameFromSQL());
         _dataSetUpdateableTableModel.setTableInfo(ti);
      }
      else
      {
         _dataSetUpdateableTableModel.setTableInfo(null);
      }
      if (schemaCheck) {
          _schemaInfoUpdateCheck.addExecutionInfo(exInfo);
      }

      return true;
   }

   
   private int getMaxRows(Statement stmt) {
   	int result = 0;
   	try
		{
			result = stmt.getMaxRows();
		}
		catch (SQLException e)
		{
			if (s_log.isDebugEnabled()) {
				s_log.debug("Unexpected exception: "+e.getMessage(), e);
			}
		}
		return result;
   }
   
   private void fireExecutionListeners(final String sql)
   {
      
      
      SwingUtilities.invokeLater(new Runnable()
      {
         public void run()
         {
            for (int i = 0; i < _executionListeners.length; i++)
            {
               _executionListeners[i].statementExecuted(sql);
            }
         }
      });
   }

   private boolean processResultSet(final ResultSet rs, final SQLExecutionInfo exInfo)
   {
      if (_stopExecution)
      {
         return false;
      }

      if (_handler != null) {
            try {
                _handler.sqlResultSetAvailable(rs, exInfo, this);
            } catch (DataSetException ex) {
                if (_stopExecution) {
                    return false;
                } else {
                    _session.showMessage(ex);
                    s_log.error("Error reading ResultSet for SQL: "
                            + exInfo.getSQL(), ex);
                }
            }
        }

      handleResultSetWarnings(rs);
      SQLUtilities.closeResultSet(rs);
      return true;
   }

   private void handleAllWarnings(ISQLConnection conn, Statement stmt)
   {
      
      
      
      
      synchronized (conn)
      {
         try
         {
            handleWarnings(conn.getWarnings());
            conn.getConnection().clearWarnings();
         }
         catch (Throwable th)
         {
            s_log.debug("Driver doesn't handle "
                        + "Connection.getWarnings()/clearWarnings()", th);
         }
      }

      try
      {
         handleWarnings(stmt.getWarnings());
         stmt.clearWarnings();
      }
      catch (Throwable th)
      {
         s_log.debug("Driver doesn't handle "
                    + "Statement.getWarnings()/clearWarnings()", th);
      }
   }

   private void handleResultSetWarnings(ResultSet rs)
   {
      try
      {
         handleWarnings(rs.getWarnings());
      }
      catch (Throwable th)
      {
         s_log.error("Can't get warnings from ResultSet", th);
         _session.showMessage(th);
      }
   }

   private void handleWarnings(SQLWarning sw)
   {
      if (_handler != null)
      {
         try
         {
            while (sw != null)
            {
               _handler.sqlExecutionWarning(sw);
               sw = sw.getNextWarning();
            }
         }
         catch (Throwable th)
         {
            s_log.debug("Driver/DBMS can't handle SQLWarnings", th);
         }
      }
   }

   private void handleError(Throwable th, String postErrorString)
   {
      if (_handler != null)
         _handler.sqlExecutionException(th, postErrorString);
   }




   


   
   public TableInfo getTableName(String tableNameFromSQL)
   {
      ITableInfo[] tables = _session.getSchemaInfo().getITableInfos();

      
      for (int i = 0; i < tables.length; ++i)
      {
         String simpleName = tables[i].getSimpleName().toUpperCase();
         String nameWithSchema = simpleName;
         String nameWithSchemaAndCatalog = simpleName;

         if (null != tables[i].getSchemaName() && 0 < tables[i].getSchemaName().length())
         {
            nameWithSchema = tables[i].getSchemaName().toUpperCase() + "." + nameWithSchema;
            nameWithSchemaAndCatalog = nameWithSchema;
         }

         if (null != tables[i].getCatalogName() && 0 < tables[i].getCatalogName().length())
         {
            nameWithSchemaAndCatalog = tables[i].getCatalogName().toUpperCase() + "." + nameWithSchema;
         }

         if (simpleName.equals(tableNameFromSQL)
            || nameWithSchema.equals(tableNameFromSQL)
            || nameWithSchemaAndCatalog.equals(tableNameFromSQL))
         {
            return (TableInfo) tables[i];
         }
      }
      
      
      String[] parts = tableNameFromSQL.split("\\.");
      if (parts.length == 2)
      {
         String catalog = parts[0];
         String simpleName = parts[1];
         tables = _session.getSchemaInfo().getITableInfos(catalog, null, simpleName);
         if (tables != null && tables.length > 0)
         {
            return (TableInfo) tables[0];
         }
         
         tables = _session.getSchemaInfo().getITableInfos(null, catalog, simpleName);
         if (tables != null && tables.length > 0)
         {
            return (TableInfo) tables[0];
         }
      }
      return null;

   }


   
   
   
   public String getWarningOnCurrentData(Object[] values, ColumnDisplayDefinition[] colDefs, int col, Object oldValue)
   {
      return _dataSetUpdateableTableModel.getWarningOnCurrentData(values, colDefs, col, oldValue);
   }

   public String getWarningOnProjectedUpdate(Object[] values, ColumnDisplayDefinition[] colDefs, int col, Object newValue)
   {
      return _dataSetUpdateableTableModel.getWarningOnProjectedUpdate(values, colDefs, col, newValue);
   }

   public Object reReadDatum(Object[] values, ColumnDisplayDefinition[] colDefs, int col, StringBuffer message)
   {
      return _dataSetUpdateableTableModel.reReadDatum(values, colDefs, col, message);
   }

   public String updateTableComponent(Object[] values, ColumnDisplayDefinition[] colDefs, int col, Object oldValue, Object newValue)
   {
      return _dataSetUpdateableTableModel.updateTableComponent(values, colDefs, col, oldValue, newValue);
   }

   public int getRowidCol()
   {
      return _dataSetUpdateableTableModel.getRowidCol();
   }

   public String deleteRows(Object[][] rowData, ColumnDisplayDefinition[] colDefs)
   {
      return _dataSetUpdateableTableModel.deleteRows(rowData, colDefs);
   }

   public String[] getDefaultValues(ColumnDisplayDefinition[] colDefs)
   {
      return _dataSetUpdateableTableModel.getDefaultValues(colDefs);
   }

   public String insertRow(Object[] values, ColumnDisplayDefinition[] colDefs)
   {
      return _dataSetUpdateableTableModel.insertRow(values, colDefs);
   }

   public void addListener(DataSetUpdateableTableModelListener l)
   {
      _dataSetUpdateableTableModel.addListener(l);
   }

   public void removeListener(DataSetUpdateableTableModelListener l)
   {
      _dataSetUpdateableTableModel.removeListener(l);
   }

   public void forceEditMode(boolean mode)
   {
      _dataSetUpdateableTableModel.forceEditMode(mode);
   }

   public boolean editModeIsForced()
   {
      return _dataSetUpdateableTableModel.editModeIsForced();
   }

   public IDataModelImplementationDetails getDataModelImplementationDetails()
   {
      return new IDataModelImplementationDetails()
      {
         public String getStatementSeparator()
         {
            return _session.getQueryTokenizer().getSQLStatementSeparator();
         }
      };
   }

   
   


}