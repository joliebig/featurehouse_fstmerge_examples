
package net.sourceforge.squirrel_sql.plugins.dbcopy;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;


public interface SessionInfoProvider {
    
    public void setCopySourceSession(ISession session);
    
    public ISession getCopySourceSession();
    
    public IDatabaseObjectInfo[] getSourceSelectedDatabaseObjects();
    
    public IDatabaseObjectInfo getDestSelectedDatabaseObject();
    
    public void setDestSelectedDatabaseObject(IDatabaseObjectInfo info);
    
    public void setDestCopySession(ISession session);
    
    public ISession getCopyDestSession();
}
