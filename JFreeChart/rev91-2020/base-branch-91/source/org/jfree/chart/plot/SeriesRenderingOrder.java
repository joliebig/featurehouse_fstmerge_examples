

package org.jfree.chart.plot;

import java.io.ObjectStreamException;
import java.io.Serializable;


public final class SeriesRenderingOrder implements Serializable {

    
    private static final long serialVersionUID = 209336477448807735L;
    
    
    public static final SeriesRenderingOrder FORWARD
        = new SeriesRenderingOrder("SeriesRenderingOrder.FORWARD");

    
    public static final SeriesRenderingOrder REVERSE
        = new SeriesRenderingOrder("SeriesRenderingOrder.REVERSE");

    
    private String name;

    
    private SeriesRenderingOrder(String name) {
        this.name = name;
    }

    
    public String toString() {
        return this.name;
    }

    
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }
        if (!(obj instanceof SeriesRenderingOrder)) {
            return false;
        }

        SeriesRenderingOrder order = (SeriesRenderingOrder) obj;
        if (!this.name.equals(order.toString())) {
            return false;
        }

        return true;

    }

    
    private Object readResolve() throws ObjectStreamException {
        if (this.equals(SeriesRenderingOrder.FORWARD)) {
            return SeriesRenderingOrder.FORWARD;
        }
        else if (this.equals(SeriesRenderingOrder.REVERSE)) {
            return SeriesRenderingOrder.REVERSE;
        }
        return null;
    }

}
