

package org.jfree.chart.plot;

import java.io.ObjectStreamException;
import java.io.Serializable;


public final class DatasetRenderingOrder implements Serializable {

    
    private static final long serialVersionUID = -600593412366385072L;    

    
    public static final DatasetRenderingOrder FORWARD
        = new DatasetRenderingOrder("DatasetRenderingOrder.FORWARD");

    
    public static final DatasetRenderingOrder REVERSE
        = new DatasetRenderingOrder("DatasetRenderingOrder.REVERSE");

    
    private String name;

    
    private DatasetRenderingOrder(String name) {
        this.name = name;
    }

    
    public String toString() {
        return this.name;
    }

    
    public boolean equals(Object o) {

        if (this == o) {
            return true;
        }
        if (!(o instanceof DatasetRenderingOrder)) {
            return false;
        }

        DatasetRenderingOrder order = (DatasetRenderingOrder) o;
        if (!this.name.equals(order.toString())) {
            return false;
        }

        return true;

    }
    
    
    private Object readResolve() throws ObjectStreamException {
        if (this.equals(DatasetRenderingOrder.FORWARD)) {
            return DatasetRenderingOrder.FORWARD;
        }
        else if (this.equals(DatasetRenderingOrder.REVERSE)) {
            return DatasetRenderingOrder.REVERSE;
        }      
        return null;
    }

}
