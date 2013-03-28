package net.sourceforge.squirrel_sql.plugins.postgres.commands;


import net.sourceforge.squirrel_sql.fw.util.ICommand;
import net.sourceforge.squirrel_sql.client.session.ISession;

public abstract class AbstractPostgresCommand implements ICommand {
    
    protected ISession _session;


    public AbstractPostgresCommand(ISession session) {
        if (session == null) throw new IllegalArgumentException("ISession cannot be null");
        _session = session;
    }


    
    protected abstract String[] generateSQLStatements();


    
    protected void getSQLStatements(final SQLResultListener listener) {
        _session.getApplication().getThreadPool().addTask(new Runnable() {
            public void run() {
                listener.finished(generateSQLStatements());
            }
        });
    }
}
