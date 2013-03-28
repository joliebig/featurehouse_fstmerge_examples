package net.sourceforge.squirrel_sql.fw.sql;

import java.sql.SQLException;

import net.sourceforge.squirrel_sql.fw.util.IMessageHandler;
import net.sourceforge.squirrel_sql.fw.util.ISessionProperties;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;



public class SQLConnectionState
{
	private final static ILogger s_log =
		LoggerController.createLogger(SQLConnectionState.class);

	private Integer _transIsolation;
	private String _catalog;
	private boolean _autoCommit;
	private SQLDriverPropertyCollection _connProps;

	public SQLConnectionState()
	{
		super();
	}

   public void saveState(ISQLConnection conn, ISessionProperties sessionProperties, IMessageHandler msgHandler)
		throws SQLException
	{
		if (conn == null)
		{
			throw new IllegalArgumentException("SQLConnection == null");
		}

		try
		{
			_transIsolation = Integer.valueOf(conn.getTransactionIsolation());
		}
		catch (SQLException ex)
		{
			String msg =
				"Error saving transaction isolation.\n" +
				"This might happen when reconnecting a Session to restore a broken connection.\n" +
				"The new connection will use the default transaction isolation.";

			s_log.error(msg, ex);
			if (msgHandler == null)
			{
				throw ex;
			}
			msgHandler.showErrorMessage(msg);
		}

		try
		{
			_catalog = conn.getCatalog();
		}
		catch (SQLException ex)
		{
			String msg =
				"Error saving current catalog.\n" +
				"This might happen when reconnecting a Session to restore a broken connection.\n" +
				"The new connection will use the default catalog.";

			s_log.error(msg, ex);
			if (msgHandler == null)
			{
				throw ex;
			}
			msgHandler.showErrorMessage(msg);
		}

		try
		{
         
         
         _autoCommit = sessionProperties.getAutoCommit();

         _autoCommit = conn.getAutoCommit();
		}
		catch (SQLException ex)
		{
			String msg =
				"Error saving autocommit state.\n" +
				"This might happen when reconnecting a Session to restore a broken connection.\n" +
				"The new connection will use the autocommit state.";


			s_log.error(msg, ex);
			if (msgHandler == null)
			{
				throw ex;
			}
			msgHandler.showErrorMessage(msg);
		}

		_connProps = conn.getConnectionProperties();
	}

	public void restoreState(ISQLConnection conn)
		throws SQLException
	{
		restoreState(conn, null);
	}

	public void restoreState(ISQLConnection conn, IMessageHandler msgHandler)
		throws SQLException
	{
		if (conn == null)
		{
			throw new IllegalArgumentException("SQLConnection == null");
		}

		if (_transIsolation != null)
		{
			try
			{
				conn.setTransactionIsolation(_transIsolation.intValue());
			}
			catch (SQLException ex)
			{
				s_log.error("Error restoring transaction isolation", ex);
				if (msgHandler == null)
				{
					throw ex;
				}
				msgHandler.showErrorMessage(ex, null);
			}
		}

		if (_catalog != null)
		{
			try
			{
				conn.setCatalog(_catalog);
			}
			catch (SQLException ex)
			{
				s_log.error("Error restoring current catalog", ex);
				if (msgHandler == null)
				{
					throw ex;
				}
				msgHandler.showErrorMessage(ex, null);
			}
		}

		try
		{
			conn.setAutoCommit(_autoCommit);
		}
		catch (SQLException ex)
		{
			s_log.error("Error restoring autocommit", ex);
			if (msgHandler == null)
			{
				throw ex;
			}
			msgHandler.showErrorMessage(ex, null);
		}
	}

	public SQLDriverPropertyCollection getConnectionProperties()
	{
		return _connProps;
	}

	public boolean getAutoCommit()
	{
		return _autoCommit;
	}
}

