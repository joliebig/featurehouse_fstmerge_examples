package net.sourceforge.squirrel_sql.plugins.refactoring.commands;



import java.sql.SQLException;

import net.sourceforge.squirrel_sql.client.gui.db.ColumnListDialog;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.SQLExecuterTask;
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

public class DropColumnCommand extends AbstractRefactoringCommand
{
	
	private final static ILogger s_log = LoggerController.createLogger(DropColumnCommand.class);

	
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(DropColumnCommand.class);

	static interface i18n
	{
		
		
		String SINGLE_COLUMN_MESSAGE = s_stringMgr.getString("RemoveColumnAction.singleColumnMessage");

		String SHOWSQL_DIALOG_TITLE = s_stringMgr.getString("RemoveColumnCommand.sqlDialogTitle");
	}

	
	protected ColumnListDialog customDialog;

	
	public DropColumnCommand(ISession session, IDatabaseObjectInfo[] info)
	{
		super(session, info);
	}

	
	@Override
	protected void onExecute() throws SQLException
	{
		if (!(_info[0] instanceof ITableInfo))
		{
			s_log.error("onExecute: _info[0] isn't an instance of ITableInfo: class="
				+ _info[0].getClass().getName());

			return;
		}

		ITableInfo ti = (ITableInfo) _info[0];
		TableColumnInfo[] columns = _session.getSQLConnection().getSQLMetaData().getColumnInfo(ti);

		if (columns.length < 2)
		{
			
			_session.showErrorMessage(i18n.SINGLE_COLUMN_MESSAGE);
		} else
		{
			showCustomDialog();
		}
	}

	
	@Override
	protected String[] generateSQLStatements() throws UserCancelledOperationException
	{
		TableColumnInfo[] columns = customDialog.getSelectedColumnList();

		String[] result = new String[columns.length];
		try
		{
			String tableName = _info[0].getSimpleName();
			for (int i = 0; i < columns.length; i++)
			{
				TableColumnInfo info = columns[i];
				String columnName = info.getColumnName();
				result[i] = _dialect.getColumnDropSQL(tableName, columnName, _qualifier, _sqlPrefs);
			}
		} catch (UnsupportedOperationException e2)
		{
			_session.showMessage(s_stringMgr.getString("RemoveColumnCommand.unsupportedOperationMsg",
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
						_session.getSchemaInfo().reload(_info[0]);
					}
				});
			}
		});
	}

	
	@Override
	protected boolean isRefactoringSupportedForDialect(HibernateDialect dialect)
	{
		return dialect.supportsDropColumn();
	}
	
	private void showCustomDialog() throws SQLException
	{
		ITableInfo ti = (ITableInfo) _info[0];
		TableColumnInfo[] columns = _session.getMetaData().getColumnInfo(ti);

		
		customDialog = new ColumnListDialog(columns, ColumnListDialog.DROP_COLUMN_MODE);
		customDialog.setMultiSelection();
		customDialog.setTableName(ti.getQualifiedName());

		customDialog.addColumnSelectionListener(new ExecuteListener());
		customDialog.addEditSQLListener(new EditSQLListener(customDialog));
		customDialog.addShowSQLListener(new ShowSQLListener(i18n.SHOWSQL_DIALOG_TITLE, customDialog));
		customDialog.setLocationRelativeTo(_session.getApplication().getMainFrame());
		customDialog.setVisible(true);
	}
	
}