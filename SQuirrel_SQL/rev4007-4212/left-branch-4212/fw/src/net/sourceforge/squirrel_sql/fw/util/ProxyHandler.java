package net.sourceforge.squirrel_sql.fw.util;

import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.util.Properties;

import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

public class ProxyHandler
{
	
	private final static ILogger s_log =
		LoggerController.createLogger(ProxyHandler.class);

	public ProxyHandler()
	{
		super();
	}

	public void apply(ProxySettings proxy)
	{
		apply(proxy, System.getProperties());
	}

	public void apply(ProxySettings proxy, Properties props)
	{
		if (proxy == null)
		{
			throw new IllegalArgumentException("ProxySettings == null");
		}

		final boolean http = proxy.getHttpUseProxy();
		if (http)
		{
			applySetting(props, "proxySet", "true");
			applySetting(props, "http.proxyHost", proxy.getHttpProxyServer());
			applySetting(props, "http.proxyPort", proxy.getHttpProxyPort());
			applySetting(props, "http.nonProxyHosts", proxy.getHttpNonProxyHosts());
			final String user = proxy.getHttpProxyUser();
			String password = proxy.getHttpProxyPassword();
			if (password == null)
			{
				password = "";
			}
			if (user != null && user.length() > 0)
			{
				s_log.debug("Using HTTP proxy with security");
				Authenticator.setDefault(new MyAuthenticator(user, password));
			}
			else
			{
				s_log.debug("Using HTTP proxy without security");
				Authenticator.setDefault(null);
			}
		}
		else
		{
			s_log.debug("Not using HTTP proxy");
			props.remove("proxySet");
			props.remove("http.proxyHost");
			props.remove("http.proxyPort");
			props.remove("http.nonProxyHosts");
			Authenticator.setDefault(null);
		}

		final boolean socks = proxy.getSocksUseProxy();
		if (socks)
		{
			applySetting(props, "socksProxyHost", proxy.getSocksProxyServer());
			applySetting(props, "socksProxyPort", proxy.getSocksProxyPort());
		}
		else
		{
			props.remove("socksProxyHost");
			props.remove("socksProxyPort");
		}
	}

	private void applySetting(Properties props, String key, String value)
	{
		if (value != null && value.length() > 0)
		{
			props.put(key, value);
		}
		else
		{
			props.remove(key);
		}
	}

	private final static class MyAuthenticator extends Authenticator
	{
		private final PasswordAuthentication _password;

		public MyAuthenticator(String user, String password)
		{
			super();
			_password = new PasswordAuthentication(user, password.toCharArray());

		}

		protected PasswordAuthentication getPasswordAuthentication()
		{
			return _password;
		}
	}
}
