

package org.jfree.data.general;

import java.io.Serializable;


public abstract class AbstractSeriesDataset extends AbstractDataset
                                            implements SeriesDataset,
                                                       SeriesChangeListener,
                                                       Serializable {

    
    private static final long serialVersionUID = -6074996219705033171L;
    
    
    protected AbstractSeriesDataset() {
        super();
    }

    
    public abstract int getSeriesCount();

    
    public abstract Comparable getSeriesKey(int series);
    
    
    public int indexOf(Comparable seriesKey) {
        int seriesCount = getSeriesCount();
        for (int s = 0; s < seriesCount; s++) {
           if (getSeriesKey(s).equals(seriesKey)) {
               return s;
           }
        }
        return -1;
    }
    
    
    public void seriesChanged(SeriesChangeEvent event) {
        fireDatasetChanged();
    }

}
