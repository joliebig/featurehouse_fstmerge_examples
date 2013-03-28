
package net.sourceforge.squirrel_sql.plugins.dbcopy.event;

import net.sourceforge.squirrel_sql.plugins.dbcopy.SessionInfoProvider;

public class CopyEvent extends AbstractCopyEvent {

    int[] tableCounts;
    
    public CopyEvent(SessionInfoProvider prov) {
        super(prov);
    }
    
    public void setTableCounts(int[] tableCounts) {
        this.tableCounts = tableCounts;
    }
    
    public int[] getTableCounts() {
        return tableCounts;
    }
    
}
