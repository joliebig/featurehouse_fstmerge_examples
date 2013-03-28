package net.sourceforge.squirrel_sql.plugins.refactoring.commands;



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
import net.sourceforge.squirrel_sql.plugins.refactoring.gui.RenameTableDialog;

public class RenameViewCommand extends AbstractRefactoringCommand
{
	
	@SuppressWarnings("unused")
	private final ILogger s_log = LoggerController.createLogger(RenameViewCommand.class);

	
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(RenameViewCommand.class);

	static interface i18n
	{
		String SHOWSQL_DIALOG_TITLE = s_stringMgr.getString("RenameViewCommand.sqlDialogTitle");
	}

	protected RenameTableDialog customDialog;

	public RenameViewCommand(ISession session, IDatabaseObjectInfo[] dbInfo)
	{
		super(session, dbInfo);
	}

	
	@Override
	protected void onExecute()
	{
		showCustomDialog();
	}

	
	@Override
	protected String[] generateSQLStatements() throws UserCancelledOperationException
	{
		String[] result = null;
		try
		{
			DatabaseObjectQualifier qualifier =
				new DatabaseObjectQualifier(_info[0].getCatalogName(), _info[0].getSchemaName());
			
			String viewName = _info[0].getSimpleName();
			String newViewName = customDialog.getNewSimpleName();
			if (_dialect.supportsRenameView()) {
				result = 
					_dialect.getRenameViewSQL(viewName, newViewName, qualifier, _sqlPrefs);
			} else {
				String viewDefSql = _dialect.getViewDefinitionSQL(viewName, qualifier, _sqlPrefs);
				String viewDefinition = getViewDef(newViewName, viewDefSql);
				String dropOldViewSql = _dialect.getDropViewSQL(viewName, false, qualifier, _sqlPrefs);
				result = new String[] { viewDefinition, dropOldViewSql };
			}
		} catch (UnsupportedOperationException e2)
		{
			_session.showMessage(s_stringMgr.getString("RenameViewCommand.unsupportedOperationMsg",
				_dialect.getDisplayName()));
		}
		return result;
	}

	private String getViewDef (String newViewName, String viewDefQuery) {
		String result = null;
		ResultSet rs = null;
		Statement stmt = null;
		try {
			stmt = _session.getSQLConnection().createStatement();
			rs = stmt.executeQuery(viewDefQuery);
			if (rs.next()) {
				result = rs.getString(1);
				int asIndex = result.toUpperCase().indexOf("AS");
				if (asIndex != -1) {
					result = "CREATE VIEW " + newViewName + " AS " + result.substring(asIndex + 2);
				}
			}
		} catch (SQLException e) {
			s_log.error("getViewDef: unexpected exception - "+e.getMessage(), e);
		} finally {
			SQLUtilities.closeResultSet(rs);
			SQLUtilities.closeStatement(stmt);
		}
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
						_session.getSchemaInfo().reloadAll();
					}
				});
			}
		});
	}

	
	protected boolean isRefactoringSupportedForDialect(HibernateDialect dialect)
	{
		return dialect.supportsRenameView() || dialect.supportsViewDefinition();
	}

	private void showCustomDialog()
	{
		_session.getApplication().getThreadPool().addTask(new Runnable()
		{
			public void run()
			{
				GUIUtils.processOnSwingEventThread(new Runnable()
				{
					public void run()
					{
						customDialog = new RenameTableDialog(_info, RenameTableDialog.DIALOG_TYPE_VIEW);
						customDialog.addExecuteListener(new ExecuteListener());
						customDialog.addEditSQLListener(new EditSQLListener(customDialog));
						customDialog.addShowSQLListener(new ShowSQLListener(i18n.SHOWSQL_DIALOG_TITLE, customDialog));
						customDialog.setLocationRelativeTo(_session.getApplication().getMainFrame());
						customDialog.setVisible(true);
					}
				});
			}
		});
	}
}
