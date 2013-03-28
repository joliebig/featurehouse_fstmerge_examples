package org.rege.isqlj.squirrel;



import java.sql.*;

import net.sourceforge.squirrel_sql.client.session.*;
import net.sourceforge.squirrel_sql.client.session.mainpanel.*;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.client.plugin.IPlugin;

import org.rege.isqlj.*;

public class SqscConnection
		implements SqlConnection
{
	private ISQLConnection squirrelCon = null;
	private Database db = null;
	private ISession session = null;
	private IPlugin plugin = null;

 
    public SqscConnection( ISession session, IPlugin plugin)
    {
		if( session == null)
		{
			throw new NullPointerException( "Null Session provided.");
		}
		if( plugin == null)
		{
			throw new NullPointerException( "Null Plugin provided.");
		}
		this.session = session;
		this.plugin = plugin;
		this.squirrelCon = session.getSQLConnection();
		db = new Database( getConnection());
    }

	public Connection getConnection()
	{
		return squirrelCon.getConnection();
	}

	public Database getDatabase()
	{
		return this.db;
	}


	public ResultSet executeQuery( String sql) throws SQLException
	{
		session.getSessionInternalFrame().getSQLPanelAPI().executeSQL( sql);
		return null;
	}

	public int executeUpdate( String sql) 
			throws SQLException
	{
		session.getSessionInternalFrame().getSQLPanelAPI().executeSQL( sql);
		return 0;
	}

}

