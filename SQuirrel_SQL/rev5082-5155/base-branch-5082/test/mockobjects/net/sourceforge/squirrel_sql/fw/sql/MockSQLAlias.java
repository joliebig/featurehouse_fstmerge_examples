
package net.sourceforge.squirrel_sql.fw.sql;

import java.beans.PropertyChangeListener;

import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.persist.ValidationException;
import net.sourceforge.squirrel_sql.client.gui.db.ISQLAliasExt;
import net.sourceforge.squirrel_sql.client.gui.db.SQLAliasSchemaProperties;

public class MockSQLAlias implements ISQLAliasExt
{

	public void assignFrom(ISQLAlias rhs) throws ValidationException
	{

	}

	public int compareTo(Object rhs)
	{

		return 0;
	}

	public String getName()
	{

		return null;
	}

	public void setName(String name) throws ValidationException
	{

	}

	public IIdentifier getDriverIdentifier()
	{

		return null;
	}

	public void setDriverIdentifier(IIdentifier data) throws ValidationException
	{

	}

	public String getUrl()
	{

		return null;
	}

	public void setUrl(String url) throws ValidationException
	{

	}

	public String getUserName()
	{

		return null;
	}

	public void setUserName(String userName) throws ValidationException
	{

	}

	public String getPassword()
	{

		return null;
	}

	public void setPassword(String password) throws ValidationException
	{

	}

	public boolean isAutoLogon()
	{

		return false;
	}

	public void setAutoLogon(boolean value)
	{

	}

	public boolean isConnectAtStartup()
	{

		return false;
	}

	public void setConnectAtStartup(boolean value)
	{

	}

	public boolean getUseDriverProperties()
	{

		return false;
	}

	public void setUseDriverProperties(boolean value)
	{

	}

	public SQLDriverPropertyCollection getDriverProperties()
	{

		return null;
	}

	public void setDriverProperties(SQLDriverPropertyCollection value)
	{

	}

	public void addPropertyChangeListener(PropertyChangeListener listener)
	{

	}

	public void removePropertyChangeListener(PropertyChangeListener listener)
	{

	}

	public IIdentifier getIdentifier()
	{

		return null;
	}

	public boolean isValid()
	{

		return false;
	}

	public SQLDriverPropertyCollection getDriverPropertiesClone()
	{
		return null;
	}

	public SQLAliasSchemaProperties getSchemaProperties()
	{
		return null;
	}

	public void setSchemaProperties(SQLAliasSchemaProperties schemaProperties)
	{

	}
}
