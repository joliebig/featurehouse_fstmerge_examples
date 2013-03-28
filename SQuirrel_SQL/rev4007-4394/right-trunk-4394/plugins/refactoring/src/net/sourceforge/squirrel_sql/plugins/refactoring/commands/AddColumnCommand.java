package net.sourceforge.squirrel_sql.plugins.refactoring.commands;



import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.sql.SQLException;

import javax.swing.JOptionPane;

import net.sourceforge.squirrel_sql.client.gui.db.ColumnDetailDialog;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.SQLExecuterTask;
import net.sourceforge.squirrel_sql.fw.dialects.DatabaseObjectQualifier;
import net.sourceforge.squirrel_sql.fw.dialects.DialectFactory;
import net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import org.hibernate.HibernateException;

public class AddColumnCommand extends AbstractRefactoringCommand
{
	
	private final static ILogger s_log = LoggerController.createLogger(AddColumnCommand.class);

	
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(AddColumnCommand.class);

	static interface i18n
	{
		String SHOWSQL_DIALOG_TITLE = s_stringMgr.getString("AddColumnCommand.sqlDialogTitle");

		String COLUMN_ALREADY_EXISTS_TITLE = s_stringMgr.getString("AddColumnCommand.columnAlreadyExistsTitle");
	}

	protected ColumnDetailDialog customDialog;

	public AddColumnCommand(ISession session, IDatabaseObjectInfo[] info)
	{
		super(session, info);
	}

	
	@Override
	protected void onExecute()
	{
		showCustomDialog();
	}

	
	@Override
	protected String[] generateSQLStatements() throws Exception
	{
		TableColumnInfo info = customDialog.getColumnInfo();
		String[] result = null;
		DatabaseObjectQualifier qualifier =
			new DatabaseObjectQualifier(info.getCatalogName(), info.getSchemaName());
		
		try
		{
			result =  _dialect.getAddColumnSQL(info, qualifier, _sqlPrefs);
		} catch (HibernateException e1)
		{
			String dataType = customDialog.getSelectedTypeName();
			JOptionPane.showMessageDialog(customDialog,
				s_stringMgr.getString("AbstractRefactoringCommand.unsupportedTypeMsg",
					_dialect.getDisplayName(),
					dataType),
				AbstractRefactoringCommand.i18n.UNSUPPORTED_TYPE_TITLE,
				JOptionPane.ERROR_MESSAGE);
		} catch (UnsupportedOperationException e2)
		{
			_session.showMessage(s_stringMgr.getString("AddColumnCommand.unsupportedOperationMsg",
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

		GUIUtils.processOnSwingEventThread(new Runnable()
		{
			public void run()
			{
				customDialog.setVisible(false);
			}
		});
	}

	
	@Override
	protected boolean isRefactoringSupportedForDialect(HibernateDialect dialect)
	{
		
		return true;
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
						customDialog = new ColumnDetailDialog(ColumnDetailDialog.ADD_MODE);
						customDialog.setTableName(_info[0].getQualifiedName());
						customDialog.setSelectedDialect(_dialect.getDisplayName());
						customDialog.addExecuteListener(new AddColumnExecuteListener());
						customDialog.addEditSQLListener(new EditSQLListener(customDialog));
						customDialog.addShowSQLListener(new ShowSQLListener(i18n.SHOWSQL_DIALOG_TITLE, customDialog));
						customDialog.addDialectListListener(new DialectListListener());
						customDialog.setLocationRelativeTo(_session.getApplication().getMainFrame());
						customDialog.setVisible(true);
					}
				});
			}
		});
	}

	private class AddColumnExecuteListener extends ExecuteListener
	{
		public void actionPerformed(ActionEvent e)
		{
			String columnName = customDialog.getColumnInfo().getColumnName();
			try
			{
				if (!isColumnNameUnique(columnName))
				{
					String message =
						s_stringMgr.getString("AddColumnCommand.columnAlreadyExistsMsg",
							customDialog.getTableName(),
							columnName);
					JOptionPane.showMessageDialog(customDialog,
						message,
						i18n.COLUMN_ALREADY_EXISTS_TITLE,
						JOptionPane.ERROR_MESSAGE);
				} else
				{
					super.actionPerformed(e);
				}
			} catch (SQLException ex)
			{
				_session.showErrorMessage(ex);
				s_log.error("Unexpected exception - " + ex.getMessage(), ex);
			}
		}

		
		private boolean isColumnNameUnique(String columnName) throws SQLException
		{
			boolean result = true;
			ISQLDatabaseMetaData md = _session.getMetaData();
			TableColumnInfo[] columnInfos = md.getColumnInfo((ITableInfo) _info[0]);
			for (TableColumnInfo columnInfo : columnInfos)
			{
				String existingColumnName = columnInfo.getColumnName();
				if (columnName.equalsIgnoreCase(existingColumnName))
				{
					result = false;
					break;
				}
			}

			return result;
		}
	}

	private class DialectListListener implements ItemListener
	{
		public void itemStateChanged(ItemEvent e)
		{
			_dialect = DialectFactory.getDialect(customDialog.getSelectedDBName());
		}
	}
}