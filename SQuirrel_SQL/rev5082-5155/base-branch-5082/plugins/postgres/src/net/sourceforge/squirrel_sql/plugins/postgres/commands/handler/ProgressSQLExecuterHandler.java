package net.sourceforge.squirrel_sql.plugins.postgres.commands.handler;


import net.sourceforge.squirrel_sql.client.gui.ProgessCallBackDialog;
import net.sourceforge.squirrel_sql.client.session.ISQLExecuterHandler;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.SQLExecutionInfo;
import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSetUpdateableTableModel;
import net.sourceforge.squirrel_sql.fw.sql.SQLExecutionException;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import javax.swing.*;
import java.sql.ResultSet;
import java.sql.SQLWarning;

public abstract class ProgressSQLExecuterHandler implements ISQLExecuterHandler
{
	protected ISession _session;

	protected ProgessCallBackDialog _pdialog;

	protected String _commandPrefix;

	
	private final static ILogger s_log = LoggerController.createLogger(ProgressSQLExecuterHandler.class);

	public ProgressSQLExecuterHandler(ISession session, JDialog owner, String progressDialogTitle,
		String commandPrefix)
	{
		_session = session;
		_pdialog = new ProgessCallBackDialog(owner, progressDialogTitle, 0);
		_commandPrefix = commandPrefix;
	}

	public void sqlToBeExecuted(String sql)
	{
		if (s_log.isDebugEnabled())
		{
			try
			{
				Thread.sleep(1000);
			} catch (InterruptedException e)
			{
				if (s_log.isDebugEnabled())
				{
					s_log.debug(e);
				}
			}
		}

		_pdialog.setLoadingPrefix(_commandPrefix);
		_pdialog.currentlyLoading(getSuffix(sql));
	}

	protected abstract String getSuffix(String sql);

	public void sqlExecutionComplete(SQLExecutionInfo info, int processedStatementCount, int statementCount)
	{
	}

	public void sqlExecutionWarning(SQLWarning warn)
	{
		_session.showMessage(warn);
	}

	public void sqlStatementCount(int statementCount)
	{
		_pdialog.setTotalItems(statementCount + 1);
	}

	public void sqlCloseExecutionHandler()
	{
	}

	public void sqlExecutionCancelled()
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				_pdialog.dispose();
			}
		});
	}

	public void sqlDataUpdated(int updateCount)
	{
	}

	public void sqlResultSetAvailable(ResultSet rst, SQLExecutionInfo info, IDataSetUpdateableTableModel model)
	{
	}

	public void sqlExecutionException(Throwable th, String postErrorString)
	{
		String message = _session.formatException(new SQLExecutionException(th, postErrorString));
		_session.showErrorMessage(message);

		if (_session.getProperties().getWriteSQLErrorsToLog())
		{
			s_log.info(message);
		}
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				_pdialog.dispose();
			}
		});
	}
}
