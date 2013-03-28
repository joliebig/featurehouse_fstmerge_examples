package net.sourceforge.squirrel_sql.plugins.refactoring.commands;



import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JDialog;

import net.sourceforge.squirrel_sql.client.gui.db.IDisposableDialog;
import net.sourceforge.squirrel_sql.client.session.DefaultSQLExecuterHandler;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.dialects.DialectFactory;
import net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect;
import net.sourceforge.squirrel_sql.fw.dialects.SqlGenerationPreferences;
import net.sourceforge.squirrel_sql.fw.dialects.UserCancelledOperationException;
import net.sourceforge.squirrel_sql.fw.gui.ErrorDialog;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.util.ICommand;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.plugins.refactoring.prefs.RefactoringPreferenceBean;
import net.sourceforge.squirrel_sql.plugins.refactoring.prefs.RefactoringPreferencesManager;

public abstract class AbstractRefactoringCommand implements ICommand
{
	
	private final static ILogger s_log = LoggerController.createLogger(AbstractRefactoringCommand.class);

	
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(AbstractRefactoringCommand.class);

	static interface i18n
	{
		String NO_CHANGES = s_stringMgr.getString("AbstractRefactoringCommand.noChanges");

		String DIALECT_SELECTION_CANCELLED =
			s_stringMgr.getString("AbstractRefactoringCommand.dialectSelectionCancelled");

		String UNSUPPORTED_TYPE_TITLE =
			s_stringMgr.getString("AbstractRefactoringCommand.unsupportedTypeTitle");
	}

	
	protected final ISession _session;

	
	protected final IDatabaseObjectInfo[] _info;

	
	protected HibernateDialect _dialect;

	
	protected final SqlGenerationPreferences _sqlPrefs;

	public AbstractRefactoringCommand(ISession session, IDatabaseObjectInfo[] info)
	{
		if (session == null)
			throw new IllegalArgumentException("ISession cannot be null");
		if (info == null)
			throw new IllegalArgumentException("IDatabaseObjectInfo[] cannot be null");

		_session = session;
		_info = info;

		RefactoringPreferenceBean prefsBean = RefactoringPreferencesManager.getPreferences();
		_sqlPrefs = new SqlGenerationPreferences();
		if (prefsBean != null)
		{
			_sqlPrefs.setQualifyTableNames(prefsBean.isQualifyTableNames());
			_sqlPrefs.setQuoteIdentifiers(prefsBean.isQuoteIdentifiers());
		} else
		{
			if (s_log.isDebugEnabled())
			{
				s_log.debug("RefactoringPreferencesManager.getPreferences returned null.  "
					+ "Unable to get user preferences");
			}
			_sqlPrefs.setQualifyTableNames(false);
			_sqlPrefs.setQuoteIdentifiers(false);
		}
		_sqlPrefs.setSqlStatementSeparator(_session.getQueryTokenizer().getSQLStatementSeparator());
	}

	
	public void execute()
	{
		try
		{
			ISQLDatabaseMetaData md = _session.getMetaData();
			_dialect =
				DialectFactory.getDialect(DialectFactory.DEST_TYPE, _session.getApplication().getMainFrame(), md);
			if (isRefactoringSupportedForDialect(_dialect))
			{
				onExecute();
			} else
			{
				String dialectName = DialectFactory.getDialectType(md).name();
				String msg =
					s_stringMgr.getString("AbstractRefactoringCommand.unsupportedRefactoringMsg", dialectName);
				_session.showErrorMessage(msg);
			}
		} catch (UserCancelledOperationException e)
		{
			_session.showErrorMessage(AbstractRefactoringCommand.i18n.DIALECT_SELECTION_CANCELLED);
		} catch (Exception e)
		{
			_session.showErrorMessage(e);
			s_log.error("Unexpected exception on execution: " + e.getMessage(), e);
		}
	}

	
	protected abstract boolean isRefactoringSupportedForDialect(HibernateDialect dialectExt);

	
	protected abstract void onExecute() throws Exception;

	
	protected abstract String[] generateSQLStatements() throws Exception;

	
	protected void getSQLStatements(final SQLResultListener listener)
	{
		_session.getApplication().getThreadPool().addTask(new Runnable()
		{
			public void run()
			{
				try
				{
					listener.finished(generateSQLStatements());
				} catch (UserCancelledOperationException ucoe)
				{
					_session.showErrorMessage(i18n.DIALECT_SELECTION_CANCELLED);
				} catch (Exception e)
				{
					_session.showErrorMessage(e);
					s_log.error("Unexpected exception on sql generation: " + e.getMessage(), e);
				}
			}
		});
	}

	
	protected abstract void executeScript(String script);

	
	protected class ShowSQLListener implements ActionListener, SQLResultListener
	{
		private final String _dialogTitle;

