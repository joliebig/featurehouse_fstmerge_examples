package net.sourceforge.squirrel_sql.plugins.postgres.commands;


import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.gui.ErrorDialog;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public abstract class AbstractPostgresDialogCommand extends AbstractPostgresCommand {
    public AbstractPostgresDialogCommand(ISession session) {
        super(session);
    }


    
    protected abstract void executeScript(String script);


    
    protected class ShowSQLListener implements ActionListener, SQLResultListener {
        private final String _dialogTitle;
        private final JDialog _parentDialog;


        public ShowSQLListener(String dialogTitle, JDialog parentDialog) {
            _dialogTitle = dialogTitle;
            _parentDialog = parentDialog;
        }


        public void actionPerformed(ActionEvent e) {
            getSQLStatements(this);
        }


        public void finished(final String[] stmts) {
            if (stmts == null || stmts.length == 0) {
                _session.showMessage("No changes have been done.");
                return;
            }
            StringBuilder script = new StringBuilder();
            for (String stmt : stmts) {
                script.append(stmt).append("\n\n");
            }

            ErrorDialog sqldialog = new ErrorDialog(_parentDialog, script.substring(0, script.length() - 2));
            sqldialog.setTitle(_dialogTitle);
            sqldialog.setVisible(true);
        }
    }

    
    protected class EditSQLListener implements ActionListener, SQLResultListener {
        private final JDialog _parentDialog;


        public EditSQLListener(JDialog parentDialog) {
            _parentDialog = parentDialog;
        }


        public void actionPerformed(ActionEvent e) {
            getSQLStatements(this);
        }


        public void finished(final String[] stmts) {
            if (stmts == null || stmts.length == 0) {
                _session.showMessage("No changes have been done.");
                return;
            }
            final StringBuilder script = new StringBuilder();
            for (String stmt : stmts) {
                script.append(stmt).append("\n\n");
            }

            GUIUtils.processOnSwingEventThread(new Runnable() {
                public void run() {
                    _parentDialog.setVisible(false);
                    _session.getSQLPanelAPIOfActiveSessionWindow().appendSQLScript(script.substring(0, script.length() - 2), true);
                    _session.selectMainTab(ISession.IMainPanelTabIndexes.SQL_TAB);
                }
            });
        }
    }

    
    protected class ExecuteListener implements ActionListener, SQLResultListener {
        public void actionPerformed(ActionEvent e) {
            getSQLStatements(this);
        }


        public void finished(String[] stmts) {
            if (stmts == null || stmts.length == 0) {
                _session.showMessage("No changes have been done.");
                return;
            }
            final StringBuilder script = new StringBuilder();
            for (String stmt : stmts) {
                script.append(stmt).append("\n");
            }

            executeScript(script.toString());
        }
    }
}
