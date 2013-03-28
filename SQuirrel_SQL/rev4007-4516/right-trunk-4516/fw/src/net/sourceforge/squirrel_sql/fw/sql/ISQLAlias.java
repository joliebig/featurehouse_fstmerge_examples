package net.sourceforge.squirrel_sql.fw.sql;

import java.beans.PropertyChangeListener;

import net.sourceforge.squirrel_sql.fw.id.IHasIdentifier;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.persist.IValidatable;
import net.sourceforge.squirrel_sql.fw.persist.ValidationException;

public interface ISQLAlias extends IHasIdentifier, IValidatable
{
	
	public interface IPropertyNames
	{
		String AUTO_LOGON = "autoLogon";
		String CONNECT_AT_STARTUP = "connectAtStartup";
		String DRIVER = "driverIdentifier";
		String DRIVER_PROPERTIES = "driverProperties";
		String ID = "identifier";
		String NAME = "name";
		String PASSWORD = "password";
		String URL = "url";
		String USE_DRIVER_PROPERTIES = "useDriverProperties";
		String USER_NAME = "userName";
		String SCHEMA_PROPERTIES = "schemaProperties";
	}

	
	int compareTo(Object rhs);

	String getName();
	void setName(String name) throws ValidationException;

	IIdentifier getDriverIdentifier();
	void setDriverIdentifier(IIdentifier data) throws ValidationException;

	String getUrl();
	void setUrl(String url) throws ValidationException;

	String getUserName();
	void setUserName(String userName) throws ValidationException;

	
	String getPassword();

	
	void setPassword(String password) throws ValidationException;

	
	boolean isAutoLogon();

	
	void setAutoLogon(boolean value);

	
	boolean isConnectAtStartup();

	
	void setConnectAtStartup(boolean value);

	boolean getUseDriverProperties();
	void setUseDriverProperties(boolean value);

	SQLDriverPropertyCollection getDriverPropertiesClone();
	void setDriverProperties(SQLDriverPropertyCollection value);

	void addPropertyChangeListener(PropertyChangeListener listener);
	void removePropertyChangeListener(PropertyChangeListener listener);
}

