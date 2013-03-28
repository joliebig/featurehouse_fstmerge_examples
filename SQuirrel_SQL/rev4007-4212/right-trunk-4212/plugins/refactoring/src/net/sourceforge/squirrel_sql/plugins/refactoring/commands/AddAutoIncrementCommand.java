package net.sourceforge.squirrel_sql.plugins.refactoring.commands;



import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

import net.sourceforge.squirrel_sql.client.gui.db.ColumnListDialog;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.SQLExecuterTask;
import net.sourceforge.squirrel_sql.fw.dialects.DatabaseObjectQualifier;
import net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect;
import net.sourceforge.squirrel_sql.fw.dialects.UserCancelledOperationException;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.plugins.refactoring.gui.AddAutoIncrementDialog;

public class AddAutoIncrementCommand extends AbstractRefactoringCommand
{
	
	@SuppressWarnings("unused")
	private final ILogger s_log = LoggerController.createLogger(AddAutoIncrementCommand.class);

	
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(AddAutoIncrementCommand.class);

	static interface i18n
	{
		String SHOWSQL_DIALOG_TITLE = s_stringMgr.getString("AddAutoIncrementCommand.sqlDialogTitle");
	}

	protected AddAutoIncrementDialog customDialog;

	private ColumnListDialog listDialog;

	private TableColumnInfo columnToModify;

	public AddAutoIncrementCommand(ISession session, IDatabaseObjectInfo[] dbInfo)
	{
		super(session, dbInfo);
	}

	
	@Override
	protected void onExecute() throws SQLException
	{
		ITableInfo selectedTable = (ITableInfo) _info[0];
		TableColumnInfo[] tableColumnInfos = _session.getMetaData().getColumnInfo(selectedTable);

		if (tableColumnInfos.length == 1)
		{
			columnToModify = tableColumnInfos[0];
			showCustomDialog();
		} else
		{
			listDialog = new ColumnListDialog(tableColumnInfos, ColumnListDialog.MODIFY_COLUMN_MODE);
			listDialog.addColumnSelectionListener(new ColumnListSelectionActionListener());
			listDialog.setLocationRelativeTo(_session.getApplication().getMainFrame());
			listDialog.setSingleSelection();
			listDialog.setTableName(selectedTable.getSimpleName());
			listDialog.setVisible(true);
		}
	}

	
	@Override
	protected String[] generateSQLStatements() throws UserCancelledOperationException
	{
		String[] result = new String[0];

		if (_dialect.supportsAutoIncrement())
		{
			DatabaseObjectQualifier qualifier =
				new DatabaseObjectQualifier(columnToModify.getCatalogName(), columnToModify.getSchemaName());

			result = _dialect.getAddAutoIncrementSQL(columnToModify, qualifier, _sqlPrefs);
		} else
		{
			_session.showMessage(s_stringMgr.getString("AddAutoIncrementCommand.unsupportedOperationMsg",
				_dialect.getDisplayName()));
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

	
	@Override
	protected boolean isRefactoringSupportedForDialect(HibernateDialect dialectExt)
	{
		return dialectExt.supportsAutoIncrement();
	}

	private void showCustomDialog()
	{
		_session.getApplication().getThreadPool().addTask(new Runnable()
		{
			public void run()
			{
				customDialog = new AddAutoIncrementDialog(columnToModify);
				customDialog.addExecuteListener(new ExecuteListener());
				customDialog.addEditSQLListener(new EditSQLListener(customDialog));
				customDialog.addShowSQLListener(new ShowSQLListener(i18n.SHOWSQL_DIALOG_TITLE, customDialog));
				customDialog.setLocationRelativeTo(_session.getApplication().getMainFrame());
				customDialog.setVisible(true);
			}
		});
	}

	private class ColumnListSelectionActionListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			if (listDialog == null)
			{
				System.err.println("dialog was null");
				return;
			}
			listDialog.setVisible(false);

			columnToModify = listDialog.getSelectedColumnList()[0];
			showCustomDialog();
		}
	}
}
