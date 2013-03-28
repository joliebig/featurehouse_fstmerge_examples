
package net.sourceforge.squirrel_sql.plugins.dbcopy;

import net.sourceforge.squirrel_sql.fw.dialects.UserCancelledOperationException;

public class MockUICallbacks implements UICallbacks {

    public boolean deleteTableData(String tableName)
            throws UserCancelledOperationException {
        return true;
    }

    public boolean appendRecordsToExisting(String tableName)
            throws UserCancelledOperationException {
        return false;
    }

}
