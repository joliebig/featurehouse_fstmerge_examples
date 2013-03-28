

package org.jfree.chart.plot;

import java.io.Serializable;
import java.util.List;


public abstract class AbstractPieLabelDistributor implements Serializable {

    
    protected List labels;

    
    public AbstractPieLabelDistributor() {
        this.labels = new java.util.ArrayList();
    }

    
    public PieLabelRecord getPieLabelRecord(int index) {
        return (PieLabelRecord) this.labels.get(index);
    }

    
    public void addPieLabelRecord(PieLabelRecord record) {
        if (record == null) {
            throw new IllegalArgumentException("Null 'record' argument.");
        }
        this.labels.add(record);
    }

    
    public int getItemCount() {
        return this.labels.size();
    }

    
    public void clear() {
        this.labels.clear();
    }

    
    public abstract void distributeLabels(double minY, double height);

}
