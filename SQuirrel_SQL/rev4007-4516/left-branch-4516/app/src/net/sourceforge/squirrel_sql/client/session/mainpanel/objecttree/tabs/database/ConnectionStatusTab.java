package net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.database;

import java.sql.Connection;
import java.util.Date;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.BaseDataSetTab;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetException;
import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSet;
import net.sourceforge.squirrel_sql.fw.datasetviewer.JavabeanDataSet;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;


public class ConnectionStatusTab extends BaseDataSetTab
{
    
    
    private static final StringManager s_stringMgr =
        StringManagerFactory.getStringManager(ConnectionStatusTab.class);
    
	
	public String getTitle()
	{
        
		return s_stringMgr.getString("ConnectionStatusTab.title");
	}

	
	public String getHint()
	{
        
		return s_stringMgr.getString("ConnectionStatusTab.hint");
	}

	
	protected IDataSet createDataSet() throws DataSetException
	{
		final ISession session = getSession();
		final ISQLConnection conn = session.getSQLConnection();
		return new JavabeanDataSet(new ConnectionInfo(conn, session));
	}

	
	public static final class ConnectionInfo
	{
		private String _catalog;
		private boolean _isReadOnly;
		private boolean _isClosed;
		private boolean _autoCommit;
		private Date _timeOpened;
		private String _transIsol;

		ConnectionInfo(ISQLConnection conn, ISession session)
		{
			super();
			Connection jdbcConn = conn.getConnection();

			try
			{
				_isClosed = jdbcConn.isClosed();
			}
			catch (Throwable th)
			{
				session.showErrorMessage(th);
			}

			try
			{
				_isReadOnly = jdbcConn.isReadOnly();
			}
			catch (Throwable th)
			{
				session.showErrorMessage(th);
			}

			try
			{
				_catalog = conn.getCatalog();
			}
			catch (Throwable th)
			{
				session.showErrorMessage(th);
			}

			try
			{
				_autoCommit = conn.getAutoCommit();
			}
			catch (Throwable th)
			{
				session.showErrorMessage(th);
			}

			try
			{
				final int isol = jdbcConn.getTransactionIsolation();
				switch(isol)
				{
					case Connection.TRANSACTION_NONE:
						_transIsol = "TRANSACTION_NONE";
						break;
					case Connection.TRANSACTION_READ_COMMITTED:
						_transIsol = "TRANSACTION_READ_COMMITTED";
						break;
					case Connection.TRANSACTION_READ_UNCOMMITTED:
						_transIsol = "TRANSACTION_READ_UNCOMMITTED";
						break;
					case Connection.TRANSACTION_REPEATABLE_READ:
						_transIsol = "TRANSACTION_REPEATABLE_READ";
						break;
					case Connection.TRANSACTION_SERIALIZABLE:
						_transIsol = "TRANSACTION_SERIALIZABLE";
						break;
					default:
						_transIsol = "Unknown: " + isol;
						break;
				}
			}
			catch (Throwable th)
			{
				session.showErrorMessage(th);
			}

			_timeOpened = conn.getTimeOpened();
		}

		public String getCatalog()
		{
			return _catalog;
		}

		public boolean isReadOnly()
		{
			return _isReadOnly;
		}

		public boolean isClosed()
		{
			return _isClosed;
		}

		public boolean getAutoCommit()
		{
			return _autoCommit;
		}

		public Date getTimeOpened()
		{
			return _timeOpened;
		}

		public String getTransactionisolationLevel()
		{
			return _transIsol;
		}
	}
}
