package net.sourceforge.squirrel_sql.fw.sql;

import java.beans.PropertyChangeListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.util.Calendar;
import java.util.Date;

import net.sourceforge.squirrel_sql.fw.dialects.DialectFactory;
import net.sourceforge.squirrel_sql.fw.util.PropertyChangeReporter;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

public class SQLConnection implements ISQLConnection
{
   private ISQLDriver _sqlDriver;

	
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(SQLConnection.class);

	
	private final static ILogger s_log =
		LoggerController.createLogger(SQLConnection.class);

	
	private Connection _conn;

	
	private final SQLDriverPropertyCollection _connProps;

	private boolean _autoCommitOnClose = false;

	private Date _timeOpened;
	private Date _timeClosed;

	
	private transient PropertyChangeReporter _propChgReporter;

    private SQLDatabaseMetaData metaData = null;
    
	public SQLConnection(Connection conn, SQLDriverPropertyCollection connProps, ISQLDriver sqlDriver)
	{
		super();
      _sqlDriver = sqlDriver;
      if (conn == null)
		{
			throw new IllegalArgumentException("SQLConnection == null");
		}
		_conn = conn;
		_connProps = connProps;
		_timeOpened = Calendar.getInstance().getTime();
        metaData = new SQLDatabaseMetaData(this);
	}

	
	public void close() throws SQLException
	{
		SQLException savedEx = null;
		if (_conn != null)
		{
			s_log.debug("Closing connection");
			try
			{
				if (!_conn.getAutoCommit())
				{
					if (_autoCommitOnClose)
					{
						_conn.commit();
					}
					else
					{
						_conn.rollback();
					}
				}
			}
			catch (SQLException ex)
			{
				savedEx = ex;
			}
			_conn.close();
			_conn = null;
			_timeClosed = Calendar.getInstance().getTime();
			if (savedEx != null)
			{
				s_log.debug("Connection close failed", savedEx);
				throw savedEx;
			}
			s_log.debug("Connection closed successfully");
		}
	}

	
	public void commit() throws SQLException
	{
		validateConnection();
		_conn.commit();
	}

	
	public void rollback() throws SQLException
	{
		validateConnection();
		_conn.rollback();
	}

    
	public SQLDriverPropertyCollection getConnectionProperties()
	{
		return _connProps;
	}

	
	public boolean getAutoCommit() throws SQLException
	{
		validateConnection();
		return _conn.getAutoCommit();
	}

	
	public void setAutoCommit(boolean value) throws SQLException
	{
		validateConnection();
		final Connection conn = getConnection();
		final boolean oldValue = conn.getAutoCommit();
		if (oldValue != value)
		{
			_conn.setAutoCommit(value);
			getPropertyChangeReporter().firePropertyChange(IPropertyNames.AUTO_COMMIT,
												oldValue, value);
		}
	}

	
	public boolean getCommitOnClose()
	{
		return _autoCommitOnClose;
	}

	
	public int getTransactionIsolation()
		throws SQLException
	{
		validateConnection();
		return _conn.getTransactionIsolation();
	}

	
	public void setTransactionIsolation(int value)
		throws SQLException
	{
		validateConnection();
		_conn.setTransactionIsolation(value);
	}

	
	public void setCommitOnClose(boolean value)
	{
		_autoCommitOnClose = value;
	}

	
	public Statement createStatement() throws SQLException
	{
		validateConnection();
		return _conn.createStatement();
	}

	
	public PreparedStatement prepareStatement(String sql) throws SQLException
	{
		validateConnection();
		return _conn.prepareStatement(sql);
	}

    
	public Date getTimeOpened()
	{
		return _timeOpened;
	}

    
	public Date getTimeClosed()
	{
		return _timeClosed;
	}
	
    
	public SQLDatabaseMetaData getSQLMetaData()
	{        
	    return metaData;
	}

	
	public Connection getConnection()
	{
        
		return _conn;
	}

	
	public String getCatalog() throws SQLException
	{
		validateConnection();
		return getConnection().getCatalog();
	}

	
	public void setCatalog(String catalogName)
		throws SQLException
	{
		validateConnection();
		final Connection conn = getConnection();
		final String oldValue = conn.getCatalog();
		if (!StringUtilities.areStringsEqual(oldValue, catalogName))
		{
			setDbSpecificCatalog(catalogName);
			getPropertyChangeReporter().firePropertyChange(IPropertyNames.CATALOG,
												oldValue, catalogName);
		}
	}

	
	private void setDbSpecificCatalog(String catalogName) throws SQLException {
		SQLDatabaseMetaData md = getSQLMetaData();
		
		if (DialectFactory.isMSSQLServer(md)) {
			setMSSQLServerCatalog(catalogName);
		} else if (DialectFactory.isInformix(md)) {
			setInformixCatalog(catalogName);
		} else {
			setGenericDbCatalog(catalogName);
		}		
	}
	
	private void setGenericDbCatalog(String catalogName) throws SQLException {
		final Connection conn = getConnection();
		conn.setCatalog(catalogName);
	}
	
	
	private void setMSSQLServerCatalog(String catalogName) throws SQLException {
		final Connection conn = getConnection();
		conn.setCatalog(quote(catalogName));
	}

	
	private void setInformixCatalog(String catalogName) throws SQLException {
		final Connection conn = getConnection();
		Statement stmt = null;
		String sql = "DATABASE "+catalogName;
		try {
			stmt = conn.createStatement();
			stmt.execute(sql);
		} catch (SQLException e) {
			s_log.error("setInformixCatalog: failed to change database with the database SQL directive: "+sql);
		} finally {
			SQLUtilities.closeStatement(stmt);
		}
		
		conn.setCatalog(catalogName);
	}
	
	
	public SQLWarning getWarnings() throws SQLException
	{
		validateConnection();
		return _conn.getWarnings();
	}

    
	public void addPropertyChangeListener(PropertyChangeListener listener)
	{
		if (listener != null)
		{
			getPropertyChangeReporter().addPropertyChangeListener(listener);
		}
		else
		{
			s_log.debug("Attempted to add a null PropertyChangeListener");
		}
	}

    
	public void removePropertyChangeListener(PropertyChangeListener listener)
	{
		if (listener != null)
		{
			getPropertyChangeReporter().removePropertyChangeListener(listener);
		}
		else
		{
			s_log.debug("Attempted to remove a null PropertyChangeListener");
		}
	}

	protected void validateConnection() throws SQLException
	{
		if (_conn == null)
		{
			throw new SQLException(s_stringMgr.getString("SQLConnection.noConn"));
		}
	}

	
	private synchronized PropertyChangeReporter getPropertyChangeReporter()
	{
		if (_propChgReporter == null)
		{
			_propChgReporter = new PropertyChangeReporter(this); 
		}
		return _propChgReporter;
	}
	
	private String quote(String str)
	{
		String identifierQuoteString = "";
		try
		{
			identifierQuoteString = getSQLMetaData().getIdentifierQuoteString();
		}
		catch (SQLException ex)
		{
			s_log.debug(
				"DBMS doesn't supportDatabasemetaData.getIdentifierQuoteString",
				ex);
		}
		if (identifierQuoteString != null
				&& !identifierQuoteString.equals(" "))
		{
			return identifierQuoteString + str + identifierQuoteString;
		}
		return str;
	}

   
public ISQLDriver getSQLDriver()
   {
      return _sqlDriver;
   }



}
