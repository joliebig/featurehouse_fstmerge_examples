
package net.sourceforge.squirrel_sql.plugins.dbcopy.event;

import net.sourceforge.squirrel_sql.plugins.dbcopy.SessionInfoProvider;


public class TableEvent extends AbstractCopyEvent {
    
    
    private int tableNumber;
    
    
    private int tableCount;
    
    
    private String tableName;
    
    public TableEvent(SessionInfoProvider provider) {
        super(provider);
    }

    
    public void setTableNumber(int tableNumber) {
        this.tableNumber = tableNumber;
    }

    
    public int getTableNumber() {
        return tableNumber;
    }

    
    public void setTableCount(int tableCount) {
        this.tableCount = tableCount;
    }

    
    public int getTableCount() {
        return tableCount;
    }

    
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    
    public String getTableName() {
        return tableName;
    }
    
    
}
