package net.sourceforge.squirrel_sql.client.session;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;

import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSetUpdateableTableModel;
import net.sourceforge.squirrel_sql.fw.util.ExceptionFormatter;


public class DefaultSQLExecuterHandler implements ISQLExecuterHandler
{
	private ISession _session;

	public DefaultSQLExecuterHandler(ISession session)
	{
		_session = session;
	}

	public void sqlToBeExecuted(String sql)
	{
	}

	public void sqlExecutionCancelled()
	{
	}

	public void sqlDataUpdated(int updateCount)
	{
	}

	public void sqlResultSetAvailable(ResultSet rst, SQLExecutionInfo info,
			IDataSetUpdateableTableModel model)
	{
	}

	public void sqlExecutionComplete(SQLExecutionInfo info, int processedStatementCount, int statementCount)
	{
	}

	public void sqlExecutionException(Throwable th, String postErrorString)
	{
      String msg = "Error: ";

      if(th instanceof SQLException)
      {
         SQLException sqlEx = (SQLException) th;
         sqlEx.getSQLState();
         sqlEx.getErrorCode();

         msg += sqlEx + ", SQL State: " + sqlEx.getSQLState() + ", Error Code: " + sqlEx.getErrorCode();
      }
      else
      {
         msg += th;
      }

      if(null != postErrorString)
      {
         msg += "\n" + postErrorString;
      }

      _session.showErrorMessage(msg);
	}

	public void sqlExecutionWarning(SQLWarning warn)
	{
		_session.showMessage(warn);
	}

   public void sqlStatementCount(int statementCount)
   {
   }

   public void sqlCloseExecutionHandler()
   {
   }
}