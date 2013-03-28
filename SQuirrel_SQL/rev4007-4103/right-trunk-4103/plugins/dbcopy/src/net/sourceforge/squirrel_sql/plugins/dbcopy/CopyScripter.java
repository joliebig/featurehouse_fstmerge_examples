
package net.sourceforge.squirrel_sql.plugins.dbcopy;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.plugins.dbcopy.event.CopyEvent;
import net.sourceforge.squirrel_sql.plugins.dbcopy.event.CopyTableAdaptor;
import net.sourceforge.squirrel_sql.plugins.dbcopy.event.CopyTableListener;
import net.sourceforge.squirrel_sql.plugins.dbcopy.event.StatementEvent;
import net.sourceforge.squirrel_sql.plugins.dbcopy.util.ScriptWriter;


public class CopyScripter extends CopyTableAdaptor implements CopyTableListener {

    
    public void copyStarted(CopyEvent e) {
        initializeScript(e.getSessionInfoProvider());
    }    

    
    public void statementExecuted(StatementEvent e) {
        String sql = e.getStatement();
        if (e.getStatementType() == StatementEvent.INSERT_RECORD_TYPE) {
            String[] values = e.getBindValues();
            ScriptWriter.write(sql, values);
        } else {
            ScriptWriter.write(sql);
        }
    }
    
    
    public void copyFinished(int seconds) {
        finalizeScript();
    }

    
    private void initializeScript(SessionInfoProvider prov) {
        ISession source = prov.getCopySourceSession();
        ISession dest = prov.getCopyDestSession();
        ScriptWriter.open(source,dest);
    }
    
    
    private void finalizeScript() {
        ScriptWriter.close();
    }
}
