package net.sourceforge.squirrel_sql.plugins.refactoring.commands;


import java.util.ArrayList;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.SQLExecuterTask;
import net.sourceforge.squirrel_sql.fw.dialects.DatabaseObjectQualifier;
import net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect;
import net.sourceforge.squirrel_sql.fw.dialects.UserCancelledOperationException;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.plugins.refactoring.gui.DefaultDropDialog;

public class DropSequenceCommand extends AbstractRefactoringCommand {
    
    @SuppressWarnings("unused")
	private final ILogger s_log = LoggerController.createLogger(DropSequenceCommand.class);
    
    private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(DropSequenceCommand.class);

    static interface i18n {
        String SHOWSQL_DIALOG_TITLE = s_stringMgr.getString("DropSequenceCommand.sqlDialogTitle");
    }

    protected DefaultDropDialog customDialog;


    public DropSequenceCommand(ISession session, IDatabaseObjectInfo[] dbInfo) {
        super(session, dbInfo);
    }


    @Override
    protected void onExecute() {
        showCustomDialog();
    }


    protected void showCustomDialog() {
        _session.getApplication().getThreadPool().addTask(new Runnable() {
            public void run() {
                GUIUtils.processOnSwingEventThread(new Runnable() {
                    public void run() {
                        customDialog = new DefaultDropDialog(_info, DefaultDropDialog.DIALOG_TYPE_SEQUENCE);
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


    @Override
    protected String[] generateSQLStatements() throws UserCancelledOperationException
	{
		ArrayList<String> result = new ArrayList<String>();

		if (_dialect.supportsDropSequence())
		{
			for (IDatabaseObjectInfo dbo : _info)
			{
				DatabaseObjectQualifier qualifier =
					new DatabaseObjectQualifier(dbo.getCatalogName(), dbo.getSchemaName());
				result.add(_dialect.getDropSequenceSQL(dbo.getSimpleName(),
					customDialog.isCascadeSelected(),
					qualifier,
					_sqlPrefs));
			}
		} else
		{
			_session.showMessage(s_stringMgr.getString("DropSequenceCommand.unsupportedOperationMsg",
				_dialect.getDisplayName()));
		}

		return result.toArray(new String[] {});
	}


    @Override
    protected void executeScript(String script) {
        CommandExecHandler handler = new CommandExecHandler(_session);

        SQLExecuterTask executer = new SQLExecuterTask(_session, script, handler);
        executer.run(); 

        _session.getApplication().getThreadPool().addTask(new Runnable() {
            public void run() {
                GUIUtils.processOnSwingEventThread(new Runnable() {
                    public void run() {
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
		return dialectExt.supportsDropSequence();
	}
}
