package net.sourceforge.squirrel_sql.plugins.sqlval;

import java.io.Serializable;

import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.SQLDatabaseMetaData;

public class WebServiceSessionProperties implements Cloneable, Serializable
{
	private static final String UNSUPPORTED = "Unsupported";

	
	private boolean _useAnonymousDBMS = false;

	
	private String _targetDBMS;

	
	private String _targetDBMSVersion;

	
	private String _connTechnology;

	
	private String _connTechnologyVersion;

	
	private WebServiceSession _webServiceSession;

	public WebServiceSessionProperties(WebServicePreferences prefs)
	{
		super();
		_webServiceSession = new WebServiceSession(prefs, this);
	}

	
	public Object clone()
	{
		try
		{
			return super.clone();
		}
		catch (CloneNotSupportedException ex)
		{
			throw new InternalError(ex.getMessage()); 
		}
	}

	
	public WebServiceSession getWebServiceSession()
	{
		return _webServiceSession;
	}

	
	public boolean getUseAnonymousDBMS()
	{
		return _useAnonymousDBMS;
	}

	
	public void setUseAnonymousDBMS(boolean value)
	{
		_useAnonymousDBMS = value;
	}

	
	public String getTargetDBMSName()
	{
		return _targetDBMS;
	}

	
	public String getTargetDBMSVersion()
	{
		return _targetDBMSVersion;
	}

	
	public String getConnectionTechnology()
	{
		return _connTechnology;
	}

	
	public String getConnectionTechnologyVersion()
	{
		return _connTechnologyVersion;
	}

	
	public void setSQLConnection(ISQLConnection conn)
	{
		if (conn == null)
		{
			throw new IllegalArgumentException("SQLConnection == null");
		}

		SQLDatabaseMetaData md = conn.getSQLMetaData();
		try
		{
			_targetDBMS = md.getDatabaseProductName();
		}
		catch (Throwable ignore)
		{
			_targetDBMS = UNSUPPORTED;
		}
		try
		{
			
			
			_targetDBMSVersion = md.getDatabaseProductVersion();
			if (_targetDBMSVersion.length() > 30)
			{
				_targetDBMSVersion = _targetDBMSVersion.substring(0, 30);
			}
		}
		catch (Throwable ignore)
		{
			_targetDBMSVersion = UNSUPPORTED;
		}
		_connTechnology = "JDBC";
		try
		{
			_connTechnologyVersion = String.valueOf(md.getJDBCVersion());
		}
		catch (Throwable ignore)
		{
			_connTechnologyVersion = UNSUPPORTED;
		}
	}
}

