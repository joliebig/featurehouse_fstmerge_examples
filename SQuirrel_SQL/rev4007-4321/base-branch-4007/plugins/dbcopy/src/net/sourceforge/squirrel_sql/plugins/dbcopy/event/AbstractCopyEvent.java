
package net.sourceforge.squirrel_sql.plugins.dbcopy.event;

import net.sourceforge.squirrel_sql.plugins.dbcopy.SessionInfoProvider;

public abstract class AbstractCopyEvent {
    protected SessionInfoProvider prov = null;
    
    public AbstractCopyEvent() {}
    
    public AbstractCopyEvent(SessionInfoProvider provider) {
        prov = provider;
    }
    
    public SessionInfoProvider getSessionInfoProvider() {
        return prov;
    }
}
