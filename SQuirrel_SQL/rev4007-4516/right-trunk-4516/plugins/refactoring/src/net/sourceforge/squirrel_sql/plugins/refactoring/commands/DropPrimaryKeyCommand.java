package net.sourceforge.squirrel_sql.plugins.refactoring.commands;



import java.sql.SQLException;
import java.util.ArrayList;

import net.sourceforge.squirrel_sql.client.gui.db.ColumnListDialog;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.SQLExecuterTask;
import net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect;
import net.sourceforge.squirrel_sql.fw.dialects.UserCancelledOperationException;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.PrimaryKeyInfo;
import net.sourceforge.squirrel_sql.fw.sql.SQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;


public class DropPrimaryKeyCommand extends AbstractRefactoringCommand
{
	
	@SuppressWarnings("unused")
	private final static ILogger s_log = LoggerController.createLogger(DropColumnCommand.class);

	
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(DropPrimaryKeyCommand.class);

	static interface i18n
	{
		String SHOWSQL_DIALOG_TITLE = s_stringMgr.getString("DropPrimaryKeyCommand.sqlDialogTitle");
	}

	
	protected ColumnListDialog customDialog;

	
	public DropPrimaryKeyCommand(ISession session, IDatabaseObjectInfo[] info)
	{
		super(session, info);
	}

	
	@Override
	protected void onExecute() throws SQLException
	{
		if (!(_info[0] instanceof ITableInfo))
		{
			return;
		}
		ITableInfo ti = (ITableInfo) _info[0];
		PrimaryKeyCommandUtility pkcUtil = new PrimaryKeyCommandUtility(super._session, _info);
		if (!pkcUtil.tableHasPrimaryKey())
		{
			_session.showErrorMessage(s_stringMgr.getString("DropPrimaryKeyCommand.noKeyToDrop",
				ti.getSimpleName()));
		} else
		{
			showCustomDialog();
		}
	}

	
	@Override
	protected String[] generateSQLStatements() throws UserCancelledOperationException
	{
		String result = null;

		try
		{
			result =
				_dialect.getDropPrimaryKeySQL(customDialog.getPrimaryKeyName(),
					customDialog.getTableName(),
					_qualifier,
					_sqlPrefs);
		} catch (UnsupportedOperationException e2)
		{
			_session.showMessage(s_stringMgr.getString("DropPrimaryKeyCommand.unsupportedOperationMsg",
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
		
		return true;
	}

	
	private void showCustomDialog() throws SQLException
	{
		ITableInfo ti = (ITableInfo) _info[0];
		TableColumnInfo[] columns = getPkTableColumns(ti);

		
		customDialog = new ColumnListDialog(columns, ColumnListDialog.DROP_PRIMARY_KEY_MODE);
		customDialog.addColumnSelectionListener(new ExecuteListener());
		customDialog.addEditSQLListener(new EditSQLListener(customDialog));
		customDialog.addShowSQLListener(new ShowSQLListener(i18n.SHOWSQL_DIALOG_TITLE, customDialog));
		customDialog.setLocationRelativeTo(_session.getApplication().getMainFrame());
		customDialog.setMultiSelection();
		customDialog.setTableName(ti.getQualifiedName());

		SQLDatabaseMetaData md = _session.getSQLConnection().getSQLMetaData();
		PrimaryKeyInfo[] infos = md.getPrimaryKey(ti);
		String pkName = infos[0].getSimpleName();
		customDialog.setPrimaryKeyName(pkName);
		customDialog.setVisible(true);
	}

	
	private TableColumnInfo[] getPkTableColumns(ITableInfo ti) throws SQLException
	{
		ArrayList<TableColumnInfo> result = new ArrayList<TableColumnInfo>();
		PrimaryKeyInfo[] pkCols = _session.getMetaData().getPrimaryKey(ti);
		TableColumnInfo[] colInfos = _session.getMetaData().getColumnInfo(ti);

		for (PrimaryKeyInfo pkInfo : pkCols)
		{
			String pkColName = pkInfo.getQualifiedColumnName();
			for (TableColumnInfo colInfo : colInfos)
			{
				if (colInfo.getSimpleName().equals(pkColName))
				{
					result.add(colInfo);
				}
			}
		}
		return result.toArray(new TableColumnInfo[result.size()]);
	}
	
}