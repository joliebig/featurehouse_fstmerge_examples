package net.sourceforge.squirrel_sql.plugins.refactoring.commands;



import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;

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
import net.sourceforge.squirrel_sql.plugins.refactoring.gui.AddUniqueConstraintDialog;

public class AddUniqueConstraintCommand extends AbstractRefactoringCommand
{
	
	@SuppressWarnings("unused")
	private final static ILogger s_log = LoggerController.createLogger(AddUniqueConstraintCommand.class);

	
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(AddUniqueConstraintCommand.class);

	static interface i18n
	{
		String SHOWSQL_DIALOG_TITLE = s_stringMgr.getString("AddUniqueConstraintCommand.sqlDialogTitle");
	}

	protected AddUniqueConstraintDialog customDialog;

	private HashMap<String, TableColumnInfo> columnMap = new HashMap<String, TableColumnInfo>();
	
	public AddUniqueConstraintCommand(ISession session, IDatabaseObjectInfo[] info)
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

	protected void showCustomDialog() throws SQLException
	{
		ITableInfo selectedTable = (ITableInfo) _info[0];
		TableColumnInfo[] tableColumnInfos = _session.getMetaData().getColumnInfo(selectedTable);
		
		TreeSet<String> localColumns = new TreeSet<String>();
		for (TableColumnInfo column : tableColumnInfos)
		{
			
			
			columnMap.put(column.getColumnName(), column);
			
			
			localColumns.add(column.getColumnName());
		}

		customDialog =
			new AddUniqueConstraintDialog(selectedTable.getSimpleName(), localColumns.toArray(new String[] {}));
		customDialog.addExecuteListener(new ExecuteListener());
		customDialog.addEditSQLListener(new EditSQLListener(customDialog));
		customDialog.addShowSQLListener(new ShowSQLListener(i18n.SHOWSQL_DIALOG_TITLE, customDialog));
		customDialog.setLocationRelativeTo(_session.getApplication().getMainFrame());
		customDialog.setVisible(true);

	}

	
	@Override
	protected String[] generateSQLStatements() throws UserCancelledOperationException
	{
		DatabaseObjectQualifier qualifier =
			new DatabaseObjectQualifier(_info[0].getCatalogName(), _info[0].getSchemaName());

		List<String> columnNames = customDialog.getUniqueColumns();
		ArrayList<TableColumnInfo> columns = new ArrayList<TableColumnInfo>();
		for (String columnName : columnNames) {
			columns.add(columnMap.get(columnName));
		}
		
		String[] result =
			_dialect.getAddUniqueConstraintSQL(_info[0].getSimpleName(),
				customDialog.getConstraintName(),
				columns.toArray(new TableColumnInfo[] {}),
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
		return dialect.supportsAddUniqueConstraint();
	}
	
}
