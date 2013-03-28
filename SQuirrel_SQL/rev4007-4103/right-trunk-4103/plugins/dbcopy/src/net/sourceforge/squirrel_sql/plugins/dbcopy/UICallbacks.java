
package net.sourceforge.squirrel_sql.plugins.dbcopy;

import net.sourceforge.squirrel_sql.fw.dialects.UserCancelledOperationException;


public interface UICallbacks {

    
    public boolean deleteTableData(String tableName) 
        throws UserCancelledOperationException;
    
    
    public boolean appendRecordsToExisting(String tableName)
        throws UserCancelledOperationException;
}
