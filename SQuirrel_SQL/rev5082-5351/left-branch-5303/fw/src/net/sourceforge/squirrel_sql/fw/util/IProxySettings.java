
package net.sourceforge.squirrel_sql.fw.util;

public interface IProxySettings
{

	public abstract boolean getHttpUseProxy();

	public abstract String getHttpProxyServer();

	public abstract String getHttpProxyPort();

	public abstract String getHttpProxyUser();

	public abstract String getHttpProxyPassword();

	public abstract String getHttpNonProxyHosts();

	public abstract boolean getSocksUseProxy();

	public abstract String getSocksProxyServer();

	public abstract String getSocksProxyPort();

}