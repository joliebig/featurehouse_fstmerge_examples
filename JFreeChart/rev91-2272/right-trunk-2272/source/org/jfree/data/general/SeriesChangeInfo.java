

package org.jfree.data.general;


public class SeriesChangeInfo {

    
    private SeriesChangeType changeType;

    
    private int index1;

    
    private int index2;

    
    public SeriesChangeInfo(SeriesChangeType t, int index1, int index2) {
        this.changeType = t;
        this.index1 = index1;
        this.index2 = index2;
    }

    
    public SeriesChangeType getChangeType() {
        return this.changeType;
    }

    
    public int getIndex1() {
        return this.index1;
    }

    
    public int getIndex2() {
        return this.index2;
    }

}