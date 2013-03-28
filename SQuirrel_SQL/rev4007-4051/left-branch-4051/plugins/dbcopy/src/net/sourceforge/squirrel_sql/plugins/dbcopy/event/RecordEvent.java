
package net.sourceforge.squirrel_sql.plugins.dbcopy.event;

import net.sourceforge.squirrel_sql.plugins.dbcopy.SessionInfoProvider;


public class RecordEvent extends AbstractCopyEvent {
    
    
    private int recordNumber;
       
     
    private int recordCount;
       
    
    public RecordEvent(SessionInfoProvider prov, int aNumber, int aCount) {
        super(prov);
        recordNumber = aNumber;
        recordCount = aCount;
    }

    
    public void setRecordNumber(int recordNumber) {
        this.recordNumber = recordNumber;
    }

    
    public int getRecordNumber() {
        return recordNumber;
    }

    
    public void setRecordCount(int recordCount) {
        this.recordCount = recordCount;
    }

    
    public int getRecordCount() {
        return recordCount;
    }
    
    
}
