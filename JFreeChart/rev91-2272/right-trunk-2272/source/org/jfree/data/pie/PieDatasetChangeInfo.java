

package org.jfree.data.pie;

import org.jfree.chart.event.DatasetChangeInfo;


public class PieDatasetChangeInfo extends DatasetChangeInfo {

    
    private PieDatasetChangeType changeType;

    
    private int index1;

    
    private int index2;

    
    public PieDatasetChangeInfo(PieDatasetChangeType t, int index1,
            int index2) {
        this.changeType = t;
        this.index1 = index1;
        this.index2 = index2;
    }

    
    public PieDatasetChangeType getChangeType() {
        return this.changeType;
    }

    
    public int getIndex1() {
        return this.index1;
    }

    
    public int getIndex2() {
        return this.index2;
    }

}