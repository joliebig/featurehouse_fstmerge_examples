
package net.sourceforge.squirrel_sql.plugins.dbdiff;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;


public interface SessionInfoProvider {
    
    public void setDiffSourceSession(ISession session);
    
    public ISession getDiffSourceSession();
    
    public IDatabaseObjectInfo[] getSourceSelectedDatabaseObjects();
    
    public IDatabaseObjectInfo[] getDestSelectedDatabaseObjects();
    
    public void setDestSelectedDatabaseObjects(IDatabaseObjectInfo[] infos);
    
    public void setDestDiffSession(ISession session);
    
    public ISession getDiffDestSession();
}
