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
import net.sourceforge.squirrel_sql.plugins.refactoring.gui.RenameTableDialog;

public class RenameTableCommand extends AbstractRefactoringCommand
{
	
	@SuppressWarnings("unused")
	private final ILogger s_log = LoggerController.createLogger(RenameTableCommand.class);

	
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(RenameTableCommand.class);

	static interface i18n
	{
		String SHOWSQL_DIALOG_TITLE = s_stringMgr.getString("RenameTableCommand.sqlDialogTitle");
	}

	protected RenameTableDialog customDialog;

	public RenameTableCommand(ISession session, IDatabaseObjectInfo[] dbInfo)
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
		String result = null;
		try
		{
			DatabaseObjectQualifier qualifier =
				new DatabaseObjectQualifier(_info[0].getCatalogName(), _info[0].getSchemaName());

			result =
				_dialect.getRenameTableSQL(_info[0].getSimpleName(),
					customDialog.getNewSimpleName(),
					qualifier,
					_sqlPrefs);
		} catch (UnsupportedOperationException e2)
		{
			_session.showMessage(s_stringMgr.getString("RenameTableCommand.unsupportedOperationMsg",
				_dialect.getDisplayName()));
		}
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
		return dialectExt.supportsRenameTable();
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
						customDialog = new RenameTableDialog(_info, RenameTableDialog.DIALOG_TYPE_TABLE);
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
