package net.sourceforge.squirrel_sql.plugins.refactoring.commands;



import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.SQLExecuterTask;
import net.sourceforge.squirrel_sql.fw.dialects.DatabaseObjectQualifier;
import net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect;
import net.sourceforge.squirrel_sql.fw.dialects.UserCancelledOperationException;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.plugins.refactoring.gui.AddViewDialog;

public class AddViewCommand extends AbstractRefactoringCommand
{
	
	@SuppressWarnings("unused")
	private final static ILogger log = LoggerController.createLogger(AddViewCommand.class);

	
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(AddViewCommand.class);

	static interface i18n
	{
		String SHOWSQL_DIALOG_TITLE = s_stringMgr.getString("AddViewCommand.sqlDialogTitle");
	}

	protected AddViewDialog customDialog;

	public AddViewCommand(ISession session, IDatabaseObjectInfo[] info)
	{
		super(session, info);
	}

	
	@Override
	protected void onExecute()
	{
		showCustomDialog();
	}

	
	@Override
	protected String[] generateSQLStatements() throws UserCancelledOperationException
	{
		DatabaseObjectQualifier qualifier =
			new DatabaseObjectQualifier(_info[0].getCatalogName(), _info[0].getSchemaName());

		String result =
			_dialect.getCreateViewSQL(customDialog.getViewName(),
				customDialog.getViewDefinition(),
				customDialog.getCheckOption(),
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
	protected boolean isRefactoringSupportedForDialect(HibernateDialect dialect)
	{
		return dialect.supportsCreateView();
	}
	
	private void showCustomDialog()
	{
		customDialog = new AddViewDialog();
		if (_dialect.supportsCheckOptionsForViews())
			customDialog.enableCheckOptions(true);

		customDialog.addExecuteListener(new ExecuteListener());
		customDialog.addEditSQLListener(new EditSQLListener(customDialog));
		customDialog.addShowSQLListener(new ShowSQLListener(i18n.SHOWSQL_DIALOG_TITLE, customDialog));
		customDialog.setLocationRelativeTo(_session.getApplication().getMainFrame());
		customDialog.setVisible(true);
	}
	
}
