package net.sourceforge.squirrel_sql.plugins.refactoring.commands;



import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

import javax.swing.JOptionPane;

import net.sourceforge.squirrel_sql.client.gui.db.ColumnDetailDialog;
import net.sourceforge.squirrel_sql.client.gui.db.ColumnListDialog;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.SQLExecuterTask;
import net.sourceforge.squirrel_sql.fw.dialects.DatabaseObjectQualifier;
import net.sourceforge.squirrel_sql.fw.dialects.DialectFactory;
import net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.plugins.refactoring.DBUtil;

import org.hibernate.HibernateException;


public class ModifyColumnCommand extends AbstractRefactoringCommand
{
	
	private final static ILogger s_log = LoggerController.createLogger(ModifyColumnCommand.class);

	
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(ModifyColumnCommand.class);

	static interface i18n
	{
		String SHOWSQL_DIALOG_TITLE = s_stringMgr.getString("ModifyColumnCommand.sqlDialogTitle");

		String MODIFY_ONE_COL_MSG = s_stringMgr.getString("ModifyColumnCommand.modifyOneColMsg");
	}

	
	private ColumnListDialog listDialog = null;

	
	protected ColumnDetailDialog customDialog;

	
	private TableColumnInfo columnToModify = null;

	
	
	public ModifyColumnCommand(ISession session, IDatabaseObjectInfo[] info)
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

		
		if (columns.length == 1)
		{
			if (s_log.isDebugEnabled()) {
				s_log.debug("Table has exactly one column, so presenting the modify column dialog");
			}
			columnToModify = columns[0];
			showCustomDialog();
		} else
		{
			if (s_log.isDebugEnabled()) {
				s_log.debug("Table has exactly " + columns.length
					+ " columns, so presenting the column list selection dialog");
			}
			listDialog = new ColumnListDialog(columns, ColumnListDialog.MODIFY_COLUMN_MODE);
			listDialog.setTableName(ti.getQualifiedName());
			listDialog.setSingleSelection();
			listDialog.addColumnSelectionListener(new ColumnListSelectionActionListener());
			listDialog.setLocationRelativeTo(_session.getApplication().getMainFrame());
			listDialog.setVisible(true);
		}
	}

	
	@Override
	protected String[] generateSQLStatements()
	{
		String[] result = null;

		TableColumnInfo to = customDialog.getColumnInfo();
		String dbName = customDialog.getSelectedDBName();
		_dialect = DialectFactory.getDialect(dbName);

		DatabaseObjectQualifier qualifier =
			new DatabaseObjectQualifier(_info[0].getCatalogName(), _info[0].getSchemaName());
		
		try
		{
			result = DBUtil.getAlterSQLForColumnChange(columnToModify, to, _dialect, qualifier, _sqlPrefs);
		} catch (HibernateException e1)
		{
			JOptionPane.showMessageDialog(customDialog,
				s_stringMgr.getString("AbstractRefactoringCommand.unsupportedTypeMsg",
					_dialect.getDisplayName(),
					customDialog.getSelectedTypeName()),
				AbstractRefactoringCommand.i18n.UNSUPPORTED_TYPE_TITLE,
				JOptionPane.ERROR_MESSAGE);
		} catch (UnsupportedOperationException e2)
		{
			_session.showErrorMessage(e2.getMessage());
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
	protected boolean isRefactoringSupportedForDialect(HibernateDialect dialectExt)
	{
		
		return true;
	}

	private void showCustomDialog()
	{
		customDialog = new ColumnDetailDialog(ColumnDetailDialog.MODIFY_MODE);
		customDialog.setExistingColumnInfo(columnToModify);
		customDialog.setTableName(_info[0].getQualifiedName());
		customDialog.setSelectedDialect(_dialect.getDisplayName());

		customDialog.addExecuteListener(new ExecuteListener());
		customDialog.addEditSQLListener(new EditSQLListener(customDialog));
		customDialog.addShowSQLListener(new ShowSQLListener(i18n.SHOWSQL_DIALOG_TITLE, customDialog));
		customDialog.setLocationRelativeTo(_session.getApplication().getMainFrame());
		customDialog.setVisible(true);
	}

	private class ColumnListSelectionActionListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			if (listDialog == null)
				return;

			listDialog.setVisible(false);
			TableColumnInfo[] colInfos = listDialog.getSelectedColumnList();
			if (colInfos == null || colInfos.length != 1)
			{
				_session.showMessage(i18n.MODIFY_ONE_COL_MSG);
				return;
			}
			columnToModify = colInfos[0];

			showCustomDialog();
		}
	}
}