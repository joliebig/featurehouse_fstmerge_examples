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


public class AddPrimaryKeyCommand extends AbstractRefactoringCommand
{
	
	@SuppressWarnings("unused")
	private final static ILogger s_log = LoggerController.createLogger(DropColumnCommand.class);

	
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(AddPrimaryKeyCommand.class);

	static interface i18n
	{
		String SHOWSQL_DIALOG_TITLE = s_stringMgr.getString("AddPrimaryKeyCommand.sqlDialogTitle");
	}

	
	protected ColumnListDialog customDialog;

	
	public AddPrimaryKeyCommand(ISession session, IDatabaseObjectInfo[] info)
	{
		super(session, info);
	}

	
	protected void onExecute() throws SQLException
	{
		if (!(_info[0] instanceof ITableInfo))
		{
			return;
		}

		ITableInfo ti = (ITableInfo) _info[0];
		PrimaryKeyCommandUtility pkcUtil = new PrimaryKeyCommandUtility(_session, _info);
		if (pkcUtil.tableHasPrimaryKey())
		{
			_session.showErrorMessage(s_stringMgr.getString("AddPrimaryKeyCommand.primaryKeyExists",
				ti.getSimpleName()));
		} else
		{
			showCustomDialog();
		}
	}

	
	@Override
	protected String[] generateSQLStatements() throws UserCancelledOperationException
	{
		return _dialect.getAddPrimaryKeySQL(customDialog.getPrimaryKeyName(),
			customDialog.getSelectedColumnList(),
			(ITableInfo) _info[0], _qualifier, _sqlPrefs);
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
		
		return true;
	}

	
	public void showCustomDialog() throws SQLException
	{
		ITableInfo ti = (ITableInfo) _info[0];
		TableColumnInfo[] columns = _session.getMetaData().getColumnInfo(ti);
		if (columns == null || columns.length == 0)
		{
			_session.showErrorMessage(s_stringMgr.getString("AddPrimaryKeyCommand.noColumns", ti.getSimpleName()));
			return;
		}

		
		customDialog = new ColumnListDialog(columns, ColumnListDialog.ADD_PRIMARY_KEY_MODE);
		customDialog.setTableName(ti.getQualifiedName());
		
		customDialog.setPrimaryKeyName("PK_" + columns[0].getTableName().toUpperCase());

		customDialog.addColumnSelectionListener(new ExecuteListener());
		customDialog.addEditSQLListener(new EditSQLListener(customDialog));
		customDialog.addShowSQLListener(new ShowSQLListener(i18n.SHOWSQL_DIALOG_TITLE, customDialog));
		customDialog.setLocationRelativeTo(_session.getApplication().getMainFrame());
		customDialog.setMultiSelection();
		customDialog.setVisible(true);
	}

}