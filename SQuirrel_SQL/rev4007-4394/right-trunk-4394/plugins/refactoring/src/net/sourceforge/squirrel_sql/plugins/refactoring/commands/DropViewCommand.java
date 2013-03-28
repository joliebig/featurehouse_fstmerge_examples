package net.sourceforge.squirrel_sql.plugins.refactoring.commands;



import java.util.ArrayList;

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
import net.sourceforge.squirrel_sql.plugins.refactoring.gui.DefaultDropDialog;


public class DropViewCommand extends AbstractRefactoringCommand
{
	
	@SuppressWarnings("unused")
	private final ILogger s_log = LoggerController.createLogger(DropViewCommand.class);

	
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(DropViewCommand.class);

	static interface i18n
	{
		String SHOWSQL_DIALOG_TITLE = s_stringMgr.getString("DropViewCommand.sqlDialogTitle");
	}

	protected DefaultDropDialog customDialog;

	public DropViewCommand(ISession session, IDatabaseObjectInfo[] dbInfo)
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
		ArrayList<String> result = new ArrayList<String>();

		try
		{
			for (IDatabaseObjectInfo dbo : _info)
			{
				result.add(_dialect.getDropViewSQL(dbo.getSimpleName(),
					customDialog.isCascadeSelected(),
					new DatabaseObjectQualifier(dbo.getCatalogName(), dbo.getSchemaName()),
					_sqlPrefs)
					+ "\n");
			}
		} catch (UnsupportedOperationException e2)
		{
			_session.showMessage(s_stringMgr.getString("DropViewCommand.unsupportedOperationMsg",
				_dialect.getDisplayName()));
		}

		return result.toArray(new String[] {});
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
		return dialect.supportsDropView();
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
						customDialog = new DefaultDropDialog(_info, DefaultDropDialog.DIALOG_TYPE_VIEW);
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
