

package org.jfree.chart.event;

import java.io.ObjectStreamException;
import java.io.Serializable;


public final class ChartChangeEventType implements Serializable {

    
    private static final long serialVersionUID = 5481917022435735602L;

    
    public static final ChartChangeEventType GENERAL
            = new ChartChangeEventType("ChartChangeEventType.GENERAL");

    
    public static final ChartChangeEventType NEW_DATASET
            = new ChartChangeEventType("ChartChangeEventType.NEW_DATASET");

    
    public static final ChartChangeEventType DATASET_UPDATED
            = new ChartChangeEventType("ChartChangeEventType.DATASET_UPDATED");

    
    private String name;

    
    private ChartChangeEventType(String name) {
        this.name = name;
    }

    
    public String toString() {
        return this.name;
    }

    
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ChartChangeEventType)) {
            return false;
        }
        ChartChangeEventType that = (ChartChangeEventType) obj;
        if (!this.name.equals(that.toString())) {
            return false;
        }
        return true;
    }

    
    public int hashCode() {
        return this.name.hashCode();
    }

    
    private Object readResolve() throws ObjectStreamException {
        if (this.equals(ChartChangeEventType.GENERAL)) {
            return ChartChangeEventType.GENERAL;
        }
        else if (this.equals(ChartChangeEventType.NEW_DATASET)) {
            return ChartChangeEventType.NEW_DATASET;
        }
        else if (this.equals(ChartChangeEventType.DATASET_UPDATED)) {
            return ChartChangeEventType.DATASET_UPDATED;
        }
        return null;
    }

}
