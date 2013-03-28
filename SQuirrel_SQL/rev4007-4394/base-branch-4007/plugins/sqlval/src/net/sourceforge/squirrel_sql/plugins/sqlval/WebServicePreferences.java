package net.sourceforge.squirrel_sql.plugins.sqlval;

import java.io.Serializable;

public class WebServicePreferences implements Cloneable, Serializable
{
	static final String UNSUPPORTED = "Unsupported";

	
	private boolean _useAnonymousLogon = true;

	
	private String _userName = "";

	
	private String _password = "";

	
	private boolean _useAnonymousClient = false;

	
	private String _clientName;

	
	private String _clientVersion;

	public WebServicePreferences()
	{
		super();
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

	
	public boolean getUseAnonymousLogon()
	{
		return _useAnonymousLogon;
	}

	
	public void setUseAnonymousLogon(boolean value)
	{
		_useAnonymousLogon = value;
	}

	
	public String getUserName()
	{
		return _userName;
	}

	
	public void setUserName(String value)
	{
		_userName = value;
	}

	
	public String retrievePassword()
	{
		return _password;
	}

	
	public void setPassword(String value)
	{
		_password = value;
	}

	
	public boolean getUseAnonymousClient()
	{
		return _useAnonymousClient;
	}

	
	public void setUseAnonymousClient(boolean value)
	{
		_useAnonymousClient = value;
	}

	
	public String getClientName()
	{
		return _clientName;
	}

	
	public void setClientName(String value)
	{
		_clientName = value;
	}

	
	public String getClientVersion()
	{
		return _clientVersion;
	}

	
	public void setClientVersion(String value)
	{
		_clientVersion = value;
	}
}