		private final JDialog _parentDialog;

		public ShowSQLListener(String dialogTitle, IDisposableDialog parentDialog)
		{
			_dialogTitle = dialogTitle;
			_parentDialog = (JDialog) parentDialog;
		}

		public void actionPerformed(ActionEvent e)
		{
			getSQLStatements(this);
		}

		public void finished(final String[] stmts)
		{
			if (stmts == null || stmts.length == 0)
			{
				_session.showMessage(i18n.NO_CHANGES);
				return;
			}
			final String script = createExecutableScriptFromStatements(stmts, false);
			GUIUtils.processOnSwingEventThread(new Runnable()
			{
				public void run()
				{
					final ErrorDialog showSQLDialog;
					if (_parentDialog != null)
						showSQLDialog = new ErrorDialog(_parentDialog, script);
					else
						showSQLDialog = new ErrorDialog(_session.getApplication().getMainFrame(), script);
					showSQLDialog.setTitle(_dialogTitle);
					showSQLDialog.setVisible(true);
				}
			});
		}
	}

	
	protected class EditSQLListener implements ActionListener, SQLResultListener
	{
		private final IDisposableDialog _parentDialog;

		public EditSQLListener(IDisposableDialog parentDialog)
		{
			_parentDialog = parentDialog;
		}

		public void actionPerformed(ActionEvent e)
		{
			getSQLStatements(this);
		}

		public void finished(final String[] stmts)
		{
			if (stmts == null || stmts.length == 0)
			{
				_session.showMessage(i18n.NO_CHANGES);
				return;
			}
			final String script = createExecutableScriptFromStatements(stmts, false);

			GUIUtils.processOnSwingEventThread(new Runnable()
			{
				public void run()
				{
					if (_parentDialog != null)
					{
						_parentDialog.dispose();
					}
					_session.getSQLPanelAPIOfActiveSessionWindow().appendSQLScript(script, true);
					_session.selectMainTab(ISession.IMainPanelTabIndexes.SQL_TAB);
				}
			});
		}
	}

	
	protected class ExecuteListener implements ActionListener, SQLResultListener
	{

		public void actionPerformed(ActionEvent e)
		{
			getSQLStatements(this);
		}

		public void finished(String[] stmts)
		{
			if (stmts == null || stmts.length == 0)
			{
				_session.showMessage(i18n.NO_CHANGES);
				return;
			}
			final String script = createExecutableScriptFromStatements(stmts, true);
			executeScript(script);
		}
	}

	protected class CommandExecHandler extends DefaultSQLExecuterHandler
	{
		protected boolean exceptionEncountered = false;

		public CommandExecHandler(ISession session)
		{
			super(session);
		}

		public void sqlExecutionException(Throwable th, String postErrorString)
		{
			super.sqlExecutionException(th, postErrorString);
			exceptionEncountered = true;
		}

		public boolean exceptionEncountered()
		{
			return exceptionEncountered;
		}
	}

	
	private String createExecutableScriptFromStatements(final String[] stmts, final boolean stripComments)
	{
		StringBuilder result = new StringBuilder();
		String seperator = _sqlPrefs.getSqlStatementSeparator();
		for (String stmt : stmts)
		{
			boolean isComment = stmt.startsWith("--");
			if (stripComments && isComment)
			{
				
				continue;
			}

			result.append(stmt);

			if (isComment)
			{
				result.append("\n");
			} else
			{
				if (!seperator.equals(";"))
				{
					result.append("\n");
				}
				result.append(seperator).append("\n\n");
			}
		}
		return result.toString();
	}
}
