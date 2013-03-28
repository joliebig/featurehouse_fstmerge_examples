package net.sourceforge.squirrel_sql.client.session;

import java.sql.ResultSet;
import java.sql.SQLWarning;

import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetException;
import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSetUpdateableTableModel;


public interface ISQLExecuterHandler
{
	
	public void sqlToBeExecuted(String sql);

	
	public void sqlExecutionCancelled();

	
	public void sqlDataUpdated(int updateCount);

	
	public void sqlResultSetAvailable(ResultSet rst, SQLExecutionInfo info,
			IDataSetUpdateableTableModel model) throws DataSetException;

	
	public void sqlExecutionComplete(SQLExecutionInfo info, int processedStatementCount, int statementCount);

	
	public void sqlExecutionException(Throwable ex, String postErrorString);

	
	public void sqlExecutionWarning(SQLWarning warn);

   
   public void sqlStatementCount(int statementCount);

   
   public void sqlCloseExecutionHandler();
}