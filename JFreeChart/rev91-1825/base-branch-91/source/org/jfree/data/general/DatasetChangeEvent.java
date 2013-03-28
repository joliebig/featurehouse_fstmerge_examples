

package org.jfree.data.general;


public class DatasetChangeEvent extends java.util.EventObject {

    
    private Dataset dataset;

    
    public DatasetChangeEvent(Object source, Dataset dataset) {
        super(source);
        this.dataset = dataset;
    }

    
    public Dataset getDataset() {
        return this.dataset;
    }

}
