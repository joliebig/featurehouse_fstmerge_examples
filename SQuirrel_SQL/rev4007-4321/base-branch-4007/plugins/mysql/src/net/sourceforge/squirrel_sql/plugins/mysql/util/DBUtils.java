package net.sourceforge.squirrel_sql.plugins.mysql.util;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.sql.DataTypeInfo;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.PrimaryKeyInfo;
import net.sourceforge.squirrel_sql.fw.sql.SQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.plugins.mysql.MysqlPlugin;

public class DBUtils
{
	
    @SuppressWarnings("unused")
	private final static ILogger s_log =
		LoggerController.createLogger(DBUtils.class);

	
	private ISession _session;

	
    @SuppressWarnings("unused")
	private final MysqlPlugin _plugin;

	
	public DBUtils(ISession session, MysqlPlugin plugin)
	{
		super();
		_session = session;
		_plugin = plugin;

	}

	
	public ITableInfo getTableInfo()
	{
		IObjectTreeAPI treeAPI = _session.getSessionInternalFrame().getObjectTreeAPI();
		IDatabaseObjectInfo[] dbInfo = treeAPI.getSelectedDatabaseObjects();

		if (dbInfo[0] instanceof ITableInfo)
		{
			return (ITableInfo)dbInfo[0];
		}
		return null;
	}

	public String[] getColumnNames()
	{
		String[] columnNames = null;
		try
		{
			final ISQLConnection conn = _session.getSQLConnection();
			Statement stmt = conn.createStatement();
			ResultSet rs =
				stmt.executeQuery("SELECT * FROM " + getTableInfo() + ";");
			ResultSetMetaData md = rs.getMetaData();
			columnNames = new String[md.getColumnCount()];
			for (int i = 0; i < columnNames.length; i++)
			{
				columnNames[i] = md.getColumnLabel(i + 1);
			}
		}
		catch (SQLException ex)
		{
			_session.showErrorMessage(ex);
		}
		return columnNames;
	}

	public String[] getFieldDataTypes()
	{
		String[] dataTypes = null;

		try
		{
			final ISQLConnection conn = _session.getSQLConnection();
			Statement stmt = conn.createStatement();
			ResultSet rs =
				stmt.executeQuery("SELECT * FROM " + getTableInfo() + ";");
			ResultSetMetaData md = rs.getMetaData();

			dataTypes = new String[md.getColumnCount()];
			for (int i = 0; i < dataTypes.length; i++)
			{
				dataTypes[i] = md.getColumnTypeName(i + 1);
			}
		}
		catch (SQLException ex)
		{
			_session.showErrorMessage(ex);
		}
		return dataTypes;
	}

	public void execute(String SQLQuery)
	{
		try
		{

			ISQLConnection conn = _session.getSQLConnection();
			Statement stmt = conn.createStatement();
			stmt.execute(SQLQuery);
		}
		catch (SQLException ex)
		{
			_session.showErrorMessage(ex);
		}
	}

	
	public Vector<String> getDataTypes()
	{

		Vector<String> dataTypes = new Vector<String>();
		try
		{
			final ISQLConnection conn = _session.getSQLConnection();
			SQLDatabaseMetaData dmd = conn.getSQLMetaData();
			DataTypeInfo[] infos = dmd.getDataTypes();
            for (int i = 0; i < infos.length; i++) {
                dataTypes.add(infos[i].getSimpleName());
            }
		}
		catch (SQLException ex)
		{
			_session.showErrorMessage(ex);
		}
		return dataTypes;
	}

	public String getPrimaryKeyColumn()
	{

		String primaryKey = "";
		try
		{
			ISQLConnection con = _session.getSQLConnection();
			SQLDatabaseMetaData db = con.getSQLMetaData();
            PrimaryKeyInfo[] infos = db.getPrimaryKey(getTableInfo());
            for (int i=0; i < infos.length; i++) {
                primaryKey = infos[i].getColumnName();
            }
		}
		catch (SQLException ex)
		{
			_session.showErrorMessage(ex);
		}

		return primaryKey;
	}
}
