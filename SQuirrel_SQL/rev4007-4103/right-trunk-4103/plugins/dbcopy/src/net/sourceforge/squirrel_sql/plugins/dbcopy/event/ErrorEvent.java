
package net.sourceforge.squirrel_sql.plugins.dbcopy.event;

import net.sourceforge.squirrel_sql.plugins.dbcopy.SessionInfoProvider;

public class ErrorEvent extends AbstractCopyEvent {
    
    public static final int SETUP_AUTO_COMMIT_TYPE = 0;
    
    public static final int RESTORE_AUTO_COMMIT_TYPE = 1;
    
    public static final int SQL_EXCEPTION_TYPE = 2;
    
    public static final int MAPPING_EXCEPTION_TYPE = 3;
    
    public static final int USER_CANCELLED_EXCEPTION_TYPE = 4;
    
    public static final int GENERIC_EXCEPTION = 5;
    
    private int type = -1;
    
    private Exception exception = null;
    
    public ErrorEvent(SessionInfoProvider provider, int aType) {
        super(provider);
        type = aType;
    }

    
    public void setType(int type) {
        this.type = type;
    }

    
    public int getType() {
        return type;
    }

    
    public void setException(Exception exception) {
        this.exception = exception;
    }

    
    public Exception getException() {
        return exception;
    }
    
}
