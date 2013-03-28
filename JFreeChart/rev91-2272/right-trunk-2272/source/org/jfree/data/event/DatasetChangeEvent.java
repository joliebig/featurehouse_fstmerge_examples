

package org.jfree.data.event;

import org.jfree.data.general.*;
import org.jfree.chart.event.DatasetChangeInfo;
import org.jfree.chart.plot.Plot;


public class DatasetChangeEvent extends java.util.EventObject {

    
    private Dataset dataset;

    
    private DatasetChangeInfo info;

    
    public DatasetChangeEvent(Object source, Dataset dataset,
            DatasetChangeInfo info) {
        super(source);
        if (info == null) {
            throw new IllegalArgumentException("Null 'info' argument.");
        }
        this.dataset = dataset;
        this.info = info;
    }

    
    public Dataset getDataset() {
        return this.dataset;
    }

    
    public DatasetChangeInfo getInfo() {
        return this.info;
    }

}
