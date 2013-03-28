package net.sourceforge.squirrel_sql.client.session.action;


import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;

import net.sourceforge.squirrel_sql.client.gui.ProgessCallBackDialog;
import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.SQLExecuterTask;
import net.sourceforge.squirrel_sql.fw.dialects.DialectFactory;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.SQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.sql.SQLUtilities;
import net.sourceforge.squirrel_sql.fw.util.ICommand;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.Utilities;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;


public class DeleteTablesCommand implements ICommand
{
	
	private final ILogger s_log = LoggerController.createLogger(DeleteTablesCommand.class);

	
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(DeleteTablesCommand.class);

	static interface i18n
	{

		
		String PROGRESS_DIALOG_TITLE = s_stringMgr.getString("DeleteTablesCommand.progressDialogTitle");

		
		String LOADING_PREFIX = s_stringMgr.getString("DeleteTablesCommand.loadingPrefix");

	}

	
	private final ISession _session;

	
	private final List<ITableInfo> _tables;

	
	private HashSet<String> matViewLookup = null;

	
	private IObjectTreeAPI _tree;

	
	public DeleteTablesCommand(IObjectTreeAPI tree, List<ITableInfo> tables)
	{
		super();
		if (tree == null) { throw new IllegalArgumentException("ISession == null"); }
		if (tables == null) { throw new IllegalArgumentException("IDatabaseObjectInfo[] == null"); }

		_session = tree.getSession();
		_tree = tree;
		_tables = tables;
	}

	
	public void execute()
	{
		ProgessCallBackDialog cb =
			new ProgessCallBackDialog(_session.getApplication().getMainFrame(), i18n.PROGRESS_DIALOG_TITLE,
				_tables.size());

		cb.setLoadingPrefix(i18n.LOADING_PREFIX);
		DeleteExecuter executer = new DeleteExecuter(cb);
		_session.getApplication().getThreadPool().addTask(executer);
	}

	private class DeleteExecuter implements Runnable
	{

		ProgessCallBackDialog _cb = null;

		public DeleteExecuter(ProgessCallBackDialog cb)
		{
			Utilities.checkNull("DeleteExecuter.init", "cb", cb);
			_cb = cb;
		}

		public void run()
		{
			final SQLDatabaseMetaData md = _session.getSQLConnection().getSQLMetaData();
			List<ITableInfo> orderedTables = _tables;
			try
			{
				orderedTables = SQLUtilities.getDeletionOrder(_tables, md, _cb);
			}
			catch (Exception e)
			{
				s_log.error("Unexpected exception while attempting to order tables", e);
			} finally {
				GUIUtils.processOnSwingEventThread(new Runnable()
				{
					public void run()				
					{
						_cb.setVisible(false);
					}
				});
			}
			final String sqlSep = _session.getQueryTokenizer().getSQLStatementSeparator();
			String cascadeClause = null;
			try
			{
				cascadeClause = md.getCascadeClause();
			}
			catch (SQLException e)
			{
				s_log.error("Unexpected exception while attempting to get cascade clause", e);
			}
			final StringBuilder buf = new StringBuilder();
			for (ITableInfo ti : orderedTables)
			{
				
				if (isMaterializedView(ti, _session))
				{
					continue;
				}
				buf.append("DELETE FROM ").append(ti.getQualifiedName());
				if (cascadeClause != null && !cascadeClause.equals(""))
				{
					buf.append(" ").append(cascadeClause);
				}
				buf.append(" ").append(sqlSep).append(" ").append('\n');
			}
			if (buf.length() == 0) { return; }
			SQLExecuterTask executer = new SQLExecuterTask(_session, buf.toString(), null);

			
			executer.run();			

			GUIUtils.processOnSwingEventThread(new Runnable()
			{
				public void run()				
				{
					_tree.refreshSelectedNodes();
				}
			});
		}
	}

	
	private boolean isMaterializedView(ITableInfo ti, ISession session)
	{
		if (!DialectFactory.isOracle(session.getMetaData()))
		{
			
			return false;
		}
		if (matViewLookup == null)
		{
			initMatViewLookup(session, ti.getSchemaName());
		}
		return matViewLookup.contains(ti.getSimpleName());
	}

	private void initMatViewLookup(ISession session, String schema)
	{
		matViewLookup = new HashSet<String>();
		
		
		
		
		String sql =
			"SELECT TABLE_NAME FROM ALL_TAB_COMMENTS " + "where COMMENTS like 'snapshot%' " + "and OWNER = ? ";

		PreparedStatement stmt = null;
		ResultSet rs = null;
		try
		{
			stmt = session.getSQLConnection().prepareStatement(sql);
			stmt.setString(1, schema);
			rs = stmt.executeQuery();
			if (rs.next())
			{
				String tableName = rs.getString(1);
				matViewLookup.add(tableName);
			}
		}
		catch (SQLException e)
		{
			s_log.error("Unexpected exception while attempting to find mat. views " + "in schema: " + schema, e);
		}
		finally
		{
			SQLUtilities.closeResultSet(rs, true);
		}

	}

}
