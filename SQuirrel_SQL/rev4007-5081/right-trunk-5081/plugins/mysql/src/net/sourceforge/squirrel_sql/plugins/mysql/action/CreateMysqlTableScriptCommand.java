package net.sourceforge.squirrel_sql.plugins.mysql.action;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.util.BaseException;
import net.sourceforge.squirrel_sql.fw.util.ICommand;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;

import net.sourceforge.squirrel_sql.plugins.mysql.MysqlPlugin;

class CreateMysqlTableScriptCommand implements ICommand
{
	
	private final static ILogger s_log =
		LoggerController.createLogger(CreateMysqlTableScriptCommand.class);

	
	private ISession _session;

	
	private final MysqlPlugin _plugin;

	
	public CreateMysqlTableScriptCommand(ISession session, MysqlPlugin plugin)
	{
		super();
		_session = session;
		_plugin = plugin;
	}

	
	public void execute() throws BaseException
	{
		final ISQLConnection conn = _session.getSQLConnection();
		final StringBuffer buf = new StringBuffer(2048);
		final String sep = " " + _session.getQueryTokenizer().getSQLStatementSeparator();

		try
		{
			final Statement stmt = conn.createStatement();
			try
			{
				IObjectTreeAPI api = _session.getSessionInternalFrame().getObjectTreeAPI();
				IDatabaseObjectInfo[] dbObjs = api.getSelectedDatabaseObjects();
				for (int i = 0; i < dbObjs.length; ++i)
				{
					final ResultSet rs = stmt.executeQuery("show create table "
												+ dbObjs[i].getQualifiedName());
					try
					{
						if (rs.next())
						{
							buf.append(rs.getString(2)).append(sep).append('\n');
						}
					}
					finally
					{
						rs.close();
					}
				}
			}
			finally
			{
				try
				{
					stmt.close();
				}
				catch (Exception ex)
				{
					s_log.error("Error occured closing PreparedStatement", ex);
				}
			}

			_session.getSessionInternalFrame().getSQLPanelAPI().appendSQLScript(buf.toString(), true);
			_session.selectMainTab(ISession.IMainPanelTabIndexes.SQL_TAB);
		}
		catch (SQLException ex)
		{
			throw new BaseException(ex);
		}
	}
}
