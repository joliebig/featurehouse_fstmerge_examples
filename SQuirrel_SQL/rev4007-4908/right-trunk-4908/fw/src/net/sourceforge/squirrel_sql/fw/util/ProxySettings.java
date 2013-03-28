package net.sourceforge.squirrel_sql.fw.util;

import java.io.Serializable;



public class ProxySettings implements Cloneable, Serializable, IProxySettings
{
	private static final long serialVersionUID = 6435632924688921646L;

	
	private boolean _httpUseProxy;

	
	private String _httpProxyServer;

	
	private String _httpProxyPort;

	
	private String _httpProxyUser;

	
	private String _httpProxyPassword;

	
	private String _httpNonProxyHosts;

	
	private boolean _socksUseProxy;

	
	private String _socksProxyServer;

	
	private String _socksProxyPort;

	
	public Object clone()
	{
		try
		{
			return super.clone();
		}
		catch(CloneNotSupportedException ex)
		{
			throw new InternalError(ex.getMessage()); 
		}
	}

	public boolean getHttpUseProxy()
	{
		return _httpUseProxy;
	}

	public void setHttpUseProxy(boolean data)
	{
		_httpUseProxy = data;
	}

	public String getHttpProxyServer()
	{
		return _httpProxyServer;
	}

	public void setHttpProxyServer(String data)
	{
		_httpProxyServer = data;
	}

	public String getHttpProxyPort()
	{
		return _httpProxyPort;
	}

	public void setHttpProxyPort(String data)
	{
		_httpProxyPort = data;
	}

	public String getHttpProxyUser()
	{
		return _httpProxyUser;
	}

	public void setHttpProxyUser(String data)
	{
		_httpProxyUser = data;
	}

	public String getHttpProxyPassword()
	{
		return _httpProxyPassword;
	}

	public void setHttpProxyPassword(String data)
	{
		_httpProxyPassword = data;
	}

	public String getHttpNonProxyHosts()
	{
		return _httpNonProxyHosts;
	}

	public void setHttpNonProxyHosts(String data)
	{
		_httpNonProxyHosts = data;
	}

	public boolean getSocksUseProxy()
	{
		return _socksUseProxy;
	}

	public void setSocksUseProxy(boolean data)
	{
		_socksUseProxy = data;
	}

	public String getSocksProxyServer()
	{
		return _socksProxyServer;
	}

	public void setSocksProxyServer(String data)
	{
		_socksProxyServer = data;
	}

	public String getSocksProxyPort()
	{
		return _socksProxyPort;
	}

	public void setSocksProxyPort(String data)
	{
		_socksProxyPort = data;
	}
}
