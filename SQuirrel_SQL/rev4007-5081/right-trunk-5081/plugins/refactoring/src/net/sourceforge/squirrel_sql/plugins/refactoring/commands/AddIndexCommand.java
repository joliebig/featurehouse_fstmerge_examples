package net.sourceforge.squirrel_sql.plugins.refactoring.commands;

import java.sql.SQLException;
import java.util.TreeSet;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.SQLExecuterTask;
import net.sourceforge.squirrel_sql.fw.dialects.DatabaseObjectQualifier;
import net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.plugins.refactoring.gui.AddIndexDialog;

public class AddIndexCommand extends AbstractRefactoringCommand
{
	
	@SuppressWarnings("unused")
	private final static ILogger s_log = LoggerController.createLogger(AddIndexCommand.class);

	
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(AddIndexCommand.class);

	static interface i18n
	{
		String SHOWSQL_DIALOG_TITLE = s_stringMgr.getString("AddIndexCommand.sqlDialogTitle");
	}

	protected AddIndexDialog customDialog;

	public AddIndexCommand(ISession session, IDatabaseObjectInfo[] info)
	{
		super(session, info);
	}

	
	@Override
	protected void onExecute() throws SQLException
	{
		if (!(_info[0] instanceof ITableInfo))
			return;

		showCustomDialog();
	}

	
	@Override
	protected String[] generateSQLStatements() throws Exception
	{
		DatabaseObjectQualifier qualifier =
			new DatabaseObjectQualifier(_info[0].getCatalogName(), _info[0].getSchemaName());

		String result =
			_dialect.getCreateIndexSQL(customDialog.getIndexName(),
				_info[0].getSimpleName(),
				customDialog.getAccessMethod(),
				customDialog.getIndexColumns(),
				customDialog.isUniqueSet(),
				customDialog.getTablespaceText(),
				customDialog.getConstraints(),
				qualifier,
				_sqlPrefs);
		return new String[] { result };
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

	
	@Override
	protected boolean isRefactoringSupportedForDialect(HibernateDialect dialectExt)
	{
		return dialectExt.supportsCreateIndex();
	}

	private void showCustomDialog() throws SQLException
	{
		ITableInfo selectedTable = (ITableInfo) _info[0];
		TableColumnInfo[] tableColumnInfos = _session.getMetaData().getColumnInfo(selectedTable);
		TreeSet<String> localColumns = new TreeSet<String>();
		for (TableColumnInfo columns : tableColumnInfos)
		{
			localColumns.add(columns.getColumnName());
		}

		customDialog = new AddIndexDialog(localColumns.toArray(new String[] {}));
		if (_dialect.supportsAccessMethods())
			customDialog.setAccessMethods(true, _dialect.getIndexAccessMethodsTypes());
		if (_dialect.supportsTablespace())
			customDialog.enableTablespaceField(true);
		customDialog.addExecuteListener(new ExecuteListener());
		customDialog.addEditSQLListener(new EditSQLListener(customDialog));
		customDialog.addShowSQLListener(new ShowSQLListener(i18n.SHOWSQL_DIALOG_TITLE, customDialog));
		customDialog.setLocationRelativeTo(_session.getApplication().getMainFrame());
		customDialog.setVisible(true);
	}

}
