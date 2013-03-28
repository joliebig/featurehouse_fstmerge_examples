package net.sourceforge.squirrel_sql.client.mainframe.action;

import java.sql.SQLException;

import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.sql.ISQLAlias;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDriver;
import net.sourceforge.squirrel_sql.fw.sql.SQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.SQLDriverManager;
import net.sourceforge.squirrel_sql.fw.sql.SQLDriverPropertyCollection;
import net.sourceforge.squirrel_sql.fw.sql.WrappedSQLException;
import net.sourceforge.squirrel_sql.fw.util.BaseException;
import net.sourceforge.squirrel_sql.fw.util.ICommand;

import net.sourceforge.squirrel_sql.client.IApplication;

public class OpenConnectionCommand implements ICommand
{
	
	private IApplication _app;

	
	private ISQLAlias _sqlAlias;

	private final String _userName;
	private final String _password;
	private final SQLDriverPropertyCollection _props;

	private SQLConnection _conn;

	
	public OpenConnectionCommand(IApplication app, ISQLAlias sqlAlias,
									String userName, String password,
									SQLDriverPropertyCollection props)
	{
		super();
		if (app == null)
		{
			throw new IllegalArgumentException("Null IApplication passed");
		}
		if (sqlAlias == null)
		{
			throw new IllegalArgumentException("Null ISQLAlias passed");
		}
		_app = app;
		_sqlAlias = sqlAlias;
		_userName = userName;
		_password = password;
		_props = props;
	}

	
	public void execute() throws BaseException
	{
		_conn = null;
		final IIdentifier driverID = _sqlAlias.getDriverIdentifier();
		final ISQLDriver sqlDriver = _app.getDataCache().getDriver(driverID);
		final SQLDriverManager mgr = _app.getSQLDriverManager();
		try
		{
			_conn = mgr.getConnection(sqlDriver, _sqlAlias, _userName, _password, _props);
		}
		catch (SQLException ex)
		{
			throw new WrappedSQLException(ex);
		}
		catch (Throwable th)
		{
			throw new BaseException(th);
		}
	}

	
	public SQLConnection getSQLConnection()
	{
		return _conn;
	}

}
