package net.sourceforge.squirrel_sql.plugins.refactoring.commands;



import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.SQLExecuterTask;
import net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.sql.ForeignKeyInfo;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.plugins.refactoring.gui.DefaultDropDialog;
import net.sourceforge.squirrel_sql.plugins.refactoring.gui.DefaultListDialog;

public class DropForeignKeyCommand extends AbstractRefactoringCommand
{
	
	@SuppressWarnings("unused")
	private final static ILogger s_log = LoggerController.createLogger(DropForeignKeyCommand.class);

	
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(DropForeignKeyCommand.class);

	static interface i18n
	{
		String SHOWSQL_DIALOG_TITLE = s_stringMgr.getString("DropForeignKeyCommand.sqlDialogTitle");
	}

	protected DefaultDropDialog customDialog;

	private DefaultListDialog _listDialog;

	private ForeignKeyInfo[] _foreignKeyInfo = null;

	public DropForeignKeyCommand(ISession session, IDatabaseObjectInfo[] info)
	{
		super(session, info);
	}

	
	@Override
	protected void onExecute() throws SQLException
	{
		if (!(_info[0] instanceof ITableInfo))
			return;

		ITableInfo ti = (ITableInfo) _info[0];

		ForeignKeyInfo[] fkInfo = _session.getMetaData().getImportedKeysInfo(ti);

		
		if (fkInfo.length == 1)
		{
			_foreignKeyInfo = fkInfo;
			showCustomDialog();
		} else if (fkInfo.length == 0)
		{
			_session.showErrorMessage(s_stringMgr.getString("DropForeignKeyCommand.noKeyToDrop",
				_info[0].getSimpleName()));
		} else
		{
			_listDialog =
				new DefaultListDialog(fkInfo, ti.getSimpleName(), DefaultListDialog.DIALOG_TYPE_FOREIGN_KEY);
			_listDialog.addColumnSelectionListener(new ColumnListSelectionActionListener());
			_listDialog.setLocationRelativeTo(_session.getApplication().getMainFrame());
			_listDialog.setVisible(true);
		}
	}

	
	@Override
	protected String[] generateSQLStatements()
	{
		ArrayList<String> result = new ArrayList<String>();

		for (ForeignKeyInfo fgInfo : _foreignKeyInfo)
		{
			StringBuilder sql = new StringBuilder();
			sql.append(_dialect.getDropForeignKeySQL(fgInfo.getForeignKeyName(),
				_info[0].getQualifiedName(),
				_qualifier,
				_sqlPrefs)); 
			
			
			
			
			
			
			
			if (customDialog.isCascadeSelected())
			{
				sql.append(" CASCADE");
			} else
			{
				sql.append(" RESTRICT");
			}

			result.add(sql.toString());
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
						for (IDatabaseObjectInfo dbinfo : _info)
						{
							_session.getSchemaInfo().reload(dbinfo);
						}
					}
				});
			}
		});
	}

	
	@Override
	protected boolean isRefactoringSupportedForDialect(HibernateDialect dialectExt)
	{
		return dialectExt.supportsDropConstraint();
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
						customDialog =
							new DefaultDropDialog(_foreignKeyInfo, DefaultDropDialog.DIALOG_TYPE_FOREIGN_KEY);
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

	private class ColumnListSelectionActionListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			if (_listDialog == null)
				return;

			_listDialog.setVisible(false);
			_foreignKeyInfo = _listDialog.getSelectedItems().toArray(new ForeignKeyInfo[] {});

			showCustomDialog();
		}
	}
}
