package net.sourceforge.squirrel_sql.plugins.refactoring.commands;



import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.SQLExecuterTask;
import net.sourceforge.squirrel_sql.fw.dialects.DatabaseObjectQualifier;
import net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect;
import net.sourceforge.squirrel_sql.fw.dialects.UserCancelledOperationException;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.SQLUtilities;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.plugins.refactoring.gui.AddModifySequenceDialog;

public class ModifySequenceCommand extends AbstractRefactoringCommand
{
	
	@SuppressWarnings("unused")
	private final static ILogger s_log = LoggerController.createLogger(ModifySequenceCommand.class);

	
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(ModifySequenceCommand.class);

	static interface i18n
	{
		String SHOWSQL_DIALOG_TITLE = s_stringMgr.getString("ModifySequenceCommand.sqlDialogTitle");

		String SQL_DIALOG_TITLE = s_stringMgr.getString("ModifySequenceCommand.sqlDialogTitle");

		String SQL_ERROR_SEQUENCE_DATA = s_stringMgr.getString("ModifySequenceCommand.sqlErrorNoSequenceData");
	}

	protected AddModifySequenceDialog customDialog;

	public ModifySequenceCommand(ISession session, IDatabaseObjectInfo[] info)
	{
		super(session, info);
	}

	
	@Override
	protected void onExecute() throws SQLException
	{
		showCustomDialog();
	}

	
	@Override
	protected String[] generateSQLStatements() throws UserCancelledOperationException
	{
		DatabaseObjectQualifier qualifier =
			new DatabaseObjectQualifier(_info[0].getCatalogName(), _info[0].getSchemaName());

		String[] result =
			_dialect.getAlterSequenceSQL(customDialog.getSequenceName(),
				customDialog.getIncrement(),
				customDialog.getMinimum(),
				customDialog.getMaximum(),
				customDialog.getStart(),
				customDialog.getCache(),
				customDialog.isCycled(),
				qualifier,
				_sqlPrefs);

		return result;
	}

	
	@Override
	protected void executeScript(String script)
	{
		CommandExecHandler handler = new CommandExecHandler(_session);

		SQLExecuterTask executer = new SQLExecuterTask(_session, script, handler);
		executer.run(); 

		_session.getApplication().getThreadPool().addTask(new Runnable()
		{
			public void run()
			{
				GUIUtils.processOnSwingEventThread(new Runnable()
				{
					public void run()
					{
						customDialog.setVisible(false);
						_session.getSchemaInfo().reload(_info[0]);
					}
				});
			}
		});
	}

	
	@Override
	protected boolean isRefactoringSupportedForDialect(HibernateDialect dialect)
	{
		return dialect.supportsAlterSequence();
	}

	private void showCustomDialog() throws SQLException
	{
		customDialog = createCustomDialog();
		customDialog.addExecuteListener(new ExecuteListener());
		customDialog.addEditSQLListener(new EditSQLListener(customDialog));
		customDialog.addShowSQLListener(new ShowSQLListener(i18n.SHOWSQL_DIALOG_TITLE, customDialog));
		customDialog.setLocationRelativeTo(_session.getApplication().getMainFrame());
		customDialog.setVisible(true);
	}

	private AddModifySequenceDialog createCustomDialog() throws SQLException
	{
		ResultSet rs = null;
		AddModifySequenceDialog result = null;

		String simpleName = _info[0].getSimpleName();
		DatabaseObjectQualifier qualifier =
			new DatabaseObjectQualifier(_info[0].getCatalogName(), _info[0].getSchemaName());
		String sql = _dialect.getSequenceInformationSQL(simpleName, qualifier, _sqlPrefs);

		try
		{
			rs = executeQuery(sql, simpleName);
			if (rs.next())
			{
				
				
				
				String last_value = rs.getString(1); 
				String max_value = rs.getString(2); 
				String min_value = rs.getString(3); 
				String cache_value = rs.getString(4); 
				String increment_by = rs.getString(5); 
				int cycleInt = rs.getInt(6); 
				boolean is_cyled = cycleInt == 1 ? true : false; 

				result =
					new AddModifySequenceDialog(	AddModifySequenceDialog.MODIFY_MODE,
															simpleName,
															last_value,
															increment_by,
															min_value,
															max_value,
															cache_value, is_cyled);
			} else {
				throw new IllegalStateException("createCustomDialog: failed to find sequence named : "+simpleName);
			}
		} finally
		{
			SQLUtilities.closeResultSet(rs, true);
		}
		return result;
	}

	
	private ResultSet executeQuery(String sql, String sequenceName) throws SQLException
	{
		ResultSet result = null;
		if (sql.endsWith("?") || sql.endsWith("?)"))
		{
			if (s_log.isDebugEnabled())
			{
				s_log.debug("ModifySequenceCommand: running sql=" + sql);
				s_log.debug("param sequenceName = " + sequenceName);
			}
			PreparedStatement stmt = _session.getSQLConnection().prepareStatement(sql);
			stmt.setString(1, sequenceName);
			result = stmt.executeQuery();
		} else
		{
			s_log.debug("ModifySequenceCommand: running sql=" + sql);
			Statement stmt = _session.getSQLConnection().createStatement();
			result = stmt.executeQuery(sql);
		}
		return result;
	}
}
