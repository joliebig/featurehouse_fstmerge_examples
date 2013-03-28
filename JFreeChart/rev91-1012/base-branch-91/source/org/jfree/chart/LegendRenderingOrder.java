

package org.jfree.chart;

import java.io.ObjectStreamException;
import java.io.Serializable;


public final class LegendRenderingOrder implements Serializable {

    
    private static final long serialVersionUID = -3832486612685808616L;
    
    
    public static final LegendRenderingOrder STANDARD 
        = new LegendRenderingOrder("LegendRenderingOrder.STANDARD");

    
    public static final LegendRenderingOrder REVERSE 
        = new LegendRenderingOrder("LegendRenderingOrder.REVERSE");

    
    private String name;

    
    private LegendRenderingOrder(String name) {
        this.name = name;
    }

    
    public String toString() {
        return this.name;
    }

    
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof LegendRenderingOrder)) {
            return false;
        }
        LegendRenderingOrder order = (LegendRenderingOrder) obj;
        if (!this.name.equals(order.toString())) {
            return false;
        }
        return true;
    }
   
    
    private Object readResolve() throws ObjectStreamException {
        if (this.equals(LegendRenderingOrder.STANDARD)) {
            return LegendRenderingOrder.STANDARD;
        }
        else if (this.equals(LegendRenderingOrder.REVERSE)) {
            return LegendRenderingOrder.REVERSE;
        }     
        return null;
    }

}
