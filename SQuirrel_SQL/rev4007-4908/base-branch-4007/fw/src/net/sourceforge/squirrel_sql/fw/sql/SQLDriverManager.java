package net.sourceforge.squirrel_sql.fw.sql;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.MalformedURLException;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

public class SQLDriverManager
{
	
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(SQLDriverManager.class);

	private static final ILogger s_log =
		LoggerController.createLogger(SQLDriverManager.class);

	
	private Map<IIdentifier, Driver> _driverInfo = 
        new HashMap<IIdentifier, Driver>();

	
	private Map<IIdentifier, SQLDriverClassLoader> _classLoaders = 
        new HashMap<IIdentifier, SQLDriverClassLoader>();

	private MyDriverListener _myDriverListener = new MyDriverListener();

	public synchronized void registerSQLDriver(ISQLDriver sqlDriver)
		throws IllegalAccessException, InstantiationException,
					ClassNotFoundException, MalformedURLException
	{
		unregisterSQLDriver(sqlDriver);
		sqlDriver.addPropertyChangeListener(_myDriverListener);
        SQLDriverClassLoader loader = new SQLDriverClassLoader(sqlDriver);
		Driver driver = 
            (Driver)(Class.forName(sqlDriver.getDriverClassName(), 
                                   false, 
                                   loader).newInstance());
		_driverInfo.put(sqlDriver.getIdentifier(), driver);
		_classLoaders.put(sqlDriver.getIdentifier(), loader);
		sqlDriver.setJDBCDriverClassLoaded(true);
	}

	public synchronized void unregisterSQLDriver(ISQLDriver sqlDriver)
	{
		sqlDriver.setJDBCDriverClassLoaded(false);
		sqlDriver.removePropertyChangeListener(_myDriverListener);
		_driverInfo.remove(sqlDriver.getIdentifier());
		_classLoaders.remove(sqlDriver.getIdentifier());
	}

	public ISQLConnection getConnection(ISQLDriver sqlDriver, ISQLAlias alias,
											String user, String pw)
		throws ClassNotFoundException, IllegalAccessException,
				InstantiationException, MalformedURLException, SQLException
	{
		return getConnection(sqlDriver, alias, user, pw, null);
	}

	public synchronized SQLConnection getConnection(ISQLDriver sqlDriver,
											ISQLAlias alias, String user,
											String pw,
											SQLDriverPropertyCollection props)
		throws ClassNotFoundException, IllegalAccessException,
			InstantiationException, MalformedURLException, SQLException
	{
		Properties myProps = new Properties();
		if (props != null)
		{
			props.applyTo(myProps);
		}
		if (user != null)
		{
			myProps.put("user", user);
		}
		if (pw != null)
		{
			myProps.put("password", pw);
		}

		Driver driver = _driverInfo.get(sqlDriver.getIdentifier());
		if (driver == null)
		{
            
			s_log.debug("Loading driver that wasn't registered: " +
							sqlDriver.getDriverClassName());
			ClassLoader loader = new SQLDriverClassLoader(sqlDriver);
            driver = (Driver)(Class.forName(sqlDriver.getDriverClassName(), 
                                            false, 
                                            loader).newInstance());

		}
		Connection jdbcConn = driver.connect(alias.getUrl(), myProps);
		if (jdbcConn == null)
		{
			throw new SQLException(s_stringMgr.getString("SQLDriverManager.error.noconnection"));
		}
		return new SQLConnection(jdbcConn, props, sqlDriver);
	}

	
	public Driver getJDBCDriver(IIdentifier id)
	{
		if (id == null)
		{
			throw new IllegalArgumentException("IIdentifier == null");
		}

		return _driverInfo.get(id);
	}

	
	public SQLDriverClassLoader getSQLDriverClassLoader(ISQLDriver driver)
	{
		if (driver == null)
		{
			throw new IllegalArgumentException("SQLDriverClassLoader == null");
		}

		return _classLoaders.get(driver.getIdentifier());
	}

	private final class MyDriverListener implements PropertyChangeListener
	{
		public void propertyChange(PropertyChangeEvent evt)
		{
			final String propName = evt.getPropertyName();
			if (propName == null
				|| propName.equals(ISQLDriver.IPropertyNames.DRIVER_CLASS)
				|| propName.equals(ISQLDriver.IPropertyNames.JARFILE_NAMES))
			{
				Object obj = evt.getSource();
				if (obj instanceof ISQLDriver)
				{
					ISQLDriver driver = (ISQLDriver) obj;
					SQLDriverManager.this.unregisterSQLDriver(driver);
					try
					{
						SQLDriverManager.this.registerSQLDriver(driver);
					}
					catch (IllegalAccessException ex)
					{
						s_log.error("Unable to create instance of Class "
										+ driver.getDriverClassName()
										+ " for JDBC driver "
										+ driver.getName(), ex);
					}
					catch (InstantiationException ex)
					{
						s_log.error("Unable to create instance of Class "
								+ driver.getDriverClassName()
								+ " for JDBC driver "
								+ driver.getName(), ex);
					}
					catch (MalformedURLException ex)
					{
						s_log.error("Unable to create instance of Class "
								+ driver.getDriverClassName()
								+ " for JDBC driver "
								+ driver.getName(), ex);
					}
					catch (ClassNotFoundException ex)
					{
                        String[] jars = driver.getJarFileNames();
                        String jarFileList = "<empty list>"; 
                        if (jars != null) {
                            jarFileList = 
                                "[ " + StringUtilities.join(jars, ", ") + " ]";
                        }
                       
						s_log.error("Unable to find Driver Class "
								+ driver.getDriverClassName()
								+ " for JDBC driver "
								+ driver.getName()
                                + "; jar filenames = "+jarFileList);
					}
				}
				else
				{
					s_log.error("SqlDriverManager.MyDriverListener is listening to a non-ISQLDriver");
				}
			}
		}
	}
}
